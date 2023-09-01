package org.kie.pmml.models.tree.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.tuples.KiePMMLProbabilityConfidence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.models.tree.model.KiePMMLTreeTestUtils.getRandomKiePMMLScoreDistributions;

public class KiePMMLNodeTest {

    @Test
    void getProbabilityConfidenceMap() {
        LinkedHashMap<String, KiePMMLProbabilityConfidence> retrieved = KiePMMLNode.getProbabilityConfidenceMap(null, 1.0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
        retrieved = KiePMMLNode.getProbabilityConfidenceMap(Collections.emptyList(), 1.0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
        List<KiePMMLScoreDistribution> kiePMMLScoreDistributions = getRandomKiePMMLScoreDistributions(false);
        retrieved = KiePMMLNode.getProbabilityConfidenceMap(kiePMMLScoreDistributions, 1.0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(kiePMMLScoreDistributions);
    }

    @Test
    void evaluateProbabilityConfidenceMap() {
        List<KiePMMLScoreDistribution> kiePMMLScoreDistributions = getRandomKiePMMLScoreDistributions(false);
        int totalRecordCount = kiePMMLScoreDistributions.stream()
                .map(KiePMMLScoreDistribution::getRecordCount)
                .reduce(0, Integer::sum);
        final double missingValuePenalty = (double) new Random().nextInt(100) / 10;
        LinkedHashMap<String, KiePMMLProbabilityConfidence> retrievedNoProbability = KiePMMLNode.getProbabilityConfidenceMap(kiePMMLScoreDistributions, missingValuePenalty);
        assertThat(retrievedNoProbability).isNotNull();
        kiePMMLScoreDistributions.forEach(kiePMMLScoreDistribution -> {
            assertThat(retrievedNoProbability).containsKey(kiePMMLScoreDistribution.getValue());
            KiePMMLProbabilityConfidence kiePMMLProbabilityConfidence = retrievedNoProbability.get(kiePMMLScoreDistribution.getValue());
            assertThat(kiePMMLProbabilityConfidence).isNotNull();
            double probabilityExpected = (double) kiePMMLScoreDistribution.getRecordCount() / (double) totalRecordCount;
            double confidenceExpected = kiePMMLScoreDistribution.getConfidence() * missingValuePenalty;
            assertThat(kiePMMLProbabilityConfidence.getProbability()).isCloseTo(probabilityExpected, Offset.offset(0.000000001));
            assertThat(kiePMMLProbabilityConfidence.getConfidence()).isCloseTo(confidenceExpected, Offset.offset(0.000000001));
        });
        //
        kiePMMLScoreDistributions = getRandomKiePMMLScoreDistributions(true);
        LinkedHashMap<String, KiePMMLProbabilityConfidence> retrievedProbability = KiePMMLNode.getProbabilityConfidenceMap(kiePMMLScoreDistributions, missingValuePenalty);
        assertThat(retrievedNoProbability).isNotNull();
        kiePMMLScoreDistributions.forEach(kiePMMLScoreDistribution -> {
            assertThat(retrievedProbability).containsKey(kiePMMLScoreDistribution.getValue());
            KiePMMLProbabilityConfidence kiePMMLProbabilityConfidence = retrievedProbability.get(kiePMMLScoreDistribution.getValue());
            assertThat(kiePMMLProbabilityConfidence).isNotNull();
            double probabilityExpected = kiePMMLScoreDistribution.getProbability();
            double confidenceExpected = kiePMMLScoreDistribution.getConfidence() * missingValuePenalty;
            assertThat(kiePMMLProbabilityConfidence.getProbability()).isCloseTo(probabilityExpected, Offset.offset(0.000000001));
            assertThat(kiePMMLProbabilityConfidence.getConfidence()).isCloseTo(confidenceExpected, Offset.offset(0.000000001));
        });
    }
}