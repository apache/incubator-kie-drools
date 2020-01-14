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
package org.kie.pmml.pmml_4_2.predictive.models;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.ruleunit.executor.InternalRuleUnitExecutor;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;
import org.kie.pmml.pmml_4_2.model.ScoreCard;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecution;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecutionState;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MiningmodelTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;
    private static final String FILE_BASE = "org/kie/pmml/pmml_4_2/";
    private static final String source1 = FILE_BASE+"test_mining_model_simple.pmml";
    private static final String source2 = FILE_BASE+"test_mining_model_simple2.pmml";
    private static final String source3 = FILE_BASE+"filebased";
    private static final String source4 = FILE_BASE+"test_mining_model_selectall.pmml";
    private static final String source5 = FILE_BASE+"test_mining_model_modelchain.pmml";
    private static final String WEIGHTED_AVG = FILE_BASE+"test_mining_model_weighted_avg.pmml";
    private static final String SUMMED = FILE_BASE+"test_mining_model_summed.pmml";
    private static final String RESOURCES_TEST_ROOT = "src/test/resources/";

    @Test
    public void testSelectFirstSegmentFirst() {
        Resource res = ResourceFactory.newClassPathResource(source1);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleMine",res,null,true);
        PMMLRequestDataBuilder rdb = new PMMLRequestDataBuilder("1234", "SampleMine")
                .addParameter("fld1", 30.0, Double.class)
                .addParameter("fld2", 60.0, Double.class)
                .addParameter("fld3", "false", String.class)
                .addParameter("fld4", "optA", String.class);
        PMMLRequestData request = rdb.build();
        PMML4Result resultHolder = helper.submitRequest(request);
        Collection<?> objects = (( InternalRuleUnitExecutor )helper.getExecutor()).getSessionObjects();
        objects.forEach(o -> {System.out.println(o);});
        helper.getMiningModelPojo().forEach(mmp -> {System.out.println(mmp);});
        helper.getResultData().iterator().forEachRemaining(rd -> {
            assertEquals(request.getCorrelationId(),rd.getCorrelationId());
            if (rd.getSegmentationId() == null) {
                assertEquals("OK",rd.getResultCode());
                assertNotNull(rd.getResultValue("Fld5", null));
                String value = rd.getResultValue("Fld5", "value", String.class).orElse(null);
                assertEquals("tgtY",value);
            }
        });

    }

    @Test
    public void testSelectSecondSegmentFirst() {
        Resource res = ResourceFactory.newClassPathResource(source1);
        KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleMine", kbase,true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234","SampleMine")
                .addParameter("fld1", 45.0, Double.class)
                .addParameter("fld2", 60.0, Double.class)
                .addParameter("fld6", "optA", String.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);
        helper.getResultData().forEach(rd -> {
            assertEquals(request.getCorrelationId(),rd.getCorrelationId());
            assertEquals("OK",rd.getResultCode());
            if (rd.getSegmentationId() == null) {
                assertNotNull(rd.getResultValue("Fld5", null));
                String value = rd.getResultValue("Fld5", "value", String.class).orElse(null);
                assertEquals("tgtZ",value);
                AbstractTreeToken token = rd.getResultValue("MissingTreeToken", null, AbstractTreeToken.class).orElse(null);
                assertNotNull(token);
                assertEquals(0.6, token.getConfidence().doubleValue(),0.0);
                assertEquals("null",token.getCurrent());
            }
        });
        int segmentsExecuted = 0;
        for (Iterator<SegmentExecution> iter = helper.getChildModelSegments().iterator(); iter.hasNext(); ) {
            SegmentExecution cms = iter.next();
            assertEquals(request.getCorrelationId(), cms.getCorrelationId());
            if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
        }
        assertEquals(1,segmentsExecuted);

    }

    @Test
    public void testWithScorecard() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScorecardMine",
                                         ResourceFactory.newClassPathResource(source2),
                                         null,true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234","SampleScorecardMine")
                .addParameter("age", 33.0, Double.class)
                .addParameter("occupation", "SKYDIVER", String.class)
                .addParameter("residenceState", "KN", String.class)
                .addParameter("validLicense", true, Boolean.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        helper.getResultData().forEach(rd -> {
            assertEquals(request.getCorrelationId(),rd.getCorrelationId());
            assertEquals("OK",rd.getResultCode());
            if (rd.getSegmentationId() == null) {
                ScoreCard sc = rd.getResultValue("ScoreCard", null, ScoreCard.class).orElse(null);
                assertNotNull(sc);
                Map map = sc.getRanking();
                assertNotNull(map);
                assertTrue(map instanceof LinkedHashMap);

                LinkedHashMap ranking = (LinkedHashMap) map;

                assertTrue(ranking.containsKey("LX00"));
                assertTrue(ranking.containsKey("RES"));
                assertTrue(ranking.containsKey("CX2"));
                assertEquals(-1.0, ranking.get("LX00"));
                assertEquals(-10.0, ranking.get("RES"));
                assertEquals(-30.0, ranking.get("CX2"));

                Iterator iter = ranking.keySet().iterator();
                assertEquals("LX00", iter.next());
                assertEquals("RES", iter.next());
                assertEquals("CX2", iter.next());
            }
        });
        int segmentsExecuted = 0;
        for (Iterator<SegmentExecution> iter = helper.getChildModelSegments().iterator(); iter.hasNext(); ) {
            SegmentExecution cms = iter.next();
            assertEquals(request.getCorrelationId(), cms.getCorrelationId());
            if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
        }
        assertEquals(1,segmentsExecuted);
    }

    @Test
    public void testWithRegression() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScorecardMine",
                ResourceFactory.newClassPathResource(source2),
                null,
                true);
        PMMLRequestData request = new PMMLRequestDataBuilder("123", "SampleScorecardMine")
                .addParameter("fld1r", 1.0, Double.class)
                .addParameter("fld2r", 1.0, Double.class)
                .addParameter("fld3r", "x", String.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        helper.getResultData().forEach(rd -> {
            assertEquals(request.getCorrelationId(),rd.getCorrelationId());
            assertEquals("OK",rd.getResultCode());
            if (rd.getSegmentationId() == null) {
                System.out.println(rd);
                assertNotNull(rd.getResultValue("RegOut", null));
                String regOutValue = rd.getResultValue("RegOut", "value", String.class).orElse(null);
                assertEquals("catC",regOutValue);
                assertNotNull(rd.getResultValue("RegProb", null));
                Double regProbValue = rd.getResultValue("RegProb", "value", Double.class).orElse(null);
                assertEquals(0.709228,regProbValue,1e-6);
                assertNotNull(rd.getResultValue("RegProbA", null));
                Double regProbValueA = rd.getResultValue("RegProbA", "value", Double.class).orElse(null);
                assertEquals(0.010635,regProbValueA,1e-6);
            }
        });
        int segmentsExecuted = 0;
        for (Iterator<SegmentExecution> iter = helper.getChildModelSegments().iterator(); iter.hasNext(); ) {
            SegmentExecution cms = iter.next();
            assertEquals(request.getCorrelationId(), cms.getCorrelationId());
            if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
        }
        assertEquals(1,segmentsExecuted);

    }

    @Test
    public void testSelectAll() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleSelectAllMine",
                ResourceFactory.newClassPathResource(source4),
                null,
                true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234","SampleSelectAllMine")
                .addParameter("age", 33.0, Double.class)
                .addParameter("occupation", "SKYDIVER", String.class)
                .addParameter("residenceState", "KN", String.class)
                .addParameter("validLicense", true, Boolean.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        helper.getResultData().forEach(rd -> {
            assertEquals("OK",rd.getResultCode());
            assertEquals(request.getCorrelationId(),rd.getCorrelationId());
            ScoreCard sc = rd.getResultValue("ScoreCard", null, ScoreCard.class).orElse(null);
            assertNotNull(sc);
            Map map = sc.getRanking();
            assertNotNull(map);
            assertTrue(map instanceof LinkedHashMap);
            LinkedHashMap ranking = (LinkedHashMap)map;
            assertTrue(ranking.containsKey("LX00") || ranking.containsKey("LC00"));
            if (ranking.containsKey("LX00")) {
                assertTrue(ranking.containsKey("RES"));
                assertTrue(ranking.containsKey("CX2"));
                assertEquals(-1.0, ranking.get("LX00"));
                assertEquals(-10.0, ranking.get("RES"));
                assertEquals(-30.0, ranking.get("CX2"));

                Iterator iter = ranking.keySet().iterator();
                assertEquals("LX00", iter.next());
                assertEquals("RES", iter.next());
                assertEquals("CX2", iter.next());
                assertEquals(41.345, sc.getScore(), 1e-6);
            } else {
                assertTrue(ranking.containsKey("RST"));
                assertTrue(ranking.containsKey("DX2"));
                assertEquals(-1.0, ranking.get("LC00"));
                assertEquals(10.0, ranking.get("RST"));
                assertEquals(-30.0, ranking.get("DX2"));

                Iterator iter = ranking.keySet().iterator();
                assertEquals("RST", iter.next());
                assertEquals("LC00", iter.next());
                assertEquals("DX2", iter.next());
                assertEquals(21.345, sc.getScore(), 1e-6);
            }

        });
        int segmentsExecuted = 0;
        for (Iterator<SegmentExecution> iter = helper.getChildModelSegments().iterator(); iter.hasNext(); ) {
            SegmentExecution cms = iter.next();
            assertEquals(request.getCorrelationId(), cms.getCorrelationId());
            if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
        }
        assertEquals(2,segmentsExecuted);
    }

    @Test
    public void testSimpleModelChain() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleModelChainMine",
                ResourceFactory.newClassPathResource(source5),
                null,
                true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234", "SampleModelChainMine")
                .addParameter("age", 33.0, Double.class)
                .addParameter("occupation", "TEACHER", String.class)
                .addParameter("residenceState", "TN", String.class)
                .addParameter("validLicense", true, Boolean.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        assertEquals("OK",resultHolder.getResultCode());
        Map<String,Object> resultVars = resultHolder.getResultVariables();
        assertNotNull(resultVars);
        assertTrue(resultVars.containsKey("QualificationLevel"));
        assertTrue(resultVars.containsKey("OverallScore"));
        String qual = resultHolder.getResultValue("QualificationLevel", "value",String.class).orElse(null);
        Double oscore = resultHolder.getResultValue("OverallScore", "value", Double.class).orElse(null);
        assertNotNull(qual);
        assertNotNull(oscore);
        assertEquals("Well",qual);
        assertEquals(56.345,oscore,1e-6);
    }

    @Test
    public void testWeightedAverage() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleMiningModelAvg",
                ResourceFactory.newClassPathResource(WEIGHTED_AVG),
                null,
                true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234", helper.getModelName())
                .addParameter("petal_length", 6.45, Double.class)
                .addParameter("petal_width", 1.75, Double.class)
                .addParameter("sepal_width", 1.23, Double.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        Double sepal_length = resultHolder.getResultValue("Sepal_length", "value",Double.class).orElse(null);
        assertEquals(7.1833385,sepal_length,1e-6);
        Double weight = resultHolder.getResultValue("Sepal_length", "weight", Double.class).orElse(null);
        assertEquals(1.00, weight, 1e-2);
    }



    @Test
    public void testSum() {
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleMiningModelSum",
                ResourceFactory.newClassPathResource(SUMMED),
                null,
                true);
        PMMLRequestData request = new PMMLRequestDataBuilder("1234", helper.getModelName())
                .addParameter("petal_length", 6.45, Double.class)
                .addParameter("petal_width", 1.75, Double.class)
                .addParameter("sepal_width", 1.23, Double.class)
                .build();
        PMML4Result resultHolder = helper.submitRequest(request);

        Double sepal_length = resultHolder.getResultValue("Sum_Sepal_length", "value",Double.class).orElse(null);
        Double total_length = 0.0;
        for (Iterator<PMML4Result> iter = helper.getResultData().iterator(); iter.hasNext();) {
            PMML4Result res = iter.next();
            if (res.getSegmentationId() != null) {
                Double segSepalLength = res.getResultValue("Sepal_length", "value", Double.class).orElse(null);
                if (segSepalLength != null) {
                    total_length += segSepalLength;
                }
            }
        }
        assertEquals(total_length, sepal_length, 1e-6);
    }
}
