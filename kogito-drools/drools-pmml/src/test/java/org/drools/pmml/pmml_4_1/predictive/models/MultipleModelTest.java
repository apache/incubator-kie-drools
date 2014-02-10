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

package org.drools.pmml.pmml_4_1.predictive.models;


import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_1.ModelMarker;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MultipleModelTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1/mock_ptsd.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1/mock_cold.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_1/mock_breastcancer.xml";
    private static final String source4 = "org/drools/pmml/pmml_4_1/test_svm.xml";

    private static final String packageName = "org.drools.pmml.pmml_4_1.test";

    @Test
    public void testCompositeBuilding() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( ResourceFactory.newClassPathResource( source1 ).setResourceType( ResourceType.PMML ) );
        kfs.write( ResourceFactory.newClassPathResource( source2 ).setResourceType( ResourceType.PMML ) );

        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.buildAll();

        KieSession kSession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
        assertEquals( 11, kSession.getObjects( new ClassObjectFilter( kSession.getKieBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );

        kSession.dispose();
    }


    @Test
    public void testIncrementalBuilding() throws Exception {
        ReleaseId releaseId1 = KieServices.Factory.get().newReleaseId( "org.test", "test", "1.0.0-SNAPSHOT" );

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieBuilder kb = ks.newKieBuilder( kfs );

        kfs.generateAndWritePomXML( releaseId1 );
        kfs.write( ResourceFactory.newClassPathResource( source1 ).setResourceType( ResourceType.PMML ) );
        kb.buildAll();

        KieContainer kc = ks.newKieContainer( releaseId1 );
        KieSession kSession = kc.newKieSession();
        kSession.fireAllRules();


        kfs.write( ResourceFactory.newClassPathResource( source2 ).setResourceType( ResourceType.PMML ) );
        IncrementalResults results = (( InternalKieBuilder ) kb ).incrementalBuild();
        kc.updateToVersion( releaseId1 );

        kSession.fireAllRules();

        System.err.println(reportWMObjects(kSession));

        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
        assertEquals( 11, kSession.getObjects( new ClassObjectFilter( kSession.getKieBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );

        kSession.dispose();
    }





    
//    @Test
//    public void testKnowledgeAgentLoadingMultipleANN() throws Exception {
//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
//        if ( kbuilder.hasErrors() ) {
//            fail( kbuilder.getErrors().toString() );
//        }
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
//
//        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
//        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
//        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
//        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );
//
//
//        StatefulKnowledgeSession kSession = kagent.getKnowledgeBase().newStatefulKnowledgeSession();
//        assertNotNull(kSession);
//
//        ChangeSetHelperImpl cs;
//        ClassPathResource res;
//
//        cs = new ChangeSetHelperImpl();
//        res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
//        res.setResourceType( ResourceType.PMML );
//        cs.addNewResource( res );
//        kagent.applyChangeSet( cs.getChangeSet() );
//        kSession.fireAllRules();
//
//
//        System.out.println( " \n\n\n DONE LOADING " + source1 + " \n\n\n " );
//
//        QueryResults q1 = kSession.getQueryResults( "getQuestionnaireByType", "MockPTSD", Variable.v );
//        assertEquals( 1, q1.size() );
//        Questionnaire ptsdQ = (Questionnaire) q1.iterator().next().get( "$quest" );
//
//        cs = new ChangeSetHelperImpl();
//        res = (ClassPathResource) ResourceFactory.newClassPathResource( source2 );
//        res.setResourceType( ResourceType.PMML );
//        cs.addNewResource( res );
//        kagent.applyChangeSet( cs.getChangeSet() );
//        kSession.fireAllRules();
//
//        System.out.println( " \n\n\n DONE LOADING " + source2 + " \n\n\n " );
//
//        cs = new ChangeSetHelperImpl();
//        res = (ClassPathResource) ResourceFactory.newClassPathResource( source3 );
//        res.setResourceType( ResourceType.PMML );
//        cs.addNewResource( res );
//        kagent.applyChangeSet( cs.getChangeSet() );
//        kSession.fireAllRules();
//
//        System.out.println( " \n\n\n DONE LOADING " + source3 + " \n\n\n " );
//
//        kSession.fireAllRules();
//
//        QueryResults q2 = kSession.getQueryResults( "getQuestionnaireByType", "MockPTSD", Variable.v );
//        assertEquals( 1, q2.size() );
//        Questionnaire ptsdQ2 = (Questionnaire) q2.iterator().next().get( "$quest" );
//
//        assertSame( ptsdQ, ptsdQ2 );
//
//        System.err.println(reportWMObjects(kSession));
//
//        assertEquals( 3, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
//        assertEquals( 3, kSession.getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
//        assertEquals( 23, kSession.getObjects( new ClassObjectFilter( kSession.getKieBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );
//
//
//        kSession.dispose();
//        kagent.dispose();
//
//    }
//



//
//    @Test
//    public void testKnowledgeAgentLoadingMix() throws Exception {
//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/informer/informer-changeset.xml" ), ResourceType.CHANGE_SET );
//        if ( kbuilder.hasErrors() ) {
//            fail( kbuilder.getErrors().toString() );
//        }
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
//
//        KnowledgeAgentConfiguration kaConfig = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
//        kaConfig.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
//        kaConfig.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );
//        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "testPmml", kbase, kaConfig );
//
//        StatefulKnowledgeSession kSession = kagent.getKieBase().newStatefulKnowledgeSession();
//        assertNotNull(kSession);
//
//        ChangeSetHelperImpl cs;
//        ClassPathResource res;
//
//        cs = new ChangeSetHelperImpl();
//        res = (ClassPathResource) ResourceFactory.newClassPathResource( source1 );
//        res.setResourceType( ResourceType.PMML );
//        cs.addNewResource( res );
//        kagent.applyChangeSet( cs.getChangeSet() );
//        kSession.fireAllRules();
//
//
//        System.out.println( " \n\n\n DONE LOADING " + source1 + " \n\n\n " );
//
//        cs = new ChangeSetHelperImpl();
//        res = (ClassPathResource) ResourceFactory.newClassPathResource( source4 );
//        res.setResourceType( ResourceType.PMML );
//        cs.addNewResource( res );
//        kagent.applyChangeSet( cs.getChangeSet() );
//        kSession.fireAllRules();
//
//
//        System.out.println( " \n\n\n DONE LOADING " + source4 + " \n\n\n " );
//
//        kSession.fireAllRules();
//
//        System.err.println(reportWMObjects(kSession));
//
//        assertEquals( 2, kSession.getObjects( new ClassObjectFilter( ModelMarker.class ) ).size() );
//        assertEquals( 1, kSession.getObjects( new ClassObjectFilter( Questionnaire.class ) ).size() );
//        assertEquals( 9, kSession.getObjects( new ClassObjectFilter( kSession.getKieBase().getFactType( packageName, "Synapse" ).getFactClass() ) ).size() );
//        assertEquals( 4, kSession.getObjects( new ClassObjectFilter( kSession.getKieBase().getFactType( packageName, "SupportVector" ).getFactClass() ) ).size() );
//
//        kSession.dispose();
//        kagent.dispose();
//    }


}
