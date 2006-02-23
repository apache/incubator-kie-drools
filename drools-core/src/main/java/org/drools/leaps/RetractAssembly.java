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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * To store all references needed to retract a fact
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.WorkingMemory
 * @see java.beans.PropertyChangeListener
 * @see java.io.Serializable
 * 
 */

class RetractAssembly {
	final List pendingTuples = new LinkedList();

	final List pendingExists = new LinkedList();

	final List pendingNots = new LinkedList();

	final List postedActivations = new LinkedList();

	final List postedExists = new LinkedList();

	final List postedNots = new LinkedList();

	RetractAssembly() {
	}

	void addPendingTuple(PendingTuple tuple) {
		this.pendingTuples.add(tuple);
	}

	Iterator getPendingTuples() {
		return this.pendingTuples.iterator();
	}

	void addPendingExists(PendingTuple tuple) {
		this.pendingTuples.add(tuple);
	}

	Iterator getPendingExists() {
		return this.pendingExists.iterator();
	}

	void addPendingNot(PendingTuple tuple) {
		this.pendingTuples.add(tuple);
	}

	Iterator getPendingNots() {
		return this.pendingNots.iterator();
	}

	void addPostedActivation(PostedActivation activation) {
		this.postedActivations.add(activation);
	}

	Iterator getPostedActivations() {
		return this.postedActivations.iterator();
	}

	void addPostedExist(PostedActivation activation) {
		this.postedExists.add(activation);
	}

	Iterator getPostedExists() {
		return this.postedExists.iterator();
	}

	void addPostedNot(PostedActivation activation) {
		this.postedNots.add(activation);
	}

	Iterator getPostedNots() {
		return this.postedNots.iterator();
	}
}
