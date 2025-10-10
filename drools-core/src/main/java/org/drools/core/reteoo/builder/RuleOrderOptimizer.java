/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo.builder;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.kie.api.definition.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Optimizes rule build order to ensure BiLinear optimization succeeds.
 *
 * BiLinear optimization requires that rules defining shared patterns
 * build before rules that reuse those patterns. This class analyzes
 * BiLinear dependencies and reorders rules using topological sorting to satisfy
 * these constraints.
 *
 * The reordering is transparent to users and maintains relative order of rules
 * not involved in BiLinear optimization.
 */
public class RuleOrderOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(RuleOrderOptimizer.class);

    public static Collection<? extends Rule> reorderForBiLinear(
            Collection<? extends Rule> rules,
            BiLinearDetector.BiLinearContext biLinearContext) {

        if (!BiLinearDetector.isBiLinearEnabled()) {
            return rules;
        }

        if (biLinearContext.sharedChains().isEmpty()) {
            return rules;
        }

        Map<String, Set<String>> dependencyGraph = buildDependencyGraph(biLinearContext);

        Map<String, RuleImpl> ruleMap = new HashMap<>();
        List<RuleImpl> originalOrder = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule instanceof RuleImpl ruleImpl) {
                ruleMap.put(rule.getName(), ruleImpl);
                originalOrder.add(ruleImpl);
            }
        }

        return topologicalSort(originalOrder, ruleMap, dependencyGraph);
    }

    /**
     * Builds a dependency graph from BiLinear pairs.
     *
     * For each Pair(chainId, consumerRuleName, providerRuleName):
     *   - providerRule = creates the shared JoinNode (simpler pattern, e.g., C-D)
     *   - consumerRule = uses BiLinearJoinNode linking to providerRule's JoinNode (complex pattern, e.g., A-B-C-D)
     *   - providerRule MUST build before consumerRule (so consumerRule can link to providerRule's node)
     *
     * @param biLinearContext BiLinear context with detected pairs
     * @return Adjacency list: consumerRule -> set of providerRules that must build before it
     */
    private static Map<String, Set<String>> buildDependencyGraph(
            BiLinearDetector.BiLinearContext biLinearContext) {

        Map<String, Set<String>> graph = new HashMap<>();

        for (Map.Entry<String, List<BiLinearDetector.Pair>> entry : biLinearContext.sharedChains().entrySet()) {
            for (BiLinearDetector.Pair pair : entry.getValue()) {
                String consumerRule = pair.consumerRuleName();
                String providerRule = pair.providerRuleName();

                graph.computeIfAbsent(consumerRule, k -> new HashSet<>()).add(providerRule);

                graph.putIfAbsent(providerRule, new HashSet<>());
            }
        }

        return graph;
    }

    /**
     * Algorithm:
     * 1. Calculate in-degree for each rule (number of rules that must build before it)
     * 2. Start with rules with in-degree 0 (no dependencies)
     * 3. Process nodes in order, choosing from available rules based on original order
     * 4. When a rule is processed, decrement in-degree of rules that depend on it
     * 5. Detect and handle cycles gracefully
     *
     * Rules not involved in BiLinear are added in their original positions.
     * The algorithm preserves relative order as much as possible (stable sort).
     *
     */
    private static List<Rule> topologicalSort(
            List<? extends Rule> originalOrder,
            Map<String, RuleImpl> ruleMap,
            Map<String, Set<String>> dependencyGraph) {

        Map<String, Integer> inDegree = new HashMap<>();
        Set<String> rulesInGraph = dependencyGraph.keySet();

        for (String ruleName : rulesInGraph) {
            inDegree.put(ruleName, 0);
        }

        for (Map.Entry<String, Set<String>> entry : dependencyGraph.entrySet()) {
            for (String target : entry.getValue()) {
                inDegree.put(target, inDegree.getOrDefault(target, 0) + 1);
            }
        }

        List<Rule> result = new ArrayList<>();
        Set<String> remaining = new HashSet<>(rulesInGraph);

        while (!remaining.isEmpty()) {
            List<String> available = new ArrayList<>();
            for (Rule rule : originalOrder) {
                String ruleName = rule.getName();
                if (remaining.contains(ruleName) && inDegree.get(ruleName) == 0) {
                    available.add(ruleName);
                }
            }

            // If no rules available but rules remain, we have a cycle
            if (available.isEmpty()) {
                logger.warn("Circular BiLinear dependencies detected. " +
                           "Remaining rules will be built in original order: {}", remaining);

                for (Rule rule : originalOrder) {
                    if (remaining.contains(rule.getName())) {
                        result.add(rule);
                    }
                }
                break;
            }

            for (String ruleName : available) {
                RuleImpl rule = ruleMap.get(ruleName);
                if (rule != null) {
                    result.add(rule);
                    remaining.remove(ruleName);

                    Set<String> dependents = dependencyGraph.get(ruleName);
                    if (dependents != null) {
                        for (String dependent : dependents) {
                            inDegree.put(dependent, inDegree.get(dependent) - 1);
                        }
                    }
                }
            }
        }

        if (rulesInGraph.size() == originalOrder.size()) {
            return result;
        }

        return mergeWithNonBiLinearRules(originalOrder, result, rulesInGraph);
    }

    /**
     * Merges topologically sorted BiLinear rules with non-BiLinear rules.
     *
     * Strategy: Use the sorted BiLinear order, but insert non-BiLinear rules
     * at their original relative positions among the BiLinear rules.
     */
    private static List<Rule> mergeWithNonBiLinearRules(
            List<? extends Rule> originalOrder,
            List<? extends Rule> sortedBiLinearRules,
            Set<String> biLinearRuleNames) {

        if (biLinearRuleNames.isEmpty()) {
            return new ArrayList<>(originalOrder);
        }
        if (biLinearRuleNames.size() == originalOrder.size()) {
            return new ArrayList<>(sortedBiLinearRules);
        }

        Map<String, Integer> originalPositions = new HashMap<>();
        for (int i = 0; i < originalOrder.size(); i++) {
            originalPositions.put(originalOrder.get(i).getName(), i);
        }

        List<Rule> result = new ArrayList<>();
        int nextBiLinearIndex = 0;

        for (Rule rule : originalOrder) {
            if (!biLinearRuleNames.contains(rule.getName())) {
                // Non-BiLinear rule: add all BiLinear rules that should come before it
                int currentPos = originalPositions.get(rule.getName());
                while (nextBiLinearIndex < sortedBiLinearRules.size()) {
                    Rule biLinearRule = sortedBiLinearRules.get(nextBiLinearIndex);
                    int originalBiLinearPos = originalPositions.get(biLinearRule.getName());
                    if (originalBiLinearPos < currentPos) {
                        result.add(biLinearRule);
                        nextBiLinearIndex++;
                    } else {
                        break;
                    }
                }
                result.add(rule);
            }
        }

        while (nextBiLinearIndex < sortedBiLinearRules.size()) {
            result.add(sortedBiLinearRules.get(nextBiLinearIndex++));
        }

        return result;
    }
}
