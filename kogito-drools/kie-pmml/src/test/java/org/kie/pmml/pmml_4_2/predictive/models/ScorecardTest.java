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
    public void testScorecard() throws Exception {
    	RuleUnitExecutor executor = createExecutor(source1);

    	PMMLRequestData requestData = new PMMLRequestData("123","Sample Score");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        
        PMML4Result resultHolder = new PMML4Result();

        List<String> possiblePackages = calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);

        assertNotNull(unitClass);
        executor.run(unitClass);
        
        data.insert(requestData);
        resultData.insert(resultHolder);
        executor.run(unitClass);

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
}
