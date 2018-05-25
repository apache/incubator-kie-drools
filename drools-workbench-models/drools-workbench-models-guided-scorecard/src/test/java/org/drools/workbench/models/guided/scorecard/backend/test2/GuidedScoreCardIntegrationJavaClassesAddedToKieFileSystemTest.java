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

package org.drools.workbench.models.guided.scorecard.backend.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.drools.workbench.models.guided.scorecard.backend.base.Helper;
import org.drools.workbench.models.guided.scorecard.backend.test1.ApplicantAttribute;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Data;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;

public class GuidedScoreCardIntegrationJavaClassesAddedToKieFileSystemTest {

    @Test
    public void testEmptyScoreCardCompilation() throws Exception {
        String xml1 = Helper.createEmptyGuidedScoreCardXML();
        Resource resource = ResourceFactory.newByteArrayResource(xml1.getBytes());
        resource.setResourceType(ResourceType.SCGD);
        resource.setTargetPath("src/main/resources/test.sgcd");
        
        KieBase kbase = new KieHelper().addResource(resource).build();
        assertNotNull(kbase);
    }

    @Test
    public void testCompletedScoreCardCompilation() throws Exception {
        String xml1 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   Helper.getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   Helper.getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/test2/backend/sc1.scgd",
                   xml1 );

        //Add complete Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );
        
        KieContainer container = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        assertNotNull(container);
        KieBase kbase = container.newKieBase(null);
        assertNotNull(kbase);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        
        DataSource<PMMLRequestData> data = executor.newDataSource("request");
        DataSource<PMML4Result> resultData = executor.newDataSource("results");
        DataSource<PMML4Data> pmmlData = executor.newDataSource("pmmlData");
        DataSource<ApplicantAttribute> applicantData = executor.newDataSource("externalBeanApplicantAttribute");
        
        PMMLRequestData request = new PMMLRequestData("123", "test");
        ApplicantAttribute appAttrib = new ApplicantAttribute();
        appAttrib.setAttribute(10);
        
        PMML4Result resultHolder = new PMML4Result("123");

        List<String> possiblePackages = calculatePossiblePackageNames("Test", "org.drools.workbench.models.guided.scorecard.backend.test1");
        Class<? extends RuleUnit> ruleUnitClass = getStartingRuleUnit("RuleUnitIndicator",(InternalKnowledgeBase)kbase,possiblePackages);
        assertNotNull(ruleUnitClass);
        
        data.insert(request);
        applicantData.insert(appAttrib);
        resultData.insert(resultHolder);
        
        int count = executor.run(ruleUnitClass);
        assertTrue(count > 0);
        System.out.println(resultHolder);
    }

    @Test
    public void testIncrementalCompilation() throws Exception {
        String xml1_1 = Helper.createEmptyGuidedScoreCardXML();
        String xml1_2 = Helper.createGuidedScoreCardXML();

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "pom.xml",
                   Helper.getPom() );
        kfs.write( "src/main/resources/META-INF/kmodule.xml",
                   Helper.getKModule() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/Applicant.java",
                   Helper.getApplicant() );
        kfs.write( "src/main/java/org/drools/workbench/models/guided/scorecard/backend/test2/ApplicantAttribute.java",
                   Helper.getApplicantAttribute() );
        kfs.write( "src/main/resources/org/drools/workbench/models/guided/scorecard/backend/test2/sc1.scgd",
                   xml1_1 );

        //Add empty Score Card
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        final List<Message> messages = kieBuilder.getResults().getMessages();
        Helper.dumpMessages( messages );
        assertEquals( 0,
                      messages.size() );

        //Update with complete Score Card
        kfs.write( "src/main/resources/sc1.scgd",
                   xml1_2 );
        IncrementalResults results = ( (InternalKieBuilder) kieBuilder ).incrementalBuild();

        final List<Message> addedMessages = results.getAddedMessages();
        final List<Message> removedMessages = results.getRemovedMessages();
        Helper.dumpMessages( addedMessages );
        assertEquals( 0,
                      addedMessages.size() );
        Helper.dumpMessages( removedMessages );
        assertEquals( 0,
                      removedMessages.size() );
    }
    
    protected Class<? extends RuleUnit> getStartingRuleUnit(String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
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
    
    protected List<String> calculatePossiblePackageNames(String modelId, String...knownPackageNames) {
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

}
