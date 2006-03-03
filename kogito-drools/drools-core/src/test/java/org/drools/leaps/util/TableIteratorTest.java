package org.drools.leaps.util;

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

import java.util.Iterator;

import org.drools.leaps.Handle;
import org.drools.leaps.conflict.*;

import junit.framework.TestCase;

/**
 * @author Alexander Bagerman
 */

public class TableIteratorTest extends TestCase {

	Handle h1;

	Handle h1000;

	Handle h100;

	Handle h10;

	Table testTable;

	protected void setUp() {
		this.testTable = new Table(LoadOrderConflictResolver.getInstance());
		this.h1 = new Handle(1, "1");
		this.h1000 = new Handle(1000, "1000");
		this.h100 = new Handle(100, "100");
		this.h10 = new Handle(10, "10");
	}

	/*
	 * Test method for
	 * 'org.drools..util.TableIterator.TableIterator()'
	 */
	public void testTableIterator() {
		BaseTableIterator it = new BaseTableIterator(null, null,null);
		assertFalse(it.hasNext());
		assertTrue(it.isEmpty());
	}

	public void testGetDominantFactIterator() {
		Iterator it = Table.singleItemIterator(this.h1000);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h1000);
		assertFalse(it.hasNext());
	}

	/*
	 * Test method for
	 * 'org.drools..util.TableIterator.TableIterator(TableRecord)'
	 */
	public void testTableIteratorTableRecord() {
		BaseTableIterator it = new BaseTableIterator(new TableRecord(this.h1));
		assertTrue(it.hasNext());
		assertFalse(it.isEmpty());
		assertEquals(this.h1, it.next());
		assertFalse(it.hasNext());
		assertFalse(it.isEmpty());

	}

	/*
	 * Test method for
	 * 'org.drools..util.TableIterator.TableIterator(TableRecord,
	 * TableRecord, TableRecord)'
	 */
	public void testTableIteratorTableRecordTableRecordTableRecord() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertTrue(it.hasNext());
		assertFalse(it.isEmpty());
		assertEquals(this.h1, it.next());
		assertFalse(it.hasNext());
		assertFalse(it.isEmpty());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertFalse(it.isEmpty());
		assertTrue(it.hasNext());
		assertEquals(this.h1000, it.next());
		assertTrue(it.hasNext());
		assertEquals(this.h100, it.next());
		assertTrue(it.hasNext());
		assertEquals(this.h10, it.next());
		assertTrue(it.hasNext());
		assertEquals(this.h1, it.next());
		assertFalse(it.hasNext());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertFalse(it.isEmpty());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertEquals(this.h1, it.next());
		assertFalse(it.hasNext());

	}

	/*
	 * Test method for 'org.drools..util.TableIterator.isEmpty()'
	 */
	public void testIsEmpty() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertFalse(it.isEmpty());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertFalse(it.isEmpty());
		it = new BaseTableIterator(this.testTable.tailRecord,
				this.testTable.headRecord, this.testTable.headRecord);
		assertFalse(it.isEmpty());
		it = new BaseTableIterator(null, null, null);
		assertTrue(it.isEmpty());

	}

	/*
	 * Test method for 'org.drools..util.TableIterator.reset()'
	 */
	public void testReset() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.next());
		it.reset();
		assertEquals(this.h1, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertEquals(this.h1000, it.next());
		it.reset();
		assertEquals(this.h1000, it.next());
		it.next();
		it.next();
		it.reset();
		assertEquals(this.h1000, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		it.reset();
		assertEquals(this.h1000, it.next());
		it = new BaseTableIterator(new TableRecord(this.h1));
		assertEquals(this.h1, it.next());
		it.reset();
		assertEquals(this.h1, it.next());

	}

	/*
	 * Test method for 'org.drools..util.TableIterator.hasNext()'
	 */
	public void testHasNext() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertTrue(it.hasNext());
		assertFalse(it.isEmpty());
		assertEquals(this.h1, it.next());
		assertFalse(it.hasNext());
		assertFalse(it.isEmpty());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertTrue(it.hasNext());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertTrue(it.hasNext());
		it = new BaseTableIterator(null, null, null);
		assertFalse(it.hasNext());
		it = new BaseTableIterator(new TableRecord(this.h1));
		assertTrue(it.hasNext());

	}

	/*
	 * Test method for 'org.drools..util.TableIterator.next()'
	 */
	public void testNext() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertEquals(this.h1000, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.next());
		it = new BaseTableIterator(new TableRecord(this.h1));
		assertEquals(this.h1, it.next());
	}

	/*
	 * Test method for 'org.drools..util.TableIterator.current()'
	 */
	public void testCurrent() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.next());
		assertEquals(this.h1, it.current());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertEquals(this.h1000, it.next());
		assertEquals(this.h1000, it.current());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.next());
		assertEquals(this.h1, it.current());
		it = new BaseTableIterator(new TableRecord(this.h1));
		assertEquals(this.h1, it.next());
		assertEquals(this.h1, it.current());

	}

	/*
	 * Test method for 'org.drools..util.TableIterator.peekNext()'
	 */
	public void testPeekNext() {
		this.testTable.add(this.h1);
		BaseTableIterator it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.peekNext());
		assertEquals(this.h1, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.headRecord, this.testTable.tailRecord);
		assertEquals(this.h1000, it.peekNext());
		assertEquals(this.h1000, it.next());
		this.testTable.clear();
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h10);
		this.testTable.add(this.h100);
		it = new BaseTableIterator(this.testTable.headRecord,
				this.testTable.tailRecord, this.testTable.tailRecord);
		assertEquals(this.h1, it.peekNext());
		assertEquals(this.h1, it.next());
		it = new BaseTableIterator(new TableRecord(this.h1));
		assertEquals(this.h1, it.peekNext());
		assertEquals(this.h1, it.next());

	}

}
