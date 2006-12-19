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
import org.antlr.stringtemplate.StringTemplate;

public class Cell {
	Row row;

	Object value;

	Column column;

	public Cell() {
		
	}
	Cell(Row r, Column c, String v) {
		row = r;
		column = c;
		value = c.getValue(v);
	}

	public String toString() {
		return "Cell[" + column + ": " + value + "]";
	}

	public Row getRow() {
		return row;
	}

	public String getColumn() {
		return column.getName();
	}

	public Object getValue() {
		return value;
	}

	public void addValue(StringTemplate t) {
		column.addValue(t, value);
	}
}