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

import java.util.concurrent.TimeUnit;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(jvmArgs = {"-Xms8172m", "-Xmx8172m"}, value = 5)
public class CategoricalBenchmark extends AbstractRegressionBenchmark {

    @Setup
    public void setupModel() {
        modelName = "Sample for logistic regression";
        fileName = "CategoricalRegressionSample.pmml";
        super.setupModel();
    }

    @Setup(Level.Iteration)
    public void setupInputData() {
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", modelName);
        pmmlRequestData.addRequestParam("age", 22);
        pmmlRequestData.addRequestParam("work", 3);
        pmmlRequestData.addRequestParam("sex", "0");
        pmmlRequestData.addRequestParam("minority", "1");
        pmmlContext = new PMMLContextImpl(pmmlRequestData);
    }

    @Benchmark
    public PMML4Result evaluate() {
        return super.evaluate();
    }
}
