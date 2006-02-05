package org.drools.leaps;

import java.io.Serializable;

import org.drools.FactHandle;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.reteoo.*;

/**
 * Item entry in the <code>Agenda</code>. Mimic RETEOO implementation
 * 
 * @see org.drools.reteoo.AgendaItem
 * 
 * @author Alexander Bagerman
 */
class AgendaItem implements Activation, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The tuple. */
	private LeapsTuple tuple;

	private TupleKey tupleKey;
	/** The rule. */
	private Rule rule;

	private PropagationContext context;

	public long activationNumber;

	/**
	 * Construct.
	 * 
	 * @param tuple
	 * @param rule
	 * 
	 */
	AgendaItem(long activationNumber, LeapsTuple tuple,
			PropagationContext context, Rule rule) {
		this.tuple = tuple;
		this.context = context;
		this.rule = rule;
		this.activationNumber = activationNumber;
		FactHandle[] factHandles = tuple.getFactHandles();
//		for(int i = 0; i < factHandles.length; i++ ){
//			if(i == 0){
//				tupleKey = new TupleKey(0, factHandles[0]);
//			}
//			else {
//				tupleKey = new TupleKey(tupleKey, new TupleKey(i, factHandles[i]));
//			}
//		}
	}

	public PropagationContext getPropagationContext() {
		return this.context;
	}

	/**
	 * Retrieve the rule.
	 * 
	 * @return The rule.
	 */
	public Rule getRule() {
		return this.rule;
	}

	/**
	 * Determine if this tuple depends on the values derrived from a particular
	 * root object.
	 * 
	 * @param handle
	 *            The root object handle.
	 * 
	 * @return <code>true<code> if this agenda item depends
	 *          upon the item, otherwise <code>false</code>.
	 */
	boolean dependsOn(FactHandle handle) {
		return this.tuple.dependsOn(handle);
	}

	/**
	 * Set the tuple.
	 * 
	 * @param tuple
	 * 
	 */
	void setTuple(LeapsTuple tuple) {
		this.tuple = tuple;
	}

	/**
	 * Retrieve the tuple.
	 * 
	 * @return The tuple.
	 */
	public Tuple getTuple() {
		return this.tuple;
	}

	/**
	 * Fire this item.
	 * 
	 * @param workingMemory -
	 *            The working memory context.
	 * 
	 * @throws ConsequenceException
	 *             If an error occurs while attempting to fire the consequence.
	 */
	void fire(WorkingMemoryImpl workingMemory) throws ConsequenceException {

		workingMemory.getAgendaEventSupport().fireBeforeActivationFired(this);

		this.rule.getConsequence().invoke(this);

		workingMemory.getAgendaEventSupport().fireAfterActivationFired(this);
	}

	public long getActivationNumber() {
		return this.activationNumber;
	}

	public String toString() {
		return "[Activation rule=" + this.rule.getName() + ", tuple="
				+ this.tuple + "]";
	}

	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if ((object == null) || !(object instanceof AgendaItem)) {
			return false;
		}

		AgendaItem otherItem = (AgendaItem) object;

		return (this.rule.equals(otherItem.getRule()) && this.tuple
				.getDominantFactHandle().equals(
						((Token) otherItem.getTuple()).getDominantFactHandle()));
	}

	TupleKey getKey() {
		return this.tupleKey;
	}
	public int hashcode() {
		return this.getTuple().hashCode();
	}
}
