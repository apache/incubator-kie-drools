/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.tree.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.True;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledAST;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledType;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.commons.utils.DrooledModelUtils.getSanitizedClassName;

/**
 * Class used to generate a <code>KiePMMLDrooledAST</code> out of a<b>TreeModel</b>
 */
public class KiePMMLTreeModelASTFactory {

    public static final String SURROGATE_PATTERN = "%s_surrogate_%s";
    static final String STATUS_NULL = "status == null";
    static final String STATUS_PATTERN = "status == \"%s\"";
    static final String PATH_PATTERN = "%s_%s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelASTFactory.class.getName());

    private KiePMMLTreeModelASTFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>KiePMMLDrooledAST</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @return
     */
    public static KiePMMLDrooledAST getKiePMMLDrooledAST(DataDictionary dataDictionary, TreeModel model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.info("getKiePMMLDrooledAST {} {}", dataDictionary, model);
        Queue<KiePMMLDrooledType> types = declareTypes(dataDictionary, fieldTypeMap);
        Queue<KiePMMLDrooledRule> rules = declareRulesFromRootNode(model.getNode(), "", fieldTypeMap);
        return new KiePMMLDrooledAST(types, rules);
    }

    static Queue<KiePMMLDrooledRule> declareRulesFromRootNode(final Node node,
                                                              final String parentPath,
                                                              final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.info("declareRulesFromRootNode {} {}", node, parentPath);
        Queue<KiePMMLDrooledRule> toReturn = new LinkedList<>();
        declareRuleFromNode(node, parentPath, fieldTypeMap, toReturn);
        return toReturn;
    }

    static void declareRuleFromNode(final Node node,
                                    final String parentPath,
                                    final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                    final Queue<KiePMMLDrooledRule> rules) {
        logger.info("declareRuleFromNode {} {}", node, parentPath);

        if (isFinalLeaf(node)) {
            declareFinalRuleFromNode(node, parentPath, fieldTypeMap, rules);
        } else {
            declareIntermediateRuleFromNode(node, parentPath, fieldTypeMap, rules);
        }
    }

    static void declareFinalRuleFromNode(final Node node,
                                         final String parentPath,
                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                         final Queue<KiePMMLDrooledRule> rules) {
        logger.info("declareFinalRuleFromNode {} {}", node, parentPath);
        final Predicate predicate = node.getPredicate();
        // This means the rule should not be created at all.
        // Different semantics has to be implemented if the "False"/"True" predicates are declared inside
        // an XOR compound predicate
        if (predicate instanceof False) {
            return;
        }
        String currentRule = String.format(PATH_PATTERN, parentPath, node.getScore().toString());
        if (!(predicate instanceof True)) {
            declareRuleFromPredicate(predicate, parentPath, currentRule, node.getScore(), fieldTypeMap, rules, true);
        }
    }

    static void declareIntermediateRuleFromNode(final Node node,
                                                final String parentPath,
                                                final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                final Queue<KiePMMLDrooledRule> rules) {
        logger.info("declareIntermediateRuleFromNode {} {}", node, parentPath);
        final Predicate predicate = node.getPredicate();
        // This means the rule should not be created at all.
        // Different semantics has to be implemented if the "False"/"True" predicates are declared inside
        // an XOR compound predicate
        if (predicate instanceof False) {
            return;
        }
        String currentRule = String.format(PATH_PATTERN, parentPath, node.getScore().toString());
        if (!(predicate instanceof True)) {
            declareRuleFromPredicate(predicate, parentPath, currentRule, node.getScore(), fieldTypeMap, rules, false);
        }
        node.getNodes().forEach(child -> declareRuleFromNode(child, currentRule, fieldTypeMap, rules));
    }

    /**
     * Manage the given <code>Predicate</code>. At this point of the execution, <b>predicate</b> could be:
     * <p>1) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate">SimplePredicate</a><p>
     * <p>2) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate">CompoundPredicate</a><p>
     * <p>3) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate">SimpleSetPredicate</a><p>
     * @param predicate
     * @param parentPath
     * @param fieldTypeMap
     * @param rules
     */
    static void declareRuleFromPredicate(final Predicate predicate,
                                         final String parentPath,
                                         final String currentRule,
                                         final Object result,
                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                         final Queue<KiePMMLDrooledRule> rules,
                                         final boolean isFinalLeaf) {
        logger.info("declareRuleFromPredicate {} {} {} {}", predicate, parentPath, currentRule, result);
        if (predicate instanceof SimplePredicate) {
            declareRuleFromSimplePredicate((SimplePredicate) predicate, parentPath, currentRule, result, fieldTypeMap, rules, isFinalLeaf);
        } else if (predicate instanceof CompoundPredicate) {
            declareRuleFromCompoundPredicate((CompoundPredicate) predicate, parentPath, currentRule, result, fieldTypeMap, rules, isFinalLeaf);
        }
    }

    static void declareRuleFromCompoundPredicate(final CompoundPredicate compoundPredicate,
                                                 final String parentPath,
                                                 final String currentRule,
                                                 final Object result,
                                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                 final Queue<KiePMMLDrooledRule> rules, boolean isFinalLeaf) {
        logger.info("declareIntermediateRuleFromCompoundPredicate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);
        switch (compoundPredicate.getBooleanOperator()) {
            case SURROGATE:
                declareRuleFromCompoundPredicateSurrogate(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, isFinalLeaf);
                break;
            case AND:
            case OR:
            case XOR:
                declareRuleFromCompoundPredicateAndOrXor(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, isFinalLeaf);
        }
    }

    static void declareRuleFromCompoundPredicateAndOrXor(final CompoundPredicate compoundPredicate,
                                                         final String parentPath,
                                                         final String currentRule,
                                                         final Object result,
                                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                         final Queue<KiePMMLDrooledRule> rules, boolean isFinalLeaf) {
        logger.info("declareIntermediateRuleFromCompoundPredicateAndOrXor {} {} {}", compoundPredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        // Managing only SimplePredicates for the moment being
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        if (CompoundPredicate.BooleanOperator.XOR.equals((compoundPredicate.getBooleanOperator()))) {
            if (simplePredicates.size() < 2) {
                throw new KiePMMLException("At least two elements expected for XOR operations");
            }
            if (simplePredicates.size() > 2) {
                // Not managed yet
                throw new KiePMMLException("More then two elements not managed, yet, for XOR operations");
            }
        }
        final Map<String, List<SimplePredicate>> predicatesByField = simplePredicates.stream()
                .map(child -> (SimplePredicate) child)
                .collect(groupingBy(child -> fieldTypeMap.get(child.getField().getValue()).getGeneratedType()));
        final Map<String, List<KiePMMLOperatorValue>> constraints = new HashMap<>();
        predicatesByField.forEach((fieldName, predicates) -> constraints.putAll(getConstraintEntryFromSimplePredicates(fieldName, predicates, fieldTypeMap)));
        String statusToSet = isFinalLeaf ? StatusCode.DONE.getName() : currentRule;
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, statusToSet)
                .withStatusConstraint(statusConstraint);
        switch (compoundPredicate.getBooleanOperator()) {
            case AND:
                builder = builder.withAndConstraints(constraints);
                break;
            case OR:
                builder = builder.withOrConstraints(constraints);
                break;
            case XOR:
                builder = builder.withXorConstraints(constraints);
                break;
            default:
                break;
        }
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(StatusCode.OK);
        }
        rules.add(builder.build());
    }

    static void declareRuleFromCompoundPredicateSurrogate(final CompoundPredicate compoundPredicate,
                                                          final String parentPath,
                                                          final String currentRule,
                                                          final Object result,
                                                          final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                          final Queue<KiePMMLDrooledRule> rules, boolean isFinalLeaf) {
        logger.info("declareRuleFromCompoundPredicateSurrogate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);
        // Managing only SimplePredicates for the moment being
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        IntStream.range(0, simplePredicates.size()).forEach(i -> {
            SimplePredicate simplePredicate = (SimplePredicate) simplePredicates.get(i);
            String statusToSet;
            if (i < simplePredicates.size() - 1) {
                statusToSet = currentRule;
            } else if (isFinalLeaf) {
                statusToSet = StatusCode.DONE.getName();
            } else {
                statusToSet = parentPath;
            }
            declareRuleFromSimplePredicateSurrogate(simplePredicate, parentPath, currentRule, statusToSet, result, fieldTypeMap, rules);
        });
    }

    static void declareRuleFromSimplePredicateSurrogate(final SimplePredicate simplePredicate,
                                                        final String parentPath,
                                                        final String currentRule,
                                                        final String statusToSet,
                                                        final Object result,
                                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                        final Queue<KiePMMLDrooledRule> rules) {
        logger.info("declareRuleFromSimplePredicateSurrogate {} {} {} {} {}", simplePredicate, parentPath, currentRule, statusToSet, result);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String ifBreakField = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String ifBreakOperator = simplePredicate.getOperator().value();
        Object ifBreakValue = simplePredicate.getValue();
        String surrogateCurrentRule = String.format(SURROGATE_PATTERN, currentRule, ifBreakField);
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(surrogateCurrentRule, statusToSet)
                .withStatusConstraint(statusConstraint)
                .withIfBreak(ifBreakField, ifBreakOperator, ifBreakValue)
                .withResult(result)
                .withResultCode(StatusCode.OK);
        rules.add(builder.build());
    }

    static void declareRuleFromSimplePredicate(final SimplePredicate simplePredicate,
                                               final String parentPath,
                                               final String currentRule,
                                               final Object result,
                                               final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                               final Queue<KiePMMLDrooledRule> rules,
                                               boolean isFinalLeaf) {
        logger.info("declareRuleFromSimplePredicate {} {} {}", simplePredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String key = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String operator = simplePredicate.getOperator().value();
        Object value = simplePredicate.getValue();
        String statusToSet = isFinalLeaf ? StatusCode.DONE.getName() : currentRule;
        Map<String, List<KiePMMLOperatorValue>> andConstraints = Collections.singletonMap(key, Collections.singletonList(new KiePMMLOperatorValue(operator, value)));
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, statusToSet)
                .withStatusConstraint(statusConstraint)
                .withAndConstraints(andConstraints);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(StatusCode.OK);
        }
        rules.add(builder.build());
    }

    /**
     * Create a <code>List&lt;KiePMMLDrooledType&gt;</code> out of original <code>DataField</code>s,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param dataDictionary
     * @param fieldTypeMap
     */
    static Queue<KiePMMLDrooledType> declareTypes(final DataDictionary dataDictionary, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return dataDictionary.getDataFields().stream().map(dataField -> declareType(dataField, fieldTypeMap)).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Create a <code>KiePMMLDrooledType</code> out of original <code>DataField</code>,
     * and <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param dataField
     * @param fieldTypeMap
     */
    static KiePMMLDrooledType declareType(DataField dataField, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        String generatedType = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
        String fieldName = dataField.getName().getValue();
        String fieldType = dataField.getDataType().value();
        fieldTypeMap.put(fieldName, new KiePMMLOriginalTypeGeneratedType(fieldType, generatedType));
        return new KiePMMLDrooledType(generatedType, DATA_TYPE.byName(fieldType).getMappedClass().getSimpleName());
    }

    static Map<String, List<KiePMMLOperatorValue>> getConstraintEntryFromSimplePredicates(final String fieldName, final List<SimplePredicate> simplePredicates, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        List<KiePMMLOperatorValue> operatorValues = simplePredicates.stream().map(simplePredicate -> {
            String operator = simplePredicate.getOperator().value();
            Object value = simplePredicate.getValue();
            return new KiePMMLOperatorValue(operator, value);
        }).collect(Collectors.toList());
        return Collections.singletonMap(fieldName, operatorValues);
    }

    static boolean isFinalLeaf(Node node) {
        return node instanceof LeafNode || node.getNodes() == null || node.getNodes().isEmpty();
    }
}
