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

import junit.framework.TestCase;

import java.util.*;

import org.drools.examples.manners.Guest;
import org.drools.examples.manners.Hobby;
import org.drools.examples.manners.Sex;
import org.drools.examples.manners.Context;

import org.drools.leaps.*;
import org.drools.leaps.conflict.*;

/**
 * @author Alexander Bagerman
 */

public class TableTest extends TestCase {

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
	 * Test method for 'org.drools..util.Table.add(Object)'
	 */
	public void testAddAndContains() {
		assertFalse(this.testTable.contains(this.h1));
		this.testTable.add(this.h1);
		assertTrue(this.testTable.contains(this.h1));
	}

	/*
	 * Test method for 'org.drools..util.Table.remove(Object)'
	 */
	public void testRemove() {
		assertFalse(this.testTable.contains(this.h1));
		this.testTable.add(this.h1);
		assertTrue(this.testTable.contains(this.h1));
		this.testTable.remove(this.h1);
		assertFalse(this.testTable.contains(this.h1));
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		assertTrue(this.testTable.contains(this.h100));
		this.testTable.remove(this.h100);
		assertFalse(this.testTable.contains(this.h100));
		assertTrue(this.testTable.contains(this.h1000));
		this.testTable.remove(this.h1000);
		assertFalse(this.testTable.contains(this.h1000));
		assertTrue(this.testTable.contains(this.h1));
		this.testTable.remove(this.h1);
		assertFalse(this.testTable.contains(this.h1));
		Iterator it = this.testTable.iterator();
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h10);
	}

	/*
	 * Test method for 'org.drools..util.Table.isEmpty()'
	 */
	public void testIsEmpty() {
		assertTrue(this.testTable.isEmpty());
		this.testTable.add(this.h1);
		assertFalse(this.testTable.isEmpty());
		this.testTable.remove(this.h1);
		assertTrue(this.testTable.isEmpty());
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		assertFalse(this.testTable.isEmpty());
		this.testTable.remove(this.h100);
		assertFalse(this.testTable.isEmpty());
		this.testTable.remove(this.h1000);
		assertFalse(this.testTable.isEmpty());
		this.testTable.remove(this.h1);
		assertFalse(this.testTable.isEmpty());
		this.testTable.remove(this.h10);
		assertTrue(this.testTable.isEmpty());
	}

	/*
	 * Test method for 'org.drools..util.Table.headObject'
	 */
	public void testHeadObject() {
		this.testTable.add(this.h1);
		assertEquals(this.h1, this.testTable.headRecord.object);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		assertEquals(this.h1000, this.testTable.headRecord.object);
	}

	/*
	 * Test method for 'org.drools..util.Table.tailObject'
	 */
	public void testTailObject() {
		this.testTable.add(this.h1);
		assertEquals(this.testTable.tailRecord.object, this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		assertEquals(this.testTable.tailRecord.object, this.h1);
	}

	/*
	 * Test method for 'org.drools..util.Table.iterator()'
	 */
	public void testIterator() {
		Iterator it;
		// empty iterator
		it = this.testTable.iterator();
		assertFalse(it.hasNext());
		// iterate over a single element
		this.testTable.add(this.h1);
		it = this.testTable.iterator();
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h1);
		assertFalse(it.hasNext());
		// several items iterator
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		it = this.testTable.iterator();
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h1000);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h100);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h10);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h1);
		assertFalse(it.hasNext());
	}

	public void testTailIterator() {
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		try {
			TableIterator it = this.testTable.tailIterator(this.h100, this.h10);
			assertTrue(it.hasNext());
			assertEquals(it.next(), this.h10);
			assertTrue(it.hasNext());
			assertEquals(it.next(), this.h1);
			assertFalse(it.hasNext());
			it.reset();
			assertTrue(it.hasNext());
			assertEquals(it.next(), this.h100);
			assertTrue(it.hasNext());
			assertEquals(it.next(), this.h10);
			assertTrue(it.hasNext());
			assertEquals(it.next(), this.h1);
			assertFalse(it.hasNext());

			this.testTable.clear();
			Handle fh1 = new Handle(1, new Guest("1", Sex.resolve("m"), Hobby
					.resolve("h2")));
			Handle fh2 = new Handle(2, new Guest("1", Sex.resolve("m"), Hobby
					.resolve("h1")));
			Handle fh3 = new Handle(3, new Guest("1", Sex.resolve("m"), Hobby
					.resolve("h3")));
			Handle fh4 = new Handle(4, new Guest("3", Sex.resolve("f"), Hobby
					.resolve("h2")));
			Handle fhC = new Handle(5, new Context("start"));
			this.testTable.add(fh1);
			this.testTable.add(fh2);
			this.testTable.add(fh3);
			this.testTable.add(fh4);
			it = this.testTable.tailIterator(fhC, fhC);
			assertTrue(it.hasNext());
			assertEquals(it.next(), fh4);

		} catch (TableOutOfBoundException ex) {

		}
	}

	public void testHeadIterator() {
		this.testTable.add(this.h1);
		this.testTable.add(this.h1000);
		this.testTable.add(this.h100);
		this.testTable.add(this.h10);
		TableIterator it = this.testTable.headIterator(this.h10);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h1000);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h100);
		assertTrue(it.hasNext());
		assertEquals(it.next(), this.h10);
		assertFalse(it.hasNext());
	}
}
