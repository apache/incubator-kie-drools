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


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertNotNull;

public class SVMTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_svm.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_svm_1vN.xml";
    private static final String source3 = "org/drools/pmml/pmml_4_2/test_svm_1v1.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testSVM() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKieBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );

        kSession.getEntryPoint( "in_X" ).insert( 0.0 );
        kSession.getEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();
        System.out.println( reportWMObjects( kSession ) );
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );


        kSession.getEntryPoint( "in_X" ).insert( 0.23 );
        kSession.getEntryPoint( "in_Y" ).insert( 0.75 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );


        kSession.getEntryPoint( "in_X" ).insert( 0.85 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );

        kSession.getEntryPoint( "in_Y" ).insert( -0.12 );
        kSession.fireAllRules();
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );

        kSession.getEntryPoint( "in_X" ).insert( 7.85 );
        kSession.fireAllRules();
        System.out.println( reportWMObjects( kSession ) );
        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );

        checkGeneratedRules();
    }



    @Test
    public void testSVM1vN() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKieBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );


        kSession.getEntryPoint( "in_X" ).insert( 0.0 );
        kSession.getEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();

        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "no" );

        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "OutZ" ),
                                                true, false, "SVMXORMODEL", "no" );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "ProbZNo" ),
                                                true, false, "SVMXORMODEL", 0.7357588 );

        checkGeneratedRules();
    }

    @Test
    public void testSVM1v1() throws Exception {
        setKSession( getModelSession( source3, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType ztype = kSession.getKieBase().getFactType( packageName, "Z" );
        assertNotNull( ztype );


        kSession.getEntryPoint( "in_X" ).insert( 0.63 );
        kSession.getEntryPoint( "in_Y" ).insert( 0.0 );
        kSession.fireAllRules();

        checkFirstDataFieldOfTypeStatus( ztype, true, false, "SVMXORMODEL", "yes" );

        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "OutZ" ),
                                                true, false, "SVMXORMODEL", "yes" );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "ProbZYes" ),
                                                true, false, "SVMXORMODEL", 0.872057 );

        checkGeneratedRules();
    }




}
