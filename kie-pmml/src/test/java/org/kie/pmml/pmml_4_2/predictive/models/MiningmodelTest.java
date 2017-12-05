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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Result;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.ParameterInfo;
import org.kie.pmml.pmml_4_2.model.ScoreCard;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecution;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class MiningmodelTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_mining_model_simple.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_mining_model_simple2.pmml";
    private static final String source3 = "org/kie/pmml/pmml_4_2/filebased";
    private static final String source4 = "org/kie/pmml/pmml_4_2/test_mining_model_selectall.pmml";
    private static final String source5 = "org/kie/pmml/pmml_4_2/test_mining_model_modelchain.pmml";
    private static final String RESOURCES_TEST_ROOT="src/test/resources/";


	@Test
	public void testSelectFirstSegmentFirst() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
        
        PMMLRequestData request = new PMMLRequestData("1234","SampleMine");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");
        kSession.insert(request);
        
        kSession.fireAllRules();
//        System.out.print(reportWMObjects( kSession ));

        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(1,objs.size());
        
        PMML4Result result = objs.iterator().next();
        assertNotNull(result);
        assertEquals("OK",result.getResultCode());
        assertNotNull(result.getResultVariables());

        Map<String,Object> resultVars = result.getResultVariables();
        assertEquals(1,resultVars.size());
        Object fldFive = getResultValue(result,"Fld5",null);
        assertNotNull(fldFive);
        
        Object fldFiveMissing = getResultValue(result,"Fld5","missing");
        assertNotNull(fldFiveMissing);
        assertTrue(fldFiveMissing.equals(Boolean.FALSE));
        
        Object fldFiveValue = getResultValue(result,"Fld5","value");
        assertNotNull(fldFiveValue);
        assertEquals("tgtY",fldFiveValue);
        
	}
	
	@Test
	public void testSelectSecondSegmentFirst() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        kSession.fireAllRules();  //init model
		
        PMMLRequestData requestData = new PMMLRequestData("1234","SampleMine");
        requestData.addRequestParam(new ParameterInfo<>("1234","fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("1234","fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("1234","fld6",String.class,"optA"));
        kSession.insert(requestData);

        kSession.fireAllRules();
//        System.out.print(  reportWMObjects( kSession ));
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(1,objs.size());
        
        PMML4Result result = objs.iterator().next();
        assertNotNull(result);
        assertEquals("OK",result.getResultCode());
        assertNotNull(result.getResultVariables());

        Map<String,Object> resultVars = result.getResultVariables();
        
        Object fldFive = getResultValue(result,"Fld5",null);
        assertNotNull(fldFive);
        Object fldFiveMissing = getResultValue(result,"Fld5","missing");
        assertNotNull(fldFiveMissing);
        assertFalse((Boolean)fldFiveMissing);
        Object fldFiveValid = getResultValue(result,"Fld5","valid");
        assertNotNull(fldFiveValid);
        assertTrue((Boolean)fldFiveValid);
        Object fldFiveValue = getResultValue(result,"Fld5","value");
        assertNotNull(fldFiveValue);
        assertEquals("tgtZ",fldFiveValue);
        
        
        Object token = getResultValue(result,"MissingTreeToken",null);
        assertNotNull(token);
        AbstractTreeToken att = (AbstractTreeToken)token;
        assertEquals(0.6, att.getConfidence().doubleValue(),0.0);
        assertEquals("null", att.getCurrent());

        Collection<SegmentExecution> segmentExecs = (Collection<SegmentExecution>)kSession.getObjects(new ClassObjectFilter(SegmentExecution.class));
        assertNotNull(segmentExecs);
        assertEquals(1,segmentExecs.size());
        SegmentExecution segEx = segmentExecs.iterator().next();
        
        
	}
	
	@Test
	public void testWithScorecard() {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        kSession.fireAllRules();  //init model
		
        PMMLRequestData requestData = new PMMLRequestData("1234","SampleScorecardMine");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        kSession.insert(requestData);

        kSession.fireAllRules();  //init model
//        System.out.print(  reportWMObjects( kSession ));
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(1,objs.size());
        
        PMML4Result result = objs.iterator().next();
        assertNotNull(result);
        
        Object sc = getResultValue(result, "ScoreCard", null);
        assertNotNull(sc);
        assertTrue(sc instanceof ScoreCard );
        ScoreCard scorecard = (ScoreCard)sc;
        
        Map map = scorecard.getRanking();
        assertNotNull(map);
        assertTrue(map instanceof LinkedHashMap);
        LinkedHashMap ranking = (LinkedHashMap)map;
        
        assertTrue( ranking.containsKey( "LX00") );
        assertTrue( ranking.containsKey( "RES") );
        assertTrue( ranking.containsKey( "CX2" ) );
        assertEquals( -1.0, ranking.get( "LX00" ) );
        assertEquals( -10.0, ranking.get( "RES" ) );
        assertEquals( -30.0, ranking.get( "CX2" ) );

        Iterator iter = ranking.keySet().iterator();
        assertEquals( "LX00", iter.next() );
        assertEquals( "RES", iter.next() );
        assertEquals( "CX2", iter.next() );
        
	}

	@Test
	public void testWithRegression() {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        kSession.fireAllRules();  //init model
		
        PMMLRequestData request = new PMMLRequestData("123","SampleScorecardMine");
        request.addRequestParam("fld1r",1.0);
        request.addRequestParam("fld2r", 1.0);
        request.addRequestParam("fld3r", "x");
        kSession.insert(request);

        kSession.fireAllRules();
//        System.out.print(  reportWMObjects( kSession ));
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertNotNull(objs);
        assertEquals(1,objs.size());
        
        PMML4Result result = objs.iterator().next();
        assertNotNull(result);

        Object regOutValue = getResultValue(result, "RegOut", "value");
        assertNotNull(regOutValue);
        assertTrue(regOutValue instanceof String);
        assertEquals("catC",regOutValue);
        
        Object regProbValue = getResultValue(result, "RegProb", "value");
        assertNotNull(regProbValue);
        assertEquals((Double)0.709228,(Double)regProbValue,1e-6 );

        Object regProbAValue = getResultValue(result, "RegProbA", "value");
        assertNotNull(regProbAValue);
        assertEquals((Double)0.010635,(Double)regProbAValue,1e-6 );
        
	}

	@Test
    public void testSelectAll() {
        setKSession( getModelSession( source4, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
        PMMLRequestData requestData = new PMMLRequestData("1234","SampleSelectAllMine");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        kSession.insert(requestData);

        kSession.fireAllRules();  //init model
//        System.out.print(  reportWMObjects( kSession ));
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(2,objs.size());
        
        PMML4Result results[] = objs.toArray(new PMML4Result[objs.size()]);
        assertNotNull(results[0]);
        assertEquals("OK",results[0].getResultCode());
        assertEquals("1234",results[0].getCorrelationId());
        
        assertNotNull(results[1]);
        assertEquals("OK",results[1].getResultCode());
        assertEquals("1234",results[1].getCorrelationId());
        
        Object sc[] = new Object[2];
        ScoreCard scorecard[] = new ScoreCard[2];
        Map map[] = new Map[2];
        
        for (int cnt = 0; cnt < 2; cnt++) {
            sc[cnt] = getResultValue(results[cnt], "ScoreCard", null);
            assertNotNull(sc[cnt]);
            assertTrue(sc[cnt] instanceof ScoreCard);
            scorecard[cnt] = (ScoreCard)sc[cnt];
            map[cnt] = scorecard[cnt].getRanking();
            assertNotNull(map[cnt]);
            assertTrue(map[cnt] instanceof LinkedHashMap);
            LinkedHashMap ranking = (LinkedHashMap)map[cnt];
            
            assertTrue( ranking.containsKey( "LX00") || ranking.containsKey("LC00") );
            if (ranking.containsKey("LX00")) {
                assertTrue( ranking.containsKey( "RES") );
                assertTrue( ranking.containsKey( "CX2" ) );
                assertEquals( -1.0, ranking.get( "LX00" ) );
                assertEquals( -10.0, ranking.get( "RES" ) );
                assertEquals( -30.0, ranking.get( "CX2" ) );

                Iterator iter = ranking.keySet().iterator();
                assertEquals( "LX00", iter.next() );
                assertEquals( "RES", iter.next() );
                assertEquals( "CX2", iter.next() );
                assertEquals( 41.345, scorecard[cnt].getScore(), 1e-6 );
            } else {
                assertTrue( ranking.containsKey( "RST") );
                assertTrue( ranking.containsKey( "DX2" ) );
                assertEquals( -1.0, ranking.get( "LC00" ) );
                assertEquals( 10.0, ranking.get( "RST" ) );
                assertEquals( -30.0, ranking.get( "DX2" ) );

                Iterator iter = ranking.keySet().iterator();
                assertEquals( "RST", iter.next() );
                assertEquals( "LC00", iter.next() );
                assertEquals( "DX2", iter.next() );
                assertEquals( 21.345, scorecard[cnt].getScore(), 1e-6 );
            }
        }
        
    }
    
    @Test
    public void testFolderBased() {
    	String filename = RESOURCES_TEST_ROOT+source3;
    	File file = new File(filename);
    	setKSession( getModelSession(file) );
    	setKbase( getKSession().getKieBase() );
    	KieSession kSession = getKSession();
    	
    	assertNotNull(kSession);
        kSession.fireAllRules();  //init model
        
        PMMLRequestData request = new PMMLRequestData("1234","SampleMine");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");
        kSession.insert(request);
        
        kSession.fireAllRules();

        // Check for a PMML4Result in WorkingMemory
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(1,objs.size());
        
        PMML4Result result = objs.iterator().next();
        assertNotNull(result);
        assertEquals("OK",result.getResultCode());
        assertNotNull(result.getResultVariables());

        Map<String,Object> resultVars = result.getResultVariables();
        assertEquals(1,resultVars.size());
        Object fldFive = getResultValue(result,"Fld5",null);
        assertNotNull(fldFive);
        
        Object fldFiveMissing = getResultValue(result,"Fld5","missing");
        assertNotNull(fldFiveMissing);
        assertTrue(fldFiveMissing.equals(Boolean.FALSE));
        
        Object fldFiveValue = getResultValue(result,"Fld5","value");
        assertNotNull(fldFiveValue);
        assertEquals("tgtY",fldFiveValue);
        
    }
    
    @Ignore
    @Test
    public void testModelChain() {
        setKSession( getModelSession( source5, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model
        PMMLRequestData requestData = new PMMLRequestData("1234","SampleModelChainMine");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        kSession.insert(requestData);

        kSession.fireAllRules();  //init model
        Collection<PMML4Result> objs = (Collection<PMML4Result>)kSession.getObjects(new ClassObjectFilter(PMML4Result.class));
        assertEquals(2,objs.size());
        
        for(Iterator<PMML4Result> iter = objs.iterator(); iter.hasNext();) {
        	PMML4Result result = iter.next();
        	System.out.println(result.toString());
        	
        	assertEquals("OK",result.getResultCode());
        	
        }
    }
    
}
