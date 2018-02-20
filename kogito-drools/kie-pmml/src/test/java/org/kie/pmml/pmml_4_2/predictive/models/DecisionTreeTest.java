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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
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
import org.kie.pmml.pmml_4_2.PMMLExecutor;
import org.kie.pmml.pmml_4_2.PMMLKieBaseUtil;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.pmml_4_2.model.AbstractModel;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.ParameterInfo;
import org.kie.api.pmml.PMML4Data;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DecisionTreeTest extends DroolsAbstractPMMLTest {

    private static final String DECISION_TREES_FOLDER = "org/kie/pmml/pmml_4_2/";

    private static final boolean VERBOSE = false;
    private static final String source1 = DECISION_TREES_FOLDER + "test_tree_simple.pmml";
    private static final String source2 = DECISION_TREES_FOLDER + "test_tree_missing.pmml";
    private static final String source3 = DECISION_TREES_FOLDER + "test_tree_handwritten.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";

    private static final String TREE_RETURN_NULL_NOTRUECHILD_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_null_notruechild_strategy.pmml";
    private static final String TREE_RETURN_LAST_NOTRUE_CHILD_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_last_notruechild_strategy.pmml";
    private static final String TREE_DEFAULT_CHILD_MISSING_STRATEGY =
            DECISION_TREES_FOLDER + "test_tree_default_child_missing_value_strategy.pmml";
    private static final String TREE_LAST_CHILD_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_last_missing_value_strategy.pmml";
    private static final String TREE_RETURN_NULL_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_return_null_missing_value_strategy.pmml";
    private static final String TREE_WEIGHTED_CONFIDENCE_MISSING_STRATEGY = DECISION_TREES_FOLDER +
            "test_tree_weightedconfidence_missing_value_strategy.pmml";

    @After
    public void tearDown() {
//        getKSession().dispose();
    }
    
    @Test
    public void testTreeFromMiningModel() throws Exception {
        RuleUnitExecutor executor = createExecutor("org/kie/pmml/pmml_4_2/test_tree_from_mm.pmml");
        PMMLRequestData request = new PMMLRequestData("1234", "SampleMineTree1");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");

        PMML4Result resultHolder = new PMML4Result();
        List<String> possiblePackages = calculatePossiblePackageNames("SampleMineTree1");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        assertTrue( x > 0);
        
        data.insert(request);
        resultData.insert(resultHolder);
        
        executor.run(unitClass);
        assertEquals("OK",resultHolder.getResultCode());
        assertNotNull(resultHolder.getResultVariables());
        assertNotNull(resultHolder.getResultValue("Fld5", null));
        String value = resultHolder.getResultValue("Fld5", "value", String.class).orElse(null);
        assertEquals("tgtY",value);
        
        System.out.println(resultHolder);
    }

    @Test
    public void testSimpleTree() throws Exception {
        RuleUnitExecutor executor = createExecutor(source1);
        
        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        request.addRequestParam("fld2", 60.0);
        request.addRequestParam("fld3", "false");
        request.addRequestParam("fld4", "optA");

        PMML4Result resultHolder = new PMML4Result();
        
        List<String> possiblePackages = calculatePossiblePackageNames("TreeTest");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        data.insert(request);
        resultData.insert(resultHolder);
        
        executor.run(unitClass);
        
        assertEquals("OK",resultHolder.getResultCode());
        Object obj = resultHolder.getResultValue("Fld5", null);
        assertNotNull(obj);
        
        String targetValue = resultHolder.getResultValue("Fld5", "value", String.class).orElse(null);
        assertEquals("tgtY",targetValue);
    }
    
    @Test
    public void testReturnNullNoTrueChildPredictionStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_RETURN_NULL_NOTRUECHILD_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld2", "value", String.class)).isEmpty();
    }

    @Test
    public void testReturnLastNoTrueChildPredictionStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_RETURN_LAST_NOTRUE_CHILD_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld2", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtX");
    }

    @Test
    @Ignore
    public void testLastPredictionMissingValueStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_LAST_CHILD_MISSING_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtA");
    }

    @Test
    public void testNullPredictionMissingValueStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_RETURN_NULL_MISSING_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        String targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        targetValue = resultHolder.getResultValue("Fld3", "value", String.class).orElse(null);
        Assertions.assertThat(targetValue).isNull();
    }

    @Test
    public void testDefaultChildMissingValueStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_DEFAULT_CHILD_MISSING_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 100.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtZ");
    }

    @Test
    @Ignore
    public void testWeightedConfidenceMissingValueStrategy() {
        KieBase kieBase = PMMLKieBaseUtil.createKieBaseWithPMML(TREE_WEIGHTED_CONFIDENCE_MISSING_STRATEGY);
        PMMLExecutor executor = new PMMLExecutor(kieBase);

        PMMLRequestData request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 30.0);
        PMML4Result resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtY");

        request = new PMMLRequestData("123","TreeTest");
        request.addRequestParam("fld1", 50.0);
        resultHolder = executor.run(request);
        Assertions.assertThat(resultHolder).isNotNull();
        Assertions.assertThat(resultHolder.getResultValue("Fld3", "value", String.class).get()).isEqualTo("tgtX");
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
        RuleUnitExecutor executor = createExecutor(source2);

        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, 45.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2",Double.class,60.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3",String.class,"optA"));

        PMML4Result resultHolder = new PMML4Result();
        
        List<String> possiblePackages = calculatePossiblePackageNames("Missing");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass); // initializes the model
        
        data.insert(requestData);
        resultData.insert(resultHolder);
        
        executor.run(unitClass);
        
        AbstractTreeToken missingTreeToken = resultHolder.getResultValue("MissingTreeToken", null,AbstractTreeToken.class).orElse(null);
        assertNotNull(missingTreeToken);
        
        Double tokVal = resultHolder.getResultValue("MissingTreeToken", "confidence",Double.class).orElse(null);
        assertNotNull(tokVal);
        assertEquals(0.6,tokVal,0.0);
        
        String current = resultHolder.getResultValue("MissingTreeToken", "current", String.class).orElse(null);
        assertNotNull(current);
        assertEquals("null",current);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        
        String fld9Val = resultHolder.getResultValue("Fld9", "value", String.class).orElse(null);
        assertNotNull(fld9Val);
        assertEquals("tgtZ",fld9Val);
        
    }


    @Test
    public void testMissingTreeWeighted1() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source2);
        KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();


        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3", String.class, "optA"));
        
        PMML4Result resultHolder = new PMML4Result();
        DataSource<PMMLRequestData> data = executor.newDataSource("request");//, requestData);
        DataSource<PMML4Result> results = executor.newDataSource("results");//, resultHolder);
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");

        List<String> possiblePackages = calculatePossiblePackageNames("Missing");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        data.insert(requestData);
        results.insert(resultHolder);
        
        int y = executor.run(unitClass);
        
        console.close();
        System.out.println(resultHolder);
        Collection<?> objects = ((InternalRuleUnitExecutor)executor).getSessionObjects();
        objects.forEach(o -> {System.out.println(o);});
        pmmlData.forEach(pd -> { System.out.println(pd);});
        
        AbstractTreeToken missingTreeToken = (AbstractTreeToken) resultHolder.getResultValue("MissingTreeToken", null);
        assertNotNull(missingTreeToken);
        assertEquals(0.8, missingTreeToken.getConfidence(), 0.0);
        assertEquals("null", missingTreeToken.getCurrent());
        assertEquals(50.0, missingTreeToken.getTotalCount(), 0.0);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        String value = (String)resultHolder.getResultValue("Fld9", "value");
        assertNotNull(value);
        assertEquals("tgtX",value);
        
    }



    @Test
    public void testMissingTreeWeighted2() throws Exception {
        Resource res = ResourceFactory.newClassPathResource(source2);
        KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();


        PMMLRequestData requestData = new PMMLRequestData("123","Missing");
        requestData.addRequestParam(new ParameterInfo<>("123","fld1", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld2", Double.class, -1.0));
        requestData.addRequestParam(new ParameterInfo<>("123","fld3", String.class, "miss"));
        PMML4Result resultHolder = new PMML4Result();
        DataSource<PMMLRequestData> data = executor.newDataSource("request");
        DataSource<PMML4Result> results = executor.newDataSource("results");
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");

        List<String> possiblePackages = calculatePossiblePackageNames("Missing");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        int x = executor.run(unitClass);
        
        data.insert(requestData);
        results.insert(resultHolder);
        
        
        
        executor.run(unitClass);
        console.close();
        System.out.println(resultHolder);
        Collection<?> objects = ((InternalRuleUnitExecutor)executor).getSessionObjects();
        objects.forEach(o -> {System.out.println(o);});
        pmmlData.forEach(pd -> { System.out.println(pd);});

        AbstractTreeToken token = (AbstractTreeToken)resultHolder.getResultValue("MissingTreeToken", null);
        assertNotNull(token);
        assertEquals(0.6, token.getConfidence(), 0.0);
        assertEquals("null", token.getCurrent());
        assertEquals(100.0, token.getTotalCount(), 0.0);
        
        Object fld9 = resultHolder.getResultValue("Fld9", null);
        assertNotNull(fld9);
        String value = (String)resultHolder.getResultValue("Fld9", "value");
        assertNotNull(value);
        assertEquals("tgtX",value);
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
