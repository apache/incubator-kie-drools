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

import junit.framework.TestCase;

public class PendingTupleTest extends TestCase {
	PendingTuple pendingTupleNoBlockingFacts;

	PendingTuple pendingTupleSevenBlockingFacts;

	PendingTuple pendingTupleNoExistsFacts;

	PendingTuple pendingTupleNoExistsFactsWhenRequired;

	PendingTuple pendingTupleSevenExistsFacts;

	protected void setUp() throws Exception {
		super.setUp();
		this.pendingTupleNoBlockingFacts = new PendingTuple(null, null, null,
				null, false, -1, false, -1);
		this.pendingTupleSevenBlockingFacts = new PendingTuple(null, null,
				null, null, false, -1, true, 7);
		this.pendingTupleNoExistsFacts = new PendingTuple(null, null, null,
				null, false, -1, false, -1);
		this.pendingTupleNoExistsFactsWhenRequired = new PendingTuple(null, null, null,
				null, true, 0, false, -1);
		this.pendingTupleSevenExistsFacts = new PendingTuple(null, null, null,
				null, true, 7, false, -1);
	}

	/*
	 * Test method for
	 */
	public void testContainsBlockingFacts() {
		assertFalse(this.pendingTupleSevenBlockingFacts
				.isValid());
		assertTrue(this.pendingTupleNoBlockingFacts
				.isValid());
		this.pendingTupleNoBlockingFacts.decrementNotCount();
		this.pendingTupleNoBlockingFacts.decrementNotCount();
		this.pendingTupleNoBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		assertFalse(this.pendingTupleSevenBlockingFacts
				.isValid());
		assertTrue(this.pendingTupleNoBlockingFacts
				.isValid());
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		this.pendingTupleSevenBlockingFacts.decrementNotCount();
		assertTrue(this.pendingTupleSevenBlockingFacts
				.isValid());
	}

	/*
	 * Test method for
	 */
	public void testContainsExistsFacts() {
		assertTrue(this.pendingTupleSevenExistsFacts
				.isValid());
		assertTrue(this.pendingTupleNoExistsFacts
				.isValid());
		assertFalse(this.pendingTupleNoExistsFactsWhenRequired
				.isValid());
		this.pendingTupleNoExistsFacts.decrementExistsCount();
		this.pendingTupleNoExistsFacts.decrementExistsCount();
		this.pendingTupleNoExistsFacts.decrementExistsCount();
		this.pendingTupleNoExistsFactsWhenRequired.decrementExistsCount();
		this.pendingTupleNoExistsFactsWhenRequired.decrementExistsCount();
		this.pendingTupleNoExistsFactsWhenRequired.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		assertTrue(this.pendingTupleSevenExistsFacts
				.isValid());
		assertTrue(this.pendingTupleNoExistsFacts
				.isValid());
		assertFalse(this.pendingTupleNoExistsFactsWhenRequired
				.isValid());
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		this.pendingTupleSevenExistsFacts.decrementExistsCount();
		assertFalse(this.pendingTupleSevenExistsFacts
				.isValid());
	}

}
