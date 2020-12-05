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

    private static final String NO_REACT = "this";

    public Graph toGraph(AnalysisModel model) {
        GraphAnalysis graphAnalysis = generateGraphAnalysis( model );
        parseGraphAnalysis( model, graphAnalysis );
        return new Graph(graphAnalysis.getNodeMap());
    }

    private GraphAnalysis generateGraphAnalysis( AnalysisModel model ) {
        GraphAnalysis graphAnalysis = new GraphAnalysis();
        for (Package pkg : model.getPackages()) {
            List<Rule> rules = pkg.getRules();
            for (Rule rule : rules) {
                graphAnalysis.addNode( new Node(rule) );

                LeftHandSide lhs = rule.getLhs();
                List<Pattern> patterns = lhs.getPatterns();
                for (Pattern pattern : patterns) {
                    Class<?> patternClass = pattern.getPatternClass();
                    Collection<String> reactOnFields = pattern.getReactOnFields();
                    if (pattern.isClassReactive()) {
                        graphAnalysis.addClassReactiveRule( patternClass, rule );
                    } else if (reactOnFields.size() == 0) {
                        // TODO: Currently reactOnFields.size() == 0 assumes that the pattern doesn't have a constraint (e.g. Person()) so no react
                        graphAnalysis.addPropertyReactiveRule(patternClass, NO_REACT, rule);
                    } else {
                        for (String field : reactOnFields) {
                            graphAnalysis.addPropertyReactiveRule(patternClass, field, rule);
                        }
                    }
                }
            }
        }
        return graphAnalysis;
    }

    private void parseGraphAnalysis( AnalysisModel model, GraphAnalysis graphAnalysis ) {
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
                            processInsert( graphAnalysis, pkgName, ruleName, action);
                            break;
                        case DELETE:
                            processDelete( graphAnalysis, pkgName, ruleName, action);
                            break;
                        case MODIFY:
                            processModify( graphAnalysis, pkgName, ruleName, (ModifyAction) action);
                            break;
                    }
                }
            }
        }
    }

    private void processInsert(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        // TODO: consider not()
        Class<?> insertedClass = action.getActionClass();
        // all rules which react to the fact
        Set<Rule> reactedRules = graphAnalysis.getRulesReactiveTo(insertedClass);
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));
        for (Rule reactedRule : reactedRules) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getName()));
            Node.linkNodes(source, target, Link.Type.POSITIVE);
        }
    }

    private void processDelete(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ConsequenceAction action) {
        // TODO: consider exists()
        Class<?> deletedClass = action.getActionClass();
        // all rules which react to the fact
        Set<Rule> reactedRules = graphAnalysis.getRulesReactiveTo(deletedClass);
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));
        for (Rule reactedRule : reactedRules) {
            Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getName()));
            Node.linkNodes(source, target, Link.Type.NEGATIVE);
        }
    }

    private void processModify(GraphAnalysis graphAnalysis, String pkgName, String ruleName, ModifyAction action) {
        // TODO: consider exists()/not()
        Node source = graphAnalysis.getNode(fqdn(pkgName, ruleName));

        Class<?> modifiedClass = action.getActionClass();
        if (!graphAnalysis.isRegisteredClass( modifiedClass )) {
            // Not likely happen but not invalid
            logger.warn("Not found " + modifiedClass + " in reactFactMap");
            return;
        }
        List<ModifiedProperty> modifiedProperties = action.getModifiedProperties();
        for (ModifiedProperty modifiedProperty : modifiedProperties) {
            String property = modifiedProperty.getProperty();
            Set<Rule> rules = graphAnalysis.getRulesReactiveTo( modifiedClass, property );
            if (rules == null) {
                // Not likely happen but not invalid
                logger.warn("Not found " + property + " in reactFieldMap");
                continue;
            }
            for (Rule reactedRule : rules) {
                List<Constraint> constraints = reactedRule.getLhs().getPatterns().stream()
                                                          .filter(pattern -> pattern.getPatternClass() == modifiedClass)
                                                          .flatMap(pattern -> pattern.getConstraints().stream())
                                                          .filter(constraint -> constraint.getProperty().equals(property))
                                                          .collect(Collectors.toList());
                Link.Type combinedLinkType = Link.Type.UNKNOWN;
                if (constraints.size() == 0) {
                    // This rule is reactive to the property but cannot find its constraint (e.g. [age > $a] non-literal constraint) It means UNKNOWN impact
                    combinedLinkType = Link.Type.UNKNOWN;
                } else {
                    // If constraints contain at least one POSITIVE, we consider it's POSITIVE.
                    for (Constraint constraint : constraints) {
                        Link.Type linkType = linkType(constraint, modifiedProperty);
                        if (linkType == Link.Type.POSITIVE) {
                            combinedLinkType = Link.Type.POSITIVE;
                            break;
                        } else if (linkType == Link.Type.NEGATIVE) {
                            combinedLinkType = Link.Type.NEGATIVE; // TODO: consider whether NEGATIVE or UNKNOWN should be stronger (meaningful for users)
                        } else {
                            combinedLinkType = linkType; // UNKNOWN
                        }
                    }
                }
                Node target = graphAnalysis.getNode(fqdn(pkgName, reactedRule.getName()));
                Node.linkNodes(source, target, combinedLinkType);
            }
        }
    }

    private Link.Type linkType(Constraint constraint, ModifiedProperty modifiedProperty) {
        Object value = constraint.getValue();
        Object modifiedValue = modifiedProperty.getValue();

        // If value and/or modifiedValue are not literal (e.g. age > $a), return Link.Type.UNKNOWN
        // Check with Mario if we can distinguish literal or not on the model side

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
