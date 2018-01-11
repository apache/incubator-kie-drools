/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Result;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.datatypes.PMML4Data;

public class ScorecardTest extends DroolsAbstractPMMLTest {


    private static final String source1 = "org/kie/pmml/pmml_4_2/test_scorecard.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_scorecardOut.pmml";

    @Test
    public void testMultipleInputData() throws Exception {
        RuleUnitExecutor executor[] = new RuleUnitExecutor[3];
        PMMLRequestData requestData[] = new PMMLRequestData[3];
        PMML4Result resultHolder[] = new PMML4Result[3];
        Resource res = ResourceFactory.newClassPathResource(source1);
        kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
        
        executor[0] = RuleUnitExecutor.create().bind(kbase);
        executor[1] = RuleUnitExecutor.create().bind(kbase);
        executor[2] = RuleUnitExecutor.create().bind(kbase);
        
        DataSource<PMMLRequestData> requests[] = new DataSource[3];
        DataSource<PMML4Result> results[] = new DataSource[3];
        DataSource<PMML4Data> pmmlDatas[] = new DataSource[3];
        
        Double expectedScores[] = new Double[3];
        expectedScores[0] = 41.345;
        expectedScores[1] = 26.345;
        expectedScores[2] = 39.345;
        
        LinkedHashMap<String,Double> expectedResults[] = new LinkedHashMap[3];
        expectedResults[0] = new LinkedHashMap<>();
        expectedResults[0].put("LX00", -1.0);
        expectedResults[0].put("RES", -10.0);
        expectedResults[0].put("CX2", -30.0);
        
        expectedResults[1] = new LinkedHashMap<>();
        expectedResults[1].put("RES", 10.0);
        expectedResults[1].put("LX00", -1.0);
        expectedResults[1].put("OCC", -10.0);
        expectedResults[1].put("ABZ", -25.0);

        expectedResults[2] = new LinkedHashMap<>();
        expectedResults[2].put("LX00", 1.0);
        expectedResults[2].put("OCC", -5.0);
        expectedResults[2].put("RES", -5.0);
        expectedResults[2].put("CX1", -30.0);

        requestData[0] = createRequest("123","Sample Score", 33.0, "SKYDIVER", "KN", true);
        requestData[1] = createRequest("124","Sample Score", 50.0, "TEACHER", "AP", true);
        requestData[2] = createRequest("125","Sample Score", 10.0, "STUDENT", "TN", false);

        for (int x = 0; x < 3; x++) {
            requests[x] = executor[x].newDataSource("request");
            results[x] = executor[x].newDataSource("results");
            pmmlDatas[x] = executor[x].newDataSource("pmmlData");
            resultHolder[x] = new PMML4Result(requestData[x].getCorrelationId());
        }
        List<String> possiblePackages = calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);

        assertNotNull(unitClass);
        for (int x = 0; x < 3; x++) {
            executor[x].run(unitClass);
        }
        
        for (int y = 0; y < 3; y++) {
            requests[y].insert(requestData[y]);
            results[y].insert(resultHolder[y]);
        }
        
        for (int z = 0; z < 3; z++) {
            executor[z].run(unitClass);
        }
        
        for (int p = 0; p < 3; p++) {
            checkResult(resultHolder[p],expectedScores[p],expectedResults[p]);
            results[p].forEach(r -> { System.out.println(r);});
        }
        
    }
    
    private void checkResult(PMML4Result result, Double score, LinkedHashMap<String, Double> expectedResults) {
        assertEquals("OK",result.getResultCode());
        assertTrue(result.getResultVariables().containsKey("ScoreCard"));
        assertNotNull(result.getResultValue("ScoreCard", null));
        Double scoreFromCard = result.getResultValue("ScoreCard", "score", Double.class).orElse(null);
        assertEquals(score,scoreFromCard,1e-3);
        
        Object ranks = result.getResultValue("ScoreCard", "ranking");
        assertNotNull(ranks);
        assertTrue(ranks instanceof LinkedHashMap);
        LinkedHashMap<String,Double> rankingMap = (LinkedHashMap<String,Double>)ranks;
        Iterator<String> expectedKeys = expectedResults.keySet().iterator();
        Iterator<String> actualKeys = rankingMap.keySet().iterator();
        assertEquals(expectedResults.keySet().size(), rankingMap.keySet().size());
        while (expectedKeys.hasNext()) {
            String expectedKey = expectedKeys.next();
            String actualKey = actualKeys.next();
            Double expectedValue = expectedResults.get(expectedKey);
            Double actualValue = rankingMap.get(actualKey);
            assertEquals(expectedKey,actualKey);
            assertEquals(expectedValue,actualValue,1e-3);
        }
    }

    @Test
    public void testScorecard() throws Exception {
        RuleUnitExecutor executor = createExecutor(source1);

        PMMLRequestData requestData = createRequest("123","Sample Score",33.0,"SKYDIVER","KN",true);
        PMML4Result resultHolder = new PMML4Result();

        List<String> possiblePackages = calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);

        assertNotNull(unitClass);
        executor.run(unitClass);
        
        data.insert(requestData);
        resultData.insert(resultHolder);
        executor.run(unitClass);
System.out.println(resultHolder);
        assertEquals(3, resultHolder.getResultVariables().size());
        Object scorecard = resultHolder.getResultValue("ScoreCard", null);
        assertNotNull(scorecard);
        
        Double score = resultHolder.getResultValue("ScoreCard", "score", Double.class).orElse(null);
        assertEquals(41.345,score,0.000);
        Object ranking = resultHolder.getResultValue("ScoreCard", "ranking");
        assertNotNull(ranking);
        assertTrue(ranking instanceof LinkedHashMap);
        LinkedHashMap map = (LinkedHashMap)ranking;
        assertTrue( map.containsKey( "LX00") );
        assertTrue( map.containsKey( "RES") );
        assertTrue( map.containsKey( "CX2" ) );
        assertEquals( -1.0, map.get( "LX00" ) );
        assertEquals( -10.0, map.get( "RES" ) );
        assertEquals( -30.0, map.get( "CX2" ) );
        Iterator iter = map.keySet().iterator();
        assertEquals( "LX00", iter.next() );
        assertEquals( "RES", iter.next() );
        assertEquals( "CX2", iter.next() );

        
    }

    @Test
    public void testScorecardOutputs() throws Exception {
        RuleUnitExecutor executor = createExecutor(source2);//RuleUnitExecutor.create().bind(kbase);


        PMMLRequestData requestData = new PMMLRequestData("123","SampleScorecard");
        requestData.addRequestParam("cage","engineering");
        requestData.addRequestParam("age",25);
        requestData.addRequestParam("wage",500.0);
        
        PMML4Result resultHolder = new PMML4Result();
        
        List<String> possiblePackages = calculatePossiblePackageNames("SampleScorecard");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);


        assertNotNull(unitClass);
        executor.run(unitClass);
        
        data.insert(requestData);
        resultData.insert(resultHolder);
        executor.run(unitClass);
        
        assertEquals("OK",resultHolder.getResultCode());
        assertEquals(6,resultHolder.getResultVariables().size());
        assertNotNull(resultHolder.getResultValue("OutRC1", null));
        assertNotNull(resultHolder.getResultValue("OutRC2", null));
        assertNotNull(resultHolder.getResultValue("OutRC3", null));
        assertEquals("RC2",resultHolder.getResultValue("OutRC1", "value"));
        assertEquals("RC1",resultHolder.getResultValue("OutRC2", "value"));
        assertEquals("RC1",resultHolder.getResultValue("OutRC3", "value"));
        
    }
    
    protected PMMLRequestData createRequest(String correlationId, 
            String model, 
            Double age, 
            String occupation, 
            String residenceState, 
            boolean validLicense) {
        PMMLRequestData data = new PMMLRequestData(correlationId,model);
        data.addRequestParam("age", age);
        data.addRequestParam("occupation", occupation);
        data.addRequestParam("residenceState", residenceState);
        data.addRequestParam("validLicense", validLicense);
        return data;
    }
}
