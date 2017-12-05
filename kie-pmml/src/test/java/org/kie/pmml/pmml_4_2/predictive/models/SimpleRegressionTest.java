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
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;

public class SimpleRegressionTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_regression.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_regression_clax.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";



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
        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("Fld1",0.9);
        request.addRequestParam("Fld2", 0.3);
        request.addRequestParam("Fld3", "x");
        kSession.insert(request);

        kSession.fireAllRules();
        String pkgName = PMML4Compiler.PMML_DROOLS+"."+request.getModelName();

        FactType tgt = kSession.getKieBase().getFactType( pkgName, "Fld4" );

        double x = 0.5
                   + 5 * 0.9 * 0.9
                   + 2 * 0.3
                   - 3.0
                   + 0.4 * 0.9 * 0.3;
        x = 1.0 / ( 1.0 + Math.exp( -x ) );
        
        checkFirstDataFieldOfTypeStatus( tgt, true, false, "LinReg", x );

        checkGeneratedRules();
    }



    @Test
    public void testClassification() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        FactType tgt = kSession.getKieBase().getFactType( packageName, "Fld4" );

        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("Fld1", 1.0);
        request.addRequestParam("Fld2", 1.0);
        request.addRequestParam("Fld3", "x");
        kSession.insert(request);
        
        kSession.fireAllRules();
        String pkgName = PMML4Compiler.PMML_DROOLS+"."+request.getModelName();

        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( pkgName, "RegOut" ),
                                            true, false, "LinReg", "catC" );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( pkgName, "RegProb" ),
                                            true, false, "LinReg", 0.709228 );
        checkFirstDataFieldOfTypeStatus( kSession.getKieBase().getFactType( pkgName, "RegProbA" ),
                                            true, false, "LinReg", 0.010635 );


        checkGeneratedRules();
    }




}
