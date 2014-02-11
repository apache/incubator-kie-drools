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


import junit.framework.Assert;
import org.drools.pmml.pmml_4_1.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NaiveBayesTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_1/test_naiveBayes.xml";


    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testNaiveBayes() throws Exception {
        KieSession kieSession = getModelSession( source1, VERBOSE );
        setKSession( kieSession );

        kieSession.fireAllRules();  //init model

        kieSession.getEntryPoint( "in_Gender" ).insert( "male" );
        kieSession.getEntryPoint( "in_NoOfClaims" ).insert( "2" );
        kieSession.getEntryPoint( "in_AgeOfCar" ).insert( 1.0 );

        kieSession.fireAllRules();

        QueryResults q1 = kieSession.getQueryResults( "ProbabilityOf500", "NaiveBayesInsurance", Variable.v );
        assertEquals( 1, q1.size() );
        Object a1 = q1.iterator().next().get( q1.getIdentifiers()[ 1 ] );
        assertTrue( a1 instanceof Double );
        assertEquals( 0.034, (Double) a1, 4 );

        QueryResults q2 = kieSession.getQueryResults( "ChosenClass", "NaiveBayesInsurance", Variable.v );
        assertEquals( 1, q2.size() );
        Object a2 = q2.iterator().next().get( q2.getIdentifiers()[ 1 ] );
        assertTrue( a2 instanceof Integer );
        assertEquals( 100, a2 );

    }




}
