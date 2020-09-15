package org.kie.kogito.codegen.prediction;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionRuleMappersGeneratorTest {

    @Test
    void getPredictionRuleMapperSource() {
        final String predictionRuleMapper = "PredictionRuleMapperImpl";
        final String packageName = "PACKAGE";
        final List<String> generatedRuleMappers = IntStream.range(0, 4).mapToObj(index -> packageName + "." +
                "subPack" + index + "." + predictionRuleMapper).collect(Collectors.toList());
        String retrieved = PredictionRuleMappersGenerator.getPredictionRuleMappersSource(packageName,
                                                                                        generatedRuleMappers);
        assertNotNull(retrieved);
        String expected = String.format("package %s;", packageName);
        assertTrue(retrieved.contains(expected));
        List<String> mod = generatedRuleMappers.stream().map(gen -> "new " + gen + "()").collect(Collectors.toList());
        expected = "Arrays.asList(" + String.join(", ", mod) + ");";
        assertTrue(retrieved.contains(expected));
    }
}
