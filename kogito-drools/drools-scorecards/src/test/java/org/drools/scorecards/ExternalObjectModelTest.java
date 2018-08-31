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

import org.dmg.pmml.pmml_4_2.descr.Extension;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.kie.pmml.pmml_4_2.PMML4Compiler;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper;
import org.kie.pmml.pmml_4_2.PMML4Helper;
import org.kie.pmml.pmml_4_2.PMML4ExecutionHelper.PMML4ExecutionHelperFactory;
import org.kie.pmml.pmml_4_2.extensions.PMMLExtensionNames;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;
import org.drools.compiler.compiler.ScoreCardFactory;
import org.drools.compiler.compiler.ScoreCardProvider;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.drools.scorecards.example.Applicant;
import org.drools.scorecards.pmml.ScorecardPMMLUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Data;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.drools.scorecards.ScorecardCompiler.DrlType.EXTERNAL_OBJECT_MODEL;

//@Ignore
public class ExternalObjectModelTest {
    private static ScorecardCompiler scorecardCompiler;
    private static ScoreCardProvider scorecardProvider;

    @Before
    public void setUp() throws Exception {
        scorecardCompiler = new ScorecardCompiler(EXTERNAL_OBJECT_MODEL);
        scorecardProvider = ScoreCardFactory.getScoreCardProvider();
    }


    @Test
    public void testPMMLCustomOutput() throws Exception {
        PMML pmmlDocument = null;
        String drl = null;
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull( pmmlDocument );
            PMML4Compiler.dumpModel( pmmlDocument, System.out );
            drl = scorecardCompiler.getDRL();
            assertTrue( drl != null && ! drl.isEmpty() );
            //System.out.println(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }

        for (Object serializable : pmmlDocument.getAssociationModelsAndBaselineModelsAndClusteringModels()){
            if (serializable instanceof Scorecard){
                Scorecard scorecard = (Scorecard)serializable;
                for (Object obj :scorecard.getExtensionsAndCharacteristicsAndMiningSchemas()){
                    if ( obj instanceof Output) {
                        Output output = (Output)obj;
                        final List<OutputField> outputFields = output.getOutputFields();
                        assertEquals(1, outputFields.size());
                        final OutputField outputField = outputFields.get(0);
                        assertNotNull(outputField);
                        assertEquals("totalScore", outputField.getName());
                        assertEquals("Final Score", outputField.getDisplayName());
                        assertEquals("double", outputField.getDataType().value());
                        assertEquals("predictedValue", outputField.getFeature().value());
                        final Extension extension = ScorecardPMMLUtils.getExtension(outputField.getExtensions(), PMMLExtensionNames.EXTERNAL_CLASS );
                        assertNotNull(extension);
                        assertEquals("org.drools.scorecards.example.Applicant",extension.getValue());
                        return;
                    }
                }
            }
        }
        fail();
    }



    @Test
    @Ignore(value="Test is duplicate of ScorecardProviderTest.testDrlGenerationWithExternalTypes")
    public void testDRLExecution() throws Exception {
        PMML pmmlDocument = null;
        String drl = null;
        if (scorecardCompiler.compileFromExcel(PMMLDocumentTest.class.getResourceAsStream("/scoremodel_externalmodel.xls")) ) {
            pmmlDocument = scorecardCompiler.getPMMLDocument();
            assertNotNull( pmmlDocument );
            PMML4Compiler.dumpModel( pmmlDocument, System.out );
            drl = scorecardCompiler.getDRL();
            assertTrue( drl != null && ! drl.isEmpty() );
            //System.out.println(drl);
        } else {
            fail("failed to parse scoremodel Excel.");
        }

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ks.getResources().newByteArrayResource( drl.getBytes() )
                           .setSourcePath( "test_scorecard_rules.drl" )
                           .setResourceType( ResourceType.DRL ) );
        KieBuilder kieBuilder = ks.newKieBuilder( kfs );
        Results res = kieBuilder.buildAll().getResults();
        System.err.print( res.getMessages() );
        KieContainer kieContainer = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() );

        KieBase kbase = kieContainer.getKieBase();
        KieSession session = kbase.newKieSession();

        Applicant applicant = new Applicant();
        applicant.setAge(10);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = 0, age = 30, validLicence -1
        assertEquals(29.0,applicant.getTotalScore(), 0.0);

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = -10, age = +10, validLicense = -1;
        assertEquals(-1.0, applicant.getTotalScore(), 0.0);

        session = kbase.newKieSession();
        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        session.insert( applicant );
        session.fireAllRules();
        session.dispose();
        //occupation = +10, age = +40, state = -10, validLicense = 1
        assertEquals(41.0,applicant.getTotalScore(), 0.0);
    }



    @Test
    public void testWithInitialScore() throws Exception {
        Map<String,List<Object>> externalData = new HashMap<>();
        List<Object> applicantValues = new ArrayList<>();

        Resource resource = ResourceFactory.newClassPathResource("scoremodel_externalmodel.xls");
        assertNotNull(resource);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setUsingExternalTypes(true);
        scconf.setWorksheetName("scorecards_initialscore");
        resource.setConfiguration(scconf);
        resource.setResourceType(ResourceType.SCARD);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Sample Score", resource, null, false);
        helper.addExternalDataSource("externalBeanApplicant");
        helper.addPossiblePackageName("org.drools.scorecards.example");

        Applicant applicant = new Applicant();
        applicant.setAge(10.0);
        applicantValues.add(applicant);
        externalData.put("externalBeanApplicant", applicantValues);

        PMMLRequestData request = new PMMLRequestData("123","Sample Score");
        PMML4Result resultHolder = helper.submitRequest(request, externalData);

        //occupation = 0, age = 30, validLicence -1, initialScore=100
        checkResults(129.0,resultHolder);

        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        applicantValues.clear();
        applicantValues.add(applicant);

        request = new PMMLRequestData("234", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);

        //occupation = -10, age = +10, validLicense = -1, initialScore=100;
        checkResults(99.0, resultHolder);

        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        applicantValues.clear();
        applicantValues.add(applicant);

        request = new PMMLRequestData("345", "Sample Score");
        resultHolder = helper.submitRequest(request, externalData);

        //occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        checkResults(141.0, resultHolder);
    }


    @Test
    public void testWithReasonCodes() throws Exception {
        Map<String,List<Object>> externalData = new HashMap<>();
        List<Object> applicantValues = new ArrayList<>();

        Resource resource = ResourceFactory.newClassPathResource("scoremodel_externalmodel.xls");
        assertNotNull(resource);
        ScoreCardConfiguration scconf = KnowledgeBuilderFactory.newScoreCardConfiguration();
        scconf.setUsingExternalTypes(true);
        scconf.setWorksheetName("scorecards_reasoncode");
        resource.setConfiguration(scconf);
        resource.setResourceType(ResourceType.SCARD);
        PMML4ExecutionHelper helper = PMML4ExecutionHelperFactory.getExecutionHelper("Sample Score", resource, null, false);
        helper.addExternalDataSource("externalBeanApplicant");
        helper.addPossiblePackageName("org.drools.scorecards.example");

        Applicant applicant = new Applicant();
        applicant.setAge(10);
        applicantValues.add(applicant);
        externalData.put("externalBeanApplicant", applicantValues);

        PMMLRequestData request = new PMMLRequestData("123","Sample Score");
        PMML4Result resultHolder = helper.submitRequest(request, externalData);

        //occupation = 0, age = 30, validLicence -1, initialScore=100
        checkResults(129.0,"VL0099",Arrays.asList("VL0099", "AGE02"),resultHolder);

        applicant = new Applicant();
        applicant.setOccupation("SKYDIVER");
        applicant.setAge(0);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("234","Sample Score");
        resultHolder = helper.submitRequest(request, externalData);

        //occupation = -10, age = +10, validLicense = -1, initialScore=100;
        checkResults(99.0,"OC0099",Arrays.asList("OC0099", "VL0099", "AGE01"),resultHolder);

        applicant = new Applicant();
        applicant.setResidenceState("AP");
        applicant.setOccupation("TEACHER");
        applicant.setAge(20);
        applicant.setValidLicense(true);
        applicantValues.clear();
        applicantValues.add(applicant);
        request = new PMMLRequestData("234","Sample Score");
        resultHolder = helper.submitRequest(request, externalData);

        //occupation = +10, age = +40, state = -10, validLicense = 1, initialScore=100
        checkResults(141.0,"RS001",Arrays.asList("RS001", "VL001", "OC0099", "AGE03"),resultHolder);
    }


    private void checkResults(Double expectedTotalScore, PMML4Result resultHolder) {
        assertEquals("OK",resultHolder.getResultCode());
        Double totalScore = resultHolder.getResultValue("TotalScore", "value", Double.class).orElse(null);
        assertEquals(expectedTotalScore,totalScore,1e-6);
    }

    private void checkResults(Double expectedTotalScore, String expectedReasonCode, List<String> expectedRanking, PMML4Result resultHolder) {
        Double totalScore = resultHolder.getResultValue("TotalScore", "value", Double.class).orElse(null);
        assertEquals(expectedTotalScore, totalScore, 1e-6);
        String reasonCode = resultHolder.getResultValue("ReasonCodes", "value", String.class).orElse(null);
        assertEquals( expectedReasonCode, reasonCode );
        Map reasonCodesMap = (Map)resultHolder.getResultValue("ScoreCard", "ranking");
        assertNotNull( reasonCodesMap );
        assertEquals( expectedRanking, new ArrayList( reasonCodesMap.keySet() ) );
    }

    protected Class<? extends RuleUnit> getStartingRuleUnit(String startingRule, InternalKnowledgeBase ikb, List<String> possiblePackages) {
        RuleUnitRegistry unitRegistry = ikb.getRuleUnitRegistry();
        Map<String, InternalKnowledgePackage> pkgs = ikb.getPackagesMap();
        RuleImpl ruleImpl = null;
        for (String pkgName : possiblePackages) {
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

    protected List<String> calculatePossiblePackageNames(String modelId, String... knownPackageNames) {
        List<String> packageNames = new ArrayList<>();
        String javaModelId = modelId.replaceAll("\\s", "");
        if (knownPackageNames != null && knownPackageNames.length > 0) {
            for (String knownPkgName : knownPackageNames) {
                packageNames.add(knownPkgName + "." + javaModelId);
            }
        }
        String basePkgName = PMML4UnitImpl.DEFAULT_ROOT_PACKAGE + "." + javaModelId;
        packageNames.add(basePkgName);
        return packageNames;
    }
}
