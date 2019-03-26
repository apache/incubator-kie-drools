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

package org.jbpm.services.task.persistence;

import static org.kie.internal.query.QueryParameterIdentifiers.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskQueryManager {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskQueryManager.class);
	
	private Map<String, String> queries = new ConcurrentHashMap<String, String>();
	
	private static TaskQueryManager instance = new TaskQueryManager();
	
	public static TaskQueryManager get() {
		return instance;
	}
	
	protected TaskQueryManager() {
		addNamedQueries("META-INF/Taskorm.xml");
	}

	public synchronized void addNamedQueries(String ormFile) {
		try {
			parse(ormFile);
		} catch (Exception e) {
			logger.debug("TaskQueryManager unable to read Taskorm file due to " + e.getMessage(), e);
			logger.warn("TaskQueryManager unable to read Taskorm file due to " + e.getMessage());
		}
	}
	
	public String getQuery(String name, Map<String, Object> params) {
		String query = null;
		if (!queries.containsKey(name)) {
			return null;
		}
		StringBuilder buf = new StringBuilder(queries.get(name)); 
		query = adaptQueryString(buf, params);
		
		return query;
	}

	public static String adaptQueryString(StringBuilder buf, Map<String, Object> params) { 
	    StringBuilder query = null;
        if (params != null && params.containsKey(FILTER)) {
            buf.append(" and " + params.get(FILTER));
            query = buf;
        }
        if (params != null && params.containsKey(ORDER_BY)) {
            buf.append(" ORDER BY " + adaptOrderBy((String) params.get(ORDER_BY)));
            Object orderTypeObj = params.get(ORDER_TYPE);
            if (orderTypeObj != null ) { 
                buf.append(" ").append(orderTypeObj);
            }
            query = buf;
        }
        return (query == null ? null : query.toString() );
	}
	
	protected void parse(String ormFile) throws XMLStreamException {
		String name = null;
		StringBuilder tagContent = new StringBuilder();
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
					tagContent.append(reader.getText());
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
					tagContent = new StringBuilder();
				}
				break;
			}
		}
	}
	
	private static String adaptOrderBy(String orderBy) {
		if (orderBy != null) {
			if (orderBy.equalsIgnoreCase("Task")) {
				return "t.name";
			} else if (orderBy.equalsIgnoreCase("Description")) {
				return "t.description";
			} else if (orderBy.equalsIgnoreCase("Id")) {
				return "t.id";
			} else if (orderBy.equalsIgnoreCase("Priority")) {
				return "t.priority";
			} else if (orderBy.equalsIgnoreCase("Status")) {
				return "t.taskData.status";
			} else if (orderBy.equalsIgnoreCase("CreatedOn")) {
				return "t.taskData.createdOn";
			} else if (orderBy.equalsIgnoreCase("CreatedBy")) {
				return "t.taskData.createdBy.id";
			} else if (orderBy.equalsIgnoreCase("DueOn")) {
				return "t.taskData.expirationTime";
			} else if (orderBy.equalsIgnoreCase("ProcessInstanceId")) {
				return "t.taskData.processInstanceId";
			} 
		}
		return orderBy;
	}
}
