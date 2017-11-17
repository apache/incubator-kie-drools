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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.drools.pmml.pmml_4_2.model.ScoreCard;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

public class ScorecardTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_scorecard.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_scorecardOut.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


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

        PMMLRequestData requestData = new PMMLRequestData("123","Sample Score");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        kSession.insert(requestData);


        kSession.fireAllRules();  //init model
        
        Collection<ScoreCard> scoreCards = (Collection<ScoreCard>)kSession.getObjects(new ClassObjectFilter(ScoreCard.class));
        assertNotNull(scoreCards);
        assertEquals(1, scoreCards.size());
        
        ScoreCard scoreCard = scoreCards.iterator().next();
        Object x = scoreCard.getRanking();
        assertTrue( x instanceof LinkedHashMap );

        System.out.print(  reportWMObjects( kSession )
        );
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

        checkGeneratedRules();
    }

    @Test
    public void testScorecardOutputs() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        
        kSession.fireAllRules();  //init model

        PMMLRequestData requestData = new PMMLRequestData("123","SampleScorecard");
        requestData.addRequestParam("cage","engineering");
        requestData.addRequestParam("age",25);
        requestData.addRequestParam("wage",500.0);
        kSession.insert(requestData);


        kSession.fireAllRules();  //init model
        String pkgName = PMML4Compiler.PMML_DROOLS+"."+requestData.getModelName();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC1"),
                        true, false,"SampleScorecard", "RC2" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC2"),
                        true, false,"SampleScorecard", "RC1" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC3"),
                        true, false,"SampleScorecard", "RC1" );

        checkGeneratedRules();
    }
}
