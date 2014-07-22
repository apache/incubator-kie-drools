/*
 * Copyright 2012 JBoss by Red Hat.
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

package org.jbpm.services.task.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class TaskQueryManager {
	
	public static final String FILTER_KEY = "filter";
	public static final String ORDER_BY_KEY = "orderby";
	public static final String ASCENDING_KEY = "asc";
	public static final String DESCENDING_KEY = "desc";
	
	private Map<String, String> queries = new ConcurrentHashMap<String, String>();
	
	private static TaskQueryManager instance;
	
	public static TaskQueryManager get() {
		if (instance == null) {
			instance = new TaskQueryManager();
		}
		
		return instance;
	}
	
	protected TaskQueryManager() {
		addNamedQueries("META-INF/Taskorm.xml");
	}

	public synchronized void addNamedQueries(String ormFile) {
		try {
			parse(ormFile);
		} catch (XMLStreamException e) {
			throw new RuntimeException("Unable to read orm file due to " + e.getMessage(), e);
		}
	}
	
	public String getQuery(String name, Map<String, Object> params) {
		String query = null;
		if (!queries.containsKey(name)) {
			return null;
		}
		StringBuffer buf = new StringBuffer(queries.get(name)); 
		if (params != null && params.containsKey(FILTER_KEY)) {
			buf.append(" and " + params.get(FILTER_KEY));
		}
		if (params != null && params.containsKey(ORDER_BY_KEY)) {
			
			buf.append(" \n ORDER BY " + adaptOrderBy((String) params.get(ORDER_BY_KEY)));
			if (params.containsKey(ASCENDING_KEY)) {
				buf.append(" ASC");
			} else if (params.containsKey(DESCENDING_KEY)) {
				buf.append(" DESC");
			}
			query = buf.toString();
		}
		
		return query;
	}
	
	protected void parse(String ormFile) throws XMLStreamException {
		String name = null;
		StringBuffer tagContent = new StringBuffer();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(ormFile));

		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if ("named-query".equals(reader.getLocalName())) {

					name = reader.getAttributeValue(0);
				}
				break;

			case XMLStreamConstants.CHARACTERS:
				if (name != null) {
					tagContent.append(" " + reader.getText().trim());
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				if ("named-query".equals(reader.getLocalName())) {
					String origQuery = tagContent.toString();
					String alteredQuery = origQuery;
					int orderByIndex = origQuery.toLowerCase().indexOf("order by");
					if (orderByIndex != -1) {
						// remove order by clause as it will be provided on request
						alteredQuery = origQuery.substring(0, orderByIndex);
					}
					queries.put(name, alteredQuery);
					name = null;
					tagContent = new StringBuffer();
				}
				break;
			}
		}
	}
	
	private String adaptOrderBy(String orderBy) {
		if (orderBy != null) {
			if (orderBy.equals("Task")) {
				return "t.name";
			} else if (orderBy.equals("Description")) {
				return "t.description";
			} else if (orderBy.equals("Id")) {
				return "t.id";
			} else if (orderBy.equals("Priority")) {
				return "t.priority";
			} else if (orderBy.equals("Status")) {
				return "t.taskData.status";
			} else if (orderBy.equals("CreatedOn")) {
				return "t.taskData.createdOn";
			} else if (orderBy.equals("CreatedBy")) {
				return "t.taskData.createdBy.id";
			} else if (orderBy.equals("DueOn")) {
				return "t.taskData.expirationTime";
			}
		}
		return orderBy;
	}
}
