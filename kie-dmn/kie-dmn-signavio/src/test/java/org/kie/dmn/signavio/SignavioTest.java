/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.signavio;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SignavioTest {
    public static final Logger LOG = LoggerFactory.getLogger(SignavioTest.class);
    
    @Test
    public void test() {
        DMNRuntime runtime = createRuntime("Test_Signavio_multiple.dmn");
        
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("persons", Arrays.asList(new String[]{"p1", "p2"}));
        
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
        
        assertThat( (List<?>) evaluateAll.getContext().get( "Greeting for each Person in Persons" ), contains( "Hello p1", "Hello p2" ) );
    }
    
    @Test
    public void test_unmarshall() {
        DMNRuntime runtime = createRuntime("Test_Signavio_multiple.dmn");
        DMNModel model0 = runtime.getModels().get(0);
        Definitions definitions = model0.getDefinitions();
        DRGElement decision = definitions.getDrgElement().stream().filter(e -> e.getName().equals("greetingForEachPersonInPersons")).findFirst().orElseThrow(IllegalStateException::new);
        Object extElement = decision.getExtensionElements().getAny().get(0);
        assertThat(extElement, is(instanceOf(MultiInstanceDecisionLogic.class)));
        MultiInstanceDecisionLogic mid = (MultiInstanceDecisionLogic) extElement;
        LOG.info("{}", mid);
        assertThat(mid.getIterationExpression(), is("persons"));
        assertThat(mid.getIteratorShapeId(), is("id-707bbdf74438414623ac5d7067805b38"));
        assertThat(mid.getAggregationFunction(), is("COLLECT"));
        assertThat(mid.getTopLevelDecisionId(), is("id-7a23e2f201e3e0db3c991313cff5cd2b"));
    }

    @Test
    public void testUsingSignavioFunctions() {
        DMNRuntime runtime = createRuntime("Using_Signavio_functions.dmn");

        List<DMNModel> models = runtime.getModels();

        DMNContext context = runtime.newContext();

        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
        LOG.info("{}", evaluateAll.getContext());
        evaluateAll.getMessages().forEach(System.out::println);

        assertEquals(true, evaluateAll.getContext().get("myContext"));
    }

    /**
     * Check the custom Signavio functions work in the LiteralExpression too
     */
    @Test
    public void testUsingSignavioFunctionsInLiteralExpression() {
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

        assertFalse(evaluateAll.getMessages().toString(), evaluateAll.hasErrors());

        assertEquals(startsWithAnA, evaluateAll.getContext().get("startsWithAnA"));
    }

    @Test
    public void testSurveyMIDSUM() {
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

        assertThat(evaluateAll.getDecisionResultByName("iterating").getResult(), is(iterating));
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
        assertTrue(results.getMessages(Level.WARNING).size() == 0);

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        return runtime;
    }

    @Test
    public void testSurveyMIDMIN() {
        DMNRuntime runtime = createRuntime("survey MID MIN.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2, 3), new BigDecimal(1));
    }

    @Test
    public void testSurveyMIDMAX() {
        DMNRuntime runtime = createRuntime("survey MID MAX.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2, 3), new BigDecimal(3));
    }

    @Test
    public void testSurveyMIDCOUNT() {
        DMNRuntime runtime = createRuntime("survey MID COUNT.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 1, 1), new BigDecimal(3));// the COUNT in MID is list size, checked on Simulator.
    }

    @Test
    public void testSurveyMIDALLTRUE() {
        DMNRuntime runtime = createRuntime("survey MID ALLTRUE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2), true);
        checkSurveryMID(runtime, Arrays.asList(-1, 2), false);
    }

    @Test
    public void testSurveyMIDANYTRUE() {
        DMNRuntime runtime = createRuntime("survey MID ANYTRUE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, -2), true);
        checkSurveryMID(runtime, Arrays.asList(-1, -2), false);
    }

    @Test
    public void testSurveyMIDALLFALSE() {
        DMNRuntime runtime = createRuntime("survey MID ALLFALSE.dmn");
        checkSurveryMID(runtime, Arrays.asList(1, 2), false);
        checkSurveryMID(runtime, Arrays.asList(-1, 2), false);
        checkSurveryMID(runtime, Arrays.asList(1, -2), false);
        checkSurveryMID(runtime, Arrays.asList(-1, -2), true);
    }
    
    @Test
    public void testZipFunctions() {
        DMNRuntime runtime = createRuntime("Test_SignavioZipFunctions.dmn");
        checkBothFunctionsAreWorking(runtime);
    }
    
    
    @Test
    @SuppressWarnings("unchecked")
    public void testMidTakesCareOfRequirements() {
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
        assertThat(result, iterableWithSize(6));
        assertThat(result, everyItem(notNullValue()));
    }
    
    
    @Test
    public void testSignavioConcatFunction() {
        DMNRuntime runtime = createRuntime("Signavio_Concat.dmn");
        
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("listOfNames", Arrays.asList("John", "Jane", "Doe"));
        
        DMNModel model0 = models.get(0);
        LOG.info("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        LOG.info("{}", evaluateAll);
    
        assertEquals("JohnJaneDoe", evaluateAll.getDecisionResultByName("concatNames").getResult());
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
        
        assertThat((List<?>) evaluateAll.getDecisionResultByName("zipvararg").getResult(), iterableWithSize(2));
        assertThat((List<?>) evaluateAll.getDecisionResultByName("zipsinglelist").getResult(), iterableWithSize(2));
    }
}
