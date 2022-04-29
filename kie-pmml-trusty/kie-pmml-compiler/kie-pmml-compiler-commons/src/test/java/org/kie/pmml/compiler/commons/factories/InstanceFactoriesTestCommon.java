/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.text.StringEscapeUtils;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.DiscretizeBin;
import org.dmg.pmml.Expression;
import org.dmg.pmml.False;
import org.dmg.pmml.FieldColumnPair;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.InlineTable;
import org.dmg.pmml.Interval;
import org.dmg.pmml.LinearNorm;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.Row;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.Target;
import org.dmg.pmml.TargetValue;
import org.dmg.pmml.TextIndex;
import org.dmg.pmml.TextIndexNormalization;
import org.dmg.pmml.True;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.KiePMMLTargetValue;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretize;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;
import org.kie.pmml.commons.model.expressions.KiePMMLMapValues;
import org.kie.pmml.commons.model.expressions.KiePMMLNormContinuous;
import org.kie.pmml.commons.model.expressions.KiePMMLNormDiscrete;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndex;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndexNormalization;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.EXPRESSION_NOT_MANAGED;

/**
 * Common methods for <b>InstanceFactory</b> tests
 */
public class InstanceFactoriesTestCommon {

    static void commonVerifyKiePMMLDefineFunction(KiePMMLDefineFunction toVerify,
                                                  DefineFunction source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getName(), toVerify.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertEquals(expectedDataType, toVerify.getDataType());
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertEquals(expectedOpType, toVerify.getOpType());
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
        List<ParameterField> sourcesParameterFields = source.getParameterFields();
        List<KiePMMLParameterField> toVerifyList = toVerify.getParameterFields();
        assertEquals(sourcesParameterFields.size(), toVerifyList.size());
        sourcesParameterFields.forEach(paramSource -> {
            Optional<KiePMMLParameterField> parameterToVerify =
                    toVerifyList.stream().filter(param -> param.getName().equals(paramSource.getName().getValue()))
                            .findFirst();
            assertTrue(parameterToVerify.isPresent());
            commonVerifyKiePMMLParameterField(parameterToVerify.get(), paramSource);
        });
    }

    static void commonVerifyKiePMMLDerivedField(KiePMMLDerivedField toVerify,
                                                DerivedField source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getName().getValue(), toVerify.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertEquals(expectedDataType, toVerify.getDataType());
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertEquals(expectedOpType, toVerify.getOpType());
        String expectedDisplayName = "Display-" + source.getName().getValue();
        assertEquals(expectedDisplayName, toVerify.getDisplayName());
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
    }

    static void commonVerifyKiePMMLParameterField(KiePMMLParameterField toVerify, ParameterField source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getName().getValue(), toVerify.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertEquals(expectedDataType, toVerify.getDataType());
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertEquals(expectedOpType, toVerify.getOpType());
        String expectedDisplayName = "Display-" + source.getName().getValue();
        assertEquals(expectedDisplayName, toVerify.getDisplayName());
    }

    // Predicates
    static void commonVerifyKiePMMLPredicate(KiePMMLPredicate toVerify, Predicate source) {
        switch (source.getClass().getSimpleName()) {
            case "CompoundPredicate":
                commonVerifyKKiePMMLCompoundPredicate((KiePMMLCompoundPredicate) toVerify, (CompoundPredicate) source);
                break;
            case "False":
                commonVerifyKiePMMLFalsePredicate((KiePMMLFalsePredicate) toVerify, (False) source);
                break;
            case "SimplePredicate":
                commonVerifyKiePMMLSimplePredicate((KiePMMLSimplePredicate) toVerify, (SimplePredicate) source);
                break;
            case "SimpleSetPredicate":
                commonVerifyKiePMMLSimpleSetPredicate((KiePMMLSimpleSetPredicate) toVerify,
                                                      (SimpleSetPredicate) source);
                break;
            case "True":
                commonVerifyKiePMMLTruePredicate((KiePMMLTruePredicate) toVerify, (True) source);
                break;
            default:
                fail(String.format("Predicate %s not managed", source.getClass()));
        }
    }

    static void commonVerifyKKiePMMLCompoundPredicate(KiePMMLCompoundPredicate toVerify, CompoundPredicate source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getBooleanOperator().value(), toVerify.getBooleanOperator().getName());
        assertEquals(source.getPredicates().size(), toVerify.getKiePMMLPredicates().size());
        IntStream.range(0, source.getPredicates().size()).forEach(i -> {
            commonVerifyKiePMMLPredicate(toVerify.getKiePMMLPredicates().get(i), source.getPredicates().get(i));
        });
    }

    static void commonVerifyKiePMMLFalsePredicate(KiePMMLFalsePredicate toVerify, False source) {
        assertThat(toVerify).isNotNull();
    }

    static void commonVerifyKiePMMLSimplePredicate(KiePMMLSimplePredicate toVerify, SimplePredicate source,
                                                   DataField dataField) {
        assertThat(toVerify).isNotNull();
        Object value = DATA_TYPE.byName(dataField.getDataType().value()).getActualValue(source.getValue());
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(value, toVerify.getValue());
        assertEquals(source.getOperator().value(), toVerify.getOperator().getName());
    }

    static void commonVerifyKiePMMLSimplePredicate(KiePMMLSimplePredicate toVerify, SimplePredicate source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(source.getOperator().value(), toVerify.getOperator().getName());
    }

    static void commonVerifyKiePMMLSimpleSetPredicate(KiePMMLSimpleSetPredicate toVerify, SimpleSetPredicate source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        Array array = source.getArray();
        assertEquals(array.getType().value(), toVerify.getArrayType().getName());
        assertEquals(source.getBooleanOperator().value(), toVerify.getInNotIn().getName());
        assertEquals(array.getN().intValue(), toVerify.getValues().size());
        String stringValue = (String) array.getValue();
        String[] valuesArray = stringValue.split(" ");
        IntStream.range(0, array.getN()).forEach(i -> {
            switch (array.getType()) {
                case INT:
                    assertEquals(Integer.valueOf(valuesArray[i]), toVerify.getValues().get(i));
                    break;
                case STRING:
                    assertEquals(valuesArray[i], toVerify.getValues().get(i));
                    break;
                case REAL:
                    assertEquals(Double.valueOf(valuesArray[i]), toVerify.getValues().get(i));
                    break;
                default:
                    throw new KiePMMLException("Unknown Array " + array.getType());
            }
        });
    }

    static void commonVerifyKiePMMLTruePredicate(KiePMMLTruePredicate toVerify, True source) {
        assertThat(toVerify).isNotNull();
    }

    // Expressions

    static void commonVerifyKiePMMLExpression(KiePMMLExpression toVerify, Expression source) {
        switch (source.getClass().getSimpleName()) {
            case "Apply":
                commonVerifyKiePMMLApply((KiePMMLApply) toVerify, (Apply) source);
                break;
            case "Constant":
                commonVerifyKiePMMLConstant((KiePMMLConstant) toVerify, (Constant) source);
                break;
            case "Discretize":
                commonVerifyKiePMMLDiscretize((KiePMMLDiscretize) toVerify, (Discretize) source);
                break;
            case "FieldRef":
                commonVerifyKiePMMLFieldRef((KiePMMLFieldRef) toVerify, (FieldRef) source);
                break;
            case "MapValues":
                commonVerifyKiePMMLMapValues((KiePMMLMapValues) toVerify, (MapValues) source);
                break;
            case "NormContinuous":
                commonVerifyKiePMMLNormContinuous((KiePMMLNormContinuous) toVerify, (NormContinuous) source);
                break;
            case "NormDiscrete":
                commonVerifyKiePMMLNormDiscrete((KiePMMLNormDiscrete) toVerify, (NormDiscrete) source);
                break;
            case "TextIndex":
                commonVerifyKiePMMLTextIndex((KiePMMLTextIndex) toVerify, (TextIndex) source);
                break;
            default:
                fail(String.format(EXPRESSION_NOT_MANAGED, source.getClass()));
        }
    }

    static void commonVerifyKiePMMLApply(KiePMMLApply toVerify, Apply source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getFunction(), toVerify.getFunction());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
        assertEquals(source.getDefaultValue(), toVerify.getDefaultValue());
        assertEquals(source.getInvalidValueTreatment().value(),
                     toVerify.getInvalidValueTreatmentMethod().getName());
        List<KiePMMLExpression> kiePMMLExpressionList = toVerify.getKiePMMLExpressions();
        assertEquals(source.getExpressions().size(), kiePMMLExpressionList.size());
        IntStream.range(0, source.getExpressions().size()).forEach(i -> commonVerifyKiePMMLExpression(kiePMMLExpressionList.get(i), source.getExpressions().get(i)));
    }

    static void commonVerifyKiePMMLConstant(KiePMMLConstant toVerify, Constant source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getValue(), toVerify.getValue());
    }

    static void commonVerifyKiePMMLDiscretize(KiePMMLDiscretize toVerify, Discretize source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
        assertEquals(source.getDefaultValue(), toVerify.getDefaultValue());
        assertEquals(source.getDataType().value(), toVerify.getDataType().getName());
        assertEquals(source.getDiscretizeBins().size(), toVerify.getDiscretizeBins().size());
        IntStream.range(0, source.getDiscretizeBins().size()).forEach(i -> commonVerifyKiePMMLDiscretizeBin(toVerify.getDiscretizeBins().get(i), source.getDiscretizeBins().get(i)));
    }

    static void commonVerifyKiePMMLFieldRef(KiePMMLFieldRef toVerify, FieldRef source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
    }

    static void commonVerifyKiePMMLMapValues(KiePMMLMapValues toVerify, MapValues source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getOutputColumn(), toVerify.getOutputColumn());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
        assertEquals(source.getDefaultValue().toString(), toVerify.getDefaultValue());
        assertEquals(source.getDataType().value(), toVerify.getDataType().getName());
        commonVerifyKiePMMLInlineTableWithCells(toVerify.getInlineTable(), source.getInlineTable());
        assertEquals(source.getFieldColumnPairs().size(), toVerify.getFieldColumnPairs().size());
        IntStream.range(0, source.getFieldColumnPairs().size()).forEach(i -> commonVerifyKiePMMLFieldColumnPair(toVerify.getFieldColumnPairs().get(i), source.getFieldColumnPairs().get(i)));
    }

    static void commonVerifyKiePMMLNormContinuous(KiePMMLNormContinuous toVerify, NormContinuous source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getOutliers().value(), toVerify.getOutlierTreatmentMethod().getName());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
        final List<LinearNorm> toConvertLinearNorms = source.getLinearNorms();
        sortLinearNorms(toConvertLinearNorms);
        final List<KiePMMLLinearNorm> retrievedLinearNorms = toVerify.getLinearNorms();
        assertEquals(toConvertLinearNorms.size(), retrievedLinearNorms.size());
        IntStream.range(0, toConvertLinearNorms.size()).forEach(i -> commonVerifyKiePMMLLinearNorm(retrievedLinearNorms.get(i), toConvertLinearNorms.get(i)));
    }

    static void commonVerifyKiePMMLNormDiscrete(KiePMMLNormDiscrete toVerify, NormDiscrete source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(source.getMapMissingTo(), toVerify.getMapMissingTo());
        assertEquals(source.getValue().toString(), toVerify.getValue());
    }

    static void commonVerifyKiePMMLTextIndex(KiePMMLTextIndex toVerify, TextIndex source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getLocalTermWeights().value(), toVerify.getLocalTermWeights().getName());
        assertEquals(source.getCountHits().value(), toVerify.getCountHits().getName());
        assertEquals(StringEscapeUtils.escapeJava(source.getWordSeparatorCharacterRE()),
                     toVerify.getWordSeparatorCharacterRE());
        commonVerifyKiePMMLExpression(toVerify.getExpression(), source.getExpression());
        assertEquals(source.getTextIndexNormalizations().size(), toVerify.getTextIndexNormalizations().size());
        IntStream.range(0, source.getTextIndexNormalizations().size()).forEach(i -> {
            commonVerifyKiePMMLTextIndexNormalization(toVerify.getTextIndexNormalizations().get(i),
                                                      source.getTextIndexNormalizations().get(i));
        });
        assertEquals(source.isCaseSensitive(), toVerify.isCaseSensitive());
        assertEquals(source.getMaxLevenshteinDistance().intValue(), toVerify.getMaxLevenshteinDistance());
        assertEquals(source.isTokenize(), toVerify.isTokenize());
    }

    static void commonVerifyKiePMMLTextIndexNormalization(KiePMMLTextIndexNormalization toVerify,
                                                          TextIndexNormalization source) {
        assertThat(toVerify).isNotNull();
    }

    //

    static void commonVerifyKiePMMLDiscretizeBin(KiePMMLDiscretizeBin toVerify, DiscretizeBin source) {
        assertThat(toVerify).isNotNull();
        commonVerifyKiePMMLInterval(toVerify.getInterval(), source.getInterval());
        assertEquals(source.getBinValue(), toVerify.getBinValue());
    }

    static void commonVerifyKiePMMLFieldColumnPair(KiePMMLFieldColumnPair toVerify, FieldColumnPair source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getField().getValue(), toVerify.getName());
        assertEquals(source.getColumn(), toVerify.getColumn());
    }

    static void commonVerifyKiePMMLInlineTableWithCells(KiePMMLInlineTable toVerify, InlineTable source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getRows().size(), toVerify.getRows().size());
        IntStream.range(0, source.getRows().size()).forEach(i -> commonVerifyKiePMMLRowWithCells(toVerify.getRows().get(i), source.getRows().get(i)));
    }

    static void commonVerifyKiePMMLInterval(KiePMMLInterval toVerify, Interval source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getLeftMargin(), toVerify.getLeftMargin());
        assertEquals(source.getRightMargin(), toVerify.getRightMargin());
        assertEquals(source.getClosure().value(), toVerify.getClosure().getName());
    }

    static void commonVerifyKiePMMLMiningField(KiePMMLMiningField toVerify, MiningField source, DataField dataField) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getName().getValue(), toVerify.getName());
        assertEquals(source.getOpType().value(), toVerify.getOpType().getName());
        assertEquals(source.getUsageType().value(), toVerify.getFieldUsageType().getName());
        assertEquals(source.getInvalidValueTreatment().value(), toVerify.getInvalidValueTreatmentMethod().getName());
        assertEquals(source.getMissingValueTreatment().value(), toVerify.getMissingValueTreatmentMethod().getName());
        assertEquals(source.getInvalidValueReplacement(), toVerify.getInvalidValueReplacement());
        assertEquals(source.getMissingValueReplacement(), toVerify.getMissingValueReplacement());
        assertEquals(dataField.getDataType().value(), toVerify.getDataType().getName());
        assertEquals(dataField.getIntervals().size(), toVerify.getIntervals().size());
        IntStream.range(0, dataField.getIntervals().size()).forEach(i -> commonVerifyKiePMMLInterval(toVerify.getIntervals().get(i), dataField.getIntervals().get(i)));
    }

    static void commonVerifyKiePMMLOutputField(KiePMMLOutputField toVerify, OutputField source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getName().getValue(), toVerify.getName());
        assertEquals(source.getValue(), toVerify.getValue());
        assertEquals(source.getDataType().value(), toVerify.getDataType().getName());
        assertEquals(source.getTargetField().getValue(), toVerify.getTargetField().get());
        assertEquals(source.getResultFeature().value(), toVerify.getResultFeature().getName());
        assertEquals(source.getRank(), toVerify.getRank());
        assertEquals(source.getValue(), toVerify.getValue());
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
    }

    static void commonVerifyKiePMMLTarget(KiePMMLTarget toVerify, Target source) {
        assertThat(toVerify).isNotNull();
        assertEquals(toVerify.getTargetValues().size(), source.getTargetValues().size());
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertEquals(expectedOpType, toVerify.getOpType());
        assertEquals(source.getField().getValue(), toVerify.getField());
        CAST_INTEGER expectedCastInteger = CAST_INTEGER.byName(source.getCastInteger().value());
        assertEquals(expectedCastInteger, toVerify.getCastInteger());
        assertEquals(source.getMin().doubleValue(), toVerify.getMin(), 0.0);
        assertEquals(source.getMax().doubleValue(), toVerify.getMax(), 0.0);
        assertEquals(source.getRescaleConstant().doubleValue(), toVerify.getRescaleConstant(), 0.0);
        assertEquals(source.getRescaleFactor().doubleValue(), toVerify.getRescaleFactor(), 0.0);
    }

    static void commonVerifyKiePMMLRow(KiePMMLRow toVerify,
                                       Row source) {
        assertThat(toVerify).isNotNull();
        assertTrue(toVerify.getColumnValues().isEmpty());
    }

    static void commonVerifyKiePMMLRowWithCells(KiePMMLRow toVerify,
                                                Row source) {
        assertThat(toVerify).isNotNull();
        assertEquals(2, toVerify.getColumnValues().size());
    }

    static void commonVerifyKiePMMLTargetValue(KiePMMLTargetValue toVerify,
                                               TargetValue source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getValue().toString(), toVerify.getValue());
        assertEquals(source.getDisplayValue(), toVerify.getDisplayValue());
        assertEquals(source.getPriorProbability().doubleValue(), toVerify.getPriorProbability(), 0.0);
        assertEquals(source.getDefaultValue().doubleValue(), toVerify.getDefaultValue(), 0.0);
    }

    static void commonVerifyKiePMMLLinearNorm(KiePMMLLinearNorm toVerify,
                                              LinearNorm source) {
        assertThat(toVerify).isNotNull();
        assertEquals(source.getOrig().doubleValue(), toVerify.getOrig(), 0.0);
        assertEquals(source.getNorm().doubleValue(), toVerify.getNorm(), 0.0);
    }

    static void sortLinearNorms(final List<LinearNorm> toSort) {
        toSort.sort((o1, o2) -> (int) (o1.getOrig().doubleValue() - o2.getOrig().doubleValue()));
    }
}
