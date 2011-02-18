/*
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.xml.jaxb.util.JaxbMapAdapter;

@XmlJavaTypeAdapter(JaxbMapAdapter.class)
public class ExecutionResultsMap {

	Map<String, Object> results = new HashMap<String, Object>();

	public Collection<String> keySet() {
		return results.keySet();
	}

	public Object get(String identifier) {
		return results.get(identifier);
	}

	public Map<String, Object> getResults() {
		return this.results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;

	}

}
