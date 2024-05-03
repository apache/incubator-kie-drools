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
package org.kie.dmn.legacy.tests.core.v1_1.imports;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.legacy.tests.core.v1_1.BaseDMN1_1VariantTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DMNTestUtil.getAndAssertModelNoErrors;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class ImportsTest extends BaseDMN1_1VariantTest {

    public static final Logger LOG = LoggerFactory.getLogger(ImportsTest.class);

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")    
    void importDependenciesForDTInAContext(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Imported_Model.dmn",
                                                                                       this.getClass(),
                                                                                       "Import_BKM_and_have_a_Decision_Ctx_with_DT.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df36",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_c3e08836-7973-4e4d-af2b-d46b23725c13",
                                                   "Import BKM and have a Decision Ctx with DT");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("A Person", mapOf(entry("name", "John"), entry("age", 47)));

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("A Decision Ctx with DT").getResult()).isEqualTo("Respectfully, Hello John!");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void import2BKMs(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Do_say_hello_with_2_bkms.dmn",
                                                                                       this.getClass(),
                                                                                       "Saying_hello_2_bkms.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_16a48e7a-0687-4c2d-b402-42925084fa1a",
                                                        "Saying hello 2 bkms");
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_01a65215-7e0d-47ac-845a-a768f6abf7fe",
                                                   "Do say hello with 2 bkms");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("Person name", "John");

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Say hello decision").getResult()).isEqualTo("Hello, John");
        assertThat(evaluateAll.getDecisionResultByName("what about hello").getResult()).isEqualTo("Hello");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void import2BKMsInvoke(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Do_invoke_hello_with_2_bkms.dmn",
                                                                                       this.getClass(),
                                                                                       "Saying_hello_2_bkms.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_16a48e7a-0687-4c2d-b402-42925084fa1a",
                                                        "Saying hello 2 bkms");
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_eedf6ecc-f113-4333-ace0-79b783e313e5",
                                                   "Do invoke hello with 2 bkms");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = runtime.newContext();

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, emptyContext);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("invocation of hello").getResult()).isEqualTo("Hello, John");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void import2BKMsInvokeUsingInputData(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2746 DMN Invocation parameters resolution with imported function
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Do_invoke_hello_with_2_bkms_using_inputdata.dmn",
                                                                                       this.getClass(),
                                                                                       "Saying_hello_2_bkms.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_16a48e7a-0687-4c2d-b402-42925084fa1a",
                                                        "Saying hello 2 bkms");
        assertThat(importedModel).isNotNull();
        
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_eedf6ecc-f113-4333-ace0-79b783e313e5",
                                                   "Do invoke hello with 2 bkms");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("Person name", "Bob");

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("what about hello").getResult()).isEqualTo("Hello, Bob");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void import3Levels(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("L3_Do_say_hello.dmn",
                                                                                       this.getClass(),
                                                                                       "Do_say_hello_with_2_bkms.dmn",
                                                                                       "Saying_hello_2_bkms.dmn");

        if (LOG.isDebugEnabled()) {
            runtime.addListener(DMNRuntimeUtil.createListener());
        }

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_16a48e7a-0687-4c2d-b402-42925084fa1a",
                                                        "Saying hello 2 bkms");
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_01a65215-7e0d-47ac-845a-a768f6abf7fe",
                                                   "Do say hello with 2 bkms");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNModel dmnModelL3 = runtime.getModel("http://www.trisotech.com/dmn/definitions/_820c548c-377d-463e-a62b-bb95ddc4758c",
                                                     "L3 Do say hello");
        assertThat(dmnModelL3).isNotNull();
        assertThat(dmnModelL3.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModelL3.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("Another Name", "Bob");
        context.set("L2import", mapOf(entry("Person name", "John")));

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModelL3, context);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("L3 decision").getResult()).isEqualTo("Hello, Bob");
        assertThat(evaluateAll.getDecisionResultByName("L3 view on M2").getResult()).isEqualTo("Hello, John");
        assertThat(evaluateAll.getDecisionResultByName("L3 what about hello").getResult()).isEqualTo("Hello");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importHardcodedDecisions(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Spell_Greeting.dmn",
                                                                                       this.getClass(),
                                                                                       "Import_Spell_Greeting.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_88f4fc88-1eb2-4188-a721-5720cf5565ce",
                                                        "Spell Greeting");
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(importedModel.getMessages())).isFalse();

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_d67f19e9-7835-4cad-9c80-16b8423cc392",
                                                   "Import Spell Greeting");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = runtime.newContext();
        context.set("Person Name", "John");

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Say the Greeting to Person").getResult()).isEqualTo("Hello, John");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importTransitiveBaseModel(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn",
                                                                                       "ModelB2.dmn",
                                                                                       "ModelC.dmn");
        getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9", "Say hello 1ID1D");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importTransitiveEvaluate2Layers(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn",
                                                                                       "ModelB2.dmn",
                                                                                       "ModelC.dmn");
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c", "Model B");

        final DMNContext context = runtime.newContext();
        context.set("modelA", mapOf(entry("Person name", "John")));

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        assertThat(evaluateAll.getDecisionResultByName("Evaluating Say Hello").getResult()).isEqualTo("Evaluating Say Hello to: Hello, John");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importTransitiveEvaluate3Layers(VariantTestConf conf) {
        testConfig = conf;
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn",
                                                                                       "ModelB2.dmn",
                                                                                       "ModelC.dmn");
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_10435dcd-8774-4575-a338-49dd554a0928", "Model C");

        final DMNContext context = runtime.newContext();
        context.set("Model B", mapOf(entry("modelA", mapOf(entry("Person name", "B.A.John")))));
        context.set("Model B2", mapOf(entry("modelA", mapOf(entry("Person name", "B2.A.John2")))));

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        assertThat(evaluateAll.getDecisionResultByName("Model C Decision based on Bs").getResult()).isEqualTo("B: Evaluating Say Hello to: Hello, B.A.John; B2:Evaluating Say Hello to: Hello, B2.A.John2");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importingID(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-2944 DMN decision logic referencing DMN<import> InputData
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "Importing_ID.dmn");
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_24bac498-2a5a-403d-8b44-d407628784c4", "Importing ID");

        final DMNContext context = runtime.newContext();
        context.set("my import hello", mapOf(entry("Person name", "DROOLS-2944")));

        final DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        assertThat(evaluateAll.getDecisionResultByName("Hello decision using imported InputData").getResult()).isEqualTo("Hello, DROOLS-2944");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void allowDMNAPItoEvaluateDirectDependencyImportedDecisions(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3012 Allow DMN API to evaluate direct-dependency imported Decisions
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn");
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c", "Model B");
        
        testAllowDMNAPItoEvaluateDirectDependencyImportedDecisions_evaluateResultsAndCheck(runtime,
                                                                                           context -> runtime.evaluateByName(dmnModel,
                                                                                                                             context,
                                                                                                                             "modelA.Greet the Person",
                                                                                                                             "Evaluating Say Hello"));
        testAllowDMNAPItoEvaluateDirectDependencyImportedDecisions_evaluateResultsAndCheck(runtime,
                                                                                           context -> runtime.evaluateById(dmnModel,
                                                                                                                           context,
                                                                                                                           "http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9#_f7fdaec4-d669-4797-b3b4-12b860de2eb5",
                                                                                                                           "_96df766e-23e1-4aa6-9d5d-545fbe2f1e23"));
    }

    private void testAllowDMNAPItoEvaluateDirectDependencyImportedDecisions_evaluateResultsAndCheck(final DMNRuntime runtime, final Function<DMNContext, DMNResult> fn) {
        final DMNContext context = runtime.newContext();
        context.set("modelA", mapOf(entry("Person name", "John")));

        final DMNResult evaluateAll = fn.apply(context);
        LOG.debug("{}", evaluateAll);
        LOG.debug("{}", evaluateAll.getDecisionResults());
        assertThat(evaluateAll.hasErrors()).as(DMNRuntimeUtil.formatMessages(evaluateAll.getMessages())).isFalse();

        assertThat(evaluateAll.getDecisionResultByName("Evaluating Say Hello").getResult()).isEqualTo("Evaluating Say Hello to: Hello, John");
        assertThat(evaluateAll.getDecisionResultByName("modelA.Greet the Person").getResult()).isEqualTo("Hello, John");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void retrieveDecisionByIDName(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3026
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn");
        final DMNModel dmnModel = getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c", "Model B");

        assertThat(dmnModel.getDecisionById("_96df766e-23e1-4aa6-9d5d-545fbe2f1e23").getName()).isEqualTo("Evaluating Say Hello");
        assertThat(dmnModel.getDecisionById("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9#_f7fdaec4-d669-4797-b3b4-12b860de2eb5").getName()).isEqualTo("Greet the Person"); // this is an imported Decision
        assertThat(dmnModel.getDecisionById("_f7fdaec4-d669-4797-b3b4-12b860de2eb5")).isNull(); // this is an imported Decision

        assertThat(dmnModel.getDecisionByName("Evaluating Say Hello").getId()).isEqualTo("_96df766e-23e1-4aa6-9d5d-545fbe2f1e23");
        assertThat(dmnModel.getDecisionByName("Greet the Person")).isNull(); // this is an imported Decision
        assertThat(dmnModel.getDecisionByName("modelA.Greet the Person").getId()).isEqualTo("_f7fdaec4-d669-4797-b3b4-12b860de2eb5"); // this is an imported Decision

        assertThat(dmnModel.getInputById("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9#_4f6c136c-8512-4d71-8bbf-7c9eb6e74063").getName()).isEqualTo("Person name"); // this is an imported InputData node.
        assertThat(dmnModel.getInputById("_4f6c136c-8512-4d71-8bbf-7c9eb6e74063")).isNull(); // this is an imported InputData node.

        assertThat(dmnModel.getInputByName("Person name")).isNull(); // this is an imported InputData node.
        assertThat(dmnModel.getInputByName("modelA.Person name").getId()).isEqualTo("_4f6c136c-8512-4d71-8bbf-7c9eb6e74063"); // this is an imported InputData node.
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void importChain(VariantTestConf conf) {
        testConfig = conf;
        // DROOLS-3045 DMN model API to display namespace transitive import dependencies
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("Sayhello1ID1D.dmn",
                                                                                       this.getClass(),
                                                                                       "ModelB.dmn",
                                                                                       "ModelB2.dmn",
                                                                                       "ModelC.dmn");

        final DMNModelImpl modelA = (DMNModelImpl) getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9", "Say hello 1ID1D");
        assertThat(modelA.getImportChainAliases()).hasSize(0);

        final DMNModelImpl modelB = (DMNModelImpl) getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c", "Model B");
        assertThat(modelB.getImportChainAliases()).hasSize(1);
        assertThat(modelB.getImportChainAliases()).containsKey("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9");
        assertThat(modelB.getImportChainAliases().get("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9")).contains(Collections.singletonList("modelA"));

        final DMNModelImpl modelC = (DMNModelImpl) getAndAssertModelNoErrors(runtime, "http://www.trisotech.com/dmn/definitions/_10435dcd-8774-4575-a338-49dd554a0928", "Model C");
        assertThat(modelC.getImportChainAliases()).hasSize(3);
        
        assertThat(modelC.getImportChainAliases()).containsKey("http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c");
        assertThat(modelC.getImportChainAliases().get("http://www.trisotech.com/dmn/definitions/_2a1d771a-a899-4fef-abd6-fc894332337c")).contains(Collections.singletonList("Model B"));
        
        assertThat(modelC.getImportChainAliases()).containsKey("http://www.trisotech.com/definitions/_9d46ece4-a96c-4cb0-abc0-0ca121ac3768");
        assertThat(modelC.getImportChainAliases().get("http://www.trisotech.com/definitions/_9d46ece4-a96c-4cb0-abc0-0ca121ac3768")).contains(Collections.singletonList("Model B2"));

        assertThat(modelC.getImportChainAliases()).containsKey("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9");
        assertThat(modelC.getImportChainAliases().get("http://www.trisotech.com/dmn/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9")).contains(Arrays.asList("Model B2", "modelA"),
                                                                               Arrays.asList("Model B", "modelA"));
    }

}

