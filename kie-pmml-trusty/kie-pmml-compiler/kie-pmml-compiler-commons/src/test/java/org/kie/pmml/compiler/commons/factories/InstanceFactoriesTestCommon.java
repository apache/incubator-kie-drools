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
package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.text.StringEscapeUtils;
import org.assertj.core.data.Offset;
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
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.EXPRESSION_NOT_MANAGED;

/**
 * Common methods for <b>InstanceFactory</b> tests
 */
public class InstanceFactoriesTestCommon {

    static void commonVerifyKiePMMLDefineFunction(KiePMMLDefineFunction toVerify,
                                                  DefineFunction source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertThat(toVerify.getDataType()).isEqualTo(expectedDataType);
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertThat(toVerify.getOpType()).isEqualTo(expectedOpType);
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
        List<ParameterField> sourcesParameterFields = source.getParameterFields();
        List<KiePMMLParameterField> toVerifyList = toVerify.getParameterFields();
        assertThat(toVerifyList).hasSameSizeAs(sourcesParameterFields);
        sourcesParameterFields.forEach(paramSource -> {
            Optional<KiePMMLParameterField> parameterToVerify =
                    toVerifyList.stream().filter(param -> param.getName().equals(paramSource.getName()))
                            .findFirst();
            assertThat(parameterToVerify).isPresent();
            commonVerifyKiePMMLParameterField(parameterToVerify.get(), paramSource);
        });
    }

    static void commonVerifyKiePMMLDerivedField(KiePMMLDerivedField toVerify,
                                                DerivedField source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertThat(toVerify.getDataType()).isEqualTo(expectedDataType);
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertThat(toVerify.getOpType()).isEqualTo(expectedOpType);
        String expectedDisplayName = "Display-" +source.getName();
        assertThat(toVerify.getDisplayName()).isEqualTo(expectedDisplayName);
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
    }

    static void commonVerifyKiePMMLParameterField(KiePMMLParameterField toVerify, ParameterField source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getName());
        DATA_TYPE expectedDataType = DATA_TYPE.byName(source.getDataType().value());
        assertThat(toVerify.getDataType()).isEqualTo(expectedDataType);
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertThat(toVerify.getOpType()).isEqualTo(expectedOpType);
        String expectedDisplayName = "Display-" +source.getName();
        assertThat(toVerify.getDisplayName()).isEqualTo(expectedDisplayName);
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
        assertThat(toVerify.getBooleanOperator().getName()).isEqualTo(source.getBooleanOperator().value());
        assertThat(toVerify.getKiePMMLPredicates()).hasSameSizeAs(source.getPredicates());
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
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getValue()).isEqualTo(value);
        assertThat(toVerify.getOperator().getName()).isEqualTo(source.getOperator().value());
    }

    static void commonVerifyKiePMMLSimplePredicate(KiePMMLSimplePredicate toVerify, SimplePredicate source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getOperator().getName()).isEqualTo(source.getOperator().value());
    }

    static void commonVerifyKiePMMLSimpleSetPredicate(KiePMMLSimpleSetPredicate toVerify, SimpleSetPredicate source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        Array array = source.getArray();
        assertThat(toVerify.getArrayType().getName()).isEqualTo(array.getType().value());
        assertThat(toVerify.getInNotIn().getName()).isEqualTo(source.getBooleanOperator().value());
        assertThat(toVerify.getValues()).hasSize(array.getN().intValue());
        String stringValue = (String) array.getValue();
        String[] valuesArray = stringValue.split(" ");
        IntStream.range(0, array.getN()).forEach(i -> {
            switch (array.getType()) {
                case INT:
                    assertThat(toVerify.getValues().get(i)).isEqualTo(Integer.valueOf(valuesArray[i]));
                    break;
                case STRING:
                    assertThat(toVerify.getValues().get(i)).isEqualTo(valuesArray[i]);
                    break;
                case REAL:
                    assertThat(toVerify.getValues().get(i)).isEqualTo(Double.valueOf(valuesArray[i]));
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
        assertThat(toVerify.getFunction()).isEqualTo(source.getFunction());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
        assertThat(toVerify.getDefaultValue()).isEqualTo(source.getDefaultValue());
        assertThat(toVerify.getInvalidValueTreatmentMethod().getName()).isEqualTo(source.getInvalidValueTreatment().value());
        List<KiePMMLExpression> kiePMMLExpressionList = toVerify.getKiePMMLExpressions();
        assertThat(kiePMMLExpressionList).hasSameSizeAs(source.getExpressions());
        IntStream.range(0, source.getExpressions().size()).forEach(i -> commonVerifyKiePMMLExpression(kiePMMLExpressionList.get(i), source.getExpressions().get(i)));
    }

    static void commonVerifyKiePMMLConstant(KiePMMLConstant toVerify, Constant source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getValue()).isEqualTo(source.getValue());
    }

    static void commonVerifyKiePMMLDiscretize(KiePMMLDiscretize toVerify, Discretize source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
        assertThat(toVerify.getDefaultValue()).isEqualTo(source.getDefaultValue());
        assertThat(toVerify.getDataType().getName()).isEqualTo(source.getDataType().value());
        assertThat(toVerify.getDiscretizeBins()).hasSameSizeAs(source.getDiscretizeBins());
        IntStream.range(0, source.getDiscretizeBins().size()).forEach(i -> commonVerifyKiePMMLDiscretizeBin(toVerify.getDiscretizeBins().get(i), source.getDiscretizeBins().get(i)));
    }

    static void commonVerifyKiePMMLFieldRef(KiePMMLFieldRef toVerify, FieldRef source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
    }

    static void commonVerifyKiePMMLMapValues(KiePMMLMapValues toVerify, MapValues source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getOutputColumn()).isEqualTo(source.getOutputColumn());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
        assertThat(toVerify.getDefaultValue()).isEqualTo(source.getDefaultValue().toString());
        assertThat(toVerify.getDataType().getName()).isEqualTo(source.getDataType().value());
        commonVerifyKiePMMLInlineTableWithCells(toVerify.getInlineTable(), source.getInlineTable());
        assertThat(toVerify.getFieldColumnPairs()).hasSameSizeAs(source.getFieldColumnPairs());
        IntStream.range(0, source.getFieldColumnPairs().size()).forEach(i -> commonVerifyKiePMMLFieldColumnPair(toVerify.getFieldColumnPairs().get(i), source.getFieldColumnPairs().get(i)));
    }

    static void commonVerifyKiePMMLNormContinuous(KiePMMLNormContinuous toVerify, NormContinuous source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getOutlierTreatmentMethod().getName()).isEqualTo(source.getOutliers().value());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
        final List<LinearNorm> toConvertLinearNorms = source.getLinearNorms();
        sortLinearNorms(toConvertLinearNorms);
        final List<KiePMMLLinearNorm> retrievedLinearNorms = toVerify.getLinearNorms();
        assertThat(retrievedLinearNorms).hasSameSizeAs(toConvertLinearNorms);
        IntStream.range(0, toConvertLinearNorms.size()).forEach(i -> commonVerifyKiePMMLLinearNorm(retrievedLinearNorms.get(i), toConvertLinearNorms.get(i)));
    }

    static void commonVerifyKiePMMLNormDiscrete(KiePMMLNormDiscrete toVerify, NormDiscrete source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getMapMissingTo()).isEqualTo(source.getMapMissingTo());
        assertThat(toVerify.getValue()).isEqualTo(source.getValue().toString());
    }

    static void commonVerifyKiePMMLTextIndex(KiePMMLTextIndex toVerify, TextIndex source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getLocalTermWeights().getName()).isEqualTo(source.getLocalTermWeights().value());
        assertThat(toVerify.getCountHits().getName()).isEqualTo(source.getCountHits().value());
        assertThat(toVerify.getWordSeparatorCharacterRE()).isEqualTo(StringEscapeUtils.escapeJava(source.getWordSeparatorCharacterRE()));
        commonVerifyKiePMMLExpression(toVerify.getExpression(), source.getExpression());
        assertThat(toVerify.getTextIndexNormalizations()).hasSameSizeAs(source.getTextIndexNormalizations());
        IntStream.range(0, source.getTextIndexNormalizations().size()).forEach(i -> {
            commonVerifyKiePMMLTextIndexNormalization(toVerify.getTextIndexNormalizations().get(i),
                                                      source.getTextIndexNormalizations().get(i));
        });
        assertThat(toVerify.isCaseSensitive()).isEqualTo(source.isCaseSensitive());
        assertThat(toVerify.getMaxLevenshteinDistance()).isEqualTo(source.getMaxLevenshteinDistance().intValue());
        assertThat(toVerify.isTokenize()).isEqualTo(source.isTokenize());
    }

    static void commonVerifyKiePMMLTextIndexNormalization(KiePMMLTextIndexNormalization toVerify,
                                                          TextIndexNormalization source) {
        assertThat(toVerify).isNotNull();
    }

    //

    static void commonVerifyKiePMMLDiscretizeBin(KiePMMLDiscretizeBin toVerify, DiscretizeBin source) {
        assertThat(toVerify).isNotNull();
        commonVerifyKiePMMLInterval(toVerify.getInterval(), source.getInterval());
        assertThat(toVerify.getBinValue()).isEqualTo(source.getBinValue());
    }

    static void commonVerifyKiePMMLFieldColumnPair(KiePMMLFieldColumnPair toVerify, FieldColumnPair source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getField());
        assertThat(toVerify.getColumn()).isEqualTo(source.getColumn());
    }

    static void commonVerifyKiePMMLInlineTableWithCells(KiePMMLInlineTable toVerify, InlineTable source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getRows()).hasSameSizeAs(source.getRows());
        IntStream.range(0, source.getRows().size()).forEach(i -> commonVerifyKiePMMLRowWithCells(toVerify.getRows().get(i), source.getRows().get(i)));
    }

    static void commonVerifyKiePMMLInterval(KiePMMLInterval toVerify, Interval source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getLeftMargin()).isEqualTo(source.getLeftMargin());
        assertThat(toVerify.getRightMargin()).isEqualTo(source.getRightMargin());
        assertThat(toVerify.getClosure().getName()).isEqualTo(source.getClosure().value());
    }

    static void commonVerifyKiePMMLMiningField(KiePMMLMiningField toVerify, MiningField source, DataField dataField) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getName());
        assertThat(toVerify.getOpType().getName()).isEqualTo(source.getOpType().value());
        assertThat(toVerify.getFieldUsageType().getName()).isEqualTo(source.getUsageType().value());
        assertThat(toVerify.getInvalidValueTreatmentMethod().getName()).isEqualTo(source.getInvalidValueTreatment().value());
        assertThat(toVerify.getMissingValueTreatmentMethod().getName()).isEqualTo(source.getMissingValueTreatment().value());
        assertThat(toVerify.getInvalidValueReplacement()).isEqualTo(source.getInvalidValueReplacement());
        assertThat(toVerify.getMissingValueReplacement()).isEqualTo(source.getMissingValueReplacement());
        assertThat(toVerify.getDataType().getName()).isEqualTo(dataField.getDataType().value());
        assertThat(toVerify.getIntervals()).hasSameSizeAs(dataField.getIntervals());
        IntStream.range(0, dataField.getIntervals().size()).forEach(i -> commonVerifyKiePMMLInterval(toVerify.getIntervals().get(i), dataField.getIntervals().get(i)));
    }

    static void commonVerifyKiePMMLOutputField(KiePMMLOutputField toVerify, OutputField source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getName()).isEqualTo(source.getName());
        assertThat(toVerify.getValue()).isEqualTo(source.getValue());
        assertThat(toVerify.getDataType().getName()).isEqualTo(source.getDataType().value());
        assertThat(toVerify.getTargetField().get()).isEqualTo(source.getTargetField());
        assertThat(toVerify.getResultFeature().getName()).isEqualTo(source.getResultFeature().value());
        assertThat(toVerify.getRank()).isEqualTo(source.getRank());
        assertThat(toVerify.getValue()).isEqualTo(source.getValue());
        commonVerifyKiePMMLExpression(toVerify.getKiePMMLExpression(), source.getExpression());
    }

    static void commonVerifyKiePMMLTarget(KiePMMLTarget toVerify, Target source) {
        assertThat(toVerify).isNotNull();
        assertThat(source.getTargetValues()).hasSameSizeAs(toVerify.getTargetValues());
        OP_TYPE expectedOpType = OP_TYPE.byName(source.getOpType().value());
        assertThat(toVerify.getOpType()).isEqualTo(expectedOpType);
        assertThat(toVerify.getField()).isEqualTo(source.getField());
        CAST_INTEGER expectedCastInteger = CAST_INTEGER.byName(source.getCastInteger().value());
        assertThat(toVerify.getCastInteger()).isEqualTo(expectedCastInteger);
        assertThat(toVerify.getMin()).isCloseTo(source.getMin().doubleValue(), Offset.offset(0.0));
        assertThat(toVerify.getMax()).isCloseTo(source.getMax().doubleValue(), Offset.offset(0.0));
        assertThat(toVerify.getRescaleConstant()).isCloseTo(source.getRescaleConstant().doubleValue(), Offset.offset(0.0));
        assertThat(toVerify.getRescaleFactor()).isCloseTo(source.getRescaleFactor().doubleValue(), Offset.offset(0.0));
    }

    static void commonVerifyKiePMMLRow(KiePMMLRow toVerify,
                                       Row source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getColumnValues()).isEmpty();
    }

    static void commonVerifyKiePMMLRowWithCells(KiePMMLRow toVerify,
                                                Row source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getColumnValues()).hasSize(2);
    }

    static void commonVerifyKiePMMLTargetValue(KiePMMLTargetValue toVerify,
                                               TargetValue source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getValue()).isEqualTo(source.getValue().toString());
        assertThat(toVerify.getDisplayValue()).isEqualTo(source.getDisplayValue());
        assertThat(toVerify.getPriorProbability()).isCloseTo(source.getPriorProbability().doubleValue(), Offset.offset(0.0));
        assertThat(toVerify.getDefaultValue()).isCloseTo(source.getDefaultValue().doubleValue(), Offset.offset(0.0));
    }

    static void commonVerifyKiePMMLLinearNorm(KiePMMLLinearNorm toVerify,
                                              LinearNorm source) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify.getOrig()).isCloseTo(source.getOrig().doubleValue(), Offset.offset(0.0));
        assertThat(toVerify.getNorm()).isCloseTo(source.getNorm().doubleValue(), Offset.offset(0.0));
    }

    static void sortLinearNorms(final List<LinearNorm> toSort) {
        toSort.sort((o1, o2) -> (int) (o1.getOrig().doubleValue() - o2.getOrig().doubleValue()));
    }
}
