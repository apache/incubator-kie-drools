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

/**
 * 
 * @author Alexander Bagerman
 * 
 */
public class TableRecord {
	// left neigbor
	TableRecord left;

	// right neigbor
	TableRecord right;

	// content of the record
	Object object;

	TableRecord(Object o) {
		this.left = null;
		this.right = null;
		this.object = o;
	}
	
	public int hashCode() {
		return this.object.hashCode();
	}
	
	public boolean equals(Object that) {
		if(this.object != null) {
			return this.object.equals(that);
		}
		else {
			return that == null;
		}
	}

}
