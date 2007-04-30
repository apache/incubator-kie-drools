package org.drools.decisiontable.parser;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Map;

import org.drools.util.StringUtils;
/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * A column in a decision table that represents an array (comma-delimited)
 * of values.
 */
public class ArrayColumn extends Column {

	public void addValue(Map vars, Object value) {

		String[] values = ((String[]) value);
		for (int i = 0; i < values.length; i++) {
			vars.put(getName() + i, values[i]);
		}
	}

	public ArrayColumn(String n) {
		super(n);
	}

	public Object getValue(String cellValue) {
		return StringUtils.splitPreserveAllTokens(cellValue, ",");
	}

}
