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
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
@Warmup(iterations = 2)
@Measurement(iterations = 5, time = 30)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 5)
public class CategoricalBatchBenchmark extends AbstractRegressionBenchmark {

    private static final Logger logger = LoggerFactory.getLogger(CategoricalBatchBenchmark.class);

    private static final String MODEL_NAME = "Sample for logistic regression";
    private static final String[] LINES = {
            "38,Private,College,Unmarried,Service,81838,Female,FALSE,72,0",
            "30,Consultant,HSgrad,Divorced,Repair,9608.48,Male,FALSE,40,0",
            "65,SelfEmp,College,Married,Sales,32963.39,Male,FALSE,40,0",
            "40,PSLocal,Vocational,Divorced,Executive,182165.08,Female,FALSE,40,0",
            "41,PSState,Bachelor,Divorced,Executive,70603.7,Male,FALSE,40,0",
            "49,PSFederal,College,Married,Support,15345.33,Male,FALSE,40,1",
            "62,Volunteer,Associate,Married,Farming,51230.5,Male,FALSE,50,0}"
    };

    @Param({"0", "1", "2", "3", "4", "5", "6"})
    int index;

    private static PMMLContext readCSV(String line) {
        if (line.startsWith("Age")) {
            return null;
        }
        final String[] split = line.split(",");
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", MODEL_NAME);
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

    @Setup
    public void setupModel() throws Exception {
        logger.debug("setup model...");
        modelName = "Sample for logistic regression";
        fileName = "CategoricalRegressionSample.pmml";
        super.setupModel();
        logger.debug("setup pmmlContext...");
        pmmlContext = readCSV(LINES[index]);
    }

    @Benchmark
    public PMML4Result evaluate() {
        return super.evaluate();
    }
}
