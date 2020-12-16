/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.impact.analysis.model.Rule;

public class GraphAnalysis {

    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<Class<?>, Map<String, Set<Rule>>> propertyReactiveMap = new HashMap<>();
    private final Map<Class<?>, Set<Rule>> classReativeMap = new HashMap<>(); // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
    private final Map<Class<?>, Set<Rule>> insertReactiveMap = new HashMap<>(); // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)

    public Node getNode(String fqn) {
        return nodeMap.get(fqn);
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public void addNode(Node node) {
        nodeMap.put(node.getId(), node);
    }

    public boolean isRegisteredClass(Class<?> clazz) {
        return propertyReactiveMap.containsKey(clazz) || classReativeMap.containsKey(clazz) || insertReactiveMap.containsKey(clazz);
    }

    public void addPropertyReactiveRule(Class<?> clazz, String property, Rule rule) {
        propertyReactiveMap.computeIfAbsent(clazz, k -> new HashMap<>()).computeIfAbsent(property, k -> new HashSet<>()).add(rule);
    }

    public void addClassReactiveRule(Class<?> clazz, Rule rule) {
        classReativeMap.computeIfAbsent(clazz, k -> new HashSet<>()).add(rule);
    }

    public void addInsertReactiveRule(Class<?> clazz, Rule rule) {
        insertReactiveMap.computeIfAbsent(clazz, k -> new HashSet<>()).add(rule);
    }

    public Set<Rule> getRulesReactiveTo(Class<?> clazz) {
        Set<Rule> rules = new HashSet<>();
        rules.addAll(propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
        rules.addAll(classReativeMap.getOrDefault(clazz, Collections.emptySet()));
        rules.addAll(insertReactiveMap.getOrDefault(clazz, Collections.emptySet()));
        return rules;
    }

    public Set<Rule> getRulesReactiveTo(Class<?> clazz, String property) {
        Set<Rule> rules = new HashSet<>();
        rules.addAll(propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).getOrDefault(property, Collections.emptySet()));
        rules.addAll(classReativeMap.getOrDefault(clazz, Collections.emptySet()));
        return rules;
    }
}
