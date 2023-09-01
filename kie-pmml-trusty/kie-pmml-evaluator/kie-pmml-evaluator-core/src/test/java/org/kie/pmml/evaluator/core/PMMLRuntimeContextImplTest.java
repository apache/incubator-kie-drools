package org.kie.pmml.evaluator.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PMML_SUFFIX;

class PMMLRuntimeContextImplTest {

    private static final String fileNameNoSuffix = "LinearRegressionSample";
    private static final String fileName = fileNameNoSuffix + PMML_SUFFIX;

    private KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader;

    @BeforeEach
    public void init() {
        memoryCompilerClassLoader =
                new KieMemoryCompiler.MemoryCompilerClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void getName() {
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileName,
                                                                      memoryCompilerClassLoader);
        assertThat(retrieved.getName()).startsWith("Context_");
    }

    @Test
    void getFileName() {
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileName,
                                                                      memoryCompilerClassLoader);
        assertThat(retrieved.getFileName()).isEqualTo(fileName);
    }

    @Test
    void getFileNameFromNoSuffix() {
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileNameNoSuffix,
                                                                      memoryCompilerClassLoader);
        assertThat(retrieved.getFileName()).isEqualTo(fileName);
    }

    @Test
    void getFileNameNoSuffix() {
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(new PMMLRequestData(), fileName,
                                                                      memoryCompilerClassLoader);
        assertThat(retrieved.getFileNameNoSuffix()).isEqualTo(fileNameNoSuffix);
    }

    @Test
    void getRequestData() {
        PMMLRequestData requestData = new PMMLRequestData();
        PMMLRuntimeContextImpl retrieved = new PMMLRuntimeContextImpl(requestData, fileName, memoryCompilerClassLoader);
        assertThat(retrieved.getRequestData()).isEqualTo(requestData);
    }

    @Test
    void getFixedProbabilityMap() {
        double initialTotalProbability = 0.99;
        AtomicReference<Double> totalReference = new AtomicReference<>(initialTotalProbability);
        Random rand = new Random();
        List<Double> doubles = IntStream.range(0, 3).mapToDouble(value -> {
                    double currentTotal = totalReference.get();
                    int nextInt = Math.abs(rand.nextInt((int) (currentTotal * 100)));
                    double toReturn = (double) nextInt / 100;
                    totalReference.set(currentTotal - toReturn);
                    return toReturn;
                }).boxed().sorted((f1, f2) -> Double.compare(f2, f1))
                .collect(Collectors.toList());
        LinkedHashMap<String, Double> probabilityResultMap = new LinkedHashMap<>();
        int counter = 0;
        for (Double toPut : doubles) {
            probabilityResultMap.put("Element-" + counter, toPut);
            counter++;
        }
        double initialProbability = probabilityResultMap.values().stream().mapToDouble(x -> x).sum();
        assertThat(initialProbability).isLessThanOrEqualTo(initialTotalProbability);
        LinkedHashMap<String, Double> retrieved = PMMLRuntimeContextImpl.getFixedProbabilityMap(probabilityResultMap);
        double totalProbability = retrieved.values().stream().mapToDouble(x -> x).sum();
        assertThat(totalProbability).isCloseTo(1.0, Percentage.withPercentage(0.01));
    }
}