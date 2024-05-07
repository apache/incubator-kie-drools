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
package org.kie.dmn.pmml;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.DrlProject;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.api.DMNFactory;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public abstract class DMNRuntimePMMLTest {

    public DMNRuntimePMMLTest() {
        super();
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimePMMLTest.class);

    private static final double COMPARISON_DELTA = 0.000001;

    @Test
    public void basic() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    @Test
    public void withInputTypes() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard_wInputType.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    @Test
    public void basicNoKieAssembler() {
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                                                 .setRelativeImportResolver((ns, n, uri) -> new InputStreamReader(DMNRuntimePMMLTest.class.getResourceAsStream(uri)))
                                                 .buildConfiguration()
                                                 .fromResources(Collections.singletonList(ResourceFactory.newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimePMMLTest.class)))
                                                 .getOrElseThrow(e -> new RuntimeException("Error compiling DMN model(s)", e));
        runDMNModelInvokingPMML(dmnRuntime);
    }

    static void runDMNModelInvokingPMML(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLScoreCard");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("my decision")).isEqualTo(new BigDecimal("41.345"));
        
        // additional import info.
        Map<String, DMNImportPMMLInfo> pmmlImportInfo = ((DMNModelImpl) dmnModel).getPmmlImportInfo();
        assertThat(pmmlImportInfo.keySet()).hasSize(1);
        DMNImportPMMLInfo p0 = pmmlImportInfo.values().iterator().next();
        assertThat(p0.getImportName()).isEqualTo("iris");
        assertThat(p0.getModels()).hasSize(1);
        DMNPMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName()).isEqualTo("Sample Score");
        assertThat(m0.getInputFields()).containsKey("age");
        assertThat(m0.getInputFields()).containsKey("occupation");
        assertThat(m0.getInputFields()).containsKey("residenceState");
        assertThat(m0.getInputFields()).containsKey("validLicense");
        assertThat(m0.getInputFields()).doesNotContainKey("overallScore");
        assertThat(m0.getInputFields()).doesNotContainKey("calculatedScore");

        assertThat(m0.getOutputFields()).containsKey("calculatedScore");
    }

    /**
     * test to use same building steps of BC/WB
     */
    @Test
    public void steppedCompilation() {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/org/acme/test_scorecard.pmml", ks.getResources().newClassPathResource("test_scorecard.pmml", DMNRuntimePMMLTest.class));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(DrlProject.class);
        assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).hasSize(0);

        kfs.write("src/main/resources/org/acme/KiePMMLScoreCard.dmn", ks.getResources().newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimePMMLTest.class));
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/acme/KiePMMLScoreCard.dmn").build();
        assertThat(addResults.getAddedMessages()).hasSize(0);
        assertThat(addResults.getRemovedMessages()).hasSize(0);

        KieRepository kr = ks.getRepository();
        KieContainer kieContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        runDMNModelInvokingPMML(dmnRuntime);
    }

    @Test
    public void multiOutputs() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLRegressionClax.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_regression_clax.pmml");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLRegressionClax");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("fld1", 1.0);
        dmnContext.set("fld2", 1.0);
        dmnContext.set("fld3", "x");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        final Map<String, Object> result = (Map<String, Object>) resultContext.get("my decision");
        assertThat((String) result.get("RegOut")).isEqualTo("catD");
        assertThat(((BigDecimal) result.get("RegProb")).doubleValue()).isCloseTo(0.8279559384018024, within(COMPARISON_DELTA));
        assertThat(((BigDecimal) result.get("RegProbA")).doubleValue()).isCloseTo(0.0022681396056233208, within(COMPARISON_DELTA));

        DMNType dmnFEELNumber = ((DMNModelImpl) dmnModel).getTypeRegistry().resolveType(dmnModel.getDefinitions().getURIFEEL(), BuiltInType.NUMBER.getName());
        DMNType dmnFEELString = ((DMNModelImpl) dmnModel).getTypeRegistry().resolveType(dmnModel.getDefinitions().getURIFEEL(), BuiltInType.STRING.getName());

        // additional import info.
        Map<String, DMNImportPMMLInfo> pmmlImportInfo = ((DMNModelImpl) dmnModel).getPmmlImportInfo();
        assertThat(pmmlImportInfo.keySet()).hasSize(1);
        DMNImportPMMLInfo p0 = pmmlImportInfo.values().iterator().next();
        assertThat(p0.getImportName()).isEqualTo("test_regression_clax");
        assertThat(p0.getModels()).hasSize(1);
        DMNPMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName()).isEqualTo("LinReg");

        Map<String, DMNType> inputFields = m0.getInputFields();
        SimpleTypeImpl fld1 = (SimpleTypeImpl)inputFields.get("fld1");
        assertThat(fld1.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(fld1.getFeelType()).isEqualTo(BuiltInType.NUMBER);
        assertThat(fld1.getBaseType()).isEqualTo(dmnFEELNumber);

        SimpleTypeImpl fld2 = (SimpleTypeImpl)inputFields.get("fld2");
        assertThat(fld2.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(fld2.getFeelType()).isEqualTo(BuiltInType.NUMBER);
        assertThat(fld2.getBaseType()).isEqualTo(dmnFEELNumber);

        SimpleTypeImpl fld3 = (SimpleTypeImpl)inputFields.get("fld3");
        assertThat(fld3.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(fld3.getFeelType()).isEqualTo(BuiltInType.STRING);
        assertThat(fld3.getBaseType()).isEqualTo(dmnFEELString);

        Map<String, DMNType> outputFields = m0.getOutputFields();
        CompositeTypeImpl output = (CompositeTypeImpl)outputFields.get("LinReg");
        assertThat(output.getNamespace()).isEqualTo("test_regression_clax");

        Map<String, DMNType> fields = output.getFields();
        SimpleTypeImpl regOut = (SimpleTypeImpl)fields.get("RegOut");

        assertThat(regOut.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(regOut.getFeelType()).isEqualTo(BuiltInType.STRING);
        assertThat(regOut.getBaseType()).isEqualTo(dmnFEELString);

        SimpleTypeImpl regProb = (SimpleTypeImpl)fields.get("RegProb");
        assertThat(regProb.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(regProb.getFeelType()).isEqualTo(BuiltInType.NUMBER);
        assertThat(regProb.getBaseType()).isEqualTo(dmnFEELNumber);

        SimpleTypeImpl regProbA = (SimpleTypeImpl)fields.get("RegProbA");
        assertThat(regProbA.getNamespace()).isEqualTo("test_regression_clax");
        assertThat(regProbA.getFeelType()).isEqualTo(BuiltInType.NUMBER);
        assertThat(regProbA.getBaseType()).isEqualTo(dmnFEELNumber);
    }
}
