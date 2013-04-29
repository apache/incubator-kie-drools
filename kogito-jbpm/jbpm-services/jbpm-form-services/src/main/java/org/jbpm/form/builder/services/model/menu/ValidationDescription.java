/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.form.builder.services.model.menu;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.form.builder.services.model.Mappable;

public class ValidationDescription implements Mappable {

    private String className;
    private Map<String, String> properties;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

	@Override
	public Map<String, Object> getDataMap() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("validationClassName", className);
		if (properties != null) {
			dataMap.putAll(properties);
		}
		return dataMap;
	}

	@Override
	public void setDataMap(Map<String, Object> dataMap) {
		this.className = String.valueOf(dataMap.get("validationClassName"));
		dataMap.remove("validationClassName");
		if (!dataMap.isEmpty()) {
			properties.clear();
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				properties.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}
	}
}
