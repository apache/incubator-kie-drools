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

import java.io.Serializable;

import org.drools.FactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;

/**
 * Leaps Tuple implementation
 * 
 * @author Alexander Bagerman
 */
class LeapsTuple implements Tuple, Serializable {
	private static final long serialVersionUID = 1L;

	private FactHandleImpl[] factHandles;

	private Activation activation;

	/**
	 * activation parts
	 */
	LeapsTuple(FactHandleImpl factHandles[]) {
		this.factHandles = factHandles;
	}

	/**
	 * Determine if this tuple depends upon a specified object.
	 * 
	 * @param handle
	 *            The object handle to test.
	 * 
	 * @return <code>true</code> if this tuple depends upon the specified
	 *         object, otherwise <code>false</code>.
	 * 
	 * @see org.drools.spi.Tuple
	 */
	public boolean dependsOn(FactHandle handle) {
		for (int i = 0; i < this.factHandles.length; i++) {
			if (handle.equals(this.factHandles[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.drools.spi.Tuple
	 */
	public FactHandle get(int col) {
		return this.factHandles[col];
	}

	/**
	 * @see org.drools.spi.Tuple
	 */
	public FactHandle get(Declaration declaration) {
		return this.get(declaration.getColumn());
	}

	/**
	 * @see org.drools.spi.Tuple
	 */
	public FactHandle[] getFactHandles() {
		return this.factHandles;
	}

	/**
	 * @see org.drools.spi.Tuple
	 */
	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	/**
	 * to determine if "active" activation needs to be removed from the queue on
	 * fact retraction
	 * 
	 * @return indicator if activation was null'ed
	 */
	public boolean isActivationNull() {
		return this.activation == null;
	}

	/**
	 * @see java.lang.Object
	 */
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (object == null || !(object instanceof LeapsTuple)) {
			return false;
		}

		FactHandle[] thatFactHandles = ((LeapsTuple) object).getFactHandles();
		if (thatFactHandles.length != this.factHandles.length) {
			return false;
		} else {
			for (int i = 0; i < this.factHandles.length; i++) {
				if (!this.factHandles[i].equals(thatFactHandles[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.factHandles.length; i++) {
			buffer.append(this.factHandles[i] + ", ");
		}
		return buffer.toString();
	}
}
