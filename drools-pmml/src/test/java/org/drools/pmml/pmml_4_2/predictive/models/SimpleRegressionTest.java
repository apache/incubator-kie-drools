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


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

public class SimpleRegressionTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_regression.xml";
    private static final String source2 = "org/drools/pmml/pmml_4_2/test_regression_clax.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";



    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testRegression() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld4" );

        kSession.getEntryPoint( "in_Fld1" ).insert( 0.9 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 0.3 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "x" );
        kSession.fireAllRules();

        double x = 0.5
                   + 5 * 0.9 * 0.9
                   + 2 * 0.3
                   - 3.0
                   + 0.4 * 0.9 * 0.3;
        x = 1.0 / ( 1.0 + Math.exp( -x ) );
        
        checkFirstDataFieldOfTypeStatus( tgt, true, false, "LinReg", x );

    }



    @Test
    public void testClassification() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld4" );

        kSession.getEntryPoint( "in_Fld1" ).insert( 1.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( 1.0 );
        kSession.getEntryPoint( "in_Fld3" ).insert( "x" );
        kSession.fireAllRules();

        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "RegOut" ),
                                            true, false, "LinReg", "catC" );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "RegProb" ),
                                            true, false, "LinReg", 0.709228 );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( packageName, "RegProbA" ),
                                            true, false, "LinReg", 0.010635 );


    }




}
