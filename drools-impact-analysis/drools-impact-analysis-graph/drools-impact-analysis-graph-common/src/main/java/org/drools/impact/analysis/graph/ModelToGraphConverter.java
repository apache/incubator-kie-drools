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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.model.Package;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.left.Constraint;
import org.drools.impact.analysis.model.left.LeftHandSide;
import org.drools.impact.analysis.model.left.Pattern;
import org.drools.impact.analysis.model.right.ConsequenceAction;
import org.drools.impact.analysis.model.right.ModifiedProperty;
import org.drools.impact.analysis.model.right.ModifyAction;
import org.drools.impact.analysis.model.right.RightHandSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelToGraphConverter {

    private static Logger logger = LoggerFactory.getLogger(ModelToGraphConverter.class);

    public Graph toGraph(AnalysisModel model) {
        GraphAnalysis graphAnalysis = generateGraphAnalysis(model);
        parseGraphAnalysis(model, graphAnalysis);
        return new Graph(graphAnalysis.getNodeMap());
    }

    private GraphAnalysis generateGraphAnalysis(AnalysisModel model) {
        GraphAnalysis graphAnalysis = new GraphAnalysis();
        for (Package pkg : model.getPackages()) {
            List<Rule> rules = pkg.getRules();
            for (Rule rule : rules) {
                LeftHandSide lhs = rule.getLhs();
                List<Pattern> patterns = lhs.getPatterns();
                for (int patternIndex = 0; patternIndex < patterns.size(); patternIndex++) {
                    Pattern pattern = patterns.get(patternIndex);
                    List<Constraint> constraints = pattern.getConstraints();
                    for (int constraintIndex = 0; constraintIndex < constraints.size(); constraintIndex++) {
                        Constraint constraint = constraints.get(constraintIndex);
                        graphAnalysis.addNode(new ConstraintNode(rule, pattern, patternIndex, constraint, constraintIndex));
                    }

                    Class<?> patternClass = pattern.getPatternClass();
                    Collection<String> reactOnFields = pattern.getReactOnFields();
                    if (pattern.isClassReactive()) {
                        // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
                        graphAnalysis.addClassReactiveRule(patternClass, rule);
                        if (constraints.isEmpty()) {
                            // Supply a dummy constraint for the empty case. TODO: model can provide such un-analyzed constraints
                            Constraint emptyConstraint = new Constraint();
                            emptyConstraint.setProperty(null);
                            emptyConstraint.setType(Constraint.Type.UNKNOWN);
                            emptyConstraint.setValue(null);
                            graphAnalysis.addClassReactiveConstraints(patternClass, Collections.singletonList(emptyConstraint));
                            graphAnalysis.addNode(new ConstraintNode(rule, pattern, patternIndex, emptyConstraint, 0));
                        } else {
                            graphAnalysis.addClassReactiveConstraints(patternClass, constraints);
                        }
                    } else if (reactOnFields.size() == 0) {
                        // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)
                        graphAnalysis.addInsertReactiveRule(patternClass, rule);
                        if (constraints.isEmpty()) {
                            // Supply a dummy constraint for the empty case.
                            Constraint emptyConstraint = new Constraint();
                            emptyConstraint.setProperty(null);
                            emptyConstraint.setType(Constraint.Type.UNKNOWN);
                            emptyConstraint.setValue(null);
                            graphAnalysis.addInsertReactiveConstraint(patternClass, emptyConstraint);
                            graphAnalysis.addNode(new ConstraintNode(rule, pattern, patternIndex, emptyConstraint, 0));
                        } else {
                            throw new RuntimeException("constraints should be empty : constraints = " + constraints);
                        }
                    } else {
                        for (String field : reactOnFields) {
                            graphAnalysis.addPropertyReactiveRule(patternClass, field, rule);
                            graphAnalysis.addPropertyReactiveConstraints(patternClass, field, pattern);
                        }
                    }
                }

                RightHandSide rhs = rule.getRhs();
                List<ConsequenceAction> actions = rhs.getActions();
                for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {
                    ConsequenceAction action = actions.get(actionIndex);
                    graphAnalysis.addNode(new ActionNode(rule, action, actionIndex));
                }
            }
        }
        return graphAnalysis;
    }

    private void parseGraphAnalysis(AnalysisModel model, GraphAnalysis graphAnalysis) {
        for (Package pkg : model.getPackages()) {
            List<Rule> rules = pkg.getRules();
            for (Rule rule : rules) {

                LeftHandSide lhs = rule.getLhs();
                List<Pattern> patterns = lhs.getPatterns();
                RightHandSide rhs = rule.getRhs();
                List<ConsequenceAction> actions = rhs.getActions();

                // Constraint to Action relationships in the same rule
                for (Pattern pattern : patterns) {
                    List<Constraint> constraints = pattern.getConstraints();
                    if (constraints.isEmpty()) {
                        Constraint constraint = graphAnalysis.getEmptyConstraintForPattern(rule, pattern);
                        for (ConsequenceAction action : actions) {
                            linkIfdependent(graphAnalysis, pattern, constraint, action);
                        }
                    }
                    for (Constraint constraint : constraints) {
                        for (ConsequenceAction action : actions) {
                            linkIfdependent(graphAnalysis, pattern, constraint, action);
                        }
                    }
                }

                // Action to other rule's Constraint relationships
                for (ConsequenceAction action : actions) {
                    switch (action.getType()) {
                        case INSERT:
                            processInsert(graphAnalysis, rule, action);
                            break;
                        case DELETE:
                            processDelete(graphAnalysis, rule, action);
                            break;
                        case MODIFY:
                            processModify(graphAnalysis, rule, (ModifyAction) action);
                            break;
                    }
                }
            }
        }
    }

    private void linkIfdependent(GraphAnalysis graphAnalysis, Pattern pattern, Constraint constraint, ConsequenceAction action) {
        // TODO: Only check class at the moment
        // Possible approach for "if their associated literals share a common term": bind variable
        if (pattern.getPatternClass().equals(action.getActionClass())) {
            Node source = graphAnalysis.lookup(constraint);
            Node target = graphAnalysis.lookup(action);
            Node.linkNodes(source, target, Link.Type.POSITIVE);
        }
    }

    private void processInsert(GraphAnalysis graphAnalysis, Rule rule, ConsequenceAction action) {
        // TODO: consider not()
        Class<?> insertedClass = action.getActionClass();
        // all rules which react to the fact
        Set<Constraint> reactedConstraints = graphAnalysis.getConstraintsReactiveTo(insertedClass);
        Node source = graphAnalysis.lookup(action);
        for (Constraint reactedConstraint : reactedConstraints) {
            Node target = graphAnalysis.lookup(reactedConstraint);
            Node.linkNodes(source, target, Link.Type.POSITIVE);
        }
    }

    private void processDelete(GraphAnalysis graphAnalysis, Rule rule, ConsequenceAction action) {
        // TODO: consider exists()
        Class<?> deletedClass = action.getActionClass();
        // all rules which react to the fact
        Set<Constraint> reactedConstraints = graphAnalysis.getConstraintsReactiveTo(deletedClass);
        Node source = graphAnalysis.lookup(action);
        for (Constraint reactedConstraint : reactedConstraints) {
            Node target = graphAnalysis.lookup(reactedConstraint);
            Node.linkNodes(source, target, Link.Type.NEGATIVE);
        }
    }

    private void processModify(GraphAnalysis graphAnalysis, Rule rule, ModifyAction action) {
        // TODO: consider exists()/not()
        Node source = graphAnalysis.lookup(action);

        Class<?> modifiedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass(modifiedClass)) {
            // Not likely happen but not invalid
            logger.warn("Not found " + modifiedClass + " in reactiveMap");
            return;
        }
        List<ModifiedProperty> modifiedProperties = action.getModifiedProperties();
        for (ModifiedProperty modifiedProperty : modifiedProperties) {
            String property = modifiedProperty.getProperty();
            Set<Constraint> constraints = graphAnalysis.getConstraintsReactiveTo(modifiedClass, property);

            if (constraints.size() == 0) {
                // This rule is reactive to the property but cannot find its constraint (e.g. [age > $a] non-literal constraint). It means UNKNOWN impact
                System.out.println("************ constraints.size() == 0");
                //                combinedLinkType = Link.Type.UNKNOWN;
                continue;
            } else {
                // If constraints contain at least one POSITIVE, we consider it's POSITIVE.
                for (Constraint constraint : constraints) {
                    Link.Type linkType = linkType(constraint, modifiedProperty);
                    Node target = graphAnalysis.lookup(constraint);
                    Node.linkNodes(source, target, linkType);
                }
            }

        }
    }

    private Link.Type linkType(Constraint constraint, ModifiedProperty modifiedProperty) {
        Object value = constraint.getValue();
        Object modifiedValue = modifiedProperty.getValue();
        if (modifiedValue == null || value == null) {
            return Link.Type.UNKNOWN;
        }

        if (value instanceof Number && modifiedValue instanceof Number) {
            value = ((Number) value).doubleValue();
            modifiedValue = ((Number) modifiedValue).doubleValue();
        }

        switch (constraint.getType()) {
            case EQUAL:
                if (modifiedValue.equals(value)) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case NOT_EQUAL:
                if (!modifiedValue.equals(value)) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case GREATER_THAN:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) > 0) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case GREATER_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) >= 0) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case LESS_THAN:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) < 0) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case LESS_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) <= 0) {
                    return Link.Type.POSITIVE;
                } else {
                    return Link.Type.NEGATIVE;
                }
            case RANGE:
                // TODO:
                break;
            case UNKNOWN:
                break;
        }
        return Link.Type.UNKNOWN;
    }

    private static String fqdn(String pckageName, String ruleName) {
        return pckageName + "." + ruleName;
    }
}
