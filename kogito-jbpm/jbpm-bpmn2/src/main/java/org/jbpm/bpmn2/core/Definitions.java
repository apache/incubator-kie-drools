/**
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

package org.jbpm.bpmn2.core;

import java.io.Serializable;
import java.util.*;

public class Definitions implements Serializable {
	
	private static final long serialVersionUID = 4L;
	
	private String targetNamespace;
	private List<DataStore> dataStores;
	private List<Association> associations;

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public void setDataStores(List<DataStore> dataStores) {
		this.dataStores = dataStores;
	}
	
	public List<DataStore> getDataStores() {
		return this.dataStores;
	}
	
	public void setAssociations(List<Association> associations) {
		this.associations = associations;
	}
	
	public List<Association> getAssociations() {
		return this.associations;
	}
}
