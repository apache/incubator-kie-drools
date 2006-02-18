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
import org.drools.common.InternalFactHandle;

/**
 * class container for each object asserted / retracted into the system
 * 
 * @author Alexander Bagerman
 * 
 */
public class FactHandleImpl extends Handle implements InternalFactHandle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * actual object that is asserted to the system no getters just a direct
	 * access to speed things up
	 */
	public FactHandleImpl(long id, Object object) {
		super(id, object);
	}

	/**
	 * Leaps fact handles considered equal if ids match and content points to
	 * the same object.
	 */
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof FactHandleImpl))
			return false;
		return this.getId() == ((FactHandleImpl) that).getId()
				&& this.getObject() == ((FactHandleImpl) that).getObject();

	}

	/**
	 * @see FactHandle
	 */
	public String toExternalForm() {
		return "[fid:" + this.getId() + "]";
	}

	/**
	 * @see Object
	 */
	public String toString() {
		return toExternalForm();
	}
}
