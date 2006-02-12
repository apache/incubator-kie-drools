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

import org.drools.FactHandle;

/**
 * Leaps handle for use with facts and rules. Is used extensively by leaps tables.
 * 
 * @author Alexander Bagerman
 * 
 */
public class Handle {
	// object to handle
	final private Object object;

	final private long id;

	/**
	 * creates a handle for object
	 * 
	 * @param id that is used to identify the object
	 * @param object to handle
	 */
	public Handle(long id, Object object) {
		this.id = id;
		this.object = object;
	}

	/**
	 * @return id of the object
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @return object being handled
	 */
	public Object getObject() {
		return this.object;
	}

	public int hashCode() {
		return (int) this.id;
	}

	/**
	 * @see FactHandle
	 */
	public long getRecency() {
		return this.id;
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		return "id=" + this.id + " [" + this.object + "]";
	}
}
