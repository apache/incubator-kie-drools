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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
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
import org.kie.pmml.pmml_4_2.PMML4Result;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.datatypes.PMML4Data;

public class ScorecardTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/kie/pmml/pmml_4_2/test_scorecard.pmml";
    private static final String source2 = "org/kie/pmml/pmml_4_2/test_scorecardOut.pmml";
    private static final String packageName = "org.kie.pmml.pmml_4_2.test";


    @After
    public void tearDown() {
        //getKSession().dispose();
    }
    
    
    private Class<? extends RuleUnit> getStartingRuleUnit(String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
    	RuleUnitRegistry unitRegistry = ikb.getRuleUnitRegistry();
    	Map<String,InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
    	RuleImpl ruleImpl = null;
    	for (String pkgName: possiblePackages) {
    		if (pkgs.containsKey(pkgName)) {
    			InternalKnowledgePackage pkg = pkgs.get(pkgName);
    			ruleImpl = pkg.getRule(startingRule);
    			if (ruleImpl != null) {
    				RuleUnitDescr descr = unitRegistry.getRuleUnitFor(ruleImpl).orElse(null);
    				if (descr != null) {
    					return descr.getRuleUnitClass();
    				}
    			}
    		}
    	}
    	return null;
    }
    
    private List<String> calculatePossiblePackageNames(String modelId, String...knownPackageNames) {
    	List<String> packageNames = new ArrayList<>();
    	String javaModelId = modelId.replaceAll("\\s","");
    	if (knownPackageNames != null && knownPackageNames.length > 0) {
    		for (String knownPkgName: knownPackageNames) {
    			packageNames.add(knownPkgName + "." + javaModelId);
    		}
    	}
		String basePkgName = PMML4UnitImpl.DEFAULT_ROOT_PACKAGE+"."+javaModelId;
		packageNames.add(basePkgName);
    	return packageNames;
    }

    @Test
    public void testScorecard() throws Exception {
    	final String prepend = "/home/lleveric/projects/drools/kie-pmml/src/test/resources/";
    	String fullFileName = prepend+source1;
		Resource res = ResourceFactory.newFileResource(fullFileName);
    	KieBase kbase = new KieHelper().addResource(res, ResourceType.PMML).build();
    	
    	RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        KieRuntimeLogger logger = ((InternalRuleUnitExecutor)executor).addFileLogger("/home/lleveric/tmp/scorecardTest");

    	PMMLRequestData requestData = new PMMLRequestData("123","Sample Score");
        requestData.addRequestParam("age",33.0);
        requestData.addRequestParam("occupation", "SKYDIVER");
        requestData.addRequestParam("residenceState","KN");
        requestData.addRequestParam("validLicense", true);
        

        DataSource<PMMLRequestData> data = executor.newDataSource("request",requestData);
        DataSource<PMML4Result> resultData = executor.newDataSource("results");
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        
        List<String> possiblePackages = calculatePossiblePackageNames("Sample Score", "org.drools.scorecards.example");
        Class<? extends RuleUnit> unitClass = getStartingRuleUnit("Extract Parameter Info",(InternalKnowledgeBase)kbase,possiblePackages);

        if (unitClass != null) {
	        int x = executor.run(unitClass);
	        System.out.println(x);
	        pmmlData.forEach(pd -> {System.out.println(pd);});
	        resultData.forEach(rd -> {System.out.println("result -> "+rd);});
	        Collection<?> sessionObjects = ((InternalRuleUnitExecutor)executor).getSessionObjects();
	        sessionObjects.forEach(obj -> {System.out.println(obj);});
        } else {
        	System.out.println("Unable to find the rule unit class");
        }
        logger.close();
        
    }

    @Test
    public void testScorecardOutputs() throws Exception {
        setKSession( getModelSession( source2, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();
        
        kSession.fireAllRules();  //init model

        PMMLRequestData requestData = new PMMLRequestData("123","SampleScorecard");
        requestData.addRequestParam("cage","engineering");
        requestData.addRequestParam("age",25);
        requestData.addRequestParam("wage",500.0);
        kSession.insert(requestData);


        kSession.fireAllRules();  //init model
        String pkgName = PMML4Compiler.PMML_DROOLS+"."+requestData.getModelName();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC1"),
                        true, false,"SampleScorecard", "RC2" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC2"),
                        true, false,"SampleScorecard", "RC1" );
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(pkgName,"OutRC3"),
                        true, false,"SampleScorecard", "RC1" );

        checkGeneratedRules();
    }
}
