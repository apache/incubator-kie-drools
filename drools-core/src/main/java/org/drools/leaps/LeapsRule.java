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
import java.util.Iterator;

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

	ColumnConstraints[] columns = new ColumnConstraints[0];

	boolean notColumnsPresent;

	Iterator notColumnsIterator;

	Iterator existsColumnsIterator;

	boolean existsColumnsPresent;

	public LeapsRule(Rule rule, ArrayList columns, ArrayList notColumns,
			ArrayList existsColumns) {
		this.rule = rule;
		this.columns = (ColumnConstraints[]) columns.toArray(this.columns);
		this.notColumnsIterator = notColumns.iterator();
		this.notColumnsPresent = (notColumns.size() != 0);
		this.existsColumnsIterator = existsColumns.iterator();
		this.existsColumnsPresent = (existsColumns.size() != 0);
	}

	public Rule getRule() {
		return this.rule;
	}

	public int getNumberOfColumns() {
		return this.columns.length;
	}

	public ClassObjectType getColumnClassObjectTypeAtPosition(int idx) {
		return (ClassObjectType) this.columns[idx].getColumn().getObjectType();
	}

	public ColumnConstraints getColumnConstraintsAtPosition(int idx) {
		return this.columns[idx];
	}

	public Iterator getNotColumnsIterator() {
		return this.notColumnsIterator;
	}

	public Iterator getExistsColumnsIterator() {
		return this.existsColumnsIterator;
	}

	public boolean containsNotColumns() {
		return this.notColumnsPresent;
	}

	public boolean containsExistsColumns() {
		return this.existsColumnsPresent;
	}
}
