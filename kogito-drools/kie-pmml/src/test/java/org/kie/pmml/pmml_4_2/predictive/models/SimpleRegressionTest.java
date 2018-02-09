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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.pmml.PMML4Data;

public class SimpleRegressionTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_regression.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_regression_clax.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";



    @After
    public void tearDown() {
//        getKSession().dispose();
    }

    @Test
    public void testRegression() throws Exception {
    	RuleUnitExecutor executor = createExecutor(source1);
    	
        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("fld1",0.9);
        request.addRequestParam("fld2", 0.3);
        request.addRequestParam("fld3", "x");
        
        PMML4Result resultHolder = new PMML4Result();
        
        List<String> possiblePackages = calculatePossiblePackageNames("LinReg");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        data.insert(request);
        resultData.insert(resultHolder);
        
        executor.run(unitClass);
        
        assertEquals("OK",resultHolder.getResultCode());
        assertNotNull(resultHolder.getResultValue("Fld4", null));
        Double value = resultHolder.getResultValue("Fld4", "value", Double.class).orElse(null);
        assertNotNull(value);

		double chkVal = 0.5 + 5 * 0.9 * 0.9 + 2 * 0.3 - 3.0 + 0.4 * 0.9 * 0.3;
		chkVal = 1.0 / (1.0 + Math.exp(-chkVal));
		assertEquals(chkVal,value, 1e-6);
        
    }



    @Test
    public void testClassification() throws Exception {
    	RuleUnitExecutor executor = createExecutor(source2);

        PMMLRequestData request = new PMMLRequestData("123","LinReg");
        request.addRequestParam("fld1", 1.0);
        request.addRequestParam("fld2", 1.0);
        request.addRequestParam("fld3", "x");

        PMML4Result resultHolder = new PMML4Result();
        
        List<String> possiblePackages = calculatePossiblePackageNames("LinReg");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(unitClass);
        
        int x = executor.run(unitClass);
        
        data.insert(request);
        resultData.insert(resultHolder);
        
        executor.run(unitClass);
        
        assertNotNull(resultHolder.getResultValue("RegOut", null));
        assertNotNull(resultHolder.getResultValue("RegProb", null));
        assertNotNull(resultHolder.getResultValue("RegProbA", null));
        
        String regOut = resultHolder.getResultValue("RegOut", "value", String.class).orElse(null);
        Double regProb = resultHolder.getResultValue("RegProb", "value", Double.class).orElse(null);
        Double regProbA = resultHolder.getResultValue("RegProbA", "value", Double.class).orElse(null);
        assertEquals("catC",regOut);
        assertEquals(0.709228,regProb,1e-6);
        assertEquals(0.010635,regProbA,1e-6);
        
    }




}
