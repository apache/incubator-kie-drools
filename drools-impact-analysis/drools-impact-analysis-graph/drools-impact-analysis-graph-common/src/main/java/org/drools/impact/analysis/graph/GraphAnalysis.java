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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.left.Constraint;
import org.drools.impact.analysis.model.left.Pattern;
import org.drools.impact.analysis.model.right.ConsequenceAction;

public class GraphAnalysis {

    private final Map<String, Node> nodeMap = new HashMap<>();
    private final Map<Rule, Map<Pattern, Map<Constraint, ConstraintNode>>> constraintLookupNodeMap = new HashMap<>();
    private final Map<Rule, Map<ConsequenceAction, ActionNode>> actionLookupNodeMap = new HashMap<>();

    private final Map<Pattern, Rule> patternToRuleLookupMap = new HashMap<>();
    private final Map<Constraint, Pattern> constraintToPatternLookupMap = new HashMap<>();
    private final Map<ConsequenceAction, Rule> actionToRuleLookupMap = new HashMap<>();

    private final Map<Class<?>, Map<String, Set<Rule>>> propertyReactiveMap = new HashMap<>();
    private final Map<Class<?>, Set<Rule>> classReativeMap = new HashMap<>(); // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
    private final Map<Class<?>, Set<Rule>> insertReactiveMap = new HashMap<>(); // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)

    private final Map<Class<?>, Map<String, Set<Constraint>>> propertyReactiveConstraintMap = new HashMap<>();
    private final Map<Class<?>, Set<Constraint>> classReativeConstraintMap = new HashMap<>(); // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
    private final Map<Class<?>, Set<Constraint>> insertReactiveConstraintMap = new HashMap<>(); // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)

    public Node getNode(String fqn) {
        return nodeMap.get(fqn);
    }

    public Map<String, Node> getNodeMap() {
        return nodeMap;
    }

    public void addNode(Node node) {
        nodeMap.put(node.getId(), node);
        if (node instanceof ConstraintNode) {
            ConstraintNode constraintNode = (ConstraintNode) node;
            Map<Pattern, Map<Constraint, ConstraintNode>> patternMap = constraintLookupNodeMap.computeIfAbsent(constraintNode.getRule(), k -> new HashMap<>());
            Map<Constraint, ConstraintNode> constraintMap = patternMap.computeIfAbsent(constraintNode.getPattern(), k -> new HashMap<>());
            constraintMap.put(constraintNode.getConstraint(), constraintNode);

            // for convenience
            constraintToPatternLookupMap.put(constraintNode.getConstraint(), constraintNode.getPattern());
            patternToRuleLookupMap.put(constraintNode.getPattern(), constraintNode.getRule());
        } else if (node instanceof ActionNode) {
            ActionNode actionNode = (ActionNode) node;
            Map<ConsequenceAction, ActionNode> actionMap = actionLookupNodeMap.computeIfAbsent(actionNode.getRule(), k -> new HashMap<>());
            actionMap.put(actionNode.getAction(), actionNode);

            // for convenience
            actionToRuleLookupMap.put(actionNode.getAction(), actionNode.getRule());
        } else {
            throw new RuntimeException("Unknown Node : " + node.getClass());
        }
    }

    public boolean isRegisteredClass(Class<?> clazz) {
        return propertyReactiveMap.containsKey(clazz) || classReativeMap.containsKey(clazz) || insertReactiveMap.containsKey(clazz);
    }

    public void addPropertyReactiveRule(Class<?> clazz, String property, Rule rule) {
        propertyReactiveMap.computeIfAbsent(clazz, k -> new HashMap<>()).computeIfAbsent(property, k -> new HashSet<>()).add(rule);
    }

    public void addPropertyReactiveConstraints(Class<?> clazz, String property, Pattern pattern) {
        Set<Constraint> constraints = pattern.getConstraints().stream()
                                             .filter(constraint -> {
                                                 // TODO: More precise analysis (e.g. PropertyTest.testPropertyInFunction): Need more test cases. Constraint needs to know its react prop?
                                                 return constraint.getProperty() != null && constraint.getProperty().equals(property);
                                             }) 
                                             .collect(Collectors.toSet());
        propertyReactiveConstraintMap.computeIfAbsent(clazz, k -> new HashMap<>()).computeIfAbsent(property, k -> new HashSet<>()).addAll(constraints);
    }

    public void addClassReactiveRule(Class<?> clazz, Rule rule) {
        classReativeMap.computeIfAbsent(clazz, k -> new HashSet<>()).add(rule);
    }

    public void addClassReactiveConstraints(Class<?> clazz, List<Constraint> constraints) {
        classReativeConstraintMap.computeIfAbsent(clazz, k -> new HashSet<>()).addAll(constraints);
    }

    public void addInsertReactiveRule(Class<?> clazz, Rule rule) {
        insertReactiveMap.computeIfAbsent(clazz, k -> new HashSet<>()).add(rule);
    }

    public void addInsertReactiveConstraint(Class<?> clazz, Constraint emptyConstraint) {
        // only one emptyConstraint
        insertReactiveConstraintMap.computeIfAbsent(clazz, k -> new HashSet<>()).add(emptyConstraint);
    }

    public Set<Rule> getRulesReactiveTo(Class<?> clazz) {
        Set<Rule> rules = new HashSet<>();
        rules.addAll(propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
        rules.addAll(classReativeMap.getOrDefault(clazz, Collections.emptySet()));
        rules.addAll(insertReactiveMap.getOrDefault(clazz, Collections.emptySet()));
        return rules;
    }

    public Set<Constraint> getConstraintsReactiveTo(Class<?> clazz) {
        Set<Constraint> constraints = new HashSet<>();
        constraints.addAll(propertyReactiveConstraintMap.getOrDefault(clazz, Collections.emptyMap()).values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
        constraints.addAll(classReativeConstraintMap.getOrDefault(clazz, Collections.emptySet()));
        constraints.addAll(insertReactiveConstraintMap.getOrDefault(clazz, Collections.emptySet()));
        return constraints;
    }

    public Set<Rule> getRulesReactiveTo(Class<?> clazz, String property) {
        Set<Rule> rules = new HashSet<>();
        rules.addAll(propertyReactiveMap.getOrDefault(clazz, Collections.emptyMap()).getOrDefault(property, Collections.emptySet()));
        rules.addAll(classReativeMap.getOrDefault(clazz, Collections.emptySet()));
        return rules;
    }

    public Set<Constraint> getConstraintsReactiveTo(Class<?> clazz, String property) {
        Set<Constraint> constraints = new HashSet<>();
        constraints.addAll(propertyReactiveConstraintMap.getOrDefault(clazz, Collections.emptyMap()).getOrDefault(property, Collections.emptySet()));
        constraints.addAll(classReativeConstraintMap.getOrDefault(clazz, Collections.emptySet()));
        return constraints;
    }

    public Node lookup(Constraint constraint) {
        Pattern pattern = constraintToPatternLookupMap.get(constraint);
        Rule rule = patternToRuleLookupMap.get(pattern);
        return lookup(rule, pattern, constraint);
    }

    public Node lookup(Rule rule, Pattern pattern, Constraint constraint) {
        if (!constraintLookupNodeMap.containsKey(rule)) {
            throw new RuntimeException(rule.getName() + " is not found in constraintLookupNodeMap");
        }
        Map<Pattern, Map<Constraint, ConstraintNode>> patternMap = constraintLookupNodeMap.get(rule);
        if (!patternMap.containsKey(pattern)) {
            throw new RuntimeException(rule.getName() + " : " + pattern + " is not found in constraintLookupNodeMap");
        }
        Map<Constraint, ConstraintNode> constraintMap = patternMap.get(pattern);
        if (!constraintMap.containsKey(constraint)) {
            throw new RuntimeException(rule.getName() + " : " + pattern + " : " + constraint + " is not found in constraintLookupNodeMap");
        }
        return constraintMap.get(constraint);
    }

    public Node lookup(ConsequenceAction action) {
        Rule rule = actionToRuleLookupMap.get(action);
        return lookup(rule, action);
    }

    public Node lookup(Rule rule, ConsequenceAction action) {
        if (!actionLookupNodeMap.containsKey(rule)) {
            throw new RuntimeException(rule.getName() + " is not found in actionLookupNodeMap");
        }
        Map<ConsequenceAction, ActionNode> actionMap = actionLookupNodeMap.get(rule);
        if (!actionMap.containsKey(action)) {
            throw new RuntimeException(rule.getName() + " : " + action + " is not found in actionLookupNodeMap");
        }
        return actionMap.get(action);
    }

    public Constraint getEmptyConstraintForPattern(Rule rule, Pattern pattern) {
        Map<Pattern, Map<Constraint, ConstraintNode>> patternMap = constraintLookupNodeMap.get(rule);
        Map<Constraint, ConstraintNode> constraintNodeMap = patternMap.get(pattern);
        if (constraintNodeMap.size() != 1) {
            throw new RuntimeException("constraintNodeMap size should be 1 : constraintNodeMap = " + constraintNodeMap);
        }
        ConstraintNode node = constraintNodeMap.values().iterator().next();
        return node.getConstraint();
    }

}
