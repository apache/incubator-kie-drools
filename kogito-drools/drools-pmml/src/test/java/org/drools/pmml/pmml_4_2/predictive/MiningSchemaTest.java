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

package org.drools.pmml.pmml_4_2.predictive;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;


public class MiningSchemaTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "org/drools/pmml/pmml_4_2/test_miningSchema.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";



    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testSchemaWithValidValues() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        getKSession().fireAllRules();

        getKSession().getEntryPoint("in_Feat1").insert(2.2);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat1"),
                true, false,"Test_MLP",2.2);
        refreshKSession();

        getKSession().getEntryPoint("in_Feat2").insert(5);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat2"),
                true, false,"Test_MLP",5);
    }



    @Test
    public void testSchemaWithOutliers() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());


        getKSession().getEntryPoint("in_Feat1").insert(0.24);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat1"),
                true, false,"Test_MLP",1.0);
        refreshKSession();

        getKSession().getEntryPoint("in_Feat1").insert(999.9);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat1"),
                true, false,"Test_MLP",6.9);




    }


    @Test
    public void testSchemaWithInvalid() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());


        //invalid as missing
        getKSession().getEntryPoint("in_Feat1").insert(-37.0);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat1"),
                false,false,null,-37.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat1"),
                true, false,"Test_MLP",3.95);
        refreshKSession();



        getKSession().getEntryPoint("in_Feat2").insert(-1);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat2"),
                false,false,null,-1);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat2"),
                true, false,"Test_MLP",5);

    }



    @Test
    public void testSchemaWithMissing() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());


        getKSession().getEntryPoint("in_Feat2").insert(0);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat2"),
                false,true,null,0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat2"),
                true, false,"Test_MLP",5);

    }



    @Test
    public void testSchemaWithMixedIntervalAndValues() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        getKSession().fireAllRules();

        getKSession().getEntryPoint("in_Feat3").insert(4.0);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat3"),
                true, false,"Test_MLP",4.0);

        getKSession().getEntryPoint("in_Feat3").insert(7.78);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat3"),
                true, false,"Test_MLP",7.78);

        getKSession().getEntryPoint("in_Feat3").insert(6.2);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"Feat3"),
                false, false,"Test_MLP",6.2);

    }






}
