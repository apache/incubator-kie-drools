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
import java.util.List;
import java.util.stream.Collectors;

import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.model.Package;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.model.left.Constraint;
import org.drools.impact.analysis.model.left.LeftHandSide;
import org.drools.impact.analysis.model.left.MapConstraint;
import org.drools.impact.analysis.model.left.Pattern;
import org.drools.impact.analysis.model.right.ConsequenceAction;
import org.drools.impact.analysis.model.right.DeleteSpecificFactAction;
import org.drools.impact.analysis.model.right.InsertAction;
import org.drools.impact.analysis.model.right.InsertedProperty;
import org.drools.impact.analysis.model.right.ModifiedMapProperty;
import org.drools.impact.analysis.model.right.ModifiedProperty;
import org.drools.impact.analysis.model.right.ModifyAction;
import org.drools.impact.analysis.model.right.RightHandSide;
import org.drools.impact.analysis.model.right.SpecificProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelToGraphConverter {

    private static Logger logger = LoggerFactory.getLogger(ModelToGraphConverter.class);

    private LinkFilter linkFilter = LinkFilter.ALL;

    public ModelToGraphConverter() {}

    // will be deprecated
    public ModelToGraphConverter(boolean positiveOnly) {
        if (positiveOnly) {
            this.linkFilter = LinkFilter.POSITIVE;
        } else {
            this.linkFilter = LinkFilter.ALL;
        }
    }

    public ModelToGraphConverter(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

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
                    } else if (reactOnFields.isEmpty()) {
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
                            processInsert(graphAnalysis, pkgName, ruleName, (InsertAction) action);
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

    private void processInsert(GraphAnalysis graphAnalysis, String pkgName, String ruleName, InsertAction action) {

        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));

        Class<?> insertedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass(insertedClass)) {
            // Not likely happen but not invalid
            logger.warn("Not found {} in reactiveMap", insertedClass);
            return;
        }

        // property based link
        List<InsertedProperty> insertedProperties = action.getInsertedProperties();
        for (InsertedProperty insertedProperty : insertedProperties) {
            String property = insertedProperty.getProperty();
            for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(insertedClass, property)) {
                List<Pattern> patterns = reactedRule.getRule().getLhs().getPatterns().stream()
                                                    .filter(pattern -> pattern.getPatternClass() == insertedClass)
                                                    .collect(Collectors.toList());
                for (Pattern pattern : patterns) {
                    List<Constraint> constraints = pattern.getConstraints().stream()
                                                          .filter(constraint -> constraint.getProperty() != null && constraint.getProperty().equals(property))
                                                          .collect(Collectors.toList());
                    ReactivityType combinedLinkType = ReactivityType.UNKNOWN;
                    if (constraints.isEmpty()) {
                        // This rule is reactive to the property but cannot find its constraint (e.g. [age > $a] non-literal constraint). It means UNKNOWN impact
                        combinedLinkType = ReactivityType.UNKNOWN;
                    } else {
                        // If constraints contain at least one POSITIVE, we consider it's POSITIVE.
                        for (Constraint constraint : constraints) {
                            ReactivityType linkType = linkType(constraint, insertedProperty);
                            if (linkType == ReactivityType.POSITIVE) {
                                combinedLinkType = ReactivityType.POSITIVE;
                                break;
                            } else if (linkType == ReactivityType.NEGATIVE) {
                                combinedLinkType = ReactivityType.NEGATIVE; // NEGATIVE is stronger than UNKNOWN (but may be configurable)
                            } else if (combinedLinkType == ReactivityType.NEGATIVE && linkType == ReactivityType.UNKNOWN) {
                                // Don't overwrite with UNKNOWN
                            } else {
                                combinedLinkType = linkType; // UNKNOWN
                            }
                        }
                    }

                    if (combinedLinkType == ReactivityType.NEGATIVE) {
                        // NEGATIVE insert means nothing. Just no link.
                        // TODO: handle "exists" case
                        continue;
                    }

                    if (combinedLinkType == ReactivityType.POSITIVE && !pattern.isPositive()) {
                        // POSITIVE insert in not() means NEGATIVE
                        combinedLinkType = combinedLinkType.negate();
                    }

                    Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
                    linkNodesIfExpected(source, target, combinedLinkType);
                }
            }
        }

        // class based link
        for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveToWithoutProperty(insertedClass)) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
            linkNodesIfExpected(source, target, reactedRule.getReactivityType());
        }
    }

    private void processDelete(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        if (action instanceof DeleteSpecificFactAction) {
            processDeleteSpecificFact(graphAnalysis, pkgName, ruleName, (DeleteSpecificFactAction) action);
        } else {
            processDeleteAnyFact(graphAnalysis, pkgName, ruleName, action);
        }
    }

    private void processDeleteAnyFact(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        Class<?> deletedClass = action.getActionClass();
        // all rules which react to the fact
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));
        for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(deletedClass)) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
            linkNodesIfExpected(source, target, reactedRule.getReactivityType().negate());
        }
    }

    private void processDeleteSpecificFact(GraphAnalysis graphAnalysis, String pkgName, String ruleName, DeleteSpecificFactAction action) {
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));

        Class<?> deletedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass(deletedClass)) {
            // Not likely happen but not invalid
            logger.warn("Not found {} in reactiveMap", deletedClass);
            return;
        }
        List<SpecificProperty> specificProperties = action.getSpecificProperties();
        for (SpecificProperty specificProperty : specificProperties) {
            String property = specificProperty.getProperty();
            for (AnalyzedRule reactedRule : graphAnalysis.getRulesReactiveTo(deletedClass, property)) {
                // Reactive to this class+property. Deleting this fact means NEGATIVE impact.
                Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
                linkNodesIfExpected(source, target, ReactivityType.NEGATIVE);
            }
        }
    }

    private void processModify(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ModifyAction action) {
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));

        Class<?> modifiedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass(modifiedClass)) {
            // Not likely happen but not invalid
            logger.warn("Not found {} in reactiveMap", modifiedClass);
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
                                                          .filter(constraint -> {
                                                              if (constraint instanceof MapConstraint) {
                                                                  return doesAssertSameKey((MapConstraint) constraint, modifiedProperty);
                                                              } else {
                                                                  return true;
                                                              }
                                                          })
                                                          .collect(Collectors.toList());
                ReactivityType combinedLinkType = ReactivityType.UNKNOWN;
                if (constraints.isEmpty()) {
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
                            combinedLinkType = ReactivityType.NEGATIVE; // NEGATIVE is stronger than UNKNOWN (but may be configurable)
                        } else if (combinedLinkType == ReactivityType.NEGATIVE && linkType == ReactivityType.UNKNOWN) {
                            // Don't overwrite with UNKNOWN
                        } else {
                            combinedLinkType = linkType; // UNKNOWN
                        }
                    }
                }
                if (reactedRule.getReactivityType() == ReactivityType.NEGATIVE) {
                    combinedLinkType = combinedLinkType.negate();
                }
                Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getRule().getName()));
                linkNodesIfExpected(source, target, combinedLinkType);
            }
        }
    }

    private boolean doesAssertSameKey(MapConstraint constraint, ModifiedProperty modifiedProperty) {
        if (modifiedProperty instanceof ModifiedMapProperty) {
            String constraintMapKey = constraint.getKey();
            String modifiedMapKey = ((ModifiedMapProperty) modifiedProperty).getKey();
            return constraintMapKey != null && constraintMapKey.equals(modifiedMapKey);
        } else {
            return false;
        }
    }

    private void linkNodesIfExpected(Node source, Node target, ReactivityType type) {
        if (linkFilter.accept(type)) {
            Node.linkNodes(source, target, type);
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
                if (((Comparable) modifiedValue).compareTo(value) > 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case GREATER_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo(value) >= 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case LESS_THAN:
                if (((Comparable) modifiedValue).compareTo(value) < 0) {
                    return ReactivityType.POSITIVE;
                } else {
                    return ReactivityType.NEGATIVE;
                }
            case LESS_OR_EQUAL:
                if (((Comparable) modifiedValue).compareTo(value) <= 0) {
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

    private static String fqdn(String packageName, String ruleName) {
        return packageName + "." + ruleName;
    }
}
