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

/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * A column in a decision table
 */
public class Column {
	private String name;

	public Column(String n) {
		this.name = n;
	}

	public String getName() {
		return name;
	}

	public Object getValue(String cellValue) {
		return cellValue;
	}

	public void addValue(Map vars, Object value) {
		vars.put(name, value);
	}

}
