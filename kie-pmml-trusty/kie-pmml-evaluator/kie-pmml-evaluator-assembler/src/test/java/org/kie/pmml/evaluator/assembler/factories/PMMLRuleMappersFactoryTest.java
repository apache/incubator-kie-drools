package org.kie.pmml.evaluator.assembler.factories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PMMLRuleMappersFactoryTest {

    @Test
    public void getPredictionRuleMapperSource() {
        final String pmmlRuleMapper = "PMMLRuleMapperImpl";
        final String packageName = "PACKAGE";
        final List<String> generatedRuleMappers = IntStream.range(0, 4).mapToObj(index -> packageName + "." +
                "subPack" + index + "." + pmmlRuleMapper).collect(Collectors.toList());
        String retrieved = PMMLRuleMappersFactory.getPMMLRuleMappersSource(packageName,
                                                                           generatedRuleMappers);
        assertNotNull(retrieved);
        String expected = String.format("package %s;", packageName);
        assertTrue(retrieved.contains(expected));
        List<String> mod = generatedRuleMappers.stream().map(gen -> "new " + gen + "()").collect(Collectors.toList());
        expected = "Arrays.asList(" + String.join(", ", mod) + ");";
        assertTrue(retrieved.contains(expected));
    }
}
