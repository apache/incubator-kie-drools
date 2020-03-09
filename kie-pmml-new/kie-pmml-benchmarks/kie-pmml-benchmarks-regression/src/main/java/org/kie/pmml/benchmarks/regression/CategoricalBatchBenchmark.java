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

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import static org.kie.test.util.filesystem.FileUtils.getFile;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Thread)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(jvmArgs = {"-Xms8172m", "-Xmx8172m"}, value = 5)
public class CategoricalBatchBenchmark {

    private static final String modelName = "Sample for logistic regression";
    private static final String fileName = "CategoricalRegressionSample.pmml";
    private static final String inputDataFile = "CategoricalRegressionSample.csv";

    private static List<PMMLContext> readCSV(File csvFile) {
        try (Stream<String> lines = Files.lines(csvFile.toPath())) {
            return lines.map(CategoricalBatchBenchmark::readLine).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

//    @Setup
//    public void setupModel() {
//        System.out.println("setupModel");
//        modelName = "Sample for logistic regression";
//        fileName = "CategoricalRegressionSample.pmml";
//        String inputDataFile = "CategoricalRegressionSample.csv";
//        super.setupModel();
//        pmmlContexts = readCSV(getFile(inputDataFile));
//    }

    private static PMMLContext readLine(String line) {
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

    @Benchmark
    public void evaluate(Blackhole blackhole, MyState myState) {
        PMMLContext pmmlContext = myState.pmmlContexts.get(0);
        blackhole.consume(myState.pmmlRuntime.evaluate(myState.model, pmmlContext, myState.releaseId));
        /*myState.pmmlContexts.forEach(pmmlContext -> blackhole.consume(myState.pmmlRuntime.evaluate(myState.model, pmmlContext, myState.releaseId)));*/
    }

    @State(Scope.Benchmark)
    public static class MyState {

        private PMMLRuntime pmmlRuntime;
        private KiePMMLModel model;
        private List<PMMLContext> pmmlContexts;
        private String releaseId;

        public MyState() {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem();
            kfs.write(ResourceFactory.newFileResource(getFile(fileName)).setResourceType(ResourceType.PMML));
            final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
            final ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
            releaseId = relId.toExternalForm();
            Results res = kieBuilder.getResults();
            KieBase kbase = ks.newKieContainer(relId).getKieBase();
            KieSession session = kbase.newKieSession();
            pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
            model = pmmlRuntime.getModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
            pmmlContexts = readCSV(getFile(inputDataFile));
        }
    }
}
