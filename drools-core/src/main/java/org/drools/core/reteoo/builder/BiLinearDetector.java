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
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.constraint.Constraint;
import org.drools.core.reteoo.BiLinearJoinNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.kie.api.definition.rule.Rule;

import java.util.*;

public class BiLinearDetector {

    public static BiLinearContext detectBiLinearOpportunities(Collection<? extends Rule> rules, String kieBaseId) {
        if (!isBiLinearEnabled()) {
            return new BiLinearContext(new HashMap<>(), new HashMap<>());
        }

        Map<String, List<Pair>> sharedChains = detectSharedPatternChains(rules, kieBaseId);

        return new BiLinearContext(sharedChains, new HashMap<>());
    }

    public record BiLinearContext(Map<String, List<Pair>> sharedChains, Map<String, BiLinearLink> biLinearJoinNodes) {

        public void setLink(String hash,
                            LeftTupleSource remoteNode) {
            BiLinearLink link = getLink(hash);
            for (BiLinearJoinNode biLinearJoinNode : link.getBiLinearJoinNodes()) {
                biLinearJoinNode.linkOutsideLeftInput(remoteNode);
            }
        }

        public void setBiLinearNode(String hash,
                                    BiLinearJoinNode biLinearNode) {
            BiLinearLink link = getLink(hash);
            link.addBiLinearJoinNode(biLinearNode);
        }

        private BiLinearLink getLink(String hash) {
            return biLinearJoinNodes.computeIfAbsent(hash, k -> new BiLinearLink());
        }
    }


    public static final class BiLinearLink {
        private List<BiLinearJoinNode> biLinearJoinNodes = new ArrayList<>();

        public void addBiLinearJoinNode(BiLinearJoinNode biLinearJoinNode) {
            this.biLinearJoinNodes.add(biLinearJoinNode);
        }

        public List<BiLinearJoinNode> getBiLinearJoinNodes() {
            return biLinearJoinNodes;
        }

    }

    private static Map<String, List<Pair>> detectSharedPatternChains(Collection<? extends Rule> rules, String kieBaseId) {
        Map<String, List<ChainMatch>> chainMatches = new HashMap<>();

        for (Rule r : rules) {
            if (r instanceof RuleImpl rule) {
                if (containsEval(rule)) {
                    continue;
                }

                if (hasAlphaConstraints(rule)) {
                    continue;
                }

                PatternChainHasher.ChainHashResult chainResult = PatternChainHasher.generateChainHashes(rule, kieBaseId);

                if (chainResult.getTailHashes().isEmpty()) {
                    continue;
                }

                for (PatternChainHasher.TailHash tailHash : chainResult.getTailHashes()) {
                    String unscopedHash = tailHash.getHash();
                    String scopedHash = kieBaseId != null && !kieBaseId.contains("::")
                            ? kieBaseId + "::" + unscopedHash
                            : kieBaseId + unscopedHash;

                    chainMatches.computeIfAbsent(scopedHash, k -> new ArrayList<>())
                              .add(new ChainMatch(rule, tailHash));
                }
            }
        }

        Map<String, List<Pair>> pairs = new HashMap<>();

        for (Map.Entry<String, List<ChainMatch>> entry : chainMatches.entrySet()) {
            List<ChainMatch> matches = entry.getValue();

            if (matches.size() >= 2) {
                ChainMatch firstMatch = matches.get(0);

                if (isSameTypeChain(firstMatch.tailHash().getPatterns())) {
                    continue;
                }

                for (int i = 0; i < matches.size(); i++) {
                    for (int j = i + 1; j < matches.size(); j++) {
                        ChainMatch match1 = matches.get(i);
                        ChainMatch match2 = matches.get(j);

                        if (match1.tailHash().isFullChain() && match2.tailHash().isFullChain()) {
                            continue;
                        }

                        if (!match1.tailHash().isFullChain() && !match2.tailHash().isFullChain()) {
                            continue;
                        }

                        if (match1.tailHash().isFullChain()) {
                            if (match1.tailHash().getLength() >= 2) {
                                pairs.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                                     .add(new Pair(match2, match2.rule().getName(), match1.rule().getName()));
                            }
                        } else {
                            if (match2.tailHash().getLength() >= 2) {
                                pairs.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                                     .add(new Pair(match1, match1.rule().getName(), match2.rule().getName()));
                            }
                        }
                    }
                }
            }
        }

        Set<String> allConsumerRules = new HashSet<>();
        for (List<Pair> pairList : pairs.values()) {
            for (Pair pair : pairList) {
                allConsumerRules.add(pair.consumerRuleName());
            }
        }

        for (Map.Entry<String, List<Pair>> entry : pairs.entrySet()) {
            entry.getValue().removeIf(pair -> allConsumerRules.contains(pair.providerRuleName()));
        }

        pairs.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return pairs;
    }

    public record Pair(ChainMatch id, String consumerRuleName, String providerRuleName){
        public int getChainLength() {
            return id.tailHash().getLength();
        }
    }

    private record ChainMatch(RuleImpl rule, PatternChainHasher.TailHash tailHash) {
    }

    public static boolean isBiLinearEnabled() {
        return Boolean.parseBoolean(System.getProperty("drools.bilinear.enabled", "false"));
    }

    private static boolean containsEval(RuleImpl rule) {
        if (rule == null || rule.getLhs() == null) {
            return false;
        }
        return containsEvalInRCE(rule.getLhs());
    }

    private static boolean hasAlphaConstraints(RuleImpl rule) {
        if (rule == null || rule.getLhs() == null) {
            return false;
        }
        return hasAlphaConstraintsInRCE(rule.getLhs());
    }

    private static boolean hasAlphaConstraintsInRCE(RuleConditionElement rce) {
        if (rce instanceof Pattern pattern) {
            for (Constraint constraint : pattern.getConstraints()) {
                if (constraint instanceof AlphaNodeFieldConstraint) {
                    return true;
                }
            }
        }
        if (rce instanceof GroupElement ge) {
            // Only check AND groups (not EXISTS/NOT/OR subnetworks)
            if (ge.isAnd()) {
                for (RuleConditionElement child : ge.getChildren()) {
                    if (hasAlphaConstraintsInRCE(child)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean containsEvalInRCE(RuleConditionElement rce) {
        if (rce instanceof EvalCondition) {
            return true;
        }
        if (rce instanceof GroupElement ge) {
            for (RuleConditionElement child : ge.getChildren()) {
                if (containsEvalInRCE(child)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSameTypeChain(List<Pattern> patterns) {
        if (patterns == null || patterns.size() < 2) {
            return true;
        }

        String firstType = patterns.get(0).getObjectType().getClassName();
        for (int i = 1; i < patterns.size(); i++) {
            if (!patterns.get(i).getObjectType().getClassName().equals(firstType)) {
                return false;
            }
        }
        return true;
    }
}