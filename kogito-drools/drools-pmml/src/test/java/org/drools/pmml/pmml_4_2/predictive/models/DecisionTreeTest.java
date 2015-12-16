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

package org.drools.pmml.pmml_4_2.predictive.models;


import org.dmg.pmml.pmml_4_2.descr.MISSINGVALUESTRATEGY;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.TreeModel;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DecisionTreeTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = false;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_tree_simple.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_tree_missing.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";



    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testSimpleTree() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

//        kSession.addEventListener( new org.drools.event.rule.DebugAgendaEventListener() );

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld5" );
        
        kSession.getEntryPoint( "in_Fld1" ).insert( 30.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 60.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "false" );
        kSession.getEntryPoint( "in_Fld4" ).insert( "optA" );

        kSession.fireAllRules();

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtY" );

        checkGeneratedRules();
    }
    
    
    
    protected Object getToken( KieSession kSession ) {
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );
        assertNotNull( tok );
        Collection c = kSession.getObjects( new ClassObjectFilter( tok.getFactClass() ) );
        assertEquals( 1, c.size() );
        return c.iterator().next();

    }


    @Test
    public void testMissingTree() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( 45.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 60.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "optA" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.6, tok.get( token, "confidence" ) );
        assertEquals( "null", tok.get( token, "current" ) );

        checkFirstDataFieldOfTypeStatus( tgt, true, false, "Missing", "tgtZ" );

        checkGeneratedRules();
    }



    @Test
    public void testMissingTreeWeighted1() throws Exception {
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

        checkGeneratedRules();
    }




    @Test
    public void testMissingTreeWeighted2() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld9" );
        FactType tok = kSession.getKieBase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "TreeToken" );

        kSession.getEntryPoint( "in_Fld1" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "miss" );

        kSession.fireAllRules();

        Object token = getToken( kSession );
        assertEquals( 0.6, tok.get( token, "confidence" ) );
        assertEquals( "null", tok.get( token, "current" ) );
        assertEquals( 100.0, tok.get( token, "totalCount" ) );

        checkFirstDataFieldOfTypeStatus(tgt, true, false, "Missing", "tgtX" );

        checkGeneratedRules();
    }




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

}
