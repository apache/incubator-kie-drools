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
/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Factory to produce a column of the correct type based on its declaration.
 * [] indicates a column that represents an array (comma-delimited) of values.
 */
public class ColumnFactory {

	public Column getColumn(String value) {
		if (value.endsWith("[]")) {
			return new ArrayColumn(value.substring(0, value.length() - 2));
		}
		return new Column(value);
	}
}
