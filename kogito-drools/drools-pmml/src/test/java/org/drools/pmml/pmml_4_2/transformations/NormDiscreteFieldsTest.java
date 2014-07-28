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

import static org.junit.Assert.assertNotNull;



public class NormDiscreteFieldsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_derived_fields_normDiscrete.xml";
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
    public void testNormDiscrete() throws Exception {

        FactType fld = getKbase().getFactType(packageName,"CatField");
        FactType val1 = getKbase().getFactType(packageName,"IsValue1");
        FactType val2 = getKbase().getFactType(packageName,"IsValue2");

        assertNotNull(getKSession().getEntryPoint( "in_CatField" ));

        //value is "missing" for age, so should be mapped by the mapMissingTo policy
        getKSession().getEntryPoint( "in_CatField" ).insert("Value1");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value1");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 1.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 0.0);

        refreshKSession();

        getKSession().getEntryPoint( "in_CatField" ).insert("Value2");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value2");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 0.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 1.0);

        refreshKSession();



        getKSession().getEntryPoint( "in_CatField" ).insert("Value3");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, true, false, null, "Value3");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 0.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 0.0);

        refreshKSession();


        getKSession().getEntryPoint( "in_CatField" ).insert("Value0");
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(fld, false, true, null, "Value0");
        checkFirstDataFieldOfTypeStatus(val1, true, false, null, 2.0);
        checkFirstDataFieldOfTypeStatus(val2, true, false, null, 2.0);

        refreshKSession();

    }








}
