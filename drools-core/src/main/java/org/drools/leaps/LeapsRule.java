package org.drools.leaps;

import java.util.ArrayList;
import java.util.Iterator;

import org.drools.rule.Rule;
import org.drools.spi.ClassObjectType;


/**
 * Wrapper class to drools generic rule to extract matching elements from it to
 * use during leaps iterations.
 * 
 * @author Alexander Bagerman
 * 
 */
class LeapsRule {
	Rule rule;

	ArrayList columns;

	ArrayList notColumns;

	ArrayList existsColumns;

	public LeapsRule(Rule rule, ArrayList columns, ArrayList notColumns,
			ArrayList existsColumns) {
		this.rule = rule;
		this.columns = columns;
		this.notColumns = notColumns;
		this.existsColumns = existsColumns;
	}

	public Rule getRule() {
		return rule;
	}

	public int getNumberOfColumns() {
		return this.columns.size();
	}

	public ClassObjectType getColumnClassObjectTypeAtPosition(int idx) {
		return (ClassObjectType) ((ColumnConstraints) this.columns.get(idx))
				.getColumn().getObjectType();
	}

	public ColumnConstraints getColumnConstraintsAtPosition(int idx) {
		return (ColumnConstraints) this.columns.get(idx);
	}

	public int getNumberOfNotColumns() {
		return this.notColumns.size();
	}

	public Iterator getColumnsIterator() {
		return this.columns.iterator();
	}

	public Iterator getNotColumnsIterator() {
		return this.notColumns.iterator();
	}

	public Iterator getExistsColumnsIterator() {
		return this.existsColumns.iterator();
	}

	public ClassObjectType getNotColumnClassObjectTypeAtPosition(int idx) {
		return (ClassObjectType) ((ColumnConstraints) this.notColumns.get(idx))
				.getColumn().getObjectType();
	}

	public ColumnConstraints getNotColumnConstraintsAtPosition(int idx) {
		return (ColumnConstraints) this.notColumns.get(idx);
	}

	public int getNumberOfExistsColumns() {
		return this.existsColumns.size();
	}

	public ClassObjectType getExistsColumnClassObjectTypeAtPosition(int idx) {
		return (ClassObjectType) ((ColumnConstraints) this.existsColumns
				.get(idx)).getColumn().getObjectType();
	}

	public ColumnConstraints getExistsColumnConstraintsAtPosition(int idx) {
		return (ColumnConstraints) this.existsColumns.get(idx);
	}

	public boolean containsNotColumns() {
		return this.notColumns.size() != 0;
	}

	public boolean containsExistsColumns() {
		return this.existsColumns.size() != 0;
	}
}
