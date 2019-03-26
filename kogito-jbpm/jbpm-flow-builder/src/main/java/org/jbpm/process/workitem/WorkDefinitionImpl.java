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

package org.jbpm.process.workitem;

import java.util.HashMap;
import java.util.Map;

public class WorkDefinitionImpl extends org.jbpm.process.core.impl.WorkDefinitionExtensionImpl {

	private static final long serialVersionUID = 5L;
	
	private String[] dependencies;
	private String[] mavenDependencies;
	private String description;
	private String defaultHandler;
	private String category;
	private String path;
	private String file;
	private String documentation;
	private String iconEncoded;
	private String version;
	private String widType;
	private Map<String, Object> parameterValues = new HashMap<>();
	
	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String[] getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(String[] dependencies) { this.dependencies = dependencies; }
	
	public String getDefaultHandler() {
		return defaultHandler;
	}
	
	public void setDefaultHandler(String defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public String getIconEncoded() {
		return iconEncoded;
	}

	public void setIconEncoded(String iconEncoded) {
		this.iconEncoded = iconEncoded;
	}

	public String[] getMavenDependencies() {
		return mavenDependencies;
	}

	public void setMavenDependencies(String[] mavenDependencies) {
		this.mavenDependencies = mavenDependencies;
	}

	public String getVersion() { return version; }

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public String getWidType() { return widType; }

	public void setWidType(String widType) { this.widType = widType; }

	public Map<String, Object> getParameterValues() {
		return this.parameterValues;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues.clear();
		if(parameterValues != null) {
			this.parameterValues.putAll(parameterValues);
		}

	}

	public void addParameterValue(String key, Object value) {
		this.parameterValues.put(key, value);
	}

	public void removeParameterValue(String key) {
		this.parameterValues.remove(key);
	}

	public String[] getParameterValueNames() {
		return (String[])this.parameterValues.keySet().toArray(new String[this.parameterValues.size()]);
	}
	
}
