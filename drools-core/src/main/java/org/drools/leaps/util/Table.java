package org.drools.leaps.util;

/*
 * Copyright 2005 Alexander Bagerman
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

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.WorkingMemory;
import org.drools.leaps.ColumnConstraints;

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class Table {

	private final TreeMap map;

	protected TableRecord headRecord;

	protected TableRecord tailRecord;

	protected final static int EQUAL_OR_LESS = -1;

	protected final static int EQUAL_OR_GREATER = 1;

	private boolean empty = true;

	private int count = 0;

	public Table(Comparator comparator) {
		this.map = new TreeMap(comparator);
	}

	protected void clear() {
		this.headRecord = new TableRecord(null);
		this.empty = true;
		this.count = 0;
		this.map.clear();
	}

	/**
	 * @param object
	 *            to add
	 */
	public void add(Object object) {
		boolean foundEqualObject = false;
		TableRecord newRecord = new TableRecord(object);
		if (this.empty) {
			this.headRecord = newRecord;
			this.empty = false;
		} else {
			SortedMap bufMap = this.map.headMap(object);
			if (!bufMap.isEmpty()) {
				TableRecord bufRec = (TableRecord) this.map.get(bufMap.lastKey());
				if (bufRec.right != null) {
					bufRec.right.left = newRecord;
				}
				newRecord.right = bufRec.right;
				bufRec.right = newRecord;
				newRecord.left = bufRec;

			} else {
				this.headRecord.left = newRecord;
				newRecord.right = this.headRecord;
				this.headRecord = newRecord;
			}
		}
		if (!foundEqualObject) {
			// check if the new record was added at the end of the list
			// and assign new value to the tail record
			if (newRecord.right == null) {
				this.tailRecord = newRecord;
			}
			//
			this.count++;
			//
			this.map.put(object, newRecord);
		}
	}

	/**
	 * Removes object from the table
	 * 
	 * @param object
	 *            to remove from the table
	 */
	public void remove(Object object) {
		if (!this.empty) {
			TableRecord record = (TableRecord) this.map.get(object);

			if (record != null) {
				if (record == this.headRecord) {
					if (record.right != null) {
						this.headRecord = record.right;
						this.headRecord.left = null;
					} else {
						// single element in table being valid
						// table is empty now
						this.headRecord = new TableRecord(null);
						this.tailRecord = this.headRecord;
						this.empty = true;
					}
				} else if (record == this.tailRecord) {
					// single element in the table case is being solved above
					// when
					// we checked for headRecord match
					this.tailRecord = record.left;
					this.tailRecord.right = null;
				} else {
					// left
					record.left.right = record.right;
					record.right.left = record.left;
				}
			}
			this.count--;
			//
			this.map.remove(object);
		}
	}

	/**
	 * @param object
	 * @return indicator of presence of given object in the table
	 */
	public boolean contains(Object object) {
		boolean ret = false;
		if (!this.empty) {
			ret = this.map.containsKey(object);
		}
		return ret;
	}

	/**
	 * @return TableIterator for this Table
	 * @see org.drools.leaps.util.TableIterator
	 * @see org.drools.leaps.util.BaseTableIterator
	 */
	public TableIterator iterator() {
		TableIterator ret;
		if (this.empty) {
			ret = new BaseTableIterator(null, null, null);
		} else {
			ret = new BaseTableIterator(this.headRecord, this.headRecord,
					this.tailRecord);
		}
		return ret;
	}

	/**
	 * iterator over "tail" part of the table data.
	 * 
	 * @param objectAtStart -
	 *            upper boundary of the iteration
	 * @param objectAtPosition -
	 *            starting point of the iteration
	 * @return leaps table iterator
	 * @throws TableOutOfBoundException
	 */
	class Markers {
		TableRecord start;
		TableRecord current;
		TableRecord last;
	}

	public TableIterator tailConstrainedIterator(WorkingMemory workingMemory,
			ColumnConstraints constraints, Object objectAtStart,
			Object objectAtPosition) throws TableOutOfBoundException {
		Markers markers = this.getTailIteratorMarkers(objectAtStart,
				objectAtPosition);
		return new ConstrainedFactTableIterator(workingMemory, constraints,
				markers.start, markers.current, markers.last);

	}

	public TableIterator tailIterator(Object objectAtStart,
			Object objectAtPosition) throws TableOutOfBoundException {
		Markers markers = this.getTailIteratorMarkers(objectAtStart, objectAtPosition);
		return new BaseTableIterator(markers.start, markers.current,
							markers.last);
	}


	private Markers getTailIteratorMarkers(Object objectAtStart,
			Object objectAtPosition) throws TableOutOfBoundException {
		// validate
		Markers ret = new Markers();
		ret.start = null;
		ret.current = null;
		ret.last = null;
		//
		if (this.map.comparator().compare(objectAtStart, objectAtPosition) > 0) {
			throw new TableOutOfBoundException(
					"object at position is out of upper bound");
		}
		TableRecord startRecord = null;
		TableRecord currentRecord = null;
		TableRecord lastRecord = this.tailRecord;

		if (!this.empty) { // validate
			// if (!this.map.isEmpty()) { // validate
			if (this.map.comparator().compare(objectAtStart,
					this.tailRecord.object) <= 0) {
				// let's check if we need iterator over the whole table
				SortedMap bufMap = this.map.tailMap(objectAtStart);
				if (!bufMap.isEmpty()) {
					startRecord = (TableRecord) bufMap.get(bufMap.firstKey());
					if (this.map.comparator().compare(objectAtStart,
							objectAtPosition) == 0) {
						currentRecord = startRecord;
					} else {
						// rewind to position
						bufMap = bufMap.tailMap(objectAtPosition);

						if (!bufMap.isEmpty()) {
							currentRecord = ((TableRecord) bufMap.get(bufMap
									.firstKey()));
						} else {
							currentRecord = startRecord;
						}
					}
					ret.start = startRecord;
					ret.current = currentRecord;
					ret.last = 					lastRecord;
				} 
			} 
		} 

		return ret;
	}

	/**
	 * iterator over "head" part of the table data. it does not take
	 * "positional" parameter because it's used for scanning shadow tables and
	 * this scan never "resumes"
	 * 
	 * @param objectAtEnd -
	 *            lower boundary of the iteration
	 * @return leaps table iterator
	 */
	public TableIterator headIterator(Object objectAtEnd) {
		TableIterator iterator = null;
		TableRecord startRecord = this.headRecord;
		TableRecord currentRecord = this.headRecord;
		TableRecord lastRecord = null;

		if (!this.empty) { // validate
			if (this.map.comparator().compare(this.headRecord.object,
					objectAtEnd) <= 0) {
				// let's check if we need iterator over the whole table
				SortedMap bufMap = this.map.headMap(objectAtEnd);
				if (!bufMap.isEmpty()) {
					lastRecord = (TableRecord) bufMap.get(bufMap.lastKey());
					// check if the next one is what we need
					if (lastRecord.right != null
							&& this.map.comparator().compare(
									lastRecord.right.object, objectAtEnd) == 0) {
						lastRecord = lastRecord.right;
					}
					iterator = new BaseTableIterator(startRecord, currentRecord,
							lastRecord);
				} else {
					// empty iterator
					iterator = new BaseTableIterator(null, null, null);
				}
			} else {
				// empty iterator
				iterator = new BaseTableIterator(null, null, null);
			}
		} else {
			// empty iterator
			iterator = new BaseTableIterator(null, null, null);
		}

		return iterator;
	}

	/**
	 * indicates if table has any elements
	 * 
	 * @return empty indicator
	 */
	public boolean isEmpty() {
		return this.empty;
	}

	public String toString() {
		String ret = "";

		for (Iterator it = this.iterator(); it.hasNext();) {
			ret = ret + it.next() + "\n";
		}
		return ret;
	}

	public int size() {
		return this.count;
	}

	public Object top() {
		return this.headRecord.object;
	}

	public Object bottom() {
		return this.tailRecord.object;
	}
	
	public static TableIterator singleItemIterator(Object object){
		return new BaseTableIterator(new TableRecord(object));
	}
}
