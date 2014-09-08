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

package org.drools.pmml.pmml_4_2.transformations;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class FunctionsWithNestedTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "org/drools/pmml/pmml_4_2/test_functions_nested_transformation.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKieBase());
    }

    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testFunctions() throws Exception {
        getKSession().getEntryPoint("in_Age").insert(10.0);


        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus( getKbase().getFactType( packageName, "MappedAge" ), true, false, null,
                                         10 + 20 //from constants
                                         + 10  //from aggregate
                                         + 45.5 // from mapvalues
                                         + 2.3 // from discretize
                                         + 0.1 // from normcontinuous
                                         + 1 // from normdiscrete
        );

        checkGeneratedRules();
    }





}
