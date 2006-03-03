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

/**
 * 
 * @author Alexander Bagerman
 *
 */
public class HandleFactoryTest extends TestCase {

	HandleFactory factory;

	protected void setUp() throws Exception {
		super.setUp();
		this.factory = new HandleFactory();
	}

	/*
	 * Test method for 'org.drools.leaps.Handlethis.factory.newFactHandle()'
	 */
	public void testNewFactHandle() {
		assertTrue(((Handle) this.factory.newFactHandle()).getObject() == null);
	}

	/*
	 * Test method for 'org.drools.leaps.Handlethis.factory.getNextId()'
	 */
	public void testGetNextId() {
		this.factory.getNextId();
		this.factory.getNextId();
		this.factory.getNextId();
		this.factory.getNextId();

		long nextId = this.factory.getNextId() + 1;
		assertEquals(nextId, this.factory.getNextId());
	}

	/*
	 * Test method for
	 * 'org.drools.leaps.Handlethis.factory.newFactHandle(Object)'
	 */
	public void testNewFactHandleObject() {
		this.factory.getNextId();
		this.factory.getNextId();

		String testObject = new String("test object");
		long nextId = this.factory.getNextId() + 1;
		Handle handle = (Handle) this.factory.newFactHandle(testObject);
		assertEquals(nextId, handle.getId());
		assertEquals(testObject, handle.getObject());

	}

	/*
	 * Test method for 'org.drools.leaps.Handlethis.factory.newFactHandle(long)'
	 */
	public void testNewFactHandleLong() {
		this.factory.getNextId();
		this.factory.getNextId();

		long nextId = this.factory.getNextId() + 1;
		assertEquals(nextId, ((Handle) this.factory.newFactHandle(984393L))
				.getId());

	}

	/*
	 * Test method for 'org.drools.leaps.Handlethis.factory.newInstance()'
	 */
	public void testNewInstance() {
		assertFalse(this.factory == this.factory.newInstance());
	}

}
