/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.pmml;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.pmml.DMNImportPMMLInfo;
import org.kie.dmn.core.pmml.DMNPMMLModelInfo;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.services.KieAssemblersImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class DMNRuntimePMMLTest {

    public DMNRuntimePMMLTest() {
        super();
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimePMMLTest.class);

    private static final double COMPARISON_DELTA = 0.000001;

    @Test
    public void testBasic() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    @Test
    public void testWithInputTypes() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard_wInputType.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    @Test
    public void testBasicNoKieAssembler() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                                                 .setRelativeImportResolver((ns, n, uri) -> new InputStreamReader(DMNRuntimePMMLTest.class.getResourceAsStream(uri)))
                                                 .buildConfiguration()
                                                 .fromResources(Arrays.asList(ResourceFactory.newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimePMMLTest.class)))
                                                 .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        runDMNModelInvokingPMML(dmnRuntime);
    }

    static void runDMNModelInvokingPMML(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLScoreCard");
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("my decision"), is(new BigDecimal("41.345")));
        
        // additional import info.
        Map<String, DMNImportPMMLInfo> pmmlImportInfo = ((DMNModelImpl) dmnModel).getPmmlImportInfo();
        assertThat(pmmlImportInfo.keySet(), hasSize(1));
        DMNImportPMMLInfo p0 = pmmlImportInfo.values().iterator().next();
        assertThat(p0.getImportName(), is("iris"));
        assertThat(p0.getModels(), hasSize(1));
        DMNPMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName(), is("Sample Score"));
        assertThat(m0.getInputFields(), hasEntry(is("age"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("occupation"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("residenceState"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("validLicense"), anything()));
        assertThat(m0.getInputFields(), not(hasEntry(is("overallScore"), anything())));
        assertThat(m0.getInputFields(), not(hasEntry(is("calculatedScore"), anything())));

        assertThat(m0.getOutputFields(), hasEntry(is("calculatedScore"), anything()));
    }

    /**
     * test to use same building steps of BC/WB
     */
    @Test
    public void testSteppedCompilation() {
        final KieAssemblersImpl assemblers = (KieAssemblersImpl) ServiceRegistry.getService(KieAssemblers.class);
        assemblers.accept(new DMNAssemblerService());

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/org/acme/test_scorecard.pmml", ks.getResources().newClassPathResource("test_scorecard.pmml", DMNRuntimePMMLTest.class));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(DrlProject.class);
        assertEquals(0, kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size());

        kfs.write("src/main/resources/org/acme/KiePMMLScoreCard.dmn", ks.getResources().newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimePMMLTest.class));
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/acme/KiePMMLScoreCard.dmn").build();
        assertEquals(0, addResults.getAddedMessages().size());
        assertEquals(0, addResults.getRemovedMessages().size());

        KieRepository kr = ks.getRepository();
        KieContainer kieContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        runDMNModelInvokingPMML(dmnRuntime);
    }

    @Test
    public void testMultiOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLRegressionClax.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_regression_clax.pmml");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLRegressionClax");
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("fld1", 1.0);
        dmnContext.set("fld2", 1.0);
        dmnContext.set("fld3", "x");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext resultContext = dmnResult.getContext();
        final Map<String, Object> result = (Map<String, Object>) resultContext.get("my decision");
        assertEquals("catD", (String)result.get("RegOut"));
        assertEquals(0.8279559384018024, ((BigDecimal)result.get("RegProb")).doubleValue(), COMPARISON_DELTA);
        assertEquals(0.0022681396056233208, ((BigDecimal)result.get("RegProbA")).doubleValue(), COMPARISON_DELTA);

        DMNType dmnFEELNumber = ((DMNModelImpl) dmnModel).getTypeRegistry().resolveType(dmnModel.getDefinitions().getURIFEEL(), BuiltInType.NUMBER.getName());
        DMNType dmnFEELString = ((DMNModelImpl) dmnModel).getTypeRegistry().resolveType(dmnModel.getDefinitions().getURIFEEL(), BuiltInType.STRING.getName());

        // additional import info.
        Map<String, DMNImportPMMLInfo> pmmlImportInfo = ((DMNModelImpl) dmnModel).getPmmlImportInfo();
        assertThat(pmmlImportInfo.keySet(), hasSize(1));
        DMNImportPMMLInfo p0 = pmmlImportInfo.values().iterator().next();
        assertThat(p0.getImportName(), is("test_regression_clax"));
        assertThat(p0.getModels(), hasSize(1));
        DMNPMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName(), is("LinReg"));

        Map<String, DMNType> inputFields = m0.getInputFields();
        SimpleTypeImpl fld1 = (SimpleTypeImpl)inputFields.get("fld1");
        assertEquals("test_regression_clax", fld1.getNamespace());
        assertEquals(BuiltInType.NUMBER, fld1.getFeelType());
        assertEquals(dmnFEELNumber, fld1.getBaseType());

        SimpleTypeImpl fld2 = (SimpleTypeImpl)inputFields.get("fld2");
        assertEquals("test_regression_clax", fld2.getNamespace());
        assertEquals(BuiltInType.NUMBER, fld2.getFeelType());
        assertEquals(dmnFEELNumber, fld2.getBaseType());

        SimpleTypeImpl fld3 = (SimpleTypeImpl)inputFields.get("fld3");
        assertEquals("test_regression_clax", fld3.getNamespace());
        assertEquals(BuiltInType.STRING, fld3.getFeelType());
        assertEquals(dmnFEELString, fld3.getBaseType());

        Map<String, DMNType> outputFields = m0.getOutputFields();
        CompositeTypeImpl output = (CompositeTypeImpl)outputFields.get("LinReg");
        assertEquals("test_regression_clax", output.getNamespace());

        Map<String, DMNType> fields = output.getFields();
        SimpleTypeImpl regOut = (SimpleTypeImpl)fields.get("RegOut");

        assertEquals("test_regression_clax", regOut.getNamespace());
        assertEquals(BuiltInType.STRING, regOut.getFeelType());
        assertEquals(dmnFEELString, regOut.getBaseType());

        SimpleTypeImpl regProb = (SimpleTypeImpl)fields.get("RegProb");
        assertEquals("test_regression_clax", regProb.getNamespace());
        assertEquals(BuiltInType.NUMBER, regProb.getFeelType());
        assertEquals(dmnFEELNumber, regProb.getBaseType());

        SimpleTypeImpl regProbA = (SimpleTypeImpl)fields.get("RegProbA");
        assertEquals("test_regression_clax", regProbA.getNamespace());
        assertEquals(BuiltInType.NUMBER, regProbA.getFeelType());
        assertEquals(dmnFEELNumber, regProbA.getBaseType());
    }
}
