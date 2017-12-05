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
package org.drools.pmml.pmml_4_2.model.mining;

import static org.junit.Assert.*;

import org.drools.pmml.pmml_4_2.PMML4Result;
import org.drools.pmml.pmml_4_2.model.PMMLRequestData;
import org.junit.Before;
import org.junit.Test;

public class MiningSegmentTransferTest {
	private PMML4Result simpleResult;
	private PMML4Result complexResult;
	
	public class MyResult {
		private String varA;
		private int varB;
		
		public MyResult(String varA, int varB) {
			this.varA = varA;
			this.varB = varB;
		}

		public String getVarA() {
			return varA;
		}

		public void setVarA(String varA) {
			this.varA = varA;
		}

		public int getVarB() {
			return varB;
		}

		public void setVarB(int varB) {
			this.varB = varB;
		}
	}
	
	@Before
	public void setup() {
		simpleResult = new PMML4Result();
		simpleResult.setCorrelationId("1234");
		simpleResult.setResultCode("OK");
		simpleResult.setSegmentationId("Segmentation_1");
		simpleResult.setSegmentId("SEGMENT_1");
		simpleResult.addResultVariable("var1", new Integer(100));
		simpleResult.addResultVariable("var2", "Just some string");
		
		complexResult = new PMML4Result();
		complexResult.setCorrelationId("7890");
		complexResult.setResultCode("OK");
		complexResult.setSegmentationId("Segmentation_1");
		complexResult.setSegmentId("SEGMENT_1");
		complexResult.addResultVariable("firstObject", "Just a string");
		complexResult.addResultVariable("myComplex", new MyResult("a test string",101));
	}
	
	private void doBaselineAssertions(PMML4Result reference, MiningSegmentTransfer mst) {
		assertNotNull(mst);
		assertEquals(reference.getCorrelationId(),mst.getCorrelationId());
		assertEquals(reference.getSegmentationId(),mst.getSegmentationId());
		assertEquals(reference.getSegmentId(),mst.getFromSegmentId());
		assertEquals("SEGMENT_2",mst.getToSegmentId());
		assertNotNull(mst.getResultFieldNameToRequestFieldName());
	}

	@Test
	public void testSimpleResult() {
		MiningSegmentTransfer mst = new MiningSegmentTransfer(simpleResult,"SEGMENT_2");
		doBaselineAssertions(simpleResult,mst);
	}

	@Test
	public void testSimpleWithFieldNamesMap() {
		MiningSegmentTransfer mst = new MiningSegmentTransfer(simpleResult,"SEGMENT_2");
		mst.addResultToRequestMapping("var1", "someVarA");
		doBaselineAssertions(simpleResult,mst);
		assertEquals(1,mst.getResultFieldNameToRequestFieldName().size());
	}
	
	@Test
	public void testComplexResult() {
		MiningSegmentTransfer mst = new MiningSegmentTransfer(complexResult,"SEGMENT_2");
		mst.addResultToRequestMapping("firstObject", "object1");
		mst.addResultToRequestMapping("myComplex.varA", "stringFromMyComplex");
		mst.addResultToRequestMapping("myComplex.varB", "intValue");
		doBaselineAssertions(complexResult,mst);
		assertEquals(3,mst.getResultFieldNameToRequestFieldName().size());
		PMMLRequestData rqst = mst.getOutboundRequest();
		assertNotNull(rqst);
		assertEquals(complexResult.getCorrelationId(),rqst.getCorrelationId());
	}
}
