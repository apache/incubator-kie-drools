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
import org.drools.pmml.pmml_4_1.PMML4Helper;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScorecardTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1/test_scorecard.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_1/test_scorecardOut.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_1.test";


    @After
    public void tearDown() {
        //getKSession().dispose();
    }

    @Test
    public void testScorecard() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        kSession.getEntryPoint( "in_Age" ).insert( 33.0 );
        kSession.getEntryPoint( "in_Occupation" ).insert( "SKYDIVER" );
        kSession.getEntryPoint( "in_ResidenceState" ).insert( "KN" );
        kSession.getEntryPoint( "in_ValidLicense" ).insert( true );

        kSession.fireAllRules();  //init model

        FactType scoreCardType = getKbase().getFactType( PMML4Helper.pmmlDefaultPackageName(), "ScoreCard" );
        assertNotNull( scoreCardType );

        assertEquals( 1, kSession.getObjects( new ClassObjectFilter( scoreCardType.getFactClass() ) ).size() );
        Object scoreCard = kSession.getObjects( new ClassObjectFilter( scoreCardType.getFactClass() ) ).iterator().next();

        assertEquals( "SampleScore", scoreCardType.get( scoreCard, "modelName" ) );
        assertEquals( 41.345, scoreCardType.get( scoreCard, "score" ) );

        Object x = scoreCardType.get( scoreCard, "ranking" );
        assertTrue( x instanceof LinkedHashMap );
        LinkedHashMap map = (LinkedHashMap) x;
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
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        kSession.getEntryPoint( "in_Cage" ).insert( "engineering" );
        kSession.getEntryPoint( "in_Age" ).insert( 25 );
        kSession.getEntryPoint( "in_Wage" ).insert( 500.0 );

        kSession.fireAllRules();  //init model

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutRC1"),
                        true, false,"SampleScorecard", "RC2" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutRC2"),
                        true, false,"SampleScorecard", "RC1" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutRC3"),
                        true, false,"SampleScorecard", "RC1" );

    }



    @Test
    public void testSimple() throws IOException {
        FileInputStream fis = new FileInputStream( "/home/davide/Projects/Git/drools6/drools/drools-pmml/src/test/resources/simpl0.drl" );
        byte[] data = new byte[ fis.available() ];
        fis.read( data );
        String s = new String( data );

        FileInputStream fis2 = new FileInputStream( "/home/davide/Projects/Git/drools6/drools/drools-pmml/src/test/resources/simpleScore.drl" );
        byte[] data2 = new byte[ fis2.available() ];
        fis2.read( data2 );
        String s2 = new String( data2 );

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/sc1.drl", s );


        //Add empty Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        assertEquals( 0,
                      messages.size() );

        //Update with complete Score Card
        kfs.write( "src/main/resources/sc1.drl",
                   s2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        final List<Message> addedMessages = results.getAddedMessages();
        final List<Message> removedMessages = results.getRemovedMessages();
        System.out.println( addedMessages );
        assertEquals( 0,
                      addedMessages.size() );
        assertEquals( 0,
                      removedMessages.size() );

    }

}
