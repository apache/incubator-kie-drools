/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;

public class DroolsDeclaredTypeSequenceFlowExpressionTest {
	
	@Test
	public void testDeclaredTypesInSequenceFlowDroolsExpression() {
		//DROOLS-1327	
		
		String drl = "package org.drools.test;" 
			+ "declare TestFact \n"
			+ "value: Integer\n"
			+ "end\n";
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();		
		
		Resource drlResource = new InputStreamResource(new ByteArrayInputStream(drl.getBytes(StandardCharsets.UTF_8))); 
		Resource bpmn2Resource = new ClassPathResource("BPMN2-DroolsDeclaredTypeSequenceFlowExpressionTest.bpmn2");
		
		ckbuilder.add(drlResource, ResourceType.DRL);
		ckbuilder.add(bpmn2Resource, ResourceType.BPMN2);
		
		ckbuilder.build();
		
		//Assert that we don't have any issues in the build.
		if (kbuilder.hasErrors()) {
			KnowledgeBuilderErrors kbErrors = kbuilder.getErrors();
			for (KnowledgeBuilderError nextError: kbErrors) {
				fail(nextError.getMessage());
			}	
		}
		
		if(kbuilder.hasResults(ResultSeverity.WARNING)) {
			KnowledgeBuilderResults kbResults = kbuilder.getResults(ResultSeverity.WARNING);
			for (KnowledgeBuilderResult nextResult: kbResults) {
				fail(nextResult.getMessage());
			}
		}
	}
	
}
