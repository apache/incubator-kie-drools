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

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieSession;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntimeInternal;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30000)
@Measurement(iterations = 5000)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public abstract class AbstractRegressionBenchmark {

    protected String modelName;
    protected String fileName;
    protected PMMLContext pmmlContext;
    private PMMLRuntimeInternal pmmlRuntime;
    private KiePMMLModel model;

    protected void setupModel() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(KieServices.get().getResources().newClassPathResource(fileName).setResourceType(ResourceType.PMML));
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
        Results res = kieBuilder.getResults();
        KieBase kbase = ks.newKieContainer(relId).getKieBase();
        KieSession session = kbase.newKieSession();
        pmmlRuntime = session.getKieRuntime(PMMLRuntimeInternal.class);
        model = pmmlRuntime.getKiePMMLModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
    }

    protected PMML4Result evaluate() {
        return pmmlRuntime.evaluate(model.getName(), pmmlContext);
    }
}
