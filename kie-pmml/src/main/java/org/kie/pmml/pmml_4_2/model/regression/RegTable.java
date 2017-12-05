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

import java.util.Arrays;

import org.kie.api.definition.type.PropertyReactive;

/*
 * declare @{ pmmlPackageName }.RegTable
@propertyReactive
    context         : String        @key
    target          : String
    category        : String
    numCoeffs       : double[]
    numExps         : double[]
    catCoeffs       : double[]
    trmCoeffs       : double[]
    intercept       : double
    index           : int           @key
end

 */
@PropertyReactive
public class RegTable {
	private String context;
	private String target;
	private String category;
	private Double[] numCoeffs;
	private Double[] numExps;
	private Double[] catCoeffs;
	private Double[] trmCoeffs;
	private Double intercept;
	private int index;
	
	public RegTable() {
		// TODO Auto-generated constructor stub
	}
	
	public RegTable(String context, int index) {
		this.context = context;
		this.index = index;
	}
	
	public RegTable(String context, String target, String category, double[] numCoeffs, double[] numExps,
			double[] catCoeffs, double[] trmCoeffs, double intercept, int index) {
		this.context = context;
		this.target = target;
		this.category = category;
		this.numCoeffs = toBoxedArray(numCoeffs);
		this.numExps = toBoxedArray(numExps);
		this.catCoeffs = toBoxedArray(catCoeffs);
		this.trmCoeffs = toBoxedArray(trmCoeffs);
		this.intercept = intercept;
		this.index = index;
	}
	
	private Double[] toBoxedArray(double[] source) {
		Double[] target = new Double[source.length];
		for (int x = 0; x < source.length; x++) {
			target[x] = source[x];
		}
		return target;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double[] getNumCoeffs() {
		return numCoeffs;
	}

	public void setNumCoeffs(Double[] numCoeffs) {
		this.numCoeffs = numCoeffs;
	}

	public Double[] getNumExps() {
		return numExps;
	}

	public void setNumExps(Double[] numExps) {
		this.numExps = numExps;
	}

	public Double[] getCatCoeffs() {
		return catCoeffs;
	}

	public void setCatCoeffs(Double[] catCoeffs) {
		this.catCoeffs = catCoeffs;
	}

	public Double[] getTrmCoeffs() {
		return trmCoeffs;
	}

	public void setTrmCoeffs(Double[] trmCoeffs) {
		this.trmCoeffs = trmCoeffs;
	}

	public Double getIntercept() {
		return intercept;
	}

	public void setIntercept(Double intercept) {
		this.intercept = intercept;
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
		RegTable other = (RegTable) obj;
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
