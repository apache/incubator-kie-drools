/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests.manners;

public class Count {
	private int value;
	
	public Count() {
		
	}

	public Count(int value) {
		super();
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String toString() {
		return "[Count value=" + this.value + "]";
	}

	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if ((object == null) || !(object instanceof Count)) {
			return false;
		}

		return this.value == ((Count) object).value;
	}

	public int hashCode() {
		return this.value;
	}

}
