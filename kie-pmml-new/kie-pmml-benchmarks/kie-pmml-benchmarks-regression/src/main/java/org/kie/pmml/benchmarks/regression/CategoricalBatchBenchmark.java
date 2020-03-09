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

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;


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
//
//    private static List<PMMLContext> readCSV(File csvFile) {
//        try (Stream<String> lines = Files.lines(csvFile.toPath())) {
//            return lines.map(CategoricalBatchBenchmark::readLine).filter(Objects::nonNull).collect(Collectors.toList());
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
//
////    @Setup
////    public void setupModel() {
////        System.out.println("setupModel");
////        modelName = "Sample for logistic regression";
////        fileName = "CategoricalRegressionSample.pmml";
////        String inputDataFile = "CategoricalRegressionSample.csv";
////        super.setupModel();
////        pmmlContexts = readCSV(getFile(inputDataFile));
////    }
//
//    private static PMMLContext readLine(String line) {
//        if (line.startsWith("Age")) {
//            return null;
//        }
//        final String[] split = line.split(",");
//        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", modelName);
//        pmmlRequestData.addRequestParam("Age", Integer.valueOf(split[0]));
//        pmmlRequestData.addRequestParam("Employment", split[1]);
//        pmmlRequestData.addRequestParam("Education", split[2]);
//        pmmlRequestData.addRequestParam("Marital", split[3]);
//        pmmlRequestData.addRequestParam("Occupation", split[4]);
//        pmmlRequestData.addRequestParam("Income", Double.valueOf(split[5]));
//        pmmlRequestData.addRequestParam("Gender", split[6]);
//        pmmlRequestData.addRequestParam("Hours", Integer.valueOf(split[8]));
//        return new PMMLContextImpl(pmmlRequestData);
//    }
//
//    @Benchmark
//    public void evaluate(Blackhole blackhole, MyState myState) {
//        PMMLContext pmmlContext = myState.pmmlContexts.get(0);
//        blackhole.consume(myState.pmmlRuntime.evaluate(myState.model, pmmlContext, myState.releaseId));
//        /*myState.pmmlContexts.forEach(pmmlContext -> blackhole.consume(myState.pmmlRuntime.evaluate(myState.model, pmmlContext, myState.releaseId)));*/
//    }
//
//    @State(Scope.Benchmark)
//    public static class MyState {
//
//        private PMMLRuntime pmmlRuntime;
//        private KiePMMLModel model;
//        private List<PMMLContext> pmmlContexts;
//        private String releaseId;
//
//        public MyState() {
//            KieServices ks = KieServices.Factory.get();
//            KieFileSystem kfs = ks.newKieFileSystem();
//            kfs.write(KieServices.get().getResources().newClassPathResource(fileName).setResourceType(ResourceType.PMML));
//            final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
//            final ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
//            releaseId = relId.toExternalForm();
//            Results res = kieBuilder.getResults();
//            KieBase kbase = ks.newKieContainer(relId).getKieBase();
//            KieSession session = kbase.newKieSession();
//            pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
//            model = pmmlRuntime.getModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
//            pmmlContexts = readCSV(getFile(inputDataFile));
//        }
//    }
}
