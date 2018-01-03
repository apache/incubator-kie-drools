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


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.PMML4Result;
import org.kie.pmml.pmml_4_2.model.AbstractModel;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.ParameterInfo;
import org.kie.pmml.pmml_4_2.model.datatypes.PMML4Data;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecisionTreeTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = false;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_tree_simple.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_tree_missing.pmml";
    private static final String source3 = "org/kie/pmml/pmml_4_2/test_tree_handwritten.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";



    @After
    public void tearDown() {
//        getKSession().dispose();
    }
    

    @Test
    public void testSimpleTree() throws Exception {
    	Resource res = ResourceFactory.newClassPathResource(source1);
    	KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
    	
    	RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
    	KieRuntimeLogger logger = ((InternalRuleUnitExecutor)executor).addFileLogger("/tmp/decisionTree");
        
        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");

        PMML4Result resultHolder = new PMML4Result();
        DataSource<PMMLRequestData> data = executor.newDataSource("request", request);
        DataSource<PMML4Result> results = executor.newDataSource("results", resultHolder);
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        
        List<String> possiblePackages = calculatePossiblePackageNames("TreeTest");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        assertEquals("OK",resultHolder.getResultCode());
        Object obj = resultHolder.getResultValue("Fld5", null);
        assertNotNull(obj);
        
        String targetValue = (String)resultHolder.getResultValue("Fld5", "value");
        assertEquals("tgtY",targetValue);
        
        logger.close();
//        System.out.println(resultHolder);
//        data.forEach(rd -> {System.out.println(rd);});
//        Collection<?> objs = ((InternalRuleUnitExecutor)executor).getSessionObjects();
//        if (objs != null) {
//        	objs.forEach(o -> {System.out.println(o.toString());});
//        } else {
//        	System.out.println("No objects found!");
//        }
//        pmmlData.forEach(pd -> {System.out.println(pd);});
        
    }
    
    
    
    protected Object getToken( KieSession kSession, String treeModelName ) {
        String className = AbstractModel.PMML_JAVA_PACKAGE_NAME + "." + treeModelName + "TreeToken";
        Collection objects = kSession.getObjects(new ObjectFilter() {
            
            @Override
            public boolean accept(Object object) {
                
                return object.getClass().getName().equals(className);
            }
        });
        assertNotNull(objects);
        assertEquals( 1, objects.size());
        Iterator iter = objects.iterator();
        assert(iter.hasNext());
        return iter.next();
    }


    @Test
    public void testMissingTree() throws Exception {
    	Resource res = ResourceFactory.newClassPathResource(source2);
    	KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
    	
    	RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
    	KieRuntimeLogger logger = ((InternalRuleUnitExecutor)executor).addFileLogger("/tmp/decisionTree");

        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3",String.class,"optA"));
        
        PMML4Result resultHolder = new PMML4Result();
        DataSource<PMMLRequestData> data = executor.newDataSource("request", requestData);
        DataSource<PMML4Result> results = executor.newDataSource("results", resultHolder);
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        
        List<String> possiblePackages = calculatePossiblePackageNames("Missing");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        Object missingTreeToken = resultHolder.getResultValue("MissingTreeToken", null);
        assertNotNull(missingTreeToken);
        
        Double tokVal = (Double)resultHolder.getResultValue("MissingTreeToken", "confidence");
        assertNotNull(tokVal);
        assertEquals(0.6,tokVal,0.0);
        
        String current = (String)resultHolder.getResultValue("MissingTreeToken", "current");
        assertNotNull(current);
        assertEquals("null",current);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        
        String fld9Val = (String)resultHolder.getResultValue("Fld9", "value");
        assertNotNull(fld9Val);
        assertEquals("tgtZ",fld9Val);
        
//        kSession.insert(requestData);

//        kSession.fireAllRules();
//        System.out.print(  reportWMObjects( kSession ));

//        String pkgName = PMML4Compiler.PMML_DROOLS+"."+requestData.getModelName();
//        FactType tgt = kSession.getKieBase().getFactType( pkgName, "Fld9" );

//        AbstractTreeToken token = (AbstractTreeToken)getToken(kSession,"Missing");
//        assertEquals(0.6, token.getConfidence().doubleValue(),0.0);
//        assertEquals("null", token.getCurrent());
//        
//
//        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtZ" );
//
//        checkGeneratedRules();
    }


    @Test
    public void testMissingTreeWeighted1() throws Exception {
    	Resource res = ResourceFactory.newClassPathResource(source2);
    	KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
    	
    	RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
    	KieRuntimeLogger logger = ((InternalRuleUnitExecutor)executor).addFileLogger("/tmp/decisionTree");


        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3", String.class, "optA"));
        
        PMML4Result resultHolder = new PMML4Result();
        DataSource<PMMLRequestData> data = executor.newDataSource("request", requestData);
        DataSource<PMML4Result> results = executor.newDataSource("results", resultHolder);
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");

        List<String> possiblePackages = calculatePossiblePackageNames("Missing");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        logger.close();
        System.out.println(resultHolder);
        pmmlData.forEach(pd -> { System.out.println(pd);});
        
        Object missingTreeToken = resultHolder.getResultValue("MissingTreeToken", null);
//        assertNotNull(missingTreeToken);
        
//        kSession.insert(data);
//
//        kSession.fireAllRules();
//
//        String pkgName = PMML4Compiler.PMML_DROOLS+"."+data.getModelName();
//        FactType tgt = kSession.getKieBase().getFactType( pkgName, "Fld9" );
//
////        System.out.print(  reportWMObjects( kSession ));
//
//        AbstractTreeToken token = (AbstractTreeToken)getToken( kSession, "Missing" );
//        assertEquals( 0.8, token.getConfidence(), 0.0 );
//        assertEquals( "null", token.getCurrent() );
//        assertEquals( 50.0, token.getTotalCount(), 0.0 );
//
//        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );
//
//        checkGeneratedRules();
    }



    @Test
    public void testMissingTreeWeighted2() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        PMMLRequestData data = new PMMLRequestData("123","Missing");
        data.addRequestParam(new ParameterInfo<>("123","Fld1", Double.class, -1.0));
        data.addRequestParam(new ParameterInfo<>("123","Fld2", Double.class, -1.0));
        data.addRequestParam(new ParameterInfo<>("123","Fld3", String.class, "miss"));
        kSession.insert(data);

        kSession.fireAllRules();
//      System.out.print(  reportWMObjects( kSession ));

        String pkgName = PMML4Compiler.PMML_DROOLS+"."+data.getModelName();
        FactType tgt = kSession.getKieBase().getFactType( pkgName, "Fld9" );


        AbstractTreeToken token = (AbstractTreeToken)getToken( kSession, "Missing" );
        assertEquals( 0.6, token.getConfidence(), 0.0 );
        assertEquals( "null", token.getCurrent() );
        assertEquals( 100.0, token.getTotalCount(), 0.0 );

        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );

        checkGeneratedRules();
    }


/*


    @Test
    public void testMissingTreeDefault() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler(); 
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.DEFAULT_CHILD );
            }
        }

        KieSession kSession = getSession( compiler.generateTheory( pmml ) );

        setKSession( kSession );
        setKbase( getKSession().getKieBase() );

        
        
        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( 70.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 40.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "miss" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.72, (Double) tok.get( token, "confidence" ), 1e-6 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 40.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );

        checkGeneratedRules();
    }


    @Test
    public void testMissingTreeAllMissingDefault() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler();
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.DEFAULT_CHILD );
            }
        }

        String theory = compiler.generateTheory( pmml );
        if ( VERBOSE ) {
            System.out.println( theory );
        }
        KieSession kSession = getSession( theory );
        setKSession( kSession );
        setKbase( getKSession().getKieBase() );



        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "miss" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 1.0, (Double) tok.get( token, "confidence" ), 1e-6 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 0.0, tok.get( token, "totalCount" ) );

//        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );
        checkGeneratedRules();
    }




    @Test
    public void testMissingTreeLastChoice() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler();
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.LAST_PREDICTION );
            }
        }

        String theory = compiler.generateTheory( pmml );
        if ( VERBOSE ) {
            System.out.println( theory );
        }
        KieSession kSession = getSession( theory );
        setKSession( kSession );
        setKbase( getKSession().getKieBase() );



        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "optA" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.8, (Double) tok.get( token, "confidence" ), 1e-6 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 50.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtX" );

        checkGeneratedRules();
    }




    @Test
    public void testMissingTreeNull() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler();
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.NULL_PREDICTION );
            }
        }

        String theory = compiler.generateTheory( pmml );
        if ( VERBOSE ) {
            System.out.println( theory );
        }
        KieSession kSession = getSession( theory );
        setKSession( kSession );
        setKbase( getKSession().getKieBase() );



        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "optA" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.0, (Double) tok.get( token, "confidence" ), 1e-6 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 0.0, tok.get( token, "totalCount" ) );

        assertEquals( 0, getKSession().getObjects( new ClassObjectFilter( tgt.getFactClass() ) ).size() );

        checkGeneratedRules();
    }



    @Test
    public void testMissingAggregate() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler();
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.AGGREGATE_NODES );
            }
        }

        String theory = compiler.generateTheory( pmml );
        if ( VERBOSE ) {
            System.out.println( theory );
        }
        KieSession kSession = getSession( theory );
        setKSession( kSession );
        setKbase( getKSession().getKieBase() );



        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( 45.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 90.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "miss" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.47, (Double) tok.get( token, "confidence" ), 1e-2 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 60.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtY" );

        checkGeneratedRules();
    }



    @Test
    public void testMissingTreeNone() throws Exception {
        PMML4Compiler compiler = new PMML4Compiler();
        PMML pmml = compiler.loadModel( PMML, ResourceFactory.newClassPathResource( source2 ).getInputStream() );

        for ( Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels() ) {
            if ( o instanceof TreeModel ) {
                TreeModel tree = (TreeModel) o;
                tree.setMissingValueStrategy( MISSINGVALUESTRATEGY.NONE );
            }
        }

        String theory = compiler.generateTheory( pmml );
        if ( VERBOSE ) {
            System.out.println( theory );
        }
        KieSession kSession = getSession( theory );
        setKSession( kSession );
        setKbase( getKSession().getKieBase() );



        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "miss" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.6, (Double) tok.get( token, "confidence" ), 1e-6 );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 100.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtX" );

        checkGeneratedRules();
    }


    @Test
    public void testSimpleTreeOutput() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "optA" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.8, tok.get( token, "confidence" ) );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 50.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );

        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "OutClass" ),
                    true, false, "Missing", "tgtX" );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "OutProb" ),
                    true, false, "Missing", 0.8 );


        checkGeneratedRules();
    }
*/
}
