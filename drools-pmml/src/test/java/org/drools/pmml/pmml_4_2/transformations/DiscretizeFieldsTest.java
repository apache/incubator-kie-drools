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

package org.drools.pmml.pmml_4_2.transformations;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.FactType;

import static org.junit.Assert.assertNotNull;


public class DiscretizeFieldsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_derived_fields_discretize.xml";
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
    public void testDiscretize() throws Exception {

        FactType age = getKbase().getFactType(packageName,"Age");
        FactType cat = getKbase().getFactType(packageName,"AgeCategories");

        assertNotNull(getKSession().getEntryPoint("in_Age"));


        getKSession().getEntryPoint("in_Age").insert(-1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, true, null, -1);
        checkFirstDataFieldOfTypeStatus(cat, true, false,null, "infant");


        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 1);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "infant");


        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(9);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 9);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "young");




        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(30);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 30);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "mature");




        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(90);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 90);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");



        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(3000);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 3000);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");



        this.refreshKSession();


        getKSession().getEntryPoint("in_Age").insert(19);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age, true, false, null, 19);
        checkFirstDataFieldOfTypeStatus(cat, true, false, null, "ancient");


        checkGeneratedRules();

    }








}
