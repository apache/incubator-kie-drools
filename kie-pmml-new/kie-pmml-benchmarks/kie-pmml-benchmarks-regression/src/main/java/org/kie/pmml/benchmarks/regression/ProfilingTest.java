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

import java.util.List;
import java.util.Random;

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

import static org.kie.test.util.filesystem.FileUtils.getFile;

public class ProfilingTest {

    private static final String MODEL_NAME = "Sample for logistic regression";
    private static final String[] EMPLOYMENT = {"Consultant", "PSFederal", "PSLocal", "PSState", "Private", "SelfEmp", "Volunteer"};
    private static final String[] EDUCATION = {"Associate", "Bachelor", "College", "Doctorate", "HSgrad", "Master", "Preschool", "Professional", "Vocational", "Yr10", "Yr11", "Yr12", "Yr1t4", "Yr5t6", "Yr7t8", "Yr9"};
    private static final String[] MARITAL = {"Absent", "Divorced", "Married", "Married-spouse-absent", "Unmarried", "Widowed"};
    private static final String[] OCCUPATION = {"Cleaner", "Clerical", "Executive", "Farming", "Home", "Machinist", "Military", "Professional", "Protective", "Repair", "Sales", "Service", "Support", "Transport"};
    private static final String[] GENDER = {"Female", "Male"};

    public static void main(String[] args) {
        String modelName = "Sample for logistic regression";
        String fileName = "CategoricalRegressionSample.pmml";
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(getFile(fileName)).setResourceType(ResourceType.PMML));
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
        String releaseId = relId.toExternalForm();
        Results res = kieBuilder.getResults();
        KieBase kbase = ks.newKieContainer(relId).getKieBase();
        KieSession session = kbase.newKieSession();
        PMMLRuntime pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
        KiePMMLModel model = pmmlRuntime.getModel(modelName).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
//        List<PMMLContext> pmmlContexts = IntStream.range(0, 600000).mapToObj(i -> getPMMLContext()).collect(Collectors.toList());
//        evaluate(pmmlContexts, releaseId, pmmlRuntime, model);
        while (true) {
            evaluate(getPMMLContext(), releaseId, pmmlRuntime, model);
        }
    }

    private static void evaluate(final List<PMMLContext> pmmlContexts, final String releaseId, final PMMLRuntime pmmlRuntime, final KiePMMLModel model) {
        pmmlContexts.forEach(pmmlContext ->
                                     pmmlRuntime.evaluate(model, pmmlContext, releaseId));
    }

    private static void evaluate(final PMMLContext pmmlContext, final String releaseId, final PMMLRuntime pmmlRuntime, final KiePMMLModel model) {
        pmmlRuntime.evaluate(model, pmmlContext, releaseId);
    }

    private static PMMLContext getPMMLContext() {
        Random rnd = new Random();
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", MODEL_NAME);
        pmmlRequestData.addRequestParam("Age", rnd.nextInt(65));
        pmmlRequestData.addRequestParam("Employment", getRandomValue(EMPLOYMENT, rnd));
        pmmlRequestData.addRequestParam("Education", getRandomValue(EDUCATION, rnd));
        pmmlRequestData.addRequestParam("Marital", getRandomValue(MARITAL, rnd));
        pmmlRequestData.addRequestParam("Occupation", getRandomValue(OCCUPATION, rnd));
        pmmlRequestData.addRequestParam("Income", rnd.nextDouble());
        pmmlRequestData.addRequestParam("Gender", getRandomValue(GENDER, rnd));
        pmmlRequestData.addRequestParam("Hours", rnd.nextInt(400));
        return new PMMLContextImpl(pmmlRequestData);
    }

    private static String getRandomValue(String[] source, Random rnd) {
        return source[rnd.nextInt(source.length)];
    }
}
