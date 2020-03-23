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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.util.StringUtils;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.commons.utils.DrooledModelUtils.getSanitizedClassName;

/**
 * Class used to generate a <b>DROOLS</b> (descr) object out of a<b>TreeModel</b>
 */
public class KiePMMLDescrFactory {

    public static final String PMML4_RESULT = "PMML4Result";
    public static final String PMML4_RESULT_IDENTIFIER = "$pmml4Result";
    public static final String UPDATE_PMML4_RESULT = "\r\n" + PMML4_RESULT_IDENTIFIER + ".setResultCode(\"%s\");" +
            "\r\n" + PMML4_RESULT_IDENTIFIER + ".addResultVariable(" + PMML4_RESULT_IDENTIFIER + ".getResultObjectName()" + ", \"%s\");";/* +
            "\r\nupdate(" + PMML4_RESULT_IDENTIFIER + ");";*/
    static final String STATUS_HOLDER = "$statusHolder";
    public static final String MODIFY_STATUS_HOLDER = "\r\nmodify(" + STATUS_HOLDER + ") {\r\n\tsetStatus(\"%s\")\r\n}";
    static final String STATUS_NULL = "status == null";
    static final String STATUS_PATTERN = "status == \"%s\"";
    static final String PATH_PATTERN = "%s_%s";
    static final String STRING_VALUE_PATTERN = "\"%s\"";
    static final String VALUE_PATTERN = "value %s %s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrFactory.class.getName());

    private KiePMMLDescrFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>PackageDescr</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param dataDictionary
     * @param model
     * @param packageName
     * @param fieldTypeMap
     * @return
     */
    public static PackageDescr getBaseDescr(DataDictionary dataDictionary, TreeModel model, String packageName, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.info("getBaseDescr {} {}", dataDictionary, model);
        PackageDescrBuilder builder = DescrFactory.newPackage()
                .name(packageName);
        builder.newImport().target(KiePMMLStatusHolder.class.getName());
        builder.newImport().target(SimplePredicate.class.getName());
        builder.newImport().target(PMML4Result.class.getName());
        builder.newGlobal().identifier(PMML4_RESULT_IDENTIFIER).type(PMML4_RESULT);
        declareTypes(builder, dataDictionary, fieldTypeMap);
        declareRules(builder, model.getNode(), "", fieldTypeMap);
        return builder.getDescr();
    }

    static void declareRules(final PackageDescrBuilder builder, Node node, String parentPath,
                             final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.info("declareRules {} {}", node, parentPath);
        String currentRule = String.format(PATH_PATTERN, parentPath, node.getScore().toString());
        final Predicate predicate = node.getPredicate();
        if (predicate instanceof False) {
            return;
        }
        final RuleDescrBuilder ruleBuilder = builder.newRule().name(currentRule);
        final CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder = ruleBuilder.lhs();
        String constraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        lhsBuilder.pattern(KiePMMLStatusHolder.class.getSimpleName()).id(STATUS_HOLDER, false).constraint(constraint);
        declarePredicate(lhsBuilder, predicate, fieldTypeMap);
        if (node instanceof LeafNode || node.getNodes() == null || node.getNodes().isEmpty()) {
            declareFinalLeafWhen(ruleBuilder, lhsBuilder, node);
        } else {
            declareBranchWhen(builder, ruleBuilder, parentPath, node, fieldTypeMap);
        }
    }

    static void declareFinalLeafWhen(final RuleDescrBuilder ruleBuilder, final CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder, final Node node) {
        logger.info("declareFinalLeafWhen {} {} {}", ruleBuilder, lhsBuilder, node);
//        lhsBuilder.pattern(PMML4Result.class.getSimpleName()).id(PMML4_RESULT_IDENTIFIER, false);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(MODIFY_STATUS_HOLDER, StatusCode.DONE.name()));
        stringBuilder.append(String.format(UPDATE_PMML4_RESULT, StatusCode.OK.name(), node.getScore().toString()));
        String rhs = stringBuilder.toString();
        ruleBuilder.rhs(rhs);
    }

    static void declareBranchWhen(final PackageDescrBuilder builder,
                                  final RuleDescrBuilder ruleBuilder, String parentPath,
                                  final Node node,
                                  final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.info("declareBranchWhen {} {} {} {} {}", builder, ruleBuilder, parentPath, node, fieldTypeMap);
        String currentRule = String.format(PATH_PATTERN, parentPath, node.getScore().toString());
        String rhs = String.format(MODIFY_STATUS_HOLDER, currentRule);
        ruleBuilder.rhs(rhs);
        for (Node child : node.getNodes()) {
            declareRules(builder, child, currentRule, fieldTypeMap);
        }
    }

    static void declarePredicate(final CEDescrBuilder<?, ?> lhsBuilder,
                                 final Predicate predicate,
                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        /*if (predicate instanceof True) {
            // TODO {gcardosi} remove this eval, since it is redundant and inefficient
            lhsBuilder.eval().constraint("true");
        } else if (predicate instanceof False) {
            // TODO {gcardosi} in this situation, the semantic would lead to just skip rule creation
            lhsBuilder.eval().constraint("false");
        } else */
        if (predicate instanceof SimplePredicate) {
            declareSimplePredicate(lhsBuilder, (SimplePredicate) predicate, fieldTypeMap);
        } else if (predicate instanceof CompoundPredicate) {
            declareCompoundPredicate(lhsBuilder, (CompoundPredicate) predicate, fieldTypeMap);
        }
    }

    static void declareSimplePredicate(final CEDescrBuilder<?, ?> lhsBuilder,
                                       final SimplePredicate predicate,
                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
        String constraint = getConstraintPattern(predicate, operator, fieldTypeMap);
        lhsBuilder.pattern(predicate.getField().getValue().toUpperCase()).constraint(constraint).end();
    }

    static void declareCompoundPredicate(final CEDescrBuilder<?, ?> lhsBuilder,
                                         final CompoundPredicate predicate,
                                         final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ?> nestedBuilder;
        final CompoundPredicate.BooleanOperator booleanOperator = (predicate).getBooleanOperator();
        switch (booleanOperator) {
            case OR:
            case SURROGATE:
                nestedBuilder = lhsBuilder.or();
                break;
            case XOR:
            case AND:
            default:
                nestedBuilder = lhsBuilder.and();
        }
        // First I need to group predicates by type
        final Map<? extends Class<? extends Predicate>, List<Predicate>> predicatesByClass =
                (predicate).getPredicates().stream().collect(groupingBy(Predicate::getClass));
        for (Map.Entry<? extends Class<? extends Predicate>, List<Predicate>> entry : predicatesByClass.entrySet()) {
            declarePredicate(entry, nestedBuilder, booleanOperator, fieldTypeMap);
        }
        lhsBuilder.end();
    }

    static void declarePredicate(final Map.Entry<? extends Class<? extends Predicate>, List<Predicate>> entry,
                                 final CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ?> nestedBuilder,
                                 final CompoundPredicate.BooleanOperator booleanOperator,
                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        Class<?> aClass = entry.getKey();
        List<Predicate> predicates = entry.getValue();
        if (SimplePredicate.class.equals(aClass)) {
            // Here I need to group simplepredicates by field
            final Map<String, List<SimplePredicate>> predicatesByField = predicates.stream()
                    .map(child -> (SimplePredicate) child)
                    .collect(groupingBy(child -> fieldTypeMap.get(child.getField().getValue()).getGeneratedType()));
            // .. and add them as a whole
            if (CompoundPredicate.BooleanOperator.XOR.equals(booleanOperator)) {
                declareXORSimplePredicates(nestedBuilder, predicatesByField, fieldTypeMap);
            } else if (CompoundPredicate.BooleanOperator.SURROGATE.equals(booleanOperator)) {
                declareSurrogateSimplePredicates(nestedBuilder, predicatesByField, fieldTypeMap);
            } else {
                for (Map.Entry<String, List<SimplePredicate>> childEntry : predicatesByField.entrySet()) {
                    declareSimplePredicates(nestedBuilder, childEntry.getKey(), childEntry.getValue(), booleanOperator, fieldTypeMap);
                }
            }
        } else {
            for (Predicate childPredicate : predicates) {
                declarePredicate(nestedBuilder, childPredicate, fieldTypeMap);
            }
        }
    }

    static void declareXORSimplePredicates(final CEDescrBuilder<?, ?> lhsBuilder,
                                           final Map<String, List<SimplePredicate>> predicatesMap,
                                           final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        String leftHand = null;
        String leftPatternType = null;
        String rightHand;
        String rightPatternType;
        CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ExistsDescr> exists = null;
        CEDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ExistsDescr>, OrDescr> orExists;
        final CEDescrBuilder<? extends CEDescrBuilder<?, ?>, AndDescr> xorRoot = lhsBuilder.and();
        final CEDescrBuilder<? extends CEDescrBuilder<?, ?>, NotDescr> not = xorRoot.not();
        List<SimplePredicate> allPredicates = predicatesMap.entrySet()
                .stream()
                .flatMap((Function<Map.Entry<String, List<SimplePredicate>>, Stream<SimplePredicate>>) stringListEntry -> stringListEntry.getValue().stream())
                .collect(Collectors.toList());
        if (allPredicates.size() < 2) {
            throw new KiePMMLException("At least two elements expected for XOR operations");
        }
        if (allPredicates.size() > 2) {
            // Not managed yet
            throw new KiePMMLException("More then two elements not managed, yet, for XOR operations");
        }
        for (SimplePredicate predicate : allPredicates) {
            OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
            if (leftHand == null) { // First element
                leftHand = getConstraintPattern(predicate, operator, fieldTypeMap);
                leftPatternType = predicate.getField().getValue().toUpperCase();
                continue;
            }
            rightHand = getConstraintPattern(predicate, operator, fieldTypeMap);
            rightPatternType = predicate.getField().getValue().toUpperCase();
            if (exists == null) { // Second element
                exists = xorRoot.exists();
                orExists = exists.or();
                orExists.pattern(leftPatternType).constraint(leftHand);
                orExists.pattern(rightPatternType).constraint(rightHand);
                not.pattern(leftPatternType).constraint(leftHand);
                not.pattern(rightPatternType).constraint(rightHand);
            } else { // Following elements
                // Not managed yet
            }
        }
    }

//    static void declareXORSimplePredicate(final AtomicReference<String> leftHand,
//                                          final AtomicReference<String> leftPatternType,
//                                          final AtomicReference<String> rightHand,
//                                          final AtomicReference<String> rightPatternType,
//                                          CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ExistsDescr> exists,
//                                          CEDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ExistsDescr>, OrDescr> orExists,
//                                          final SimplePredicate predicate,
//                                          final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
//        OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
//        if (leftHand.get() == null) { // First element
//            leftHand.set(getConstraintPattern(predicate, operator, fieldTypeMap));
//            leftPatternType.set(predicate.getField().getValue().toUpperCase());
//            return;
//        }
//        rightHand.set(getConstraintPattern(predicate, operator, fieldTypeMap));
//        rightPatternType.set(predicate.getField().getValue().toUpperCase());
//        if (exists == null) { // Second element
//            exists = xorRoot.exists();
//            orExists = exists.or();
//            orExists.pattern(leftPatternType).constraint(leftHand);
//            orExists.pattern(rightPatternType).constraint(rightHand);
//            not.pattern(leftPatternType).constraint(leftHand);
//            not.pattern(rightPatternType).constraint(rightHand);
//        } else { // Following elements
//            // Not managed yet
//        }
//    }

    static void declareSurrogateSimplePredicates(final CEDescrBuilder<?, ?> lhsBuilder,
                                                 final Map<String, List<SimplePredicate>> predicatesMap,
                                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        /*
        https://docs.jboss.org/drools/release/7.33.0.Final/drools-docs/html_single/#_activation_groups_for_rules
         */
        List<SimplePredicate> allPredicates = predicatesMap.entrySet()
                .stream()
                .flatMap((Function<Map.Entry<String, List<SimplePredicate>>, Stream<SimplePredicate>>) stringListEntry -> stringListEntry.getValue().stream())
                .collect(Collectors.toList());
        for (SimplePredicate predicate : allPredicates) {
            String fieldName = fieldTypeMap.get(predicate.getField().getValue()).getGeneratedType();
            final CEDescrBuilder<? extends CEDescrBuilder<?, ?>, AndDescr> nestedAnd = lhsBuilder.and();
            nestedAnd.pattern(fieldName).end();
            OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
            String constraint = getConstraintPattern(predicate, operator, fieldTypeMap);
            nestedAnd.pattern(fieldName).constraint(constraint).end();
            nestedAnd.end();
        }
    }

    static void declareSimplePredicates(final CEDescrBuilder<?, ?> lhsBuilder,
                                        final String fieldName,
                                        final List<SimplePredicate> predicates,
                                        final CompoundPredicate.BooleanOperator booleanOperator,
                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        PatternDescrBuilder<? extends CEDescrBuilder<?, ?>> pattern = lhsBuilder.pattern(fieldName);
        String constraint;
        StringBuilder constraintBuilder;
        switch (booleanOperator) {
            case OR:
                constraintBuilder = new StringBuilder();
                for (int i = 0; i < predicates.size(); i++) {
                    if (i > 0) {
                        constraintBuilder.append(" || ");
                    }
                    SimplePredicate predicate = predicates.get(i);
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    constraint = getConstraintPattern(predicate, operator, fieldTypeMap);
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
                break;
            case XOR:
                break;
            case SURROGATE: // TODO {gcardosi} ?
                pattern = pattern.end().and().pattern(fieldName);
                constraintBuilder = new StringBuilder();
                for (int i = 0; i < predicates.size(); i++) {
                    if (i > 0) {
                        constraintBuilder.append(" || ");
                    }
                    SimplePredicate predicate = predicates.get(i);
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    constraint = getConstraintPattern(predicate, operator, fieldTypeMap);
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
                break;
            case AND:
            default:
                constraintBuilder = new StringBuilder();
                for (int i = 0; i < predicates.size(); i++) {
                    if (i > 0) {
                        constraintBuilder.append(" && ");
                    }
                    SimplePredicate predicate = predicates.get(i);
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    constraint = getConstraintPattern(predicate, operator, fieldTypeMap);
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
        }
        pattern.end();
    }

    /**
     * Create types out of original <code>DataField</code>s,
     * <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param builder
     * @param dataDictionary
     * @param fieldTypeMap
     */
    static void declareTypes(PackageDescrBuilder builder, DataDictionary dataDictionary, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        for (DataField dataField : dataDictionary.getDataFields()) {
            declareType(builder, dataField, fieldTypeMap);
        }
    }

    /**
     * Create type out of original <code>DataField</code>;
     * <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param builder
     * @param dataField
     * @param fieldTypeMap
     */
    static void declareType(PackageDescrBuilder builder, DataField dataField, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        String generatedType = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
        String fieldName = dataField.getName().getValue();
        String fieldType = dataField.getDataType().value();
        fieldTypeMap.put(fieldName, new KiePMMLOriginalTypeGeneratedType(fieldType, generatedType));
        builder.newDeclare()
                .type()
                .name(generatedType)
                .newField("value").type(DATA_TYPE.byName(fieldType).getMappedClass().getSimpleName())
                .end()
                .end();
    }

    static String getConstraintPattern(final SimplePredicate predicate, final OPERATOR operator, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        Object value = predicate.getValue();
        String valueString = null;
        if (value != null) {
            String fieldName = predicate.getField().getValue();
            if (fieldTypeMap.containsKey(fieldName) &&
                    fieldTypeMap.get(fieldName).getOriginalType().equals(DataType.STRING.value())) {
                valueString = String.format(STRING_VALUE_PATTERN, value);
            } else {
                valueString = value.toString();
            }
        }

        return String.format(VALUE_PATTERN, operator.getOperator(), valueString != null ? valueString : "");
    }
}
