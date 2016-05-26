/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.parser;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ParserWorkItemHandlerTest {

	final int AGE = 27;
	final String NAME = "William";
	final String PERSON_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><age>"
			+ AGE + "</age><name>" + NAME + "</name></person>";
	final String PERSON_JSON = "{\"name\":\"" + NAME + "\",\"age\":" + AGE
			+ "}";

	ParserWorkItemHandler handler;

	@Before
	public void init() {
		handler = new ParserWorkItemHandler();
	}

	@Test
	public void testXmlToObject() {
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter(ParserWorkItemHandler.INPUT, PERSON_XML);
		workItem.setParameter(ParserWorkItemHandler.FORMAT, ParserWorkItemHandler.XML);
		workItem.setParameter(ParserWorkItemHandler.TYPE,
				"org.jbpm.process.workitem.parser.Person");
		handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
		Person result = (Person) workItem.getResult(ParserWorkItemHandler.RESULT);
		assertEquals(AGE, result.getAge());
		assertEquals(NAME, result.getName());
	}

	@Test
	public void testObjectToXml() {
		Person p = new Person(NAME, AGE);
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter(ParserWorkItemHandler.INPUT, p);
		workItem.setParameter(ParserWorkItemHandler.FORMAT, ParserWorkItemHandler.XML);
		handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
		String result = (String) workItem.getResult(ParserWorkItemHandler.RESULT);
		assertEquals(PERSON_XML, result);
	}

	@Test
	public void testJsonToObject() {
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter(ParserWorkItemHandler.INPUT, PERSON_JSON);
		workItem.setParameter(ParserWorkItemHandler.FORMAT, ParserWorkItemHandler.JSON);
		workItem.setParameter(ParserWorkItemHandler.TYPE,
				"org.jbpm.process.workitem.parser.Person");
		handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
		Person result = (Person) workItem.getResult(ParserWorkItemHandler.RESULT);
		assertEquals(AGE, result.getAge());
		assertEquals(NAME, result.getName());
	}

	@Test
	public void testObjectToJson() {
		Person p = new Person(NAME, AGE);
		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter(ParserWorkItemHandler.INPUT, p);
		workItem.setParameter(ParserWorkItemHandler.FORMAT, ParserWorkItemHandler.JSON);
		handler.executeWorkItem(workItem, new TestWorkItemManager(workItem));
		String result = (String) workItem.getResult(ParserWorkItemHandler.RESULT);
		assertEquals(PERSON_JSON, result);
	}

	private class TestWorkItemManager implements WorkItemManager {

		private WorkItem workItem;

		TestWorkItemManager(WorkItem workItem) {
			this.workItem = workItem;
		}

		public void completeWorkItem(long id, Map<String, Object> results) {
			((WorkItemImpl) workItem).setResults(results);

		}

		public void abortWorkItem(long id) {

		}

		public void registerWorkItemHandler(String workItemName,
				WorkItemHandler handler) {

		}
	}

}
