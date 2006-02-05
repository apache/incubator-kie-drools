package org.drools.leaps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.ReteooNodeEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.leaps.util.TableIterator;
import org.drools.leaps.util.TableOutOfBoundException;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.ClassObjectType;
import org.drools.spi.PropagationContext;

/**
 * Followed RETEOO implementation for interfaces.
 * 
 * This class is a repository for leaps specific containers (fact tables).
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.WorkingMemory
 * @see java.beans.PropertyChangeListener
 * @see java.io.Serializable
 * 
 */
public class WorkingMemoryImpl implements WorkingMemory,
		PropertyChangeListener, Serializable {
	private static final Class[] ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[] { PropertyChangeListener.class };

	/** The arguments used when adding/removing a property change listener. */
	private final Object[] addRemovePropertyChangeListenerArgs = new Object[] { this };

	private static final long serialVersionUID = 1L;

	private final String lock = new String("lock");

	private long idsSequence;

	private long idLastFireAllAt = -1;

	private boolean addedRulesAfterLastFire = false;

	/** The <code>RuleBase</code> with which this memory is associated. */
	private final RuleBaseImpl ruleBase;

	/** Application data which is associated with this memory. */
	private final Map applicationData = new HashMap();

	/** Object-to-handle mapping. */
	private final Map handles = new IdentityHashMap();

	/** Flag to determine if a rule is currently being fired. */
	private boolean firing;

	private long propagationIdCounter;

	/**
	 * algorithm stack. TreeSet is used to facilitate dynamic rule add/remove
	 */

	Stack stack = new Stack();

	// Table stack = new Table(new Comparator() {
	// public int compare(Object o1, Object o2) {
	// Token tuple1 = (Token) o1;
	// Token tuple2 = (Token) o2;
	// // negatives are behind positive
	// int ret = tuple2.getTupleType() - tuple1.getTupleType();
	// if (ret == 0) {
	// ret = (int) (tuple1.getDominantFactHandle().getId() - tuple2
	// .getDominantFactHandle().getId());
	// }
	// return ret;
	// }
	// });

	// to store facts to cursor over it
	private final Hashtable tables = new Hashtable();

	// to store negated facts to cursor over it in negative condition search
	private final Hashtable shadowTables = new Hashtable();

	/** The eventSupport */
	private final WorkingMemoryEventSupport workingMemoryEventSupport = new WorkingMemoryEventSupport(
			this);

	protected final AgendaEventSupport agendaEventSupport = new AgendaEventSupport(
			this);

	public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
		this.ruleBase = ruleBase;
		this.idsSequence = 0;
	}

	/**
	 * event support
	 * 
	 * @param listener
	 */

	public void addEventListener(AgendaEventListener listener) {
		this.agendaEventSupport.addEventListener(listener);
	}

	public void removeEventListener(AgendaEventListener listener) {
		this.agendaEventSupport.removeEventListener(listener);
	}

	public void addEventListener(WorkingMemoryEventListener listener) {
		this.workingMemoryEventSupport.addEventListener(listener);
	}

	public void removeEventListener(WorkingMemoryEventListener listener) {
		this.workingMemoryEventSupport.removeEventListener(listener);
	}

	public List getAgendaEventListeners() {
		return this.agendaEventSupport.getEventListeners();
	}

	public List getWorkingMemoryEventListeners() {
		return this.workingMemoryEventSupport.getEventListeners();
	}

	public AgendaEventSupport getAgendaEventSupport() {
		return this.agendaEventSupport;
	}

	/**
	 * TODO no meaning in leaps context
	 * 
	 * @see WorkingMemory
	 * 
	 */
	public void addEventListener(ReteooNodeEventListener listener) {
		// do nothing
	}

	public void removeEventListener(ReteooNodeEventListener listener) {
		// do nothing
	}

	public List getReteooNodeEventListeners() {
		return null;
	}

	protected synchronized long getNextId() {
		this.idsSequence++;
		return this.idsSequence;
	}

	/**
	 * @see WorkingMemory
	 */
	public boolean containsObject(FactHandle factHandle) {
		return ((FactHandleImpl) factHandle).getObject() != null;
	}

	/**
	 * @see WorkingMemory
	 */

	public FactHandle assertObject(Object object) throws FactException {
		return this.assertObject(object, false);
	}

	public FactHandle assertObject(Object object, boolean dynamic)
			throws FactException {
		synchronized (lock) {
			FactHandle factHandle = new FactHandleImpl(this.getNextId(), object);
			// ret = ret + "\n" +"==> " + factHandle);
			// determine what classes it belongs to put it into the "table" on
			// class name key
			List tablesList = this.getFactTablesList(object.getClass());
			for (Iterator it = tablesList.iterator(); it.hasNext();) {
				// adding fact to container
				((FactTable) it.next()).add(factHandle);
			}

			this.handles.put(object, factHandle);

			Token token = new Token(this, (FactHandleImpl) factHandle,
					Token.ASSERTED);
			this.pushTokenOnStack(token);

			// event support
			PropagationContextImpl propagationContext = new PropagationContextImpl(
					++this.propagationIdCounter, PropagationContext.ASSERTION,
					(Rule) null, (Activation) null);
			this.workingMemoryEventSupport.fireObjectAsserted(
					propagationContext, factHandle,
					((FactHandleImpl) factHandle).getObject());
			//
			if (dynamic) {
				this.addPropertyChangeListener(object);
			}

			return factHandle;
		}
	}

	private void addPropertyChangeListener(Object object) {
		try {
			Method method = object
					.getClass()
					.getMethod(
							"addPropertyChangeListener",
							WorkingMemoryImpl.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES);

			method.invoke(object, this.addRemovePropertyChangeListenerArgs);
		} catch (NoSuchMethodException e) {
			System.err
					.println("Warning: Method addPropertyChangeListener not found"
							+ " on the class "
							+ object.getClass()
							+ " so Drools will be unable to process JavaBean"
							+ " PropertyChangeEvents on the asserted Object");
		} catch (IllegalArgumentException e) {
			System.err.println("Warning: The addPropertyChangeListener method"
					+ " on the class " + object.getClass() + " does not take"
					+ " a simple PropertyChangeListener argument"
					+ " so Drools will be unable to process JavaBean"
					+ " PropertyChangeEvents on the asserted Object");
		} catch (IllegalAccessException e) {
			System.err.println("Warning: The addPropertyChangeListener method"
					+ " on the class " + object.getClass() + " is not public"
					+ " so Drools will be unable to process JavaBean"
					+ " PropertyChangeEvents on the asserted Object");
		} catch (InvocationTargetException e) {
			System.err.println("Warning: The addPropertyChangeListener method"
					+ " on the class " + object.getClass()
					+ " threw an InvocationTargetException"
					+ " so Drools will be unable to process JavaBean"
					+ " PropertyChangeEvents on the asserted Object: "
					+ e.getMessage());
		} catch (SecurityException e) {
			System.err
					.println("Warning: The SecurityManager controlling the class "
							+ object.getClass()
							+ " did not allow the lookup of a"
							+ " addPropertyChangeListener method"
							+ " so Drools will be unable to process JavaBean"
							+ " PropertyChangeEvents on the asserted Object: "
							+ e.getMessage());
		}
	}

	private void removePropertyChangeListener(FactHandle handle)
			throws NoSuchFactObjectException {
		Object object = null;
		try {
			object = getObject(handle);

			Method mehod = handle
					.getClass()
					.getMethod(
							"removePropertyChangeListener",
							WorkingMemoryImpl.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES);

			mehod.invoke(handle, this.addRemovePropertyChangeListenerArgs);
		} catch (NoSuchMethodException e) {
			// The removePropertyChangeListener method on the class
			// was not found so Drools will be unable to
			// stop processing JavaBean PropertyChangeEvents
			// on the retracted Object
		} catch (IllegalArgumentException e) {
			System.err
					.println("Warning: The removePropertyChangeListener method"
							+ " on the class "
							+ object.getClass()
							+ " does not take"
							+ " a simple PropertyChangeListener argument"
							+ " so Drools will be unable to stop processing JavaBean"
							+ " PropertyChangeEvents on the retracted Object");
		} catch (IllegalAccessException e) {
			System.err
					.println("Warning: The removePropertyChangeListener method"
							+ " on the class "
							+ object.getClass()
							+ " is not public"
							+ " so Drools will be unable to stop processing JavaBean"
							+ " PropertyChangeEvents on the retracted Object");
		} catch (InvocationTargetException e) {
			System.err
					.println("Warning: The removePropertyChangeL istener method"
							+ " on the class "
							+ object.getClass()
							+ " threw an InvocationTargetException"
							+ " so Drools will be unable to stop processing JavaBean"
							+ " PropertyChangeEvents on the retracted Object: "
							+ e.getMessage());
		} catch (SecurityException e) {
			System.err
					.println("Warning: The SecurityManager controlling the class "
							+ object.getClass()
							+ " did not allow the lookup of a"
							+ " removePropertyChangeListener method"
							+ " so Drools will be unable to stop processing JavaBean"
							+ " PropertyChangeEvents on the retracted Object: "
							+ e.getMessage());
		}
	}

	public List getFactHandles() {
		return new ArrayList(this.handles.values());
	}

	/**
	 * @see WorkingMemory
	 */
	public List getObjects() {
		LinkedHashSet set = new LinkedHashSet();

		for (Iterator it = this.tables.entrySet().iterator(); it.hasNext();) {
			for (Iterator inIt = ((FactTable) it.next()).iterator(); inIt
					.hasNext();) {
				set.add(((FactHandleImpl) inIt.next()).getObject());
			}
		}
		return new ArrayList(set);
	}

	/**
	 * @see WorkingMemory
	 */
	public List getObjects(Class objectClass) {
		List list = new LinkedList();
		for (Iterator it = this.getFactTable(objectClass).iterator(); it
				.hasNext();) {
			list.add(it.next());

		}

		return list;
	}

	/**
	 * @see WorkingMemory
	 */
	public Object getObject(FactHandle factHandle)
			throws NoSuchFactObjectException {

		return ((FactHandleImpl) factHandle).getObject();
	}

	/**
	 * @see WorkingMemory
	 */
	public FactHandle getFactHandle(Object object)
			throws NoSuchFactHandleException {
		FactHandle factHandle = (FactHandle) this.handles.get(object);

		if (factHandle == null) {
			throw new NoSuchFactHandleException(object);
		}

		return factHandle;
	}

	/**
	 * generates or just return List of internal tables that correspond a class
	 * can be used to generate tables
	 * 
	 * @return
	 */
	protected List getFactTablesList(Class c) {
		ArrayList list = new ArrayList();
		Class bufClass = c;
		while (bufClass != null) {
			//
			list.add(this.getFactTable(bufClass));
			// and get the next class on the list
			bufClass = bufClass.getSuperclass();
		}
		return list;
	}

	protected List getShadowFactTablesList(Class c) {
		ArrayList list = new ArrayList();
		Class bufClass = c;
		while (bufClass != null) {
			//
			list.add(this.getShadowFactTable(bufClass));
			// and get the next class on the list
			bufClass = bufClass.getSuperclass();
		}
		return list;
	}

	/**
	 * @see WorkingMemory
	 */
	public void retractObject(FactHandle factHandle) throws FactException {
		this.retractFact(factHandle);
	}

	public FactHandle retractObject(Object object) throws FactException {
		return this.retractFact(this.getFactHandle(object));
	}

	public FactHandleImpl retractFact(FactHandle factHandle)
			throws FactException {
		synchronized (lock) {
			Object object = ((FactHandleImpl) factHandle).getObject();
			FactHandleImpl retractedFactHandle = new FactHandleImpl(this
					.getNextId(), object);
			// ret = ret + "\n" +"<== " + factHandle);
			// remove fact from all tables
			for (Iterator it = this.getFactTablesList(object.getClass())
					.iterator(); it.hasNext();) {
				// removing fact to container
				((FactTable) it.next()).remove(factHandle);
			}
			// remove fact from handles
			this.handles.remove(object);

			// add fact to all shadow tables
			for (Iterator it = this.getShadowFactTablesList(object.getClass())
					.iterator(); it.hasNext();) {
				// adding fact to container
				((FactTable) it.next()).add(retractedFactHandle);
			}
			// remove it from stack
			this.removeLeapsTokenFromStack(new Token(this,
					(FactHandleImpl) factHandle, Token.ASSERTED));
			// put the new one back on stack
			Token token = new Token(this, retractedFactHandle, Token.RETRACTED);
			this.pushTokenOnStack(token);

			// event support
			PropagationContextImpl propagationContext = new PropagationContextImpl(
					++this.propagationIdCounter, PropagationContext.ASSERTION,
					(Rule) null, (Activation) null);

			this.workingMemoryEventSupport.fireObjectRetracted(
					propagationContext, factHandle,
					((FactHandleImpl) factHandle).getObject());

			return retractedFactHandle;
		}
	}

	/**
	 * @see WorkingMemory
	 */
	public void modifyObject(FactHandle handle, Object object)
			throws FactException {
		this.modifyFact(this.getFactHandle(object), object);
	}

	public FactHandle modifyObject(Object object) throws FactException {
		return this.modifyFact(this.getFactHandle(object), object);
	}

	public FactHandle modifyFact(FactHandle factHandle, Object object)
			throws FactException {
		synchronized (lock) {
			this.retractFact(factHandle);
			return this.assertObject(object);
		}
	}

	protected boolean isStackEmpty() {
		return this.stack.isEmpty();
	}

	protected Token getTopLeapsTokenFromStack() {
		return (Token) this.stack.peek();
		// return (Token) this.stack.top();
	}

	/**
	 * 
	 * @return
	 */

	protected void popTopTokenFromStack(Token token) {
		this.stack.remove(token);
	}

	private void removeLeapsTokenFromStack(Token token) {
		// add whatever additional info for token that you need : rules, ces
		// etc.
		this.stack.remove(token);
	}

	protected void pushTokenOnStack(Token token) {
		// add whatever additional info for token that you need : rules, ces
		// etc.
		this.stack.push(token);
	}

	// getting shadow table
	// no need to create one. it would be created with the regular one
	// shadow is never hit before regular
	protected FactTable getShadowFactTable(Class c) {
		return (FactTable) this.shadowTables.get(c);
	}

	// regular table
	protected FactTable getFactTable(Class c) {
		FactTable table;
		if (this.tables.containsKey(c)) {
			table = (FactTable) this.tables.get(c);
		} else {
			table = new FactTable(this.ruleBase.getLeapsConflictResolver());
			this.tables.put(c, table);
			// shadow tables created here
			this.shadowTables.put(c, new FactTable(this.ruleBase
					.getLeapsConflictResolver()));
		}

		return table;
	}

	protected void addLeapsRules(List rules) {
		synchronized (lock) {
			this.addedRulesAfterLastFire = true;

			LeapsRule rule;
			RuleHandle ruleHandle;
			for (Iterator it = rules.iterator(); it.hasNext();) {
				rule = (LeapsRule) it.next();
				for (int i = 0; i < rule.getNumberOfColumns(); i++) {
					ruleHandle = new RuleHandle(this.getNextId(), rule, i,
							(ClassObjectType) ((ColumnConstraints) rule
									.getColumnConstraintsAtPosition(i))
									.getColumn().getObjectType(), true);
					this.addRuleHandle(ruleHandle);
				}
				for (int i = 0; i < rule.getNumberOfNotColumns(); i++) {
					ruleHandle = new RuleHandle(this.getNextId(), rule, i,
							(ClassObjectType) ((ColumnConstraints) rule
									.getNotColumnConstraintsAtPosition(i))
									.getColumn().getObjectType(), false);
				}
			}
		}
	}

	private void addRuleHandle(RuleHandle ruleHandle) {
		// positive
		if (ruleHandle.isDominantFactAsserted()) {
			this.getFactTable(
					ruleHandle.getDominantPositionType().getClassType())
					.addPositiveRule(this, ruleHandle);
		} else {

			this.getFactTable(
					ruleHandle.getDominantPositionType().getClassType())
					.addNegativeRule(this, ruleHandle);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		Object object = event.getSource();

		try {
			this.modifyFact(getFactHandle(object), object);
		} catch (NoSuchFactHandleException e) {
			// Not a fact so unable to process the change event
		} catch (FactException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * main loop
	 * 
	 */

	public void fireAllRules() throws FactException {
		fireAllRules(null);
	}

	public void fireAllRules(AgendaFilter agendaFilter) throws FactException {
		// If we're already firing a rule, then it'll pick up
		// the firing for any other assertObject(..) that get
		// nested inside, avoiding concurrent-modification
		// exceptions, depending on code paths of the actions.

		if (!this.firing) {
			try {
				this.firing = true;

				Token token;
				boolean done;
				try {
					while (!this.isStackEmpty()) {
						token = this.getTopLeapsTokenFromStack();
						done = false;
						while (!done) {
							if (!token.isResume()) {
								if (token.hasNextRuleHandle()) {
									token.nextRuleHandle();
								} else {
									this.popTopTokenFromStack(token);
									done = true;
								}
							}
							if (!done) {
								try {
									// ok. now we have tuple, dominant fact and
									// rules and
									// ready to seek to checks if any activation
									// matches on
									// current rule
									this.seek(token);

									token.setResume(true);
									done = true;
								} catch (NoMatchesFoundException ex) {
									token.setResume(false);
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println("exception - " + e);
								}
							}
						}
					}
				} catch (TableOutOfBoundException e) {
					new RuntimeException(e);
				}
				if (this.addedRulesAfterLastFire) {
					this.addedRulesAfterLastFire = false;
					// mark when method was called last time
					this.idLastFireAllAt = this.getNextId();
					// set all tables to be reseeded
					for (Enumeration e = this.tables.elements(); e
							.hasMoreElements();) {
						((FactTable) e.nextElement()).setReseededStack(true);
					}
				}
			} finally {
				this.firing = false;
			}
		}
	}

	public void seek(Token token) throws NoMatchesFoundException, Exception,
			InvalidRuleException {
		RuleHandle ruleHandle = token.getCurrentRuleHandle();
		TableIterator[] iterators = new TableIterator[ruleHandle.getLeapsRule()
				.getNumberOfColumns()];
		int numberOfColumns = ruleHandle.getLeapsRule().getNumberOfColumns();
		// getting iterators first
		for (int i = 0; i < numberOfColumns; i++) {
			// there is no dominant fact for negative based searches
			if (token.getTokenType() == Token.ASSERTED
					&& i == ruleHandle.getDominantPosition()) {
				iterators[i] = TableIterator.baseFactIterator(token
						.getDominantFactHandle());
			} else {
				iterators[i] = this.getFactTable(
						ruleHandle.getLeapsRule()
								.getColumnClassObjectTypeAtPosition(i)
								.getClassType()).tailIterator(
						token.getDominantFactHandle(),
						(token.isResume() ? token.getFactHandleAtPosition(i)
								: token.getDominantFactHandle()));
			}
		}
		// check if any iterators are empty to abort
		// check if we resume and any facts disappeared
		boolean someIteratorsEmpty = false;
		boolean doReset = false;
		boolean skip = token.isResume();
		TableIterator currentIterator;
		for (int i = 0; i < numberOfColumns && !someIteratorsEmpty; i++) {
			currentIterator = iterators[i];
			if (currentIterator.isEmpty()) {
				someIteratorsEmpty = true;
			} else {
				if (!doReset) {
					if (skip
							&& currentIterator.hasNext()
							&& !currentIterator.peekNext().equals(
									token.getFactHandleAtPosition(i))) {
						skip = false;
						doReset = true;
					}
				} else {
					currentIterator.reset();
				}
			}

		}
		// check if one of them is empty and immediate return
		if (someIteratorsEmpty) {
			throw new NoMatchesFoundException();
			// "some of tables do not have facts");
		}
		// iterating is done in nested loop
		// column position in the nested loop
		int jj = 0;
		boolean found = false;
		boolean done = false;
		while (!done) {
			currentIterator = iterators[jj];
			if (!currentIterator.hasNext()) {
				if (jj == 0) {
					done = true;
				} else {
					//                    
					currentIterator.reset();
					token.setCurrentFactHandleAtPosition(jj,
							(FactHandleImpl) null);
					jj = jj - 1;
					if (skip) {
						skip = false;
					}
				}
			} else {
				currentIterator.next();
				token.setCurrentFactHandleAtPosition(jj,
						(FactHandleImpl) iterators[jj].current());
				// check if match found
				if (this.evaluatePositiveConditions(token, jj, iterators)) {
					// start iteratating next iterator
					// or for the last one check negative conditions and fire
					// consequence
					if (jj == (numberOfColumns - 1)) {
						if (!skip) {
							// check for negative conditions
							if (this.evaluateNotAndExistsConditions(token)) {
								// event support
								PropagationContextImpl propagationContext = new PropagationContextImpl(
										++this.propagationIdCounter,
										(token.getTokenType() == Token.ASSERTED) ? PropagationContext.ASSERTION
												: PropagationContext.RETRACTION,
										(Rule) null, (Activation) null);

								AgendaItem item = new AgendaItem(
										propagationContext
												.getPropagationNumber(), token
												.getTuple(),
										propagationContext, token
												.getCurrentRuleHandle()
												.getLeapsRule().getRule());
								this.agendaEventSupport
										.fireBeforeActivationFired(item);

								done = true;
								found = true;
								// System.out.println("FIRE: " + token);
								item.fire(this);
								// store current state if to resume iterations
								// here

								// no need to do anything iterators already
								// there

								// event support
								this.agendaEventSupport
										.fireAfterActivationFired(item);
							}
						} else {
							skip = false;
						}
					} else {
						jj = jj + 1;
					}
				} else {
					if (skip) {
						skip = false;
					}
				}
			}
		}
		if (!found) {
			throw new NoMatchesFoundException();
			// "iteration did not find anything");
		}
	}

	/**
	 * 
	 * Check if any conditions with max value of declaration index at this
	 * position (<code>index</code>) are satisfied
	 * 
	 * @param index
	 *            Position of the iterator that needs condition checking
	 * @return success Indicator if all conditions at this position were
	 *         satisfied.
	 * @throws Exception
	 */
	private boolean evaluatePositiveConditions(Token token, int index,
			TableIterator[] iterators) throws Exception {
		FactHandleImpl factHandle = (FactHandleImpl) iterators[index].current();
		return checkConstraints(token, token.getCurrentRuleHandle()
				.getLeapsRule().getColumnConstraintsAtPosition(index),
				factHandle);
	}

	private boolean checkConstraints(Token token,
			ColumnConstraints constraints, FactHandleImpl factHandle) {
		// check alphas
		for (Iterator alphas = constraints.getAlpha(); alphas.hasNext();) {
//		for (Iterator alphas = constraints.getAlpha(); alphas.hasNext();) {
			if (!((LiteralConstraint) alphas.next()).isAllowed(factHandle
					.getObject())) {
				// escape immediately
				return false;
			}
		}
		// finaly beta
		return constraints.getBeta().isAllowed(factHandle.getObject(),
				factHandle, token, this);

	}

	/**
	 * Check if any of the negative conditions are satisfied success when none
	 * found
	 * 
	 * @param memory
	 * @param token
	 * @return success
	 * @throws Exception
	 */
	private boolean evaluateNotAndExistsConditions(Token token)
			throws Exception {
		if (!token.getCurrentRuleHandle().getLeapsRule().containsNotColumns()
				&& !token.getCurrentRuleHandle().getLeapsRule()
						.containsExistsColumns()) {
			return true;
		} else {
			ColumnConstraints constraints;
			// not found is used in the begining in an opposite sense
			// to see if retracted fact trigger rule conditions to be sutisfied
			boolean checkSucceed = true;
			//
			// NOT conditions checking first - checking retracted negative that
			// becomes positive
			if (token.getTokenType() == Token.RETRACTED) {
				// check alphas
				checkSucceed = checkConstraints(token, token
						.getCurrentRuleHandle().getLeapsRule()
						.getNotColumnConstraintsAtPosition(
								token.getCurrentRuleHandle()
										.getDominantPosition()), token
						.getDominantFactHandle());
			}
			TableIterator tableIterator;
			if (checkSucceed) {
				// let's now iterate over not and exists columns to see if
				// conditions satisfied

				// get each NOT column spec and check
				boolean done = false;
				for (Iterator it = token.getCurrentRuleHandle().getLeapsRule()
						.getNotColumnsIterator(); it.hasNext() && !done;) {
					constraints = (ColumnConstraints) it.next();
					// 1. starting with regular tables
					// scan the whole table
					tableIterator = this.getFactTable(
							((ClassObjectType) constraints.getColumn()
									.getObjectType()).getClassType())
							.iterator();
					// fails if exists
					done = this.matchingFactExists(constraints, token,
							tableIterator);
					if (!done) {
						// 2. checking shadow tables scan only higher facts
						tableIterator = this.getShadowFactTable(
								((ClassObjectType) constraints.getColumn()
										.getObjectType()).getClassType())
								.headIterator(token.getDominantFactHandle());
						// fails if exists
						done = this.matchingFactExists(constraints, token,
								tableIterator);
					}
				}

				if (!done) {
					// get each EXISTS column spec and check
					for (Iterator it = token.getCurrentRuleHandle()
							.getLeapsRule().getExistsColumnsIterator(); it
							.hasNext()
							&& !done;) {
						constraints = (ColumnConstraints) it.next();
						// regular tables - scan the whole table
						tableIterator = this.getFactTable(
								((ClassObjectType) constraints.getColumn()
										.getObjectType()).getClassType())
								.iterator();
						// fails if does not exists
						done = !this.matchingFactExists(constraints, token,
								tableIterator);
					}
				}
				checkSucceed = !done;
			}

			return checkSucceed;
		}
	}

	private boolean matchingFactExists(ColumnConstraints constraints,
			Token token, TableIterator tableIterator) {
		boolean found = false;
		FactHandleImpl factHandle;
		while (tableIterator.hasNext() && !found) {
			factHandle = (FactHandleImpl) tableIterator.next();
			// check alphas
			for (Iterator alphas = constraints.getAlpha(); alphas.hasNext()
					&& !found;) {
				// escape immediately
				found = ((LiteralConstraint) alphas.next())
				.isAllowed(factHandle.getObject());
			}
			if (!found) {
				// finaly beta
				found = constraints.getBeta().isAllowed(factHandle.getObject(),
						factHandle, token, this);
			}
		}

		return found;
	}

	protected long getIdLastFireAllAt() {
		return idLastFireAllAt;
	}

	/**
	 * return rule base this working memory is a part of
	 * 
	 * @return rule base
	 */
	public RuleBase getRuleBase() {
		return ruleBase;
	}

	public String toString() {
		String ret = "";
		Object key;
		ret = ret + "\n" + "Working memory";
		ret = ret + "\n" + "Fact Tables by types:";
		for (Enumeration e = this.tables.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			ret = ret + "\n" + "******************   " + key;
			((FactTable) this.tables.get(key)).toString();
		}
		ret = ret + "\n" + "Shadow Fact Tables by types:";
		for (Enumeration e = this.shadowTables.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			ret = ret + "\n" + "******************   " + key;
			((FactTable) this.shadowTables.get(key)).toString();
		}
		System.out.print("Stack:\n");
		for (Iterator it = stack.iterator(); it.hasNext();) {
			ret = ret + "\n" + "\t" + it.next();
		}
		return ret;
	}

	/**
	 * @see WorkingMemory
	 */
	public Object getApplicationData(String name) {
		return this.applicationData.get(name);
	}

	/**
	 * @see WorkingMemory
	 */
	public void setApplicationData(String name, Object value) {
		// Make sure the application data has been declared in the RuleBase
		Map applicationDataDefintions = this.ruleBase.getApplicationData();
		Class type = (Class) applicationDataDefintions.get(name);
		if ((type == null)) {
			throw new RuntimeException("Unexpected application data [" + name
					+ "]");
		} else if (!type.isInstance(value)) {
			throw new RuntimeException("Illegal class for application data. "
					+ "Expected [" + type.getName() + "], " + "found ["
					+ value.getClass().getName() + "].");

		} else {
			this.applicationData.put(name, value);
		}
	}

	/**
	 * @see WorkingMemory
	 */
	public void dispose() {
		this.ruleBase.disposeWorkingMemory(this);
	}

	/**
	 * @see WorkingMemory
	 */
	public Map getApplicationDataMap() {
		return this.applicationData;
	}

	/**
	 * TODO no meaning in leaps context
	 * 
	 * @see WorkingMemory
	 */
	public void clearAgenda() {
		// do nothing
	}

	/**
	 * TODO no meaning in leaps context
	 * 
	 * 
	 * Sets the AsyncExceptionHandler to handle exceptions thrown by the Agenda
	 * Scheduler used for duration rules.
	 * 
	 * @param handler
	 * @see WorkingMemory
	 */
	public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
		// do nothing
	}

}
