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



public class TargetsAndOutputsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "org/drools/pmml/pmml_4_2/test_target_and_output.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";


    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testTarget1() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        getKSession().getEntryPoint("in_PetalNumber").insert(4);
        getKSession().getEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",3.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.0);

        checkGeneratedRules();
    }


    @Test
    public void testOutputValue() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        getKSession().getEntryPoint("in_PetalNumber").insert(4);
        getKSession().getEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP1"),
                true, false,"IRIS_MLP",3.0);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutNCSP2"),
                true, false,"IRIS_MLP",4.0);

        checkGeneratedRules();
    }


    @Test
    public void testOutputDisplayValue() throws Exception {
        setKSession(getModelSession(source,VERBOSE));
        setKbase(getKSession().getKieBase());

        getKSession().getEntryPoint("in_PetalNumber").insert(4);
        getKSession().getEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutFeatDV"),
                true, false,"IRIS_MLP", "NC Species II");

        checkGeneratedRules();
    }

    @Test
    public void testOutputResidual() throws Exception {
        setKSession(getModelSession( source, VERBOSE ) );
        setKbase(getKSession().getKieBase());

        getKSession().getEntryPoint("in_PetalNumber").insert(4);
        getKSession().getEntryPoint("in_PetalLength").insert(3.5);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"OutResidual"),
                true, false,"IRIS_MLP", 2.0 );

        checkGeneratedRules();
    }





}
