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
package org.kie.pmml.models.drools.scorecard.evaluator;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.kie.pmml.models.drools.scorecard.compiler.executor.ScorecardModelImplementationProvider;
import org.kie.pmml.models.drools.scorecard.evaluator.implementations.HasKnowledgeBuilderMock;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PMMLScorecardModelEvaluatorTest {

    private static final String SOURCE_1 = "ScorecardSample.pmml";
    private static final Logger logger = LoggerFactory.getLogger(PMMLScorecardModelEvaluatorTest.class);
    private static final String modelName = "Sample Score";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("org", "test", "1.0.0");
    private static final ScorecardModelImplementationProvider provider = new ScorecardModelImplementationProvider();
    private static KieBase kieBase;
    private static KiePMMLScorecardModel kiePMMLModel;
    private static PMMLScorecardModelEvaluator evaluator;
    private final String AGE = "age";
    private final String OCCUPATION = "occupation";
    private final String RESIDENCESTATE = "residenceState";
    private final String VALIDLICENSE = "validLicense";
    private final String TARGET_FIELD = "overallScore";
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;
    private double expectedResult;

    public PMMLScorecardModelEvaluatorTest(double age, String occupation, String residenceState,
                                           boolean validLicense, double expectedResult) {
        this.age = age;
        this.occupation = occupation;
        this.residenceState = residenceState;
        this.validLicense = validLicense;
        this.expectedResult = expectedResult;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        evaluator = new PMMLScorecardModelEvaluator();
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof Scorecard);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        kiePMMLModel = provider.getKiePMMLModel(PACKAGE_NAME,
                                                pmml.getDataDictionary(),
                                                pmml.getTransformationDictionary(),
                                                (Scorecard) pmml.getModels().get(0),
                                                new HasKnowledgeBuilderMock(knowledgeBuilder));
        kieBase = new KieHelper()
                .addContent(knowledgeBuilder.getPackageDescrs(kiePMMLModel.getKModulePackageName()).get(0))
                .setReleaseId(RELEASE_ID)
                .build( ExecutableModelProject.class );
        assertNotNull(kieBase);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4.0, "SKYDIVER", "AP", true, -8.655},
                {4.0, "SKYDIVER", "AP", false, -10.655},
                {4.0, "SKYDIVER", "KN", true, 11.345},
                {4.0, "SKYDIVER", "KN", false, 9.345},
                {4.0, "SKYDIVER", "TN", true, 6.345000000000001},
                {4.0, "SKYDIVER", "TN", false, 4.345000000000001},
                {4.0, "ASTRONAUT", "AP", true, -8.655},
                {4.0, "ASTRONAUT", "AP", false, -10.655},
                {4.0, "ASTRONAUT", "KN", true, 11.345},
                {4.0, "ASTRONAUT", "KN", false, 9.345},
                {4.0, "ASTRONAUT", "TN", true, 6.345000000000001},
                {4.0, "ASTRONAUT", "TN", false, 4.345000000000001},
                {4.0, "PROGRAMMER", "AP", true, 6.345000000000001},
                {4.0, "PROGRAMMER", "AP", false, 4.345000000000001},
                {4.0, "PROGRAMMER", "KN", true, 26.345},
                {4.0, "PROGRAMMER", "KN", false, 24.345},
                {4.0, "PROGRAMMER", "TN", true, 21.345},
                {4.0, "PROGRAMMER", "TN", false, 19.345},
                {4.0, "TEACHER", "AP", true, 11.344999999999999},
                {4.0, "TEACHER", "AP", false, 9.344999999999999},
                {4.0, "TEACHER", "KN", true, 31.345},
                {4.0, "TEACHER", "KN", false, 29.345},
                {4.0, "TEACHER", "TN", true, 26.345},
                {4.0, "TEACHER", "TN", false, 24.345},
                {4.0, "INSTRUCTOR", "AP", true, 11.344999999999999},
                {4.0, "INSTRUCTOR", "AP", false, 9.344999999999999},
                {4.0, "INSTRUCTOR", "KN", true, 31.345},
                {4.0, "INSTRUCTOR", "KN", false, 29.345},
                {4.0, "INSTRUCTOR", "TN", true, 26.345},
                {4.0, "INSTRUCTOR", "TN", false, 24.345},
                {5.0, "SKYDIVER", "AP", true, -8.655},
                {5.0, "SKYDIVER", "AP", false, -10.655},
                {5.0, "SKYDIVER", "KN", true, 11.345},
                {5.0, "SKYDIVER", "KN", false, 9.345},
                {5.0, "SKYDIVER", "TN", true, 6.345000000000001},
                {5.0, "SKYDIVER", "TN", false, 4.345000000000001},
                {5.0, "ASTRONAUT", "AP", true, -8.655},
                {5.0, "ASTRONAUT", "AP", false, -10.655},
                {5.0, "ASTRONAUT", "KN", true, 11.345},
                {5.0, "ASTRONAUT", "KN", false, 9.345},
                {5.0, "ASTRONAUT", "TN", true, 6.345000000000001},
                {5.0, "ASTRONAUT", "TN", false, 4.345000000000001},
                {5.0, "PROGRAMMER", "AP", true, 6.345000000000001},
                {5.0, "PROGRAMMER", "AP", false, 4.345000000000001},
                {5.0, "PROGRAMMER", "KN", true, 26.345},
                {5.0, "PROGRAMMER", "KN", false, 24.345},
                {5.0, "PROGRAMMER", "TN", true, 21.345},
                {5.0, "PROGRAMMER", "TN", false, 19.345},
                {5.0, "TEACHER", "AP", true, 11.344999999999999},
                {5.0, "TEACHER", "AP", false, 9.344999999999999},
                {5.0, "TEACHER", "KN", true, 31.345},
                {5.0, "TEACHER", "KN", false, 29.345},
                {5.0, "TEACHER", "TN", true, 26.345},
                {5.0, "TEACHER", "TN", false, 24.345},
                {5.0, "INSTRUCTOR", "AP", true, 11.344999999999999},
                {5.0, "INSTRUCTOR", "AP", false, 9.344999999999999},
                {5.0, "INSTRUCTOR", "KN", true, 31.345},
                {5.0, "INSTRUCTOR", "KN", false, 29.345},
                {5.0, "INSTRUCTOR", "TN", true, 26.345},
                {5.0, "INSTRUCTOR", "TN", false, 24.345},
                {6.0, "SKYDIVER", "AP", true, 11.344999999999999},
                {6.0, "SKYDIVER", "AP", false, 9.344999999999999},
                {6.0, "SKYDIVER", "KN", true, 31.345},
                {6.0, "SKYDIVER", "KN", false, 29.345},
                {6.0, "SKYDIVER", "TN", true, 26.345},
                {6.0, "SKYDIVER", "TN", false, 24.345},
                {6.0, "ASTRONAUT", "AP", true, 11.344999999999999},
                {6.0, "ASTRONAUT", "AP", false, 9.344999999999999},
                {6.0, "ASTRONAUT", "KN", true, 31.345},
                {6.0, "ASTRONAUT", "KN", false, 29.345},
                {6.0, "ASTRONAUT", "TN", true, 26.345},
                {6.0, "ASTRONAUT", "TN", false, 24.345},
                {6.0, "PROGRAMMER", "AP", true, 26.345},
                {6.0, "PROGRAMMER", "AP", false, 24.345},
                {6.0, "PROGRAMMER", "KN", true, 46.345},
                {6.0, "PROGRAMMER", "KN", false, 44.345},
                {6.0, "PROGRAMMER", "TN", true, 41.345},
                {6.0, "PROGRAMMER", "TN", false, 39.345},
                {6.0, "TEACHER", "AP", true, 31.345},
                {6.0, "TEACHER", "AP", false, 29.345},
                {6.0, "TEACHER", "KN", true, 51.345},
                {6.0, "TEACHER", "KN", false, 49.345},
                {6.0, "TEACHER", "TN", true, 46.345},
                {6.0, "TEACHER", "TN", false, 44.345},
                {6.0, "INSTRUCTOR", "AP", true, 31.345},
                {6.0, "INSTRUCTOR", "AP", false, 29.345},
                {6.0, "INSTRUCTOR", "KN", true, 51.345},
                {6.0, "INSTRUCTOR", "KN", false, 49.345},
                {6.0, "INSTRUCTOR", "TN", true, 46.345},
                {6.0, "INSTRUCTOR", "TN", false, 44.345},
                {13.0, "SKYDIVER", "AP", true, 21.345},
                {13.0, "SKYDIVER", "AP", false, 19.345},
                {13.0, "SKYDIVER", "KN", true, 41.345},
                {13.0, "SKYDIVER", "KN", false, 39.345},
                {13.0, "SKYDIVER", "TN", true, 36.345},
                {13.0, "SKYDIVER", "TN", false, 34.345},
                {13.0, "ASTRONAUT", "AP", true, 21.345},
                {13.0, "ASTRONAUT", "AP", false, 19.345},
                {13.0, "ASTRONAUT", "KN", true, 41.345},
                {13.0, "ASTRONAUT", "KN", false, 39.345},
                {13.0, "ASTRONAUT", "TN", true, 36.345},
                {13.0, "ASTRONAUT", "TN", false, 34.345},
                {13.0, "PROGRAMMER", "AP", true, 36.345},
                {13.0, "PROGRAMMER", "AP", false, 34.345},
                {13.0, "PROGRAMMER", "KN", true, 56.345},
                {13.0, "PROGRAMMER", "KN", false, 54.345},
                {13.0, "PROGRAMMER", "TN", true, 51.345},
                {13.0, "PROGRAMMER", "TN", false, 49.345},
                {13.0, "TEACHER", "AP", true, 41.345},
                {13.0, "TEACHER", "AP", false, 39.345},
                {13.0, "TEACHER", "KN", true, 61.345},
                {13.0, "TEACHER", "KN", false, 59.345},
                {13.0, "TEACHER", "TN", true, 56.345},
                {13.0, "TEACHER", "TN", false, 54.345},
                {13.0, "INSTRUCTOR", "AP", true, 41.345},
                {13.0, "INSTRUCTOR", "AP", false, 39.345},
                {13.0, "INSTRUCTOR", "KN", true, 61.345},
                {13.0, "INSTRUCTOR", "KN", false, 59.345},
                {13.0, "INSTRUCTOR", "TN", true, 56.345},
                {13.0, "INSTRUCTOR", "TN", false, 54.345},
                {43.0, "SKYDIVER", "AP", true, 21.345},
                {43.0, "SKYDIVER", "AP", false, 19.345},
                {43.0, "SKYDIVER", "KN", true, 41.345},
                {43.0, "SKYDIVER", "KN", false, 39.345},
                {43.0, "SKYDIVER", "TN", true, 36.345},
                {43.0, "SKYDIVER", "TN", false, 34.345},
                {43.0, "ASTRONAUT", "AP", true, 21.345},
                {43.0, "ASTRONAUT", "AP", false, 19.345},
                {43.0, "ASTRONAUT", "KN", true, 41.345},
                {43.0, "ASTRONAUT", "KN", false, 39.345},
                {43.0, "ASTRONAUT", "TN", true, 36.345},
                {43.0, "ASTRONAUT", "TN", false, 34.345},
                {43.0, "PROGRAMMER", "AP", true, 36.345},
                {43.0, "PROGRAMMER", "AP", false, 34.345},
                {43.0, "PROGRAMMER", "KN", true, 56.345},
                {43.0, "PROGRAMMER", "KN", false, 54.345},
                {43.0, "PROGRAMMER", "TN", true, 51.345},
                {43.0, "PROGRAMMER", "TN", false, 49.345},
                {43.0, "TEACHER", "AP", true, 41.345},
                {43.0, "TEACHER", "AP", false, 39.345},
                {43.0, "TEACHER", "KN", true, 61.345},
                {43.0, "TEACHER", "KN", false, 59.345},
                {43.0, "TEACHER", "TN", true, 56.345},
                {43.0, "TEACHER", "TN", false, 54.345},
                {43.0, "INSTRUCTOR", "AP", true, 41.345},
                {43.0, "INSTRUCTOR", "AP", false, 39.345},
                {43.0, "INSTRUCTOR", "KN", true, 61.345},
                {43.0, "INSTRUCTOR", "KN", false, 59.345},
                {43.0, "INSTRUCTOR", "TN", true, 56.345},
                {43.0, "INSTRUCTOR", "TN", false, 54.345},
                {45.0, "SKYDIVER", "AP", true, 6.344999999999999},
                {45.0, "SKYDIVER", "AP", false, 4.344999999999999},
                {45.0, "SKYDIVER", "KN", true, 26.345},
                {45.0, "SKYDIVER", "KN", false, 24.345},
                {45.0, "SKYDIVER", "TN", true, 21.345},
                {45.0, "SKYDIVER", "TN", false, 19.345},
                {45.0, "ASTRONAUT", "AP", true, 6.344999999999999},
                {45.0, "ASTRONAUT", "AP", false, 4.344999999999999},
                {45.0, "ASTRONAUT", "KN", true, 26.345},
                {45.0, "ASTRONAUT", "KN", false, 24.345},
                {45.0, "ASTRONAUT", "TN", true, 21.345},
                {45.0, "ASTRONAUT", "TN", false, 19.345},
                {45.0, "PROGRAMMER", "AP", true, 21.345},
                {45.0, "PROGRAMMER", "AP", false, 19.345},
                {45.0, "PROGRAMMER", "KN", true, 41.345},
                {45.0, "PROGRAMMER", "KN", false, 39.345},
                {45.0, "PROGRAMMER", "TN", true, 36.345},
                {45.0, "PROGRAMMER", "TN", false, 34.345},
                {45.0, "TEACHER", "AP", true, 26.345},
                {45.0, "TEACHER", "AP", false, 24.345},
                {45.0, "TEACHER", "KN", true, 46.345},
                {45.0, "TEACHER", "KN", false, 44.345},
                {45.0, "TEACHER", "TN", true, 41.345},
                {45.0, "TEACHER", "TN", false, 39.345},
                {45.0, "INSTRUCTOR", "AP", true, 26.345},
                {45.0, "INSTRUCTOR", "AP", false, 24.345},
                {45.0, "INSTRUCTOR", "KN", true, 46.345},
                {45.0, "INSTRUCTOR", "KN", false, 44.345},
                {45.0, "INSTRUCTOR", "TN", true, 41.345},
                {45.0, "INSTRUCTOR", "TN", false, 39.345}
        });
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.SCORECARD_MODEL, evaluator.getPMMLModelType());
    }

    @Test
    public void testScorecardSample() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(AGE, age);
        inputData.put(OCCUPATION, occupation);
        inputData.put(RESIDENCESTATE, residenceState);
        inputData.put(VALIDLICENSE, validLicense);
        commonEvaluate(inputData);
    }

    private void commonEvaluate(Map<String, Object> inputData) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        commonEvaluate(pmmlContext);
    }

    private void commonEvaluate(PMMLContext pmmlContext) {
        PMML4Result retrieved = evaluator.evaluate(kieBase, kiePMMLModel, pmmlContext);
        assertNotNull(retrieved);
        logger.trace(retrieved.toString());
        assertEquals(TARGET_FIELD, retrieved.getResultObjectName());
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertNotNull(resultVariables);
        assertEquals(ResultCode.OK.getName(), retrieved.getResultCode());
        assertFalse(resultVariables.isEmpty());
        assertTrue(resultVariables.containsKey(TARGET_FIELD));
        assertEquals(expectedResult, resultVariables.get(TARGET_FIELD));
    }

    private PMMLRequestData getPMMLRequestData(String modelName, Map<String, Object> parameters) {
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }
}
