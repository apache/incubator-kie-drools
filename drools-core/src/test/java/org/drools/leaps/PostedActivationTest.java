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

import org.drools.common.AgendaItem;

import junit.framework.TestCase;

public class PostedActivationTest extends TestCase {

	PostedActivation postedActivation;

	AgendaItem item;

	protected void setUp() throws Exception {
		super.setUp();
		FactHandleImpl h1 = new FactHandleImpl(1, "one");
		FactHandleImpl h2 = new FactHandleImpl(2, "two");
		FactHandleImpl h3 = new FactHandleImpl(3, "three");
		FactHandleImpl h4 = new FactHandleImpl(4, "four");
		FactHandleImpl arr[] = { h1, h2, h3, h4 };
		LeapsTuple tuple = new LeapsTuple(arr);
		this.item = new AgendaItem(0L, tuple, null, null, null);
		this.postedActivation = new PostedActivation(this.item, false, 1, false, -1);
	}

	/*
	 * Test method for
	 * 'org.drools.leaps.PostedActivation.setWasRemoved(boolean)'
	 */
	public void testSetRemoved() {
		assertTrue(this.postedActivation.isValid());
		this.postedActivation.invalidate();
		assertFalse(this.postedActivation.isValid());
	}

	/*
	 * Test method for 'org.drools.leaps.PostedActivation.getActivation()'
	 */
	public void testGetActivation() {
		assertEquals(this.item, this.postedActivation.getAgendaItem());
	}

}
