/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.io.ReaderResource;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ErrorOnInsertLogicalTest {
	
	private static final String RULES_FILE =
			"package rules\n" +
			"import " + FieldObject.class.getCanonicalName() + "\n" +
			"\n" +
			"function String f0(FieldObject objectToTest) {\n" +
			"    if (objectToTest.getValue().equals(\"TriggerError\")) {\n" +
			"        throw new RuntimeException(\"Forced error triggered.\");\n" +
			"    }\n" +
			"    return \"There is no error: \" + objectToTest.getValue();\n" +
			"}\n" +
			"\n" +
			"rule R1 when\n" +
			"    o1: FieldObject(getKey() == \"o1\" && getValue() == \"ABC\")\n" +
			"    o2: FieldObject(getKey() == \"o2\")\n" +
			"then\n" +
			"    insertLogical(new FieldObject(\"o3\", f0(o2)));\n" +
			"end;";
	
	/**
	 * Test object to use in rules.
	 */
	public static class FieldObject {
		private String key;
		private String value; 
		
		public FieldObject(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	@Test
	public void testErrorHandling() {
		// DROOLS-7175
		KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
		KnowledgeBuilderImpl kbuilder = (KnowledgeBuilderImpl)KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(new ReaderResource(new StringReader(RULES_FILE)), ResourceType.DRL);
		
		KieBase kb = kbuilder.newKnowledgeBase(config);
		KieSession session = kb.newKieSession();
		
		// Setup the rules to create a new object.
		FieldObject o1 = new FieldObject("o1", "ABC");
		FieldObject o2 = new FieldObject("o2", "1");
		FactHandle fh1 = session.insert(o1);
		FactHandle fh2 = session.insert(o2);
		
		// Create an 'o3' object with rule running.  It is tied to the rule execution.
		session.fireAllRules();
 		List<FieldObject> createdObjects = session.getObjects().stream().map(o -> (FieldObject)o).filter(o -> o.getKey().equals("o3")).collect(Collectors.toList());
		assertThat(createdObjects.size()).isEqualTo(1);
		assertThat(createdObjects.get(0).getKey()).isEqualTo("o3");
		assertThat(createdObjects.get(0).getValue()).isEqualTo("There is no error: 1");
		
		// Trigger the error.  This will throw a runtime exception when trying the actions.
		o2.setValue("TriggerError");
		session.update(fh1, o1);
		session.update(fh2, o2);
		try {
			session.fireAllRules();
			fail("this firing should throw an exception");
		} catch (RuntimeException e) {
			assertThat(e.getCause().getMessage()).isEqualTo("Forced error triggered.");
		}

		// Since the rule didn't run successfully, the old value to still be there.
		createdObjects = session.getObjects().stream().map(o -> (FieldObject)o).filter(o -> o.getKey().equals("o3")).collect(Collectors.toList());
		assertThat(createdObjects.size()).isEqualTo(1);
		assertThat(createdObjects.get(0).getKey()).isEqualTo("o3");
		assertThat(createdObjects.get(0).getValue()).isEqualTo("There is no error: 1");
		
		// Set the inputs back to create an object.  It will remove the old and make a new.
		o2.setValue("2");
		session.update(fh1, o1);
		session.update(fh2, o2);
		session.fireAllRules();
		
		// Test that the old product has been removed via insertLogical undo and a new one created.
		createdObjects = session.getObjects().stream().map(o -> (FieldObject)o).filter(o -> o.getKey().equals("o3")).collect(Collectors.toList());
		assertThat(createdObjects.size()).isEqualTo(1);
		assertThat(createdObjects.get(0).getKey()).isEqualTo("o3");
		assertThat(createdObjects.get(0).getValue()).isEqualTo("There is no error: 2");
	}
}