package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

import java.util.ArrayList;

import org.drools.base.ClassObjectType;
import org.drools.rule.Rule;

/**
 * Wrapper class to drools generic rule to extract matching elements from it to
 * use during leaps iterations.
 * 
 * @author Alexander Bagerman
 * 
 */
class LeapsRule {
	Rule rule;

	final ColumnConstraints[] columns;

	final ColumnConstraints[] notColumns;

	final ColumnConstraints[] existsColumns;

	boolean notColumnsPresent;

	boolean existsColumnsPresent;

	public LeapsRule(Rule rule, ArrayList columns, ArrayList notColumns,
			ArrayList existsColumns) {
		this.rule = rule;
		this.columns = (ColumnConstraints[]) columns.toArray(new ColumnConstraints[0]);
		this.notColumns = (ColumnConstraints[]) notColumns.toArray(new ColumnConstraints[0]);
		this.existsColumns = (ColumnConstraints[]) existsColumns.toArray(new ColumnConstraints[0]);
		this.notColumnsPresent = (notColumns.size() != 0);
		this.existsColumnsPresent = (existsColumns.size() != 0);
	}

	Rule getRule() {
		return this.rule;
	}

	int getNumberOfColumns() {
		return this.columns.length;
	}

	int getNumberOfNotColumns() {
		return this.notColumns.length;
	}

	int getNumberOfExistsColumns() {
		return this.existsColumns.length;
	}

	ClassObjectType getColumnClassObjectTypeAtPosition(int idx) {
		return (ClassObjectType) this.columns[idx].getColumn().getObjectType();
	}

	ColumnConstraints getColumnConstraintsAtPosition(int idx) {
		return this.columns[idx];
	}

	ColumnConstraints[] getNotColumnConstraints() {
		return this.notColumns;
	}

	ColumnConstraints[] getExistsColumnConstraints() {
		return this.existsColumns;
	}

	boolean containsNotColumns() {
		return this.notColumnsPresent;
	}

	boolean containsExistsColumns() {
		return this.existsColumnsPresent;
	}
}
