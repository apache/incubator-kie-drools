package org.kie.pmml.models.scorecard.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Scorecard.html#xsdElement_Characteristics>Characteristics</a>
 */
public class KiePMMLCharacteristics extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = 2399787298848608820L;
    protected final List<KiePMMLCharacteristic> characteristics;

    public KiePMMLCharacteristics(String name, List<KiePMMLExtension> extensions,
                                  List<KiePMMLCharacteristic> characteristics) {
        super(name, extensions);
        this.characteristics = characteristics;
    }

    public static Number addNumbers(Number a, Number b) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.doubleValue() + b.doubleValue();
    }

    public static Number calculatePartialScore(Number baselineScore, Number partialScore,
                                               REASONCODE_ALGORITHM reasoncodeAlgorithm) {
        if (baselineScore == null && partialScore == null) {
            return null;
        }
        if (baselineScore == null) {
            return partialScore;
        }
        if (partialScore == null) {
            return baselineScore;
        }
        switch (reasoncodeAlgorithm) {
            case POINTS_BELOW:
                return baselineScore.doubleValue() - partialScore.doubleValue();
            case POINTS_ABOVE:
                return partialScore.doubleValue() - baselineScore.doubleValue();
            default:
                throw new IllegalArgumentException(String.format("Unknown REASONCODE_ALGORITHM %s",
                                                                 reasoncodeAlgorithm));
        }
    }

    /**
     * Method to return the <b>first</b> matching <code>Characteristic</code> score
     * @param defineFunctions
     * @param derivedFields
     * @param outputFields
     * @param inputData
     * @param context
     * @param initialScore
     * @return
     */
    public Optional<Number> evaluate(final List<KiePMMLDefineFunction> defineFunctions,
                                     final List<KiePMMLDerivedField> derivedFields,
                                     final List<KiePMMLOutputField> outputFields,
                                     final Map<String, Object> inputData,
                                     final PMMLRuntimeContext context,
                                     final Number initialScore,
                                     final REASONCODE_ALGORITHM reasoncodeAlgorithm,
                                     final boolean useReasonCodes,
                                     final Number baselineScore) {
        Number accumulator = null;
        for (KiePMMLCharacteristic characteristic : characteristics) {
            final KiePMMLCharacteristic.ReasonCodeValue evaluation = characteristic.evaluate(defineFunctions,
                                                                                             derivedFields,
                                                                                             outputFields, inputData);
            if (evaluation != null) {
                final Number evaluationScore = evaluation.getScore();
                if (accumulator == null) {
                    accumulator = initialScore != null ? initialScore : 0;
                }
                accumulator = addNumbers(accumulator, evaluationScore);
                if (useReasonCodes && evaluation.getReasonCode() != null) {
                    populateReasonCodes(evaluation, characteristic, reasoncodeAlgorithm, context.getOutputFieldsMap()
                            , baselineScore);
                }
            }
        }
        return Optional.ofNullable(accumulator);
    }

    private void populateReasonCodes(final KiePMMLCharacteristic.ReasonCodeValue evaluation,
                                     final KiePMMLCharacteristic characteristic,
                                     final REASONCODE_ALGORITHM reasoncodeAlgorithm,
                                     final Map<String, Object> outputFieldsMap,
                                     final Number baselineScore) {
        Number baselineScoreToUse = characteristic.getBaselineScore() != null ?
                characteristic.getBaselineScore() : baselineScore;
        Number rankingScore = calculatePartialScore(baselineScoreToUse, evaluation.getScore(),
                                                    reasoncodeAlgorithm);
        outputFieldsMap.put(evaluation.getReasonCode(), rankingScore);
    }
}
