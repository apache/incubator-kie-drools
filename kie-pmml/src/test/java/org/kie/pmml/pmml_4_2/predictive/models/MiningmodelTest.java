/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.junit.Ignore;
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
import org.kie.pmml.pmml_4_2.PMML4Result;
import org.kie.pmml.pmml_4_2.model.AbstractPMMLData;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.ParameterInfo;
import org.kie.pmml.pmml_4_2.model.ScoreCard;
import org.kie.pmml.pmml_4_2.model.datatypes.PMML4Data;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecution;
import org.kie.pmml.pmml_4_2.model.mining.SegmentExecutionState;
import org.kie.pmml.pmml_4_2.model.tree.AbstractTreeToken;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MiningmodelTest extends DroolsAbstractPMMLTest {
	private static final boolean VERBOSE = true;
	private static final String source1 = "org/kie/pmml/pmml_4_2/test_mining_model_simple.pmml";
	private static final String source2 = "org/kie/pmml/pmml_4_2/test_mining_model_simple2.pmml";
	private static final String source3 = "org/kie/pmml/pmml_4_2/filebased";
	private static final String source4 = "org/kie/pmml/pmml_4_2/test_mining_model_selectall.pmml";
	private static final String source5 = "org/kie/pmml/pmml_4_2/test_mining_model_modelchain.pmml";
	private static final String RESOURCES_TEST_ROOT = "src/test/resources/";

	@Test
	public void testSelectFirstSegmentFirst() {
		RuleUnitExecutor executor = createExecutor(source1);
//		KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();

		PMMLRequestData request = new PMMLRequestData("1234", "SampleMine");
		request.addRequestParam("fld1", 30.0);
		request.addRequestParam("fld2", 60.0);
		request.addRequestParam("fld3", "false");
		request.addRequestParam("fld4", "optA");
		
		PMML4Result resultHolder = new PMML4Result();
		resultHolder.setCorrelationId(request.getCorrelationId());

		DataSource<PMMLRequestData> childModelRequest = executor.newDataSource("childModelRequest");
		DataSource<PMML4Result> childModelResults = executor.newDataSource("childModelResults");
		DataSource<SegmentExecution> childModelSegments = executor.newDataSource("childModelSegments");
		DataSource<? extends AbstractPMMLData> miningModelPojo = executor.newDataSource("miningModelPojo");

		List<String> possiblePackages = this.calculatePossiblePackageNames("SampleMine");
		Class<? extends RuleUnit> ruleUnitClass = this.getStartingRuleUnit("Start Mining - SampleMine",(InternalKnowledgeBase)kbase,possiblePackages);
		
		assertNotNull(ruleUnitClass);
		
		data.insert(request);
		resultData.insert(resultHolder);
		
		executor.run(ruleUnitClass);
//		console.close();
		Collection<?> objects = ((InternalRuleUnitExecutor)executor).getSessionObjects();
		objects.forEach(o -> {System.out.println(o);});
		miningModelPojo.forEach(mmp -> {System.out.println(mmp);});
		resultData.iterator().forEachRemaining(rd -> {
			assertEquals(request.getCorrelationId(),rd.getCorrelationId());
			if (rd.getSegmentationId() == null) {
				assertEquals("OK",rd.getResultCode());
				assertNotNull(rd.getResultValue("Fld5", null));
				String value = rd.getResultValue("Fld5", "value", String.class).orElse(null);
				assertEquals("tgtY",value);
			}
		});
	}

	@Test
	public void testSelectSecondSegmentFirst() {
		RuleUnitExecutor executor = createExecutor(source1);
//		KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();


		PMMLRequestData request = new PMMLRequestData("1234", "SampleMine");
		request.addRequestParam(new ParameterInfo<>("1234", "fld1", Double.class, 45.0));
		request.addRequestParam(new ParameterInfo<>("1234", "fld2", Double.class, 60.0));
		request.addRequestParam(new ParameterInfo<>("1234", "fld6", String.class, "optA"));
		PMML4Result resultHolder = new PMML4Result();
		resultHolder.setCorrelationId(request.getCorrelationId());

		DataSource<PMMLRequestData> childModelRequest = executor.newDataSource("childModelRequest");
		DataSource<PMML4Result> childModelResults = executor.newDataSource("childModelResults");
		DataSource<SegmentExecution> childModelSegments = executor.newDataSource("childModelSegments");
		DataSource<? extends AbstractPMMLData> miningModelPojo = executor.newDataSource("miningModelPojo");
		

		List<String> possiblePackages = this.calculatePossiblePackageNames("SampleMine");
		Class<? extends RuleUnit> ruleUnitClass = this.getStartingRuleUnit("Start Mining - SampleMine",(InternalKnowledgeBase)kbase,possiblePackages);
		
		assertNotNull(ruleUnitClass);
		
		data.insert(request);
		resultData.insert(resultHolder);
		
		executor.run(ruleUnitClass);
//		console.close();
		resultData.forEach(rd -> {
			assertEquals(request.getCorrelationId(),rd.getCorrelationId());
			assertEquals("OK",rd.getResultCode());
			if (rd.getSegmentationId() == null) {
				assertNotNull(rd.getResultValue("Fld5", null));
				String value = rd.getResultValue("Fld5", "value", String.class).orElse(null);
				assertEquals("tgtZ",value);
				AbstractTreeToken token = rd.getResultValue("MissingTreeToken", null, AbstractTreeToken.class).orElse(null);
				assertNotNull(token);
				assertEquals(0.6, token.getConfidence().doubleValue(),0.0);
				assertEquals("null",token.getCurrent());
			}
		});
		int segmentsExecuted = 0;
		for (Iterator<SegmentExecution> iter = childModelSegments.iterator(); iter.hasNext(); ) {
			SegmentExecution cms = iter.next();
			assertEquals(request.getCorrelationId(), cms.getCorrelationId());
			if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
		}
		assertEquals(1,segmentsExecuted);
		
	}

	@Test
	public void testWithScorecard() {
		RuleUnitExecutor executor = createExecutor(source2);
//		KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();

		PMMLRequestData request = new PMMLRequestData("1234", "SampleScorecardMine");
		request.addRequestParam("age", 33.0);
		request.addRequestParam("occupation", "SKYDIVER");
		request.addRequestParam("residenceState", "KN");
		request.addRequestParam("validLicense", true);
		PMML4Result resultHolder = new PMML4Result();
		resultHolder.setCorrelationId(request.getCorrelationId());

		DataSource<PMMLRequestData> childModelRequest = executor.newDataSource("childModelRequest");
		DataSource<PMML4Result> childModelResults = executor.newDataSource("childModelResults");
		DataSource<SegmentExecution> childModelSegments = executor.newDataSource("childModelSegments");
		DataSource<? extends AbstractPMMLData> miningModelPojo = executor.newDataSource("miningModelPojo");
		

		List<String> possiblePackages = this.calculatePossiblePackageNames("SampleScorecardMine");
		Class<? extends RuleUnit> ruleUnitClass = this.getStartingRuleUnit("Start Mining - SampleScorecardMine",(InternalKnowledgeBase)kbase,possiblePackages);
		
		assertNotNull(ruleUnitClass);
		
		data.insert(request);
		resultData.insert(resultHolder);
		
		executor.run(ruleUnitClass);
//		console.close();
		resultData.forEach(rd -> {
			assertEquals(request.getCorrelationId(),rd.getCorrelationId());
			assertEquals("OK",rd.getResultCode());
			if (rd.getSegmentationId() == null) {
				ScoreCard sc = rd.getResultValue("ScoreCard", null, ScoreCard.class).orElse(null);
				assertNotNull(sc);
				Map map = sc.getRanking();
				assertNotNull(map);
				assertTrue(map instanceof LinkedHashMap);
				
				LinkedHashMap ranking = (LinkedHashMap) map;

				assertTrue(ranking.containsKey("LX00"));
				assertTrue(ranking.containsKey("RES"));
				assertTrue(ranking.containsKey("CX2"));
				assertEquals(-1.0, ranking.get("LX00"));
				assertEquals(-10.0, ranking.get("RES"));
				assertEquals(-30.0, ranking.get("CX2"));

				Iterator iter = ranking.keySet().iterator();
				assertEquals("LX00", iter.next());
				assertEquals("RES", iter.next());
				assertEquals("CX2", iter.next());
			}
		});
		int segmentsExecuted = 0;
		for (Iterator<SegmentExecution> iter = childModelSegments.iterator(); iter.hasNext(); ) {
			SegmentExecution cms = iter.next();
			assertEquals(request.getCorrelationId(), cms.getCorrelationId());
			if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
		}
		assertEquals(1,segmentsExecuted);
	}

	@Test
	public void testWithRegression() {
		RuleUnitExecutor executor = createExecutor(source2);
//		KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();

		PMMLRequestData request = new PMMLRequestData("123", "SampleScorecardMine");
		request.addRequestParam("fld1r", 1.0);
		request.addRequestParam("fld2r", 1.0);
		request.addRequestParam("fld3r", "x");
		
		PMML4Result resultHolder = new PMML4Result();
		resultHolder.setCorrelationId(request.getCorrelationId());

		DataSource<PMMLRequestData> childModelRequest = executor.newDataSource("childModelRequest");
		DataSource<PMML4Result> childModelResults = executor.newDataSource("childModelResults");
		DataSource<SegmentExecution> childModelSegments = executor.newDataSource("childModelSegments");
		DataSource<? extends AbstractPMMLData> miningModelPojo = executor.newDataSource("miningModelPojo");
		

		List<String> possiblePackages = this.calculatePossiblePackageNames("SampleScorecardMine");
		Class<? extends RuleUnit> ruleUnitClass = this.getStartingRuleUnit("Start Mining - SampleScorecardMine",(InternalKnowledgeBase)kbase,possiblePackages);
		
		assertNotNull(ruleUnitClass);
		
		data.insert(request);
		resultData.insert(resultHolder);
		
		executor.run(ruleUnitClass);
//		console.close();
		resultData.forEach(rd -> {
			assertEquals(request.getCorrelationId(),rd.getCorrelationId());
			assertEquals("OK",rd.getResultCode());
			if (rd.getSegmentationId() == null) {
				System.out.println(rd);
				assertNotNull(rd.getResultValue("RegOut", null));
				String regOutValue = rd.getResultValue("RegOut", "value", String.class).orElse(null);
				assertEquals("catC",regOutValue);
				assertNotNull(rd.getResultValue("RegProb", null));
				Double regProbValue = rd.getResultValue("RegProb", "value", Double.class).orElse(null);
				assertEquals(0.709228,regProbValue,1e-6);
				assertNotNull(rd.getResultValue("RegProbA", null));
				Double regProbValueA = rd.getResultValue("RegProbA", "value", Double.class).orElse(null);
				assertEquals(0.010635,regProbValueA,1e-6);
			}
		});
		int segmentsExecuted = 0;
		for (Iterator<SegmentExecution> iter = childModelSegments.iterator(); iter.hasNext(); ) {
			SegmentExecution cms = iter.next();
			assertEquals(request.getCorrelationId(), cms.getCorrelationId());
			if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
		}
		assertEquals(1,segmentsExecuted);
		
	}

	@Test
	public void testSelectAll() {
		RuleUnitExecutor executor = createExecutor(source4);
//		KieRuntimeLogger console = ((InternalRuleUnitExecutor)executor).addConsoleLogger();

		PMMLRequestData request = new PMMLRequestData("1234", "SampleSelectAllMine");
		request.addRequestParam("age", 33.0);
		request.addRequestParam("occupation", "SKYDIVER");
		request.addRequestParam("residenceState", "KN");
		request.addRequestParam("validLicense", true);
		PMML4Result resultHolder = new PMML4Result();
		resultHolder.setCorrelationId(request.getCorrelationId());

		DataSource<PMMLRequestData> childModelRequest = executor.newDataSource("childModelRequest");
		DataSource<PMML4Result> childModelResults = executor.newDataSource("childModelResults");
		DataSource<SegmentExecution> childModelSegments = executor.newDataSource("childModelSegments");
		DataSource<? extends AbstractPMMLData> miningModelPojo = executor.newDataSource("miningModelPojo");
		

		List<String> possiblePackages = this.calculatePossiblePackageNames("SampleSelectAllMine");
		Class<? extends RuleUnit> ruleUnitClass = this.getStartingRuleUnit("Start Mining - SampleSelectAllMine",(InternalKnowledgeBase)kbase,possiblePackages);
		
		assertNotNull(ruleUnitClass);
		
		data.insert(request);
		resultData.insert(resultHolder);
		
		executor.run(ruleUnitClass);
//		console.close();
		resultData.forEach(rd -> {
			assertEquals("OK",rd.getResultCode());
			assertEquals(request.getCorrelationId(),rd.getCorrelationId());
			ScoreCard sc = rd.getResultValue("ScoreCard", null, ScoreCard.class).orElse(null);
			assertNotNull(sc);
			Map map = sc.getRanking();
			assertNotNull(map);
			assertTrue(map instanceof LinkedHashMap);
			LinkedHashMap ranking = (LinkedHashMap)map;
			assertTrue(ranking.containsKey("LX00") || ranking.containsKey("LC00"));
			if (ranking.containsKey("LX00")) {
				assertTrue(ranking.containsKey("RES"));
				assertTrue(ranking.containsKey("CX2"));
				assertEquals(-1.0, ranking.get("LX00"));
				assertEquals(-10.0, ranking.get("RES"));
				assertEquals(-30.0, ranking.get("CX2"));

				Iterator iter = ranking.keySet().iterator();
				assertEquals("LX00", iter.next());
				assertEquals("RES", iter.next());
				assertEquals("CX2", iter.next());
				assertEquals(41.345, sc.getScore(), 1e-6);
			} else {
				assertTrue(ranking.containsKey("RST"));
				assertTrue(ranking.containsKey("DX2"));
				assertEquals(-1.0, ranking.get("LC00"));
				assertEquals(10.0, ranking.get("RST"));
				assertEquals(-30.0, ranking.get("DX2"));

				Iterator iter = ranking.keySet().iterator();
				assertEquals("RST", iter.next());
				assertEquals("LC00", iter.next());
				assertEquals("DX2", iter.next());
				assertEquals(21.345, sc.getScore(), 1e-6);
			}
			
		});
		int segmentsExecuted = 0;
		for (Iterator<SegmentExecution> iter = childModelSegments.iterator(); iter.hasNext(); ) {
			SegmentExecution cms = iter.next();
			assertEquals(request.getCorrelationId(), cms.getCorrelationId());
			if (cms.getState() == SegmentExecutionState.COMPLETE) segmentsExecuted++;
		}
		assertEquals(2,segmentsExecuted);

		
	}


}
