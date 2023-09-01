package org.kie.pmml.models.tree.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;

public class KiePMMLTreeTestUtils {

    public static List<KiePMMLScoreDistribution> getRandomKiePMMLScoreDistributions(boolean withProbability) {
        List<Double> probabilities = withProbability ? Arrays.asList(0.1, 0.3, 0.6) : Arrays.asList(null, null, null);
        return IntStream.range(0, 3)
                .mapToObj(i -> getRandomKiePMMLScoreDistribution(probabilities.get(i)))
                .collect(Collectors.toList());
    }

    public static KiePMMLScoreDistribution getRandomKiePMMLScoreDistribution(Double probability) {
        Random random = new Random();
        return new KiePMMLScoreDistribution(RandomStringUtils.random(6, true, false),
                                            null,
                                            RandomStringUtils.random(6, true, false),
                                            random.nextInt(100),
                                            (double) random.nextInt(1) / 100,
                                            probability);
    }
}