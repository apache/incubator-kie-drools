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


import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
@Ignore
public class NaiveBayesTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_naiveBayes.xml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_bayes_continuousDist.xml";


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
        Object a1 = q1.iterator().next().get( "$result" );
        assertTrue( a1 instanceof Double );
        assertEquals( 0.034, (Double) a1, 4 );

        QueryResults q2 = kieSession.getQueryResults( "ChosenClass", "NaiveBayesInsurance", Variable.v );
        assertEquals( 1, q2.size() );
        Object a2 = q2.iterator().next().get( "$result" );
        assertTrue( a2 instanceof Integer );
        assertEquals( 100, a2 );

        checkGeneratedRules();

    }

    @Test
    public void testNaiveBayesWithGaussianDistr() throws Exception {
        KieSession kieSession = getModelSession( source2, VERBOSE );
        setKSession( kieSession );

        kieSession.fireAllRules();  //init model

        kieSession.getEntryPoint( "in_Gender" ).insert( "male" );
        kieSession.getEntryPoint( "in_AgeOfIndividual" ).insert( 24.0 );
        kieSession.getEntryPoint( "in_NoOfClaims" ).insert( "2" );
        kieSession.getEntryPoint( "in_AgeOfCar" ).insert( 1.0 );

        kieSession.fireAllRules();

        System.out.println( reportWMObjects( kieSession ));

        QueryResults q1 = kieSession.getQueryResults( "ProbabilityOf1000", "NaiveBayesInsurance", Variable.v );
        assertEquals( 1, q1.size() );
        Object a1 = q1.iterator().next().get( "$result" );
        assertTrue( a1 instanceof Double );
        assertEquals( 0.112, (Double) a1, 4 );

        QueryResults q2 = kieSession.getQueryResults( "ChosenClass", "NaiveBayesInsurance", Variable.v );
        assertEquals( 1, q2.size() );
        Object a2 = q2.iterator().next().get( "$result" );
        assertTrue( a2 instanceof Integer );
        assertEquals( 100, a2 );

        checkGeneratedRules();

    }




}
