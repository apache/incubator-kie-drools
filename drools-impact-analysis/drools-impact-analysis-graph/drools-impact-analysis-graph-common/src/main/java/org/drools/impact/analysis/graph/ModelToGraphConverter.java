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
import java.util.List;
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
                graphAnalysis.addNode(new Node(rule));

                LeftHandSide lhs = rule.getLhs();
                List<Pattern> patterns = lhs.getPatterns();
                for (Pattern pattern : patterns) {
                    Class<?> patternClass = pattern.getPatternClass();
                    Collection<String> reactOnFields = pattern.getReactOnFields();
                    if (pattern.isClassReactive()) {
                        // Pattern which cannot analyze reactivity (e.g. Person(blackBoxMethod())) so reacts to all properties
                        graphAnalysis.addClassReactiveRule(patternClass, rule, pattern.isPositive());
                    } else if (reactOnFields.size() == 0) {
                        // Pattern without constraint (e.g. Person()) so doesn't react to properties (only react to  insert/delete)
                        graphAnalysis.addInsertReactiveRule(patternClass, rule, pattern.isPositive());
                    } else {
                        for (String field : reactOnFields) {
                            graphAnalysis.addPropertyReactiveRule(patternClass, field, rule, pattern.isPositive());
                        }
                    }
                }
            }
        }
        return graphAnalysis;
    }

    private void parseGraphAnalysis(AnalysisModel model, GraphAnalysis graphAnalysis) {
        for (Package pkg : model.getPackages()) {
            String pkgName = pkg.getName();
            List<Rule> rules = pkg.getRules();
            for (Rule rule : rules) {
                String ruleName = rule.getName();
                RightHandSide rhs = rule.getRhs();
                List<ConsequenceAction> actions = rhs.getActions();
                for (ConsequenceAction action : actions) {
                    switch (action.getType()) {
                        case INSERT:
                            processInsert(graphAnalysis, pkgName, ruleName, action);
                            break;
                        case DELETE:
                            processDelete(graphAnalysis, pkgName, ruleName, action);
                            break;
                        case MODIFY:
                            processModify(graphAnalysis, pkgName, ruleName, (ModifyAction) action);
                            break;
                    }
                }
            }
        }
    }

    private void processInsert(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        Class<?> insertedClass = action.getActionClass();
        // all rules which react to the fact
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));
        for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(insertedClass)) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
            Node.linkNodes(source, target, reactedRule.getReactivityType());
        }
    }

    private void processDelete(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        Class<?> deletedClass = action.getActionClass();
        // all rules which react to the fact
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));
        for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(deletedClass)) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
            Node.linkNodes(source, target, reactedRule.getReactivityType().negate());
        }
    }

    private void processModify(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ModifyAction action) {
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));

        Class<?> modifiedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass(modifiedClass)) {
            // Not likely happen but not invalid
            logger.warn("Not found " + modifiedClass + " in reactiveMap");
            return;
        }
        List<ModifiedProperty> modifiedProperties = action.getModifiedProperties();
        for (ModifiedProperty modifiedProperty : modifiedProperties) {
            String property = modifiedProperty.getProperty();
            for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(modifiedClass, property)) {
                List<Constraint> constraints = reactedRule.getRule().getLhs().getPatterns().stream()
                                                          .filter(pattern -> pattern.getPatternClass() == modifiedClass)
                                                          .flatMap(pattern -> pattern.getConstraints().stream())
                                                          .filter(constraint -> constraint.getProperty() != null && constraint.getProperty().equals(property))
                                                          .collect(Collectors.toList());
                ReactivityType combinedLinkType = ReactivityType.UNKNOWN;
                if (constraints.size() == 0) {
                    // This rule is reactive to the property but cannot find its constraint (e.g. [age > $a] non-literal constraint). It means UNKNOWN impact
                    combinedLinkType = ReactivityType.UNKNOWN;
                } else {
                    // If constraints contain at least one POSITIVE, we consider it's POSITIVE.
                    for (Constraint constraint : constraints) {
                        ReactivityType linkType = linkType(constraint, modifiedProperty);
                        if (linkType == ReactivityType.POSITIVE) {
                            combinedLinkType = ReactivityType.POSITIVE;
                            break;
                        } else if (linkType == ReactivityType.NEGATIVE) {
                            combinedLinkType = ReactivityType.NEGATIVE; // TODO: consider whether NEGATIVE or UNKNOWN should be stronger (meaningful for users)
                        } else {
                            combinedLinkType = linkType; // UNKNOWN
                        }
                    }
                }
                if (reactedRule.getReactivityType() == ReactivityType.NEGATIVE) {
                    combinedLinkType = combinedLinkType.negate();
                }
                Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
                Node.linkNodes(source, target, combinedLinkType);
            }
        }
    }

    private ReactivityType linkType(Constraint constraint, ModifiedProperty modifiedProperty) {
        Object value = constraint.getValue();
        Object modifiedValue = modifiedProperty.getValue();

        if (modifiedValue == null || value == null) {
            return ReactivityType.UNKNOWN;
        }

        if (value instanceof Number && modifiedValue instanceof Number) {
            value = ((Number) value).doubleValue();
            modifiedValue = ((Number) modifiedValue).doubleValue();
        }

        switch (constraint.getType()) {
            case EQUAL:
                if (modifiedValue.equals(value)) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case NOT_EQUAL:
                if (!modifiedValue.equals(value)) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case GREATER_THAN:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) > 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case GREATER_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) >= 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case LESS_THAN:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) < 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case LESS_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo((Comparable) value) <= 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case RANGE:
                // TODO:
                break;
            case UNKNOWN:
                break;
        }
        return ReactivityType.UNKNOWN;
    }

    private static String fqdn(String pckageName, String ruleName) {
        return pckageName + "." + ruleName;
    }
}
