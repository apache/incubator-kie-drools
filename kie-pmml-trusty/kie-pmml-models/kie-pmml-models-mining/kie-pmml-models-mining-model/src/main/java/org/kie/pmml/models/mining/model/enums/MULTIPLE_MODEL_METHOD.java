package org.kie.pmml.models.mining.model.enums;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;
import org.kie.pmml.commons.model.tuples.KiePMMLValueWeight;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MultipleModels.html#xsdType_MULTIPLE-MODEL-METHOD>MULTIPLE-MODEL-METHOD</a>
 */
public enum MULTIPLE_MODEL_METHOD {

    MAJORITY_VOTE("majorityVote", inputData -> {
        Map<Object, Long> groupedValues = objectStream(inputData.values().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Object toReturn = null;
        long currentMax = 0L;
        for (Map.Entry<Object, Long> entry : groupedValues.entrySet()) {
            if (entry.getValue() > currentMax) {
                toReturn = entry.getKey();
                currentMax = entry.getValue();
            }
        }
        if (toReturn == null) {
            throw new KieEnumException("Failed to retrieve MAJORITY_VOTE");
        }
        return toReturn;
    }),
    WEIGHTED_MAJORITY_VOTE("weightedMajorityVote", inputData -> {
        throw new KieEnumException("WEIGHTED_MAJORITY_VOTE not implemented, yet");
    }),
    AVERAGE("average", inputData -> doubleStream(inputData.values().stream(), "AVERAGE")
                .average().orElseThrow(() -> new KieEnumException("Failed to get AVERAGE"))),
    WEIGHTED_AVERAGE("weightedAverage", inputData -> {
        AtomicReference<Double> weightedSum = new AtomicReference<>(0.0);
        AtomicReference<Double> weights = new AtomicReference<>(0.0);
        valueWeightStream(inputData.values().stream(), "WEIGHTED_AVERAGE")
                .forEach(elem -> {
                    weightedSum.accumulateAndGet(elem.weightedValue(), Double::sum);
                    weights.accumulateAndGet(elem.getWeight(), Double::sum);
                });
        return weightedSum.get() / weights.get();
    }),
    MEDIAN("median", inputData -> {
        DoubleStream sortedValues = doubleStream(inputData.values().stream(), "MEDIAN").sorted();
        OptionalDouble toReturn = inputData.size() % 2 == 0 ?
                sortedValues.skip(inputData.size() / 2 - (long)1).limit(2).average() :
                sortedValues.skip(inputData.size() / 2).findFirst();
        return toReturn.orElseThrow(() -> new KieEnumException("Failed to get MEDIAN"));
    }),
    WEIGHTED_MEDIAN("x-weightedMedian", inputData -> {
        final List<KiePMMLValueWeight> kiePMMLValueWeights = valueWeightStream(inputData.values()
                                                                                       .stream(),
                                                                               "WEIGHTED_MEDIAN")
                .sorted((o1, o2) -> {
                    int toReturn = 0;
                    if (o1.getValue() > o2.getValue()) {
                        toReturn = 1;
                    } else if (o1.getValue() < o2.getValue()) {
                        toReturn = -1;
                    }
                    return toReturn;
                }).collect(Collectors.toList());
        double weightsSum = kiePMMLValueWeights.stream().map(KiePMMLValueWeight::getWeight)
                .reduce(Double::sum)
                .orElseThrow(() -> new KieEnumException("Failed to get WEIGHTED_MEDIAN"));
        double weightsMedian = weightsSum / 2;
        double weightedMedianSum = 0;
        for (KiePMMLValueWeight kiePMMLValueWeight : kiePMMLValueWeights) {
            weightedMedianSum += kiePMMLValueWeight.getWeight();
            if (weightedMedianSum > weightsMedian) {
                return kiePMMLValueWeight.getValue();
            }
        }
        throw new KieEnumException("Failed to get WEIGHTED_MEDIAN");
    }),
    MAX("max", inputData -> doubleStream(inputData.values().stream(), "MAX").max()
            .orElseThrow(() -> new KieEnumException("Failed to get MAX"))),
    SUM("sum", inputData -> doubleStream(inputData.values().stream(), "SUM").sum()),
    WEIGHTED_SUM("x-weightedSum", inputData ->  valueWeightStream(inputData.values().stream(), "WEIGHTED_SUM")
                .mapToDouble(KiePMMLValueWeight::weightedValue).sum()),
    SELECT_FIRST("selectFirst", inputData -> {
        if (inputData.entrySet().iterator().hasNext()) {
            Object toReturn = inputData.entrySet().iterator().next().getValue().getValue();
            if (toReturn instanceof KiePMMLValueWeight) {
                toReturn = ((KiePMMLValueWeight) toReturn).getValue();
            }
            return toReturn;
        } else {
            throw new KieEnumException("Failed to SELECT_FIRST");
        }
    }),
    SELECT_ALL("selectAll", inputData ->
            objectStream(inputData.values().stream())
                    .filter(Objects::nonNull)
                    .map(value -> {
                        if (value instanceof KiePMMLValueWeight) {
                            return ((KiePMMLValueWeight) value).getValue();
                        } else {
                            return value;
                        }
                    })
                    .collect(Collectors.toList())),
    MODEL_CHAIN("modelChain", inputData -> {
        throw new KieEnumException("MODEL_CHAIN not implemented, yet");
    });

    private final String name;
    /**
     * The function mapped to the given method
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation.
     * It has to be a <code>LinkedHashMap</code> to keep inserrction order and allow evaluation of
     * <code>SELECT_FIRST</code> method
     */
    private final Function<LinkedHashMap<String, KiePMMLNameValue>, Object> function;

    MULTIPLE_MODEL_METHOD(String v, Function<LinkedHashMap<String, KiePMMLNameValue>, Object> function) {
        name = v;
        this.function = function;
    }

    /**
     * Returns a <code>Stream&lt;Object&gt;</code> representing the values inside the original <code>Stream&lt;
     * KiePMMLNameValue&gt;</code>
     * <p>
     * {@link KiePMMLNameValue#getValue()}
     * @param toUnwrap
     * @return
     */
    private static Stream<Object> objectStream(Stream<KiePMMLNameValue> toUnwrap) {
        return toUnwrap.map(KiePMMLNameValue::getValue);
    }

    /**
     * Returns a <code>Stream&lt;KiePMMLValueWeight&gt;</code> representing the values inside the original
     * <code>Stream&lt;KiePMMLNameValue&gt;</code>
     * {@link KiePMMLNameValue#getValue()}
     * @param toUnwrap
     * @param enumName
     * @return
     */
    private static Stream<KiePMMLValueWeight> valueWeightStream(Stream<KiePMMLNameValue> toUnwrap, String enumName) {
        return toUnwrap.map(KiePMMLNameValue::getValue)
                .map(elem -> {
                    if (!(elem instanceof KiePMMLValueWeight)) {
                        throw new KieEnumException("Failed to get " + enumName + ". Expecting KiePMMLValueWeight, " +
                                                           "found " + elem.getClass().getSimpleName());
                    }
                    return ((KiePMMLValueWeight) elem);
                });
    }

    /**
     * Returns a <code>DoubleStream</code> representing the values inside the original <code>Stream&lt;
     * KiePMMLNameValue&gt;</code>
     * <p>
     * {@link KiePMMLValueWeight#getValue()}
     * @param toUnwrap
     * @param enumName
     * @return
     * @throws KieEnumException
     */
    private static DoubleStream doubleStream(Stream<KiePMMLNameValue> toUnwrap, String enumName) {
        return valueWeightStream(toUnwrap, enumName)
                .mapToDouble(KiePMMLValueWeight::getValue);
    }

    public static MULTIPLE_MODEL_METHOD byName(String name) {
        return Arrays.stream(MULTIPLE_MODEL_METHOD.values()).filter(value -> name.equals(value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MULTIPLE_MODEL_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }

    /**
     * Return the overall evaluation of the input data
     * The <b>key</b> of the map is the name of the (inner) model, the <b>value</b> is the result of the model
     * evaluation
     * @param inputData
     * @return
     * @throws KieEnumException
     */
    public Object apply(LinkedHashMap<String, KiePMMLNameValue> inputData) {
        return function.apply(inputData);
    }
}