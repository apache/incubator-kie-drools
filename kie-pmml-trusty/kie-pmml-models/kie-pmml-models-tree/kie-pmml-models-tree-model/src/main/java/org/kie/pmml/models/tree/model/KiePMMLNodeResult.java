package org.kie.pmml.models.tree.model;

import java.util.LinkedHashMap;

import org.kie.pmml.commons.model.tuples.KiePMMLProbabilityConfidence;

/**
 * Class used as DTO to propagate Tree model results
 */
public class KiePMMLNodeResult {

    private final Object score;

    private final LinkedHashMap<String, Double> probabilityMap;
    private final LinkedHashMap<String, Double> confidenceMap;

    public KiePMMLNodeResult(Object score,
                             LinkedHashMap<String, KiePMMLProbabilityConfidence> probabilityConfidenceMap) {
        this.score = score;
        probabilityMap = new LinkedHashMap<>();
        confidenceMap = new LinkedHashMap<>();
        probabilityConfidenceMap.forEach((targetClass, probabilityConfidenceTuple) -> {
            probabilityMap.put(targetClass, probabilityConfidenceTuple.getProbability());
            confidenceMap.put(targetClass, probabilityConfidenceTuple.getConfidence());
        });
    }

    public Object getScore() {
        return score;
    }

    public LinkedHashMap<String, Double> getProbabilityMap() {
        return probabilityMap;
    }

    public LinkedHashMap<String, Double> getConfidenceMap() {
        return confidenceMap;
    }
}
