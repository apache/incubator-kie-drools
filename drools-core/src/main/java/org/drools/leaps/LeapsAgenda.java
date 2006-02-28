package org.drools.leaps;

import org.drools.base.ClassObjectType;
import org.drools.common.Agenda;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;

public class LeapsAgenda extends Agenda {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7985611305408622557L;

	private WorkingMemoryImpl workingMemory;

	public LeapsAgenda(WorkingMemoryImpl workingMemory) {
		super(workingMemory);
		this.workingMemory = workingMemory;
	}

	public synchronized void fireActivation(Activation activation)
			throws ConsequenceException {
		super.fireActivation(activation);
		// and remove tuple from
		LeapsTuple tuple = (LeapsTuple) activation.getTuple();
		// fact handles
		ColumnConstraints[] constraints;

		if (tuple.isNotConstraintsPresent()) {
			constraints = tuple.getNotConstraints();
			// remove from not related fact tables
			for (int i = 0; i < constraints.length; i++) {
				FactTable factTable = this.workingMemory
						.getFactTable(((ClassObjectType) constraints[i]
								.getColumn().getObjectType()).getClassType());
				// remove tuple from container
				factTable.removeTuple(tuple);
			}
		}
	}
}
