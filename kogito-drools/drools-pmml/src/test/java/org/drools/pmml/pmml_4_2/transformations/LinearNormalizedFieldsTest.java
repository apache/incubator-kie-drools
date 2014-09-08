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


import org.kie.api.definition.type.FactType;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class LinearNormalizedFieldsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_derived_fields_linearNorm.xml";
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
    public void testDerivedTypesLinearNormMapMissing() throws Exception {

        //new PMML4Wrapper().getPmml().getTransformationDictionary().getDerivedField().get(0).getNormContinuous().getOutliers().value()


        FactType age = getKbase().getFactType(packageName,"Age");
        FactType age1 = getKbase().getFactType(packageName,"Age_norm");
        FactType age2 = getKbase().getFactType(packageName,"Age_norm2");
        FactType age3 = getKbase().getFactType(packageName,"Age_norm3");

        assertNotNull(getKSession().getEntryPoint( "in_Age" ));
        assertNull(getKSession().getEntryPoint( "in_Age_mis" ));
        assertNull(getKSession().getEntryPoint( "in_Age_norm" ));

        //value is "missing" for age, so should be mapped by the mapMissingTo policy
        getKSession().getEntryPoint( "in_Age" ).insert(-1);
        getKSession().fireAllRules();


        checkFirstDataFieldOfTypeStatus(age,true,true, null,-1);

        checkFirstDataFieldOfTypeStatus(age1,true,false, null,0.0);

        checkFirstDataFieldOfTypeStatus(age2,true,false, null,-931.0);

        checkFirstDataFieldOfTypeStatus(age3,true,false, null,789.0);

        checkGeneratedRules();
    }



    @Test
    public void testDerivedTypesLinearNormOutliers() throws Exception {

        //new PMML4Wrapper().getPmml().getTransformationDictionary().getDerivedField().get(0).getNormContinuous().getOutliers().value()


        FactType age = getKbase().getFactType(packageName,"Age");
        FactType age1 = getKbase().getFactType(packageName,"Age_norm");
        FactType age2 = getKbase().getFactType(packageName,"Age_norm2");
        FactType age3 = getKbase().getFactType(packageName,"Age_norm3");

        //value is an outlier
        getKSession().getEntryPoint( "in_Age" ).insert(-100);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age,true,false, null,-100);

        checkFirstDataFieldOfTypeStatus(age1,true,false, null,0.0);

        checkFirstDataFieldOfTypeStatus(age2,true,true, null,0.0);

        checkFirstDataFieldOfTypeStatus(age3,true,false, null,-1.25);


        refreshKSession();


        //value is an outlier
        getKSession().getEntryPoint( "in_Age" ).insert(1000);
        getKSession().fireAllRules();


        checkFirstDataFieldOfTypeStatus(age,true,false, null,1000);

        checkFirstDataFieldOfTypeStatus(age1,true,false, null,2.0);

        checkFirstDataFieldOfTypeStatus(age2,true,true, null,0.0);

        checkFirstDataFieldOfTypeStatus(age3,true,false, null,12.5);

        checkGeneratedRules();
    }


    @Test
    public void testDerivedTypesLinearNormInterpolation() throws Exception {

        //new PMML4Wrapper().getPmml().getTransformationDictionary().getDerivedField().get(0).getNormContinuous().getOutliers().value()

        FactType age = getKbase().getFactType(packageName,"Age");
        FactType age1 = getKbase().getFactType(packageName,"Age_norm");

        getKSession().getEntryPoint( "in_Age" ).insert(30);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(age,true,false, null,30);

        checkFirstDataFieldOfTypeStatus(age1,true,false, null,0.375);



        refreshKSession();

        getKSession().getEntryPoint( "in_Age" ).insert(90);
        getKSession().fireAllRules();


        checkFirstDataFieldOfTypeStatus(age,true,false, null,90);

        checkFirstDataFieldOfTypeStatus(age1,true,false, null,1.5);

        checkGeneratedRules();
    }








}
