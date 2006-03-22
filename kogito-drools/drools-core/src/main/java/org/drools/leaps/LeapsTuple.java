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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Leaps Tuple implementation
 * 
 * @author Alexander Bagerman
 */
class LeapsTuple implements Tuple, Serializable {
	private static final long serialVersionUID = 1L;
	
	private final PropagationContext context;

	private boolean readyForActivation;

	private final FactHandleImpl[] factHandles;

	private final boolean notConstraintsPresent;
	
	private final ColumnConstraints[] notConstraints;

	private Set[] notFactHandles;

	private final boolean existsConstraintsPresent;
	
	private final ColumnConstraints[] existsConstraints;

	private Set[] existsFactHandles;

	private Set logicalDependencies;
	
	private Activation activation;

	/**
	 * agendaItem parts
	 */
	LeapsTuple(FactHandleImpl factHandles[],
			ColumnConstraints[] notConstraints,
			ColumnConstraints[] existsConstraints
			, PropagationContext context) {
		this.factHandles = factHandles;
		this.notConstraints = notConstraints;
		if (this.notConstraints != null && this.notConstraints.length > 0) {
			this.notConstraintsPresent = true;
			this.notFactHandles = new HashSet[this.notConstraints.length];
		}
		else {
			this.notConstraintsPresent = false;
		}
		this.existsConstraints = existsConstraints;
		if (this.existsConstraints != null && this.existsConstraints.length > 0) {
			this.existsConstraintsPresent = true;
			this.existsFactHandles = new HashSet[this.existsConstraints.length];
		}
		else {
			this.existsConstraintsPresent = false;
		}
		
		this.context = context;
		this.readyForActivation = !this.existsConstraintsPresent;
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
		for (int i = 0, length = this.factHandles.length; i < length; i++) {
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
	 * to determine if "active" agendaItem needs to be valid from the queue on
	 * fact retraction
	 * 
	 * @return indicator if agendaItem was null'ed
	 */
	boolean isActivationNull() {
		return this.activation == null;
	}

	Activation getActivation() {
		return this.activation;
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
		}

		for (int i = 0, length = this.factHandles.length; i < length; i++) {
			if (!this.factHandles[i].equals(thatFactHandles[i])) {
				return false;
			}

		}
		return true;
	}

	/**
	 * indicates if exists conditions complete and there is no blocking facts
	 * 
	 * @return
	 */
	boolean isReadyForActivation() {
		return this.readyForActivation;
	}

	/**
	 * @see java.lang.Object
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("LeapsTuple ["
				+ this.context.getRuleOrigin().getName() + "] ");

		for (int i = 0, length = this.factHandles.length; i < length; i++) {
			buffer.append(((i==0)?"":", ") + this.factHandles[i]);
		}

		if(this.existsFactHandles != null) {
			buffer.append("\nExists fact handles by position");
			for (int i = 0, length = this.existsFactHandles.length; i < length; i++) {
				buffer.append("\nposition " + i);
				for (Iterator it = this.existsFactHandles[i].iterator(); it
						.hasNext();) {
					buffer.append("\n\t" + it.next());
				}
			}
		}
		if(this.notFactHandles != null) {
			buffer.append("\nNot fact handles by position");
			for (int i = 0, length = this.notFactHandles.length; i < length; i++) {
				buffer.append("\nposition " + i);
				for (Iterator it = this.notFactHandles[i].iterator(); it
						.hasNext();) {
					buffer.append("\n\t" + it.next());
				}
			}
		}
		return buffer.toString();
	}

	ColumnConstraints[] getNotConstraints() {
		return this.notConstraints;
	}

	ColumnConstraints[] getExistsConstraints() {
		return this.existsConstraints;
	}

	void addNotFactHandle(FactHandle factHandle, int index) {
		this.readyForActivation = false;
		Set facts = this.notFactHandles[index];
		if (facts == null) {
			facts = new HashSet();
			this.notFactHandles[index] = facts;
		}
		facts.add(factHandle);
	}

	void removeNotFactHandle(FactHandle factHandle, int index) {
		if (this.notFactHandles[index] != null) {
			this.notFactHandles[index].remove(factHandle);
		}
		this.setReadyForActivation();
	}

	void addExistsFactHandle(FactHandle factHandle, int index) {
		Set facts = this.existsFactHandles[index];
		if (facts == null) {
			facts = new HashSet();
			this.existsFactHandles[index] = facts;
		}
		facts.add(factHandle);
		this.setReadyForActivation();
	}

	void removeExistsFactHandle(FactHandle factHandle, int index) {
		if (this.existsFactHandles[index] != null) {
			this.existsFactHandles[index].remove(factHandle);
		}
		this.setReadyForActivation();
	}

	private void setReadyForActivation(){
		this.readyForActivation = true;

		if (this.notFactHandles != null) {
			for (int i = 0, length = this.notFactHandles.length; this.notConstraintsPresent
					&& i < length && this.readyForActivation; i++) {
				if (this.notFactHandles[i].size() > 0) {
					this.readyForActivation = false;
				}
			}
		}
		if (this.existsFactHandles != null) {
			for (int i = 0, length = this.existsFactHandles.length; this.existsConstraintsPresent
					&& i < length && this.readyForActivation; i++) {
				if (this.existsFactHandles[i].size() == 0) {
					this.readyForActivation = false;
				}
			}
		}
	}

	PropagationContext getContext() {
		return this.context;
	}

	boolean isExistsConstraintsPresent() {
		return this.existsConstraintsPresent;
	}

	boolean isNotConstraintsPresent() {
		return this.notConstraintsPresent;
	}

	void addLogicalDependency(FactHandle handle) {
		if(this.logicalDependencies == null){
			this.logicalDependencies = new HashSet();
		}
		this.logicalDependencies.add(handle);
	}
	
	Iterator getLogicalDependencies() {
		if(this.logicalDependencies != null){
			return this.logicalDependencies.iterator();
		}
		return null;
	}
}
