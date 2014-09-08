/*
* Copyright 2011 JBoss Inc
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


import org.junit.Assert;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.ModelMarker;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class CleanupTest extends DroolsAbstractPMMLTest {


    private static final String source1 = "org/drools/pmml/pmml_4_2/test_ann_iris_prediction.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_tree_simple.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_2/test_regression.xml";
    private static final String source4 = "org/drools/pmml/pmml_4_2/test_clustering.xml";
    private static final String source5 = "org/drools/pmml/pmml_4_2/test_svm.xml";
    private static final String source6 = "org/drools/pmml/pmml_4_2/test_scorecard.xml";

    private static final String source9 = "org/drools/pmml/pmml_4_2/mock_cold.xml";

    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


    @Test
    public void testCleanupANN() {
        KieSession kSession = getModelSession( source1 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "Neuiris" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );
        kSession.dispose();

        checkGeneratedRules();
    }

    private Collection getModelMarker( final KieSession kSession, final String modelName ) {
        return kSession.getObjects( new ObjectFilter() {
            public boolean accept( Object o ) {
                return o instanceof ModelMarker && modelName.equals( ((ModelMarker) o).getModelName() );
            }
        } );
    }


    @Test
    public void testReenableANN() {
        setKSession( getModelSession( source1 ) );
        getKSession().fireAllRules();

        assertTrue( getKSession().getObjects().size() > 0 );

        Collection qres = getModelMarker( getKSession(), "Neuiris" );
        assertEquals( 1, qres.size() );

        getKSession().getEntryPoint( "enable_Neuiris" ).insert( Boolean.FALSE );

        getKSession().fireAllRules();

        assertEquals( 1, getKSession().getObjects().size() );

        getKSession().getEntryPoint( "enable_Neuiris" ).insert( Boolean.TRUE );
        getKSession().fireAllRules();

        getKSession().getEntryPoint( "in_PetalNum" ).insert(101);
        getKSession().getEntryPoint( "in_PetalWid" ).insert(2);
        getKSession().getEntryPoint( "in_Species" ).insert("virginica");
        getKSession().getEntryPoint( "in_SepalWid" ).insert(30);
        getKSession().fireAllRules();

        Assert.assertEquals( 24.0, queryIntegerField( "OutSepLen", "Neuiris" ), 0.0);

        assertEquals( 38, getKSession().getObjects().size() );

        getKSession().dispose();

        checkGeneratedRules();
    }



    @Test
    public void testCleanupDT() {
        KieSession kSession = getModelSession( source2 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "TreeTest" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_TreeTest" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();

        checkGeneratedRules();
    }

    @Test
    public void testCleanupRegression() {
        KieSession kSession = getModelSession( source3 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "LinReg" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_LinReg" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();

        checkGeneratedRules();
    }

    @Test
    public void testCleanupClustering() {
        KieSession kSession = getModelSession( source4 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "CenterClustering" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_CenterClustering" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();

        checkGeneratedRules();
    }

    @Test
    public void testCleanupSVM() {
        KieSession kSession = getModelSession( source5 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "SVMXORModel" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_SVMXORModel" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();

        checkGeneratedRules();
    }

    @Test
    public void testCleanupScorecard() {
        KieSession kSession = getModelSession( source6 );
        kSession.fireAllRules();
        assertTrue( kSession.getObjects().size() > 0 );

        Collection qres = getModelMarker( kSession, "SampleScore" );
        assertEquals( 1, qres.size() );

        kSession.getEntryPoint( "enable_SampleScore" ).insert(Boolean.FALSE);
        kSession.fireAllRules();

        assertEquals( 1, kSession.getObjects().size() );

        kSession.dispose();

        checkGeneratedRules();
    }







//    @Test
//    public void testCleanupANNRulesWithIncrementalKA() {
//
//        KnowledgeAgent kAgent = initIncrementalKA();
////        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKnowledgeBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
//        res.setResourceType( ResourceType.PMML );
//        ClassPathResource res2 = (ClassPathResource) ResourceFactory.newClassPathResource( source9 );
//        res2.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//        csAdd.addNewResource( res2 );
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 41, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.out.println( "************************ REMOVING resource 2 ");
//
//        ChangeSetHelperImpl csRem2 = new ChangeSetHelperImpl();
//        csRem2.addRemovedResource( res2 );
//        kAgent.applyChangeSet( csRem2.getChangeSet() );
//
//        kSession.fireAllRules();
//
//
//        System.out.println(reportWMObjects(kSession));
//
//        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//
//    }
//
//
//    @Test
//    public void testCleanupDTRulesWithIncrementalKA() {
//        KnowledgeAgent kAgent = initIncrementalKA();
//        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKieBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source2 );
//        res.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//    }
//
//    @Test
//    public void testCleanupClusteringRulesWithIncrementalKA() {
//        KnowledgeAgent kAgent = initIncrementalKA();
//        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKieBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source3 );
//        res.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//    }
//
//    @Test
//    public void testCleanupRegressionRulesWithIncrementalKA() {
//        KnowledgeAgent kAgent = initIncrementalKA();
//        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKieBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source4 );
//        res.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//    }
//
//    @Test
//    public void testCleanupSVMRulesWithIncrementalKA() {
//        KnowledgeAgent kAgent = initIncrementalKA();
//        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKieBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source5 );
//        res.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( packageName ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 0, kBase.getKnowledgePackage( packageName ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//    }
//
//    @Test
//    public void testCleanupScorecardRulesWithIncrementalKA() {
//        KnowledgeAgent kAgent = initIncrementalKA();
//        //        kAgent.setSystemEventListener( new PrintStreamSystemEventListener() );
//
//        KnowledgeBase kBase = kAgent.getKieBase();
//        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
//
//        ChangeSetHelperImpl csAdd = new ChangeSetHelperImpl();
//        ClassPathResource res = (ClassPathResource) ResourceFactory.newClassPathResource( source6 );
//        res.setResourceType( ResourceType.PMML );
//        csAdd.addNewResource(res);
//
//        System.out.println( "************************ ADDING resources ");
//
//        kAgent.applyChangeSet( csAdd.getChangeSet() );
//
//        assertTrue( kBase.getKnowledgePackage( "org.drools.scorecards.example" ).getRules().size() > 0 );
//        kSession.fireAllRules();
//        assertTrue( kSession.getObjects().size() > 0 );
//
//
//        System.out.println( "************************ REMOVING resource 1 ");
//
//        ChangeSetHelperImpl csRem = new ChangeSetHelperImpl();
//        csRem.addRemovedResource( res );
//        kAgent.applyChangeSet( csRem .getChangeSet() );
//
//        kSession.fireAllRules();
//
//        assertEquals( 0, kBase.getKnowledgePackage( "org.drools.scorecards.example" ).getRules().size() );
//
//        System.err.println( reportWMObjects( kSession ) );
//        assertEquals( 0, kSession.getObjects().size() );
//
//        kSession.dispose();
//        kAgent.dispose();
//    }








}
