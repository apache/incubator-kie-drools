/**
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
package org.drools.impact.analysis.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.impact.analysis.model.Rule;

public class GraphAnalysis {

    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<Class<?>, Map<String, AnalyzedRuleSet>> propertyReactiveMap = new HashMap<>();
    private final Map<Class<?>, AnalyzedRuleSet> classReativeMap = new HashMap<>(); // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
    private final Map<Class<?>, AnalyzedRuleSet> insertReactiveMap = new HashMap<>(); // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)

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

    public void addPropertyReactiveRule(Class<?> clazz, String property, Rule rule, boolean positive) {
        propertyReactiveMap.computeIfAbsent(clazz, k -> new HashMap<>()).computeIfAbsent(property, k -> new AnalyzedRuleSet()).add(new AnalyzedRule( rule, positive ));
    }

    public void addClassReactiveRule(Class<?> clazz, Rule rule, boolean positive) {
        classReativeMap.computeIfAbsent(clazz, k -> new AnalyzedRuleSet()).add(new AnalyzedRule( rule, positive ));
    }

    public void addInsertReactiveRule(Class<?> clazz, Rule rule, boolean positive) {
        insertReactiveMap.computeIfAbsent(clazz, k -> new AnalyzedRuleSet()).add(new AnalyzedRule( rule, positive ));
    }

    public Collection<AnalyzedRule> getRulesReactiveTo(Class<?> clazz) {
        AnalyzedRuleSet rules = new AnalyzedRuleSet();
        rules.addAll(propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).values().stream().flatMap(AnalyzedRuleSet::stream).collect(Collectors.toSet()));
        rules.addAll(classReativeMap.getOrDefault(clazz, AnalyzedRuleSet.EMPTY));
        rules.addAll(insertReactiveMap.getOrDefault(clazz, AnalyzedRuleSet.EMPTY));
        return rules;
    }

    public Collection<AnalyzedRule> getRulesReactiveToWithoutProperty(Class<?> clazz) {
        AnalyzedRuleSet rules = new AnalyzedRuleSet();
        rules.addAll(classReativeMap.getOrDefault(clazz, AnalyzedRuleSet.EMPTY));
        rules.addAll(insertReactiveMap.getOrDefault(clazz, AnalyzedRuleSet.EMPTY));
        return rules;
    }

    public Collection<AnalyzedRule> getRulesReactiveTo(Class<?> clazz, String property) {
        AnalyzedRuleSet rules = new AnalyzedRuleSet();
        rules.addAll( propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).getOrDefault(property, AnalyzedRuleSet.EMPTY) );
        rules.addAll(classReativeMap.getOrDefault(clazz, AnalyzedRuleSet.EMPTY));
        return rules;
    }

    private static class AnalyzedRuleSet implements Collection<AnalyzedRule> {

        private static final AnalyzedRuleSet EMPTY = new AnalyzedRuleSet();

        private final Map<Rule, AnalyzedRule> rules = new HashMap<>();

        @Override
        public int size() {
            return rules.size();
        }

        @Override
        public boolean isEmpty() {
            return rules.isEmpty();
        }

        @Override
        public boolean contains( Object o ) {
            return o instanceof AnalyzedRule ? rules.containsKey( (( AnalyzedRule ) o).getRule() ) : false;
        }

        @Override
        public Iterator<AnalyzedRule> iterator() {
            return rules.values().iterator();
        }

        @Override
        public Object[] toArray() {
            return rules.keySet().toArray();
        }

        @Override
        public <T> T[] toArray( T[] a ) {
            return (T[]) rules.keySet().toArray( new AnalyzedRule[rules.size()] );
        }

        @Override
        public boolean add( AnalyzedRule analyzedRule ) {
            AnalyzedRule existingRule = rules.get(analyzedRule.getRule());
            if (existingRule == null) {
                rules.put( analyzedRule.getRule(), analyzedRule );
                return true;
            }
            existingRule.combineReactivityType( analyzedRule.getReactivityType() );
            return false;
        }

        @Override
        public void clear() {
            rules.clear();
        }

        @Override
        public boolean remove( Object o ) {
            if (o instanceof AnalyzedRule) {
                return rules.remove( (( AnalyzedRule ) o).getRule() ) != null;
            }
            return false;
        }

        @Override
        public boolean containsAll( Collection<?> c ) {
            return c.stream().allMatch( this::contains );
        }

        @Override
        public boolean addAll( Collection<? extends AnalyzedRule> c ) {
            boolean result = false;
            for (AnalyzedRule o : c) {
                if ( add( o ) ) {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public boolean removeAll( Collection<?> c ) {
            boolean result = false;
            for (Object o : c) {
                if ( o instanceof AnalyzedRule && remove(o) ) {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public boolean retainAll( Collection<?> c ) {
            throw new UnsupportedOperationException();
        }
    }
}
