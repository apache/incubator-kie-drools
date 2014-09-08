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
import org.kie.api.definition.type.FactType;


public class MapValuesTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_derived_fields_mapvalues.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());
    }

    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testMapValues() throws Exception {

//        FactType age = getKbase().getFactType(packageName,"Age");
//        FactType hgt = getKbase().getFactType(packageName,"Height");
//        FactType hair = getKbase().getFactType(packageName,"HairColor");
        FactType out = getKbase().getFactType(packageName,"Mapped");

        getKSession().getEntryPoint( "in_Age" ).insert(18);
        getKSession().getEntryPoint( "in_Height" ).insert(80.0);
        getKSession().getEntryPoint( "in_HairColor" ).insert("red");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(out, true, false, null, "klass1");

        refreshKSession();




        getKSession().getEntryPoint( "in_Age" ).insert(33);
        getKSession().getEntryPoint( "in_Height" ).insert(120.0);
        getKSession().getEntryPoint( "in_HairColor" ).insert("black");

        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(out, true, false, null, "klass2");


        checkGeneratedRules();

    }








}
