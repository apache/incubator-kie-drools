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
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
@Warmup(iterations = 2)
@Measurement(iterations = 5, time = 30)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 5)
public class CategoricalBenchmark extends AbstractRegressionBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(CategoricalBenchmark.class);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CategoricalBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(500)
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void setupModel() throws Exception {
        logger.debug("setup model...");
        modelName = "Sample for logistic regression";
        fileName = "CategoricalRegressionSample.pmml";
        super.setupModel();
        logger.debug("setup pmmlContext...");
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
