package org.drools.leaps;

import java.io.Serializable;
import java.util.Iterator;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.leaps.util.TableIterator;
import org.drools.leaps.util.TableOutOfBoundException;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;

/**
 * this object wears multiple hats - Tuple and being main element
 * that wraps fact handle on main leaps stack
 *  
 * @author Alexander Bagerman
 * 
 */
public class Token implements Tuple, Serializable {

	private static final long serialVersionUID = 1L;

	public final static int ASSERTED = 0;

	public final static int RETRACTED = 1;

	private WorkingMemoryImpl workingMemory;

	private final int tupleType;

	private final FactHandleImpl dominantFactHandle;

	private RuleHandle currentRuleHandle = null;

	private FactHandleImpl[] currentFactHandles = new FactHandleImpl[0];

	boolean resume = false;

	private Iterator rules = null;
	
	/**
	 * activation parts
	 */

	public Token(WorkingMemoryImpl workingMemory, FactHandleImpl factHandle,
			int tupleType) {
		this.workingMemory = workingMemory;
		this.dominantFactHandle = factHandle;
		this.tupleType = tupleType;
		this.rules = null;
	}

	private Iterator rulesIterator() {
		if (this.rules == null) {
			FactTable factTable = this.workingMemory
					.getFactTable(this.dominantFactHandle.getObject()
							.getClass());
			if (this.tupleType == Token.ASSERTED) {
				this.rules = factTable.getPositiveRulesIterator();
			} else {
				this.rules = factTable.getNegativeRulesIterator();
			}
		}
		return this.rules;
	}

	public FactHandleImpl getFactHandleAtPosition(int idx) {
		return this.currentFactHandles[idx];
	}

	public RuleHandle nextRuleHandle() throws TableOutOfBoundException {
		this.currentRuleHandle = (RuleHandle) rules.next();
		this.currentFactHandles = new FactHandleImpl[this.currentRuleHandle
				.getLeapsRule().getNumberOfColumns()];
		return this.currentRuleHandle;
	}

	/**
	 * 
	 * @param memory
	 * @return indicator if there are more rules
	 * @throws TableOutOfBoundException
	 */

	public boolean hasNextRuleHandle() throws TableOutOfBoundException {
		boolean ret = false;
		// starting with calling rulesIterator() to make sure that we picks
		// rules because fact can be asserted before rules added
		if (this.rulesIterator() != null) {
			long levelId = this.workingMemory.getIdLastFireAllAt();
			if (this.dominantFactHandle.getId() >= levelId) {
				ret = this.rules.hasNext();
			} else {
				// then we need to skip rules that have id lower than
				// workingMemory.idLastFireAllAt
				boolean done = false;
				while (!done) {
					if (this.rules.hasNext()) {
						if (((RuleHandle) ((TableIterator) this.rules)
								.peekNext()).getId() > levelId) {
							ret = true;
							done = true;
						} else {
							this.rules.next();
						}
					} else {
						ret = false;
						done = true;
					}
				}
			}
		}
		return ret;
	}

	public int hashCode() {
		return (int) this.dominantFactHandle.getId();
	}

	public void setCurrentFactHandleAtPosition(int idx,
			FactHandleImpl factHandle) {
		this.currentFactHandles[idx] = factHandle;
	}

	public FactHandleImpl getDominantFactHandle() {
		return this.dominantFactHandle;
	}

	public RuleHandle getCurrentRuleHandle() {
		return this.currentRuleHandle;
	}

	public int getTokenType() {
		return this.tupleType;
	}

	public boolean isResume() {
		return this.resume;
	}

	public void setResume(boolean resume) {
		this.resume = resume;
	}

	/**
	 * We always have only one Tuple per fact handle hence match on handle id
	 * 
	 * @see Object
	 */
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Token))
			return false;
		return this.dominantFactHandle.getId() == ((Token) that).dominantFactHandle
				.getId();
	}

	/**
	 * Retrieve the value at position
	 * 
	 * @param position
	 * 
	 * @return The currently bound <code>Object</code> value.
	 */
	public FactHandle get(int idx) {
		return this.getFactHandleAtPosition(idx);
	}

	/**
	 * 
	 */
	public FactHandle get(Declaration declaration) {
		return this.get(declaration.getColumn());
	}

	public Object get(FactHandle factHandle) {
		return ((FactHandleImpl) factHandle).getObject();
	}

	/**
	 * Retrieve the <code>FactHandle</code> for a given object.
	 * 
	 * <p>
	 * Within a consequence of a rule, if the desire is to retract or modify a
	 * root fact this method provides a way to retrieve the
	 * <code>FactHandle</code>. Facts that are <b>not </b> root fact objects
	 * have no handle.
	 * </p>
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @return The fact-handle or <code>null</code> if the supplied object is
	 *         not a root fact object.
	 */
	public FactHandle getFactHandleForObject(Object object) {
		if (this.currentFactHandles != null) {
			for (int i = 0; i < this.currentFactHandles.length; i++) {
				if (this.currentFactHandles[i].getObject() == object) {
					return this.getFactHandleAtPosition(i);
				}
			}
		}

		return null;
	}

	/**
	 * @see Tuple
	 */
	public FactHandle getFactHandleForDeclaration(Declaration declaration) {
		return this.getFactHandleAtPosition(declaration.getColumn());
	}

	/**
	 * @see Tuple
	 */
	public FactHandle[] getFactHandles() {
		return this.currentFactHandles;
	}

	/**
	 * Returns a reference to the <code>WorkingMemory</code> associated with
	 * this object.
	 * 
	 * @return WorkingMemory
	 */
	public WorkingMemory getWorkingMemory() {
		return this.workingMemory;
	}

	/**
	 * does not matter at all for leaps.
	 * 
	 * @see Tuple.
	 */
	public long getMostRecentFactTimeStamp() {
		if (this.currentFactHandles != null) {
			long recency = -1;
			for (int i = 0; i < this.currentFactHandles.length; i++) {
				if (i == 0) {
					recency = this.currentFactHandles[0].getRecency();
				} else if (this.currentFactHandles[i].getRecency() > recency) {
					recency = this.currentFactHandles[i].getRecency();
				}
			}
			return recency;
		} else {
			return -1L;
		}
	}

	/**
	 * does not matter at all for leaps.
	 * 
	 * @see Tuple.
	 */
	public long getLeastRecentFactTimeStamp() {
		if (this.currentFactHandles != null) {
			long recency = -1;
			for (int i = 0; i < this.currentFactHandles.length; i++) {
				if (i == 0) {
					recency = this.currentFactHandles[0].getRecency();
				} else if (this.currentFactHandles[i].getRecency() < recency) {
					recency = this.currentFactHandles[i].getRecency();
				}
			}
			return recency;
		} else {
			return -1L;
		}
	}

	public String toString() {
		String ret = "TOKEN ["
				+ ((this.tupleType == Token.ASSERTED) ? "ASSERTED"
						: "RETRACTED") + ":: " + dominantFactHandle + "]\n"
				+ "\tRULE : " + this.currentRuleHandle + "\n";
		if (this.currentFactHandles != null) {
			for (int i = 0; i < this.currentFactHandles.length; i++) {
				ret = ret
						+ ((i == this.currentRuleHandle.getDominantPosition()) ? "***"
								: "") + "\t" + i + " -> "
						+ this.currentFactHandles[i].getObject() + "\n";
			}
		}
		return ret;
	}

	LeapsTuple getTuple() {
		return new LeapsTuple(this.currentFactHandles);
	}


    /**
     * Determine if this tuple depends upon a specified object.
     * 
     * @param handle
     *            The object handle to test.
     * 
     * @return <code>true</code> if this tuple depends upon the specified
     *         object, otherwise <code>false</code>.
     */
    public boolean dependsOn(FactHandle handle) {
    	for (int i = 0; i < this.currentFactHandles.length; i++){
    		if (this.currentFactHandles[i].equals(handle)) {
    			return true;
    		}
    	}
    	return false;
    }

	public void setActivation(Activation activation) {
		// do nothing
	}

    
}
