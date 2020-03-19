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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
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
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.commons.utils.DrooledModelUtils.getSanitizedClassName;

/**
 * Class used to generate a <b>DROOLS</b> (descr) object out of a<b>TreeModel</b>
 */
public class KiePMMLDescrFactory {

    static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrFactory.class.getName());
    static final String VALUE_PATTERN = "value %s \"%s\"";
    static final String STATUS_HOLDER = "$statusHolder";
    static final String PMML4_RESULT = "$pmml4Result";
    static final String MODIFY_STATUS_HOLDER = "\r\nmodify(" + STATUS_HOLDER + ") {\r\n\tsetStatus(\"%s\")\r\n}";
    static final String UPDATE_PMML4_RESULT = "\r\n" + PMML4_RESULT + ".setResultCode(\"%s\");" +
            "\r\n" + PMML4_RESULT + ".addResultVariable(" + PMML4_RESULT + ".getResultObjectName()" + ", \"%s\");" +
            "\r\nupdate(" + PMML4_RESULT + ");";

    private KiePMMLDescrFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>PackageDescr</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldTypeMap</b> with mapping between original field' name and generated type' name
     * @param dataDictionary
     * @param model
     * @param packageName
     * @param fieldTypeMap
     * @return
     */
    public static PackageDescr getBaseDescr(DataDictionary dataDictionary, TreeModel model, String packageName, Map<String, String> fieldTypeMap) {
        logger.info("getBaseDescr {} {}", dataDictionary, model);
        PackageDescrBuilder builder = DescrFactory.newPackage()
                .name(packageName);
        builder.newImport().target(KiePMMLStatusHolder.class.getName());
        builder.newImport().target(SimplePredicate.class.getName());
        builder.newImport().target(PMML4Result.class.getName());
        declareTypes(builder, dataDictionary, fieldTypeMap);
        declareRules(builder, model.getNode(), "", fieldTypeMap);
        return builder.getDescr();
    }

    static void declareRules(PackageDescrBuilder builder, Node node, String parentPath, final Map<String, String> fieldTypeMap) {
        logger.info("declareRules {} {}", node, parentPath);
        String currentRule = String.format("%s_%s", parentPath, node.getScore().toString());
        final Predicate predicate = node.getPredicate();
        if (predicate instanceof False) {
            return;
        }
        final RuleDescrBuilder ruleBuilder = builder.newRule().name(currentRule);
        final CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder = ruleBuilder.lhs();
        String constraint = StringUtils.isEmpty(parentPath) ? "status == null" : String.format("status == \"%s\"", parentPath);
        lhsBuilder.pattern(KiePMMLStatusHolder.class.getSimpleName()).id(STATUS_HOLDER, false).constraint(constraint);
        declarePredicate(lhsBuilder, predicate, fieldTypeMap);
        if (node instanceof LeafNode || node.getNodes() == null || node.getNodes().isEmpty()) {
            declareFinalLeafWhen(ruleBuilder, lhsBuilder, node);
        } else {
            declareBranchWhen(builder, ruleBuilder, parentPath, node, fieldTypeMap);
        }
    }

    static void declareFinalLeafWhen(final RuleDescrBuilder ruleBuilder, final CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder, final Node node) {
        lhsBuilder.pattern(PMML4Result.class.getSimpleName()).id(PMML4_RESULT, false);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(MODIFY_STATUS_HOLDER, "****DONE****"));
        stringBuilder.append(String.format(UPDATE_PMML4_RESULT, "OK", node.getScore().toString()));
        String rhs = stringBuilder.toString();
        ruleBuilder.rhs(rhs);
    }

    static void declareBranchWhen(final PackageDescrBuilder builder, final RuleDescrBuilder ruleBuilder, String parentPath, final Node node, final Map<String, String> fieldTypeMap) {
        String currentRule = String.format("%s_%s", parentPath, node.getScore().toString());
        String rhs = String.format(MODIFY_STATUS_HOLDER, currentRule);
        ruleBuilder.rhs(rhs);
        for (Node child : node.getNodes()) {
            declareRules(builder, child, currentRule, fieldTypeMap);
        }
    }

    static void declarePredicate(final CEDescrBuilder<?, ?> lhsBuilder, final Predicate predicate, final Map<String, String> fieldTypeMap) {
        /*if (predicate instanceof True) {
            // TODO {gcardosi} remove this eval, since it is redundant and inefficient
            lhsBuilder.eval().constraint("true");
        } else if (predicate instanceof False) {
            // TODO {gcardosi} in this situation, the semantic would lead to just skip rule creation
            lhsBuilder.eval().constraint("false");
        } else */
        if (predicate instanceof SimplePredicate) {
            declareSimplePredicate(lhsBuilder, (SimplePredicate) predicate);
        } else if (predicate instanceof CompoundPredicate) {
            declareCompoundPredicate(lhsBuilder, (CompoundPredicate) predicate, fieldTypeMap);
        }
    }

    static void declareSimplePredicate(final CEDescrBuilder<?, ?> lhsBuilder, final SimplePredicate predicate) {
        OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
        String constraint = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
        lhsBuilder.pattern(predicate.getField().getValue().toUpperCase()).constraint(constraint).end();
    }

    static void declareCompoundPredicate(final CEDescrBuilder<?, ?> lhsBuilder, final CompoundPredicate predicate, final Map<String, String> fieldTypeMap) {
        CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ?> nestedBuilder;
        final CompoundPredicate.BooleanOperator booleanOperator = (predicate).getBooleanOperator();
        switch (booleanOperator) {
            case OR:
                nestedBuilder = lhsBuilder.or();
                break;
            case XOR:
            case AND:
            case SURROGATE:
            default:
                nestedBuilder = lhsBuilder.and();
        }
        // First I need to group predicates by type
        final Map<? extends Class<? extends Predicate>, List<Predicate>> predicatesByClass =
                (predicate).getPredicates().stream().collect(groupingBy(Predicate::getClass));
        for (Map.Entry<? extends Class<? extends Predicate>, List<Predicate>> entry : predicatesByClass.entrySet()) {
            Class<?> aClass = entry.getKey();
            List<Predicate> predicates = entry.getValue();
            if (SimplePredicate.class.equals(aClass)) {
                // Here I need to group simplepredicates by field
                final Map<String, List<SimplePredicate>> predicatesByField = predicates.stream()
                        .map(child -> (SimplePredicate) child)
                        .collect(groupingBy(child -> fieldTypeMap.get(child.getField().getValue())));
                // .. and add them as a whole
                if (CompoundPredicate.BooleanOperator.XOR.equals(booleanOperator)) {
                    declareXORSimplePredicates(nestedBuilder, predicatesByField);
                } else {
                    for (Map.Entry<String, List<SimplePredicate>> childEntry : predicatesByField.entrySet()) {
                        declareSimplePredicates(nestedBuilder, childEntry.getKey(), childEntry.getValue(), booleanOperator);
                    }
                }
            } else {
                for (Predicate childPredicate : predicates) {
                    declarePredicate(nestedBuilder, childPredicate, fieldTypeMap);
                }
            }
        }
        lhsBuilder.end();
    }

    static void declareXORSimplePredicates(final CEDescrBuilder<?, ?> lhsBuilder, final Map<String, List<SimplePredicate>> predicatesMap) {
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
                leftHand = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
                leftPatternType = predicate.getField().getValue().toUpperCase();
                continue;
            }
            rightHand = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
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

    static void declareSimplePredicates(final CEDescrBuilder<?, ?> lhsBuilder, final String fieldName, final List<SimplePredicate> predicates, final CompoundPredicate.BooleanOperator booleanOperator) {
        final PatternDescrBuilder<? extends CEDescrBuilder<?, ?>> pattern = lhsBuilder.pattern(fieldName);
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
                    constraint = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
                break;
            case XOR:
                break;
            case SURROGATE: // TODO {gcardosi} ?
            case AND:
            default:
                constraintBuilder = new StringBuilder();
                for (int i = 0; i < predicates.size(); i++) {
                    if (i > 0) {
                        constraintBuilder.append(" && ");
                    }
                    SimplePredicate predicate = predicates.get(i);
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    constraint = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
        }
        pattern.end();
    }

    /**
     * Create types out of original <code>DataField</code>s and <b>populate</b> the <b>fieldTypeMap</b> with mapping between original field' name and generated type' name
     * @param builder
     * @param dataDictionary
     * @param fieldTypeMap
     */
    static void declareTypes(PackageDescrBuilder builder, DataDictionary dataDictionary, Map<String, String> fieldTypeMap) {
        for (DataField dataField : dataDictionary.getDataFields()) {
            declareType(builder, dataField, fieldTypeMap);
        }
    }

    /**
     * Create type out of original <code>DataField</code> and <b>populate</b> the <b>fieldTypeMap</b> with mapping between original field' name and generated type' name
     * @param builder
     * @param dataField
     * @param fieldTypeMap
     */
    static void declareType(PackageDescrBuilder builder, DataField dataField, Map<String, String> fieldTypeMap) {
        String generatedTypeName = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
        fieldTypeMap.put(dataField.getName().getValue(), generatedTypeName);
        builder.newDeclare()
                .type()
                .name(generatedTypeName)
                .newField("value").type(DATA_TYPE.byName(dataField.getDataType().value()).getMappedClass().getSimpleName())
                .end()
                .end();
    }
}
