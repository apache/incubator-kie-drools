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

package org.jbpm.shared.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class QueryManagerTest {

	@Test
	public void testLoadQueriesNotFound() {
		QueryManager manager = new QueryManager();
		
		manager.addNamedQueries("test-orm.xml");
		
		String query = manager.getQuery("test-query-1", null);
		assertNull(query);
	}
	
	@Test
	public void testLoadQueriesFound() {
		QueryManager manager = new QueryManager();
		
		manager.addNamedQueries("test-orm.xml");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderby", "log.date");
		String query = manager.getQuery("test-query-3", params);
		assertNotNull(query);
		assertTrue(query.endsWith("ORDER BY log.date"));
	}
	
	@Test
	public void testLoadQueriesFoundAsc() {
		QueryManager manager = new QueryManager();
		
		manager.addNamedQueries("test-orm.xml");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderby", "log.date");
		params.put("asc", "true");
		String query = manager.getQuery("test-query-3", params);
		assertNotNull(query);
		assertTrue(query.endsWith("ORDER BY log.date ASC"));
	}
	
	@Test
	public void testLoadQueriesFoundDesc() {
		QueryManager manager = new QueryManager();
		
		manager.addNamedQueries("test-orm.xml");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderby", "log.date");
		params.put("desc", "true");
		String query = manager.getQuery("test-query-3", params);
		assertNotNull(query);
		assertTrue(query.endsWith("ORDER BY log.date DESC"));
	}
}
