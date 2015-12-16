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

package org.drools.pmml.pmml_4_2.global;


import org.junit.Assert;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;

import static org.junit.Assert.assertEquals;


public class DataDictionaryTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_data_dic.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testDataTypes() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        FactType gender = getKbase().getFactType(packageName,"Gender");
        FactType noclaims = getKbase().getFactType(packageName,"NoOfClaims");
        FactType scrambled = getKbase().getFactType(packageName,"Scrambled");
        FactType domicile = getKbase().getFactType(packageName,"Domicile");
        FactType agecar = getKbase().getFactType(packageName,"AgeOfCar");
        FactType amklaims = getKbase().getFactType(packageName,"AmountOfClaims");


        Object g = gender.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.Gender", g.getClass().getName() );
        gender.set(g,"value","M");
            assertEquals( "M", gender.get( g, "value" ) );


        Object n = noclaims.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.NoOfClaims", n.getClass().getName() );
        noclaims.set(n, "value", "> 3");
            assertEquals( "> 3", noclaims.get( n, "value" ) );

        Object s = scrambled.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.Scrambled", s.getClass().getName() );
        scrambled.set(s, "value", 1);
            assertEquals( 1, scrambled.get( s, "value" ) );

        Object d = domicile.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.Domicile", d.getClass().getName() );
        domicile.set(d, "value", "SomeWhere");
            assertEquals( "SomeWhere", domicile.get( d, "value" ) );

        Object a = agecar.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.AgeOfCar", a.getClass().getName() );
        agecar.set(a, "value", 24.3);
            assertEquals( 24.3, agecar.get( a, "value" ) );

        Object k = amklaims.newInstance();
            assertEquals( "org.drools.pmml.pmml_4_2.test.AmountOfClaims", k.getClass().getName() );
        amklaims.set(k, "value", 9);
            assertEquals( 9, amklaims.get( k, "value" ) );


        try {
            agecar.set(a,"value","Not a String field");
            Assert.fail();
        } catch (ClassCastException cce) {
            assertEquals( 24.3, agecar.get( a, "value" ) );
        }

            Assert.assertFalse((Boolean) agecar.get(a, "valid"));
        agecar.set(a, "valid", true);
            Assert.assertTrue((Boolean) agecar.get(a, "valid"));

            Assert.assertFalse((Boolean) agecar.get(a, "missing"));
        agecar.set(a,"missing",true);
            Assert.assertTrue((Boolean) agecar.get(a, "missing"));

        //    assertFalse((Boolean) agecar.get(a, "cyclic"));
        //agecar.set(a,"cyclic",true);
        //    assertTrue((Boolean) agecar.get(a,"cyclic"));

        checkGeneratedRules();

    }



    @Test
    public void testEntryPoints() throws Exception {

        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        FactType gender = getKbase().getFactType(packageName,"Gender");
        FactType noclaims = getKbase().getFactType(packageName,"NoOfClaims");
        FactType scrambled = getKbase().getFactType(packageName,"Scrambled");
        FactType domicile = getKbase().getFactType(packageName,"Domicile");
        FactType agecar = getKbase().getFactType(packageName,"AgeOfCar");
        FactType amklaims = getKbase().getFactType(packageName,"AmountOfClaims");

        assertEquals( 7, getKSession().getEntryPoints().size() );

        EntryPoint gender_ep = getKSession().getEntryPoint("in_Gender");
            Assert.assertNotNull(gender_ep);
        EntryPoint noclaims_ep = getKSession().getEntryPoint("in_NoOfClaims");
            Assert.assertNotNull(noclaims_ep);
        EntryPoint scrambled_ep = getKSession().getEntryPoint("in_Scrambled");
            Assert.assertNotNull(scrambled_ep);
        EntryPoint domicile_ep = getKSession().getEntryPoint("in_Domicile");
            Assert.assertNotNull(domicile_ep);
        EntryPoint agecar_ep = getKSession().getEntryPoint("in_AgeOfCar");
            Assert.assertNotNull(agecar_ep);
        EntryPoint amklaims_ep = getKSession().getEntryPoint("in_AmountOfClaims");
            Assert.assertNotNull(amklaims_ep);

        gender_ep.insert("M");
        noclaims_ep.insert("> 4");
        scrambled_ep.insert(4);
        domicile_ep.insert("way out");
        agecar_ep.insert(new Double("3.4"));
        amklaims_ep.insert(9);

        getKSession().fireAllRules();

        assertEquals( 6, getKSession().getObjects().size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( gender.getFactClass() ) ).size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( noclaims.getFactClass() ) ).size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( scrambled.getFactClass() ) ).size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( domicile.getFactClass() ) ).size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( agecar.getFactClass() ) ).size() );
        assertEquals( 1, getKSession().getObjects( new ClassObjectFilter( amklaims.getFactClass() ) ).size() );


        checkFirstDataFieldOfTypeStatus(amklaims,true,false, null,9);

        checkFirstDataFieldOfTypeStatus(domicile,false,false,null,"way out");

        checkGeneratedRules();
    }








}
