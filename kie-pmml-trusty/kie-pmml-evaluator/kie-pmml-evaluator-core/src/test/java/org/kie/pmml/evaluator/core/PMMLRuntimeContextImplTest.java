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
package org.kie.pmml.evaluator.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.apache.commons.math3.util.Precision;
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
            double remainingProbability = totalReference.get();
            double toReturn = invalidBound(remainingProbability) ? 0.00 :
                    Precision.round(rand.nextDouble(remainingProbability), 2);
            totalReference.set(Precision.round((remainingProbability - toReturn), 2));
            return toReturn;
        }).boxed().sorted((f1, f2) -> Double.compare(f2, f1)).toList();
        LinkedHashMap<String, Double> probabilityResultMap = new LinkedHashMap<>();
        int counter = 0;
        for (Double toPut : doubles) {
            probabilityResultMap.put("Element-" + counter, toPut);
            counter++;
        }
        double initialProbability = Precision.round(probabilityResultMap.values().stream().mapToDouble(x -> x).sum(),
                                                    2);
        assertThat(initialProbability).isLessThanOrEqualTo(initialTotalProbability);
        LinkedHashMap<String, Double> retrieved = PMMLRuntimeContextImpl.getFixedProbabilityMap(probabilityResultMap);
        double totalProbability = retrieved.values().stream().mapToDouble(x -> x).sum();
        assertThat(totalProbability).isCloseTo(1.0, Percentage.withPercentage(0.01));
    }

    private static boolean invalidBound(double bound) {
        return (!(0.0 < bound && bound < Double.POSITIVE_INFINITY));
    }
}