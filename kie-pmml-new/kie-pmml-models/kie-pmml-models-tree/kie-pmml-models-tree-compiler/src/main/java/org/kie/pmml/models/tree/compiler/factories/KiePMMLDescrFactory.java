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

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.Value;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.EnumDeclarationDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;

/**
 * Class used to generate a <b>DROOLS</b> (descr) object out of a<b>TreeMoodel</b>
 */
public class KiePMMLDescrFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrFactory.class.getName());
    private static final String VALUE_PATTERN = "value %s \"%s\"";
    private static final String STATUS_HOLDER = "$statusHolder";

    private KiePMMLDescrFactory() {
        // Avoid instantiation
    }

    public static PackageDescr getBaseDescr(DataDictionary dataDictionary, TreeModel model, String packageName) {
        logger.info("getBaseDescr {} {}", dataDictionary, model);
        PackageDescrBuilder builder = DescrFactory.newPackage()
                .name(packageName);
        builder.newImport().target(KiePMMLStatusHolder.class.getName());
        declareTypes(builder, dataDictionary);
        declareRules(builder, model.getNode(), "");
        return builder.getDescr();
    }

    private static void declareRules(PackageDescrBuilder builder, Node node, String parentPath) {
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
        declarePredicate(lhsBuilder, predicate);
        String rhs;
        if (node instanceof LeafNode) {
            rhs = String.format("modify(" + STATUS_HOLDER + ") {" +
                                        "\r\n\tsetStatus(\"****DONE****\")\r\n" +
                                        "\r\n\tsetResult(\"%s\")\r\n" +
                                        "}", node.getScore().toString());
            ruleBuilder.rhs(rhs);
        } else {
            rhs = String.format("modify(" + STATUS_HOLDER + ") {" +
                                        "\r\n\tsetStatus(\"%s\")\r\n" +
                                        "}", currentRule);
            ruleBuilder.rhs(rhs);
            for (Node child : node.getNodes()) {
                declareRules(builder, child, currentRule);
            }
        }
    }

    private static void declarePredicate(final CEDescrBuilder<?, ?> lhsBuilder, final Predicate predicate) {
        String constraint;
        if /*(predicate instanceof True) {
            // TODO {gcardosi} remove this eval, since it is redundant and inefficient
            lhsBuilder.eval().constraint("true");
        } else if (predicate instanceof False) {
            // TODO {gcardosi} in this situation, the semantic would lead to just skip rule creation
            lhsBuilder.eval().constraint("false");
        } else if*/ (predicate instanceof SimplePredicate) {
            OPERATOR operator = OPERATOR.byName(((SimplePredicate) predicate).getOperator().value());
            constraint = String.format(VALUE_PATTERN, operator.getOperator(), ((SimplePredicate) predicate).getValue() != null ? ((SimplePredicate) predicate).getValue() : "");
            lhsBuilder.pattern(((SimplePredicate) predicate).getField().getValue().toUpperCase()).constraint(constraint).end();
        } else if (predicate instanceof CompoundPredicate) {
            final CEDescrBuilder<? extends CEDescrBuilder<?, ?>, ?> andBuilder;
            final CompoundPredicate.BooleanOperator booleanOperator = ((CompoundPredicate) predicate).getBooleanOperator();
            switch (booleanOperator) {
                case OR:
                    andBuilder = lhsBuilder.or();
                    break;
                case AND:
                case XOR: // TODO {gcardosi} (A() not B()) or (not(A()) B()) - A^B => (A & !B) | (!A & B)
                case SURROGATE:
                default:
                    andBuilder = lhsBuilder.and();
            }
            // First I need to group predicates by type
            final Map<? extends Class<? extends Predicate>, List<Predicate>> predicatesByClass = ((CompoundPredicate) predicate).getPredicates().stream().collect(groupingBy(child -> child.getClass()));

            for (Map.Entry<? extends Class<? extends Predicate>, List<Predicate>> entry : predicatesByClass.entrySet()) {
                Class<?> aClass = entry.getKey();
                List<Predicate> predicates = entry.getValue();
                if (SimplePredicate.class.equals(aClass)) {
                    // Here I need to group simplepredicates by field
                    final Map<String, List<SimplePredicate>> predicatesByField = predicates.stream()
                            .map(child -> (SimplePredicate) child)
                            .collect(groupingBy(child -> child.getField().getValue().toUpperCase()));
                    // .. and add them as a whole
                    for (Map.Entry<String, List<SimplePredicate>> childEntry : predicatesByField.entrySet()) {
                        declarePredicate(andBuilder, childEntry.getKey(), childEntry.getValue(), booleanOperator);
                    }
                } else {
                    for (Predicate childPredicate : predicates) {
                        declarePredicate(andBuilder, childPredicate);
                    }
                }
            }
            andBuilder.end();
        }
    }

    private static void declarePredicate(final CEDescrBuilder<?, ?> lhsBuilder, final String fieldName, final List<SimplePredicate> predicates, final CompoundPredicate.BooleanOperator booleanOperator) {
        final PatternDescrBuilder<? extends CEDescrBuilder<?, ?>> pattern = lhsBuilder.pattern(fieldName);
        switch (booleanOperator) {
            case OR:
                StringBuilder constraintBuilder = new StringBuilder();
                for (int i = 0; i < predicates.size(); i++) {
                    if (i > 0) {
                        constraintBuilder.append(" OR ");
                    }
                    SimplePredicate predicate = predicates.get(i);
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    String constraint = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
                    constraintBuilder.append(constraint);
                }
                pattern.constraint(constraintBuilder.toString());
                break;
            case AND:
            case XOR: // TODO {gcardosi} (A() not B()) or (not(A()) B()) - A^B => (A & !B) | (!A & B)
            case SURROGATE:
            default:
                for (SimplePredicate predicate : predicates) {
                    OPERATOR operator = OPERATOR.byName(predicate.getOperator().value());
                    String constraint = String.format(VALUE_PATTERN, operator.getOperator(), predicate.getValue() != null ? predicate.getValue() : "");
                    pattern.constraint(constraint);
                }
        }
        pattern.end();
    }

    private static void declareTypes(PackageDescrBuilder builder, DataDictionary dataDictionary) {
        for (DataField dataField : dataDictionary.getDataFields()) {
            declareType(builder, dataField);
        }
    }

    private static void declareType(PackageDescrBuilder builder, DataField dataField) {
        if (OpType.CATEGORICAL.equals(dataField.getOpType()) && DataType.STRING.equals(dataField.getDataType()) && dataField.hasValues()) {
            declareEnumType(builder, dataField);
        } else {
            builder.newDeclare()
                    .type()
                    .name(dataField.getName().getValue().toUpperCase())
                    .newField("value").type(DATA_TYPE.byName(dataField.getDataType().value()).getMappedClass().getSimpleName())
                    .end()
                    .end();
        }
    }

    private static void declareEnumType(PackageDescrBuilder builder, DataField dataField) {
        final EnumDeclarationDescrBuilder enumBuilder = builder.newDeclare()
                .enumerative().name(dataField.getName().getValue().toUpperCase());
        enumBuilder.newField("value").type(String.class.getName());
        for (Value value : dataField.getValues()) {
            enumBuilder.newEnumLiteral(value.getValue().toString().toUpperCase().replace(' ', '_')).constructorArg(String.format("\"%s\"", value.getValue().toString())).end();
        }
    }
}
