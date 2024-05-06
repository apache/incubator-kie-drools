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
package org.kie.dmn.signavio;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class SignavioTest {
    public static final Logger LOG = LoggerFactory.getLogger(SignavioTest.class);

    @Test
    void test() {
        DMNRuntime runtime = createRuntime("Test_Signavio_multiple.dmn");
        
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("persons", Arrays.asList("p1", "p2"));
        
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
        
        assertThat((List<String>) evaluateAll.getContext().get("Greeting for each Person in Persons")).contains("Hello p1", "Hello p2");
    }

    @Test
    void unmarshall() {
        DMNRuntime runtime = createRuntime("Test_Signavio_multiple.dmn");
        DMNModel model0 = runtime.getModels().get(0);
        Definitions definitions = model0.getDefinitions();
        DRGElement decision = definitions.getDrgElement().stream().filter(e -> e.getName().equals("greetingForEachPersonInPersons")).findFirst().orElseThrow(IllegalStateException::new);
        Object extElement = decision.getExtensionElements().getAny().get(0);
        assertThat(extElement).isInstanceOf(MultiInstanceDecisionLogic.class);
        MultiInstanceDecisionLogic mid = (MultiInstanceDecisionLogic) extElement;
        LOG.info("{}", mid);
        assertThat(mid.getIterationExpression()).isEqualTo("persons");
        assertThat(mid.getIteratorShapeId()).isEqualTo("id-707bbdf74438414623ac5d7067805b38");
        assertThat(mid.getAggregationFunction()).isEqualTo("COLLECT");
        assertThat(mid.getTopLevelDecisionId()).isEqualTo("id-7a23e2f201e3e0db3c991313cff5cd2b");
    }

    @Test
    void usingSignavioFunctions() {
        DMNRuntime runtime = createRuntime("Using_Signavio_functions.dmn");

        List<DMNModel> models = runtime.getModels();

        DMNContext context = runtime.newContext();

        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
        LOG.info("{}", evaluateAll.getContext());
        evaluateAll.getMessages().forEach(System.out::println);

        assertThat(evaluateAll.getContext().get("myContext")).isEqualTo(true);
    }

    /**
     * Check the custom Signavio functions work in the LiteralExpression too
     */
    @Test
    void usingSignavioFunctionsInLiteralExpression() {
        DMNRuntime runtime = createRuntime("Starts_with_an_A.dmn");

        assertStartsWithAnA(runtime, "Abc", true);
        assertStartsWithAnA(runtime, "Xyz", false);
    }

    private void assertStartsWithAnA(final DMNRuntime runtime, final String testString, final boolean startsWithAnA) {
        DMNContext context = runtime.newContext();
        context.set("surname", testString);

        DMNModel model0 = runtime.getModels().get(0);
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        evaluateAll.getMessages().forEach(System.out::println);

        assertThat(evaluateAll.hasErrors()).as(evaluateAll.getMessages().toString()).isFalse();

        assertThat(evaluateAll.getContext().get("startsWithAnA")).isEqualTo(startsWithAnA);
    }

    @Test
    void surveyMIDSUM() {
        DMNRuntime runtime = createRuntime("survey MID SUM.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2, 3), new BigDecimal(6));
    }

    private void checkSurveryMID(DMNRuntime runtime, Object numbers, Object iterating) {
        List<DMNModel> models = runtime.getModels();

        DMNContext context = runtime.newContext();
        context.set("numbers", numbers);

        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);

        assertThat(evaluateAll.getDecisionResultByName("iterating").getResult()).isEqualTo(iterating);
    }

    private DMNRuntime createRuntime(String modelFileName) {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.signavio", "org.kie.dmn.signavio.KieDMNSignavioProfile");
        kfs.writeKModuleXML(kmm.toXML());
        kfs.write(ks.getResources().newClassPathResource(modelFileName, this.getClass()));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach(e -> LOG.warn("{}", e));
        assertThat(results.getMessages(Level.WARNING)).isEmpty();

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        return runtime;
    }

    @Test
    void surveyMIDMIN() {
        DMNRuntime runtime = createRuntime("survey MID MIN.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2, 3), new BigDecimal(1));
    }

    @Test
    void surveyMIDMAX() {
        DMNRuntime runtime = createRuntime("survey MID MAX.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2, 3), new BigDecimal(3));
    }

    @Test
    void surveyMIDCOUNT() {
        DMNRuntime runtime = createRuntime("survey MID COUNT.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 1, 1), new BigDecimal(3));// the COUNT in MID is list size, checked on Simulator.
    }

    @Test
    void surveyMIDALLTRUE() {
        DMNRuntime runtime = createRuntime("survey MID ALLTRUE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2), true);
        checkSurveryMID(runtime, Arrays.asList(-1, 2), false);
    }

    @Test
    void surveyMIDANYTRUE() {
        DMNRuntime runtime = createRuntime("survey MID ANYTRUE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, -2), true);
        checkSurveryMID(runtime, Arrays.asList(-1, -2), false);
    }

    @Test
    void surveyMIDALLFALSE() {
        DMNRuntime runtime = createRuntime("survey MID ALLFALSE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2), false);
        checkSurveryMID(runtime, Arrays.asList(-1, 2), false);
        checkSurveryMID(runtime, Arrays.asList(1, -2), false);
        checkSurveryMID(runtime, Arrays.asList(-1, -2), true);
    }

    @Test
    void zipFunctions() {
        DMNRuntime runtime = createRuntime("Test_SignavioZipFunctions.dmn");
        checkBothFunctionsAreWorking(runtime);
    }


    @Test
    @SuppressWarnings("unchecked")
    void midTakesCareOfRequirements() {
        DMNRuntime runtime = createRuntime("Test_SignavioMID.dmn");
    
        List<DMNModel> models = runtime.getModels();
    
        DMNContext context = runtime.newContext();
        context.set("numbers1", Arrays.asList(1,2));
        context.set("numbers2", Arrays.asList(2,3));
    
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
    
        List<Object> result = (List<Object>) evaluateAll.getDecisionResultByName("calculate").getResult();
        assertThat(result).hasSize(6);
        
        assertThat(result).doesNotContainNull();
    }


    @Test
    void signavioConcatFunction() {
        DMNRuntime runtime = createRuntime("Signavio_Concat.dmn");
        
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("listOfNames", Arrays.asList("John", "Jane", "Doe"));
        
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);

        assertThat(evaluateAll.getDecisionResultByName("concatNames").getResult()).isEqualTo("JohnJaneDoe");
    }
    
    
    private void checkBothFunctionsAreWorking(DMNRuntime runtime) {
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("names", Arrays.asList("John Doe", "Jane Doe"));
        context.set("ages", Arrays.asList(37, 35));
        
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
        
        assertThat((List<?>) evaluateAll.getDecisionResultByName("zipvararg").getResult()).hasSize(2);
        assertThat((List<?>) evaluateAll.getDecisionResultByName("zipsinglelist").getResult()).hasSize(2);
    }

    @Test
    void signavioIterateMultiinstanceWithComplexInputs() {
        DMNRuntime runtime = createRuntime("Iterate Complex List.dmn");
        
        DMNContext context = runtime.newContext();
        Map<String, Object> johnDoe = new HashMap<>();
        johnDoe.put("iD", "id-john");
        johnDoe.put("name", "John Doe");
        Map<String, Object> alice = new HashMap<>();
        alice.put("iD", "id-alice");
        alice.put("name", "Alice");
        context.set("customer", Collections.singletonMap("persons", Arrays.asList(johnDoe, alice)));
        
        DMNModel model0 = runtime.getModels().get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);

        assertThat(evaluateAll.getDecisionResultByName("extractNames").getResult()).isEqualTo(Arrays.asList("John Doe", "Alice"));
    }

    @Test
    void signavioIterateMultiinstanceMultipleDecisions() {
        DMNRuntime runtime = createRuntime("MID with multiple inside decisions.dmn");
        
        DMNContext context = runtime.newContext();
        context.set("names", Arrays.asList("John", "Alice"));
        
        DMNModel model0 = runtime.getModels().get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
    
        assertThat(evaluateAll.getDecisionResultByName("overallage").getResult()).isEqualTo(new BigDecimal("18"));
    }

    @Test
    void signavioIterateMultiinstanceMultipleDecisionsOutside() {
        DMNRuntime runtime = createRuntime("MID with outside requirement.dmn");
        
        DMNContext context = runtime.newContext();
        context.set("numbers", Arrays.asList(1,2,3));
        context.set("operand", "PLUS");
        
        DMNModel model0 = runtime.getModels().get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
    
        assertThat(evaluateAll.getDecisionResultByName("sumUp").getResult()).isEqualTo(new BigDecimal("6"));
    }
}
