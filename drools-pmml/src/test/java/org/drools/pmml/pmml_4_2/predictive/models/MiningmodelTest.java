package org.drools.pmml.pmml_4_2.predictive.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.drools.pmml.pmml_4_2.PMML4Result;
import org.drools.pmml.pmml_4_2.model.AbstractModel;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.drools.pmml.pmml_4_2.model.ParameterInfo;
import org.drools.pmml.pmml_4_2.model.ScoreCard;
import org.drools.pmml.pmml_4_2.model.mining.SegmentExecution;
import org.drools.pmml.pmml_4_2.model.mining.SegmentExecutionState;
import org.drools.pmml.pmml_4_2.model.tree.AbstractTreeToken;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;


public class MiningmodelTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_mining_model_simple.pmml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_mining_model_simple2.pmml";
    private static final String source3 = "org/drools/pmml/pmml_4_2/filebased";
    private static final String source4 = "org/drools/pmml/pmml_4_2/test_mining_model_selectall.pmml";
    private static final String source5 = "org/drools/pmml/pmml_4_2/test_mining_model_modelchain.pmml";
    private static final String RESOURCES_TEST_ROOT="src/test/resources/";


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
    
    @Test
    public void testModelChain() {
        setKSession( getModelSession( source5, VERBOSE ) );
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
	
	private String getGetterMethodName(Object wrapper, String fieldName, String prefix) {
		String capFieldName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
		return prefix + capFieldName;
	}
	
	private Object getResultValue(PMML4Result source, String objName, String objField, Object...params) {
		Object value = null;
		if (source != null && source.getResultVariables() != null && !source.getResultVariables().isEmpty()) {
			Object holder = source.getResultVariables().get(objName);
			if (holder != null) {
				if (objField != null && !objField.trim().isEmpty()) {
					String defFldRetriever = getGetterMethodName(holder,objField,"get");
					try {
						Class[] paramTypes = null;
						Method m = null;
						boolean retry = true;
						if (params != null && params.length > 0) {
							paramTypes = new Class[params.length];
							for (int x = 0; x < params.length;x++) {
								paramTypes[x] = params[x].getClass();
							}
							do {
								try {
									m = holder.getClass().getMethod(defFldRetriever, paramTypes);
								} catch (NoSuchMethodException nsmx) {
									if (m == null && defFldRetriever.startsWith("get")) {
										defFldRetriever = getGetterMethodName(holder,objField,"is");
									} else {
										retry = false;
									}
								}
							} while (m == null && retry);
						} else {
							do {
								try {
									m = holder.getClass().getMethod(defFldRetriever);
								} catch (NoSuchMethodException nsmx) {
									if (m == null && defFldRetriever.startsWith("get")) {
										defFldRetriever = getGetterMethodName(holder,objField,"is");
									} else {
										retry = false;
									}
								}
							} while (m == null && retry);
						}
						if (m != null) {
							if (params != null && params.length > 0) {
								value = m.invoke(holder, params);
							} else {
								value = m.invoke(holder);
							}
						}
					} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
						e1.printStackTrace();
					}
				} else {
					value = holder;
				}
				
			}
		}
		return value;
	}
	
	@Test
	public void testSelectSecondSegmentFirst() {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        kSession.fireAllRules();  //init model
		
        PMMLRequestData requestData = new PMMLRequestData("1234","SampleMine");
        requestData.addRequestParam(new ParameterInfo<>("fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("fld6",String.class,"optA"));
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
        assertTrue(sc instanceof ScoreCard);
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
}
