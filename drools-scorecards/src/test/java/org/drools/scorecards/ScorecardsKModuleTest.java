/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.scorecards;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.PMMLRequestDataBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ScorecardsKModuleTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testScorecardFromKModule2() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase kBase = kContainer.getKieBase("namedkiesession");
        assertNotNull(kBase);

        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScore", kBase);
        helper.addPossiblePackageName("org.drools.scorecards.example");
        PMMLRequestData request = new PMMLRequestDataBuilder("123", helper.getModelName())
        		.addParameter("age", 10.0, Double.class)
        		.addParameter("validLicense", false, Boolean.class)
        		.build();
        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        Double calcScore = resultHolder.getResultValue("CalculatedScore", "value", Double.class).orElse(null);
        assertEquals(29.0,calcScore,1e-6);

    }

    @Test
    public void testScorecardFromKBase2() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase kBase = kContainer.getKieBase("kbase2");
        assertNotNull(kBase);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("SampleScore", kBase);
        helper.addPossiblePackageName("org.drools.scorecards.example");
        PMMLRequestData request = new PMMLRequestDataBuilder("123", helper.getModelName())
        		.addParameter("age", 50.0, Double.class)
        		.addParameter("occupation", "PROGRAMMER", String.class)
        		.addParameter("validLicense", true, Boolean.class)
        		.build();
        
        PMML4Result resultHolder = helper.submitRequest(request);
        assertEquals("OK",resultHolder.getResultCode());
        Double calcScore = resultHolder.getResultValue("CalculatedScore", "value", Double.class).orElse(null);
        assertEquals(30.0, calcScore, 1e-6);

    }
}
