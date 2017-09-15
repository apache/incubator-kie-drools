/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.pmml.pmml_4_2.model.datatypes;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@Role(Type.EVENT)
public abstract class PMML4Data<T> implements PMML4DataType {
	private T value;
	private String name;
	private boolean valid = false;
	private boolean missing = false;
	private boolean placeholder = false;
	private String context;
	private String displayName;
	private Double weight = 1.0;
	
	protected PMML4Data(String name, String context, boolean placeholder) {
		this.name = name;
		this.context = context;
		this.placeholder = placeholder;
	}
	
	protected PMML4Data(String name, String context, String displayName, T value) {
		this.name = name;
		this.context = context;
		this.displayName = displayName;
		this.value = value;
	}
	
	protected PMML4Data(String name, String context, String displayName, T value, Double weight) {
		this.name = name;
		this.context = context;
		this.displayName = displayName;
		this.value = value;
		this.weight = weight;
	}
	
	protected PMML4Data(String name, String context, String displayName, T value, Double weight, Boolean valid, Boolean missing) {
		this.name = name;
		this.context = context;
		this.displayName = displayName;
		this.value = value;
		this.weight = weight;
		this.valid = (valid != null) ? valid : false;
		this.missing = (missing != null) ? missing : false;
	}
	
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCapitalizedName() {
		return this.name.substring(0, 1).toUpperCase()+this.name.substring(1);
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public boolean isMissing() {
		return missing;
	}
	public void setMissing(boolean missing) {
		this.missing = missing;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getDisplayValue() {
		return displayName;
	}
	public void setDisplayValue(String displayValue) {
		this.displayName = displayValue;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Class<?> getValueClass() {
		return (value != null) ? value.getClass() : null;
	}
	public boolean isPlaceholder() {
		return this.placeholder;
	}
	public void setPlaceholder(Boolean placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + (missing ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (valid ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		result = prime * result + (placeholder ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PMML4Data other = (PMML4Data) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (displayName == null) {
			if (other.displayName != null) {
				return false;
			}
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (missing != other.missing) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (valid != other.valid) {
			return false;
		}
		if (placeholder != other.placeholder) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		if (weight == null) {
			if (other.weight != null) {
				return false;
			}
		} else if (!weight.equals(other.weight)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"( name="+this.getName()+", context="
				+this.getContext()+", displayName="+this.getDisplayValue()
				+", missing="+this.isMissing()+", valid="+this.isValid()
				+", value="+this.getValue().toString()+", weight="
				+this.getWeight()+" )";
	}
	
}
