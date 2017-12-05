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
package org.kie.pmml.pmml_4_2.model.regression;

import org.kie.api.definition.type.PropertyReactive;

/*
 * declare @{ pmmlPackageName }.RegTargetBit
@propertyReactive
    context         : String        @key
    target          : String
    value           : String
    weight          : double
    normalized      : boolean       = false
    cumulative      : boolean       = false
    index           : int           @key
end

 */
@PropertyReactive
public class RegTargetBit {
	private String context;
	private String target;
	private String value;
	private Double weight;
	private boolean normalized = false;
	private boolean cumulative = false;
	private int index;
	
	public RegTargetBit() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	public RegTargetBit(String context, String target, String value, Double weight, boolean normalized,
			boolean cumulative, int index) {
		this.context = context;
		this.target = target;
		this.value = value;
		this.weight = weight;
		this.normalized = normalized;
		this.cumulative = cumulative;
		this.index = index;
	}



	public RegTargetBit(String context, int index) {
		this.context = context;
		this.index = index;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public boolean isNormalized() {
		return normalized;
	}

	public void setNormalized(boolean normalized) {
		this.normalized = normalized;
	}

	public boolean isCumulative() {
		return cumulative;
	}

	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + index;
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
		RegTargetBit other = (RegTargetBit) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (index != other.index) {
			return false;
		}
		return true;
	}

	
}
