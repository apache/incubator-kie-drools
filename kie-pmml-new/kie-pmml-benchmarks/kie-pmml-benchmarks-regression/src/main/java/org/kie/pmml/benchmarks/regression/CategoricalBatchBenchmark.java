/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.benchmarks.regression;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import static org.kie.test.util.filesystem.FileUtils.getFile;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 3000)
@Measurement(iterations = 5000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CategoricalBatchBenchmark extends AbstractRegressionBenchmark {

    private List<PMMLContext> pmmlContexts;

    @Setup
    public void setupModel() {
        modelName = "Sample for logistic regression";
        fileName = "CategoricalRegressionSample.pmml";
        String inputDataFile = "CategoricalRegressionSample.csv";
        super.setupModel();
        pmmlContexts = readCSV(getFile(inputDataFile));
    }

    @Benchmark
    public PMML4Result evaluate() {
        pmmlContexts.forEach(pmmlContext1 -> {
            pmmlContext = pmmlContext1;
            super.evaluate();
        });
        return new PMML4Result();
    }

    private List<PMMLContext> readCSV(File csvFile) {
        try (Stream<String> lines = Files.lines(csvFile.toPath())) {
            return lines.map(this::readLine).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private PMMLContext readLine(String line) {
        if (line.startsWith("Age")) {
            return null;
        }
        final String[] split = line.split(",");
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", modelName);
        pmmlRequestData.addRequestParam("Age", Integer.valueOf(split[0]));
        pmmlRequestData.addRequestParam("Employment", split[1]);
        pmmlRequestData.addRequestParam("Education", split[2]);
        pmmlRequestData.addRequestParam("Marital", split[3]);
        pmmlRequestData.addRequestParam("Occupation", split[4]);
        pmmlRequestData.addRequestParam("Income", Double.valueOf(split[5]));
        pmmlRequestData.addRequestParam("Gender", split[6]);
        pmmlRequestData.addRequestParam("Hours", Integer.valueOf(split[8]));
        return new PMMLContextImpl(pmmlRequestData);
    }
}
