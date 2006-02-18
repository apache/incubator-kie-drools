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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.ActivationQueue;
import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.EventSupport;
import org.drools.common.LogicalDependency;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.leaps.conflict.DefaultConflictResolver;
import org.drools.leaps.util.TableOutOfBoundException;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.util.PrimitiveLongMap;

/**
 * Followed RETEOO implementation for interfaces.
 * 
 * This class is a repository for leaps specific containers (fact factTables).
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.WorkingMemory
 * @see java.beans.PropertyChangeListener
 * @see java.io.Serializable
 * 
 */
class WorkingMemoryImpl extends AbstractWorkingMemory implements EventSupport,
		PropertyChangeListener {
	private static final long serialVersionUID = -2524904474925421759L;

	/**
	 * the following member variables are used to handle retraction we are not
	 * following original leaps approach that involves shadow fact tables due to
	 * the fact that objects can mutate and there is no easy way of puting
	 * original object image into shadow/retracted fact tables
	 */
	// collection of pending activations for each blocking fact handle
	private final PrimitiveLongMap blockingFactHandles;

	// collection of pending activations for each blocking fact handle
	private final PrimitiveLongMap factHandleActivations;

	/**
	 * Construct.
	 * 
	 * @param ruleBase
	 *            The backing rule-base.
	 */
	public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
		super(ruleBase, ruleBase.newFactHandleFactory());
		this.blockingFactHandles = new PrimitiveLongMap();
		this.factHandleActivations = new PrimitiveLongMap();
	}

	/**
	 * Create a new <code>FactHandle</code>.
	 * 
	 * @return The new fact handle.
	 */
	FactHandle newFactHandle(Object object) {
		return ((HandleFactory) this.handleFactory).newFactHandle(object);
	}

	/**
	 * @see WorkingMemory
	 */
	public void setGlobal(String name, Object value) {
		// Make sure the application data has been declared in the RuleBase
		Map applicationDataDefintions = ((RuleBaseImpl) this.ruleBase)
				.getApplicationData();
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
	 * Returns the fact Object for the given <code>FactHandle</code>. It
	 * actually returns the value from the handle, before retrieving it from
	 * objects map.
	 * 
	 * @see WorkingMemory
	 * 
	 * @param handle
	 *            The <code>FactHandle</code> reference for the
	 *            <code>Object</code> lookup
	 * 
	 */
	public Object getObject(FactHandle handle) {
		return ((FactHandleImpl) handle).getObject();
	}

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
	public boolean containsObject(FactHandle handle) {
		// return this.objects.containsKey( ((FactHandleImpl) handle).getId() );
		return ((FactHandleImpl) handle).getObject() != null;
	}

	/**
	 * @see WorkingMemory
	 */
	public FactHandle assertObject(Object object) throws FactException {
		return assertObject(object, /* Not-Dynamic */
		false, false, null, null);
	}

	/**
	 * @see WorkingMemory
	 */
	public FactHandle assertLogicalObject(Object object) throws FactException {
		return assertObject(object, /* Not-Dynamic */
		false, true, null, null);
	}

	public FactHandle assertObject(Object object, boolean dynamic)
			throws FactException {
		return assertObject(object, dynamic, false, null, null);
	}

	public FactHandle assertLogicalObject(Object object, boolean dynamic)
			throws FactException {
		return assertObject(object, dynamic, true, null, null);
	}

	/**
	 * 
	 * @param object
	 * @param dynamic
	 * @param logical
	 * @param rule
	 * @param activation
	 * @return
	 * @throws FactException
	 * 
	 * @see WorkingMemory
	 */
	FactHandle assertObject(Object object, boolean dynamic, boolean logical,
			Rule rule, Activation activation) throws FactException {
		// check if the object already exists in the WM
		FactHandleImpl handle = (FactHandleImpl) this.identityMap.get(object);

		// return if the handle exists and this is a logical assertion
		if ((handle != null) && (logical)) {
			return handle;
		}

		// lets see if the object is already logical asserted
		Object logicalState = this.equalsMap.get(object);

		// if we have a handle and this STATED fact was previously STATED
		if ((handle != null) && (!logical)
				&& logicalState == AbstractWorkingMemory.STATED) {
			return handle;
		}

		if (!logical) {
			// If this stated assertion already has justifications then we need
			// to cancel them
			if (logicalState instanceof FactHandleImpl) {
				handle = (FactHandleImpl) logicalState;
				removeLogicalDependencies(handle);
			} else {
				handle = (FactHandleImpl) newFactHandle(object);
			}

			putObject(handle, object);

			this.equalsMap.put(object, AbstractWorkingMemory.STATED);

			if (dynamic) {
				addPropertyChangeListener(object);
			}
		} else {
			// This object is already STATED, we cannot make it justifieable
			if (logicalState == AbstractWorkingMemory.STATED) {
				return null;
			}

			handle = (FactHandleImpl) logicalState;
			// we create a lookup handle for the first asserted equals object
			// all future equals objects will use that handle
			if (handle == null) {
				handle = (FactHandleImpl) newFactHandle(object);

				putObject(handle, object);

				this.equalsMap.put(object, handle);
			}
			addLogicalDependency(handle, activation, activation
					.getPropagationContext(), rule);
		}

		// leaps handle already has object attached
		// handle.setObject( object );

		// this.ruleBase.assertObject( handle,
		// object,
		// propagationContext,
		// this );

		// determine what classes it belongs to put it into the "table" on
		// class name key
		List tablesList = this.getFactTablesList(object.getClass());
		for (Iterator it = tablesList.iterator(); it.hasNext();) {
			// adding fact to container
			((FactTable) it.next()).add(handle);
		}
		Token token = new Token(this, handle);

		this.pushTokenOnStack(token);

		this.workingMemoryEventSupport.fireObjectAsserted(
				new PropagationContextImpl(++this.propagationIdCounter,
						PropagationContext.ASSERTION, rule, activation),
				handle, object);
		return handle;
	}

	/**
	 * Associate an object with its handle.
	 * 
	 * @param handle
	 *            The handle.
	 * @param object
	 *            The object.
	 */
	Object putObject(FactHandle handle, Object object) {

		this.identityMap.put(object, handle);

		return this.objects.put(((FactHandleImpl) handle).getId(), object);

	}

	Object removeObject(FactHandle handle) {

		this.identityMap.remove(((FactHandleImpl) handle).getObject());

		return this.objects.remove(((FactHandleImpl) handle).getId());

	}

	/**
	 * @see WorkingMemory
	 */
	public void retractObject(FactHandle handle, boolean removeLogical,
			boolean updateEqualsMap, Rule rule, Activation activation)
			throws FactException {
		removePropertyChangeListener(handle);
		PropagationContextImpl context = new PropagationContextImpl(
				++this.propagationIdCounter, PropagationContext.RETRACTION,
				rule, activation);
		// this.ruleBase.retractObject( handle,
		// propagationContext,
		// this );

		//
		// leaps specific actions
		//
		Set pendingActivations = (Set) this.blockingFactHandles
				.remove(((FactHandleImpl) handle).getId());
		if (pendingActivations != null) {
			for (Iterator it = pendingActivations.iterator(); it.hasNext();) {
				PendingActivation item = (PendingActivation) it.next();
				item.decrementBlockingFactsCount();
				if (!item.containsBlockingFacts()) {
					boolean allPresent = true;
					FactHandleImpl[] factHandles = (FactHandleImpl[]) item
							.getTuple().getFactHandles();
					for (int i = 0; i < factHandles.length && allPresent; i++) {
						allPresent = this.objects.containsKey(factHandles[i]
								.getId());
					}
					if (allPresent) {
						this.assertTuple(item.getTuple(), new HashSet(), item
								.getContext(), item.getRule());
					}
				}
			}
		}
		Set postedActivations = (Set) this.factHandleActivations
				.remove(((FactHandleImpl) handle).getId());
		if (postedActivations != null) {
			for (Iterator it = postedActivations.iterator(); it.hasNext();) {
				PostedActivation item = (PostedActivation) it.next();
				if (!item.isRemoved()) {
					Activation itemActivation = (AgendaItem) item
							.getActivation();
					if (!((LeapsTuple) itemActivation.getTuple())
							.isActivationNull()
							&& itemActivation.isActivated()) {

						item.getActivation().remove();

						getAgendaEventSupport().fireActivationCancelled(
								item.getActivation());
					}
					removeLogicalDependencies(item.getActivation(), context,
							rule);
					item.setRemoved();
				}
			}
		}
		// remove it from stack
		this.stack.remove(new Token(this, (FactHandleImpl) handle));

		//
		// end leaps specific actions
		//
		Object oldObject = removeObject(handle);

		/* check to see if this was a logical asserted object */
		if (removeLogical) {
			removeLogicalDependencies(handle);
			this.equalsMap.remove(oldObject);
		}

		if (updateEqualsMap) {
			this.equalsMap.remove(oldObject);
		}

		// not applicable to leaps implementation
		// this.factHandlePool.push( ((FactHandleImpl) handle).getId() );

		this.workingMemoryEventSupport.fireObjectRetracted(context, handle,
				oldObject);
		// not applicable to leaps fact handle
		// ((FactHandleImpl) handle).invalidate();
	}

	/**
	 * @see WorkingMemory
	 */
	public void modifyObject(FactHandle handle, Object object, Rule rule,
			Activation activation) throws FactException {

		this.retractObject(handle);

		this.assertObject(object);

		/*
		 * this.ruleBase.modifyObject( handle, object, this );
		 */
		this.workingMemoryEventSupport.fireObjectModified(
				new PropagationContextImpl(++this.propagationIdCounter,
						PropagationContext.MODIFICATION, rule, activation),
				handle, ((FactHandleImpl) handle).getObject(), object);
	}

	/**
	 * leaps section
	 */

	private final String lock = new String("lock");

	// private long idsSequence;

	private long idLastFireAllAt = -1;

	/**
	 * algorithm stack. TreeSet is used to facilitate dynamic rule add/remove
	 */

	private Stack stack = new Stack();

	// to store facts to cursor over it
	private final Hashtable factTables = new Hashtable();

	/**
	 * generates or just return List of internal factTables that correspond a
	 * class can be used to generate factTables
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

	/**
	 * adds new leaps token on main stack
	 * 
	 * @param token
	 */
	protected void pushTokenOnStack(Token token) {
		this.stack.push(token);
	}

	/**
	 * get leaps fact table of specific type (class)
	 * 
	 * @param type
	 *            of objects
	 * @return fact table of requested class type
	 */
	protected FactTable getFactTable(Class c) {
		FactTable table;
		if (this.factTables.containsKey(c)) {
			table = (FactTable) this.factTables.get(c);
		} else {
			table = new FactTable(DefaultConflictResolver.getInstance());
			this.factTables.put(c, table);
		}

		return table;
	}

	/**
	 * Add Leaps wrapped rules into the working memory
	 * 
	 * @param rules
	 */
	protected void addLeapsRules(List rules) {
		synchronized (this.lock) {
			// this.addedRulesAfterLastFire = true;

			LeapsRule rule;
			RuleHandle ruleHandle;
			for (Iterator it = rules.iterator(); it.hasNext();) {
				rule = (LeapsRule) it.next();
				for (int i = 0; i < rule.getNumberOfColumns(); i++) {
					ruleHandle = new RuleHandle(
							((HandleFactory) this.handleFactory).getNextId(),
							rule, i);

					this.getFactTable(
							((ClassObjectType) (rule
									.getColumnConstraintsAtPosition(i))
									.getColumn().getObjectType())
									.getClassType()).addRule(this, ruleHandle);
				}
			}
		}
	}

	/**
	 * main loop
	 * 
	 */
	public void fireAllRules(AgendaFilter agendaFilter) throws FactException {
		// If we're already firing a rule, then it'll pick up
		// the firing for any other assertObject(..) that get
		// nested inside, avoiding concurrent-modification
		// exceptions, depending on code paths of the actions.

		if (!this.firing) {
			try {
				this.firing = true;

				try {
					while (!this.stack.isEmpty()) {
						Token token = (Token) this.stack.peek();
						boolean done = false;
						while (!done) {
							if (!token.isResume()) {
								if (token.hasNextRuleHandle()) {
									token.nextRuleHandle();
								} else {
									// we do not pop because something might get
									// asserted
									// and placed on hte top of the stack during
									// firing
									this.stack.remove(token);
									done = true;
								}
							}
							if (!done) {
								try {
									// ok. now we have tuple, dominant fact and
									// rules and ready to seek to checks if any
									// activation
									// matches on current rule
									TokenEvaluator.evaluate(token);
									// something was found so set marks for
									// resume processing
									token.setResume(true);
									done = true;
								} catch (NoMatchesFoundException ex) {
									token.setResume(false);
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println("exception - " + e);
								}
							}
							// we put everything on agenda
							// and if there is no modules or anything like it
							// it would fire just activated rule
							while (this.agenda.fireNextItem(agendaFilter)) {
								;
							}
						}
					}
				} catch (TableOutOfBoundException e) {
					new RuntimeException(e);
				}
				// mark when method was called last time
				this.idLastFireAllAt = ((HandleFactory) this.handleFactory)
						.getNextId();
				// set all factTables to be reseeded
				for (Enumeration e = this.factTables.elements(); e
						.hasMoreElements();) {
					((FactTable) e.nextElement()).setReseededStack(true);
				}
			} finally {
				this.firing = false;
			}
		}
	}

	protected long getIdLastFireAllAt() {
		return this.idLastFireAllAt;
	}

	public String toString() {
		String ret = "";
		Object key;
		ret = ret + "\n" + "Working memory";
		ret = ret + "\n" + "Fact Tables by types:";
		for (Enumeration e = this.factTables.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			ret = ret + "\n" + "******************   " + key;
			((FactTable) this.factTables.get(key)).toString();
		}
		ret = ret + "\n" + "Stack:";
		for (Iterator it = this.stack.iterator(); it.hasNext();) {
			ret = ret + "\n" + "\t" + it.next();
		}
		return ret;
	}

	/**
	 * Assert a new <code>Tuple</code>.
	 * 
	 * @param tuple
	 *            The <code>Tuple</code> being asserted.
	 * @param workingMemory
	 *            The working memory seesion.
	 * @throws AssertionException
	 *             If an error occurs while asserting.
	 */
	public void assertTuple(LeapsTuple tuple, Set tupleBlockingFactHandles,
			PropagationContext context, Rule rule) {
		// if the current Rule is no-loop and the origin rule is the same then
		// return
		if (rule.getNoLoop() && rule.equals(context.getRuleOrigin())) {
			return;
		}
		// see if there are blocking facts and put it on pending activations
		// list
		if (tupleBlockingFactHandles.size() != 0) {
			FactHandleImpl factHandle;
			PendingActivation item = new PendingActivation(tuple,
					tupleBlockingFactHandles.size(), context, rule);
			Set pendingActivations;
			for (Iterator it = tupleBlockingFactHandles.iterator(); it
					.hasNext();) {
				factHandle = (FactHandleImpl) it.next();
				pendingActivations = (Set) this.blockingFactHandles
						.get(factHandle.getId());
				if (pendingActivations == null) {
					pendingActivations = new HashSet();
					this.blockingFactHandles.put(factHandle.getId(),
							pendingActivations);
				}
				pendingActivations.add(item);
			}
			return;
		}

		Duration dur = rule.getDuration();

		if (dur != null && dur.getDuration(tuple) > 0) {
			ScheduledAgendaItem item = new ScheduledAgendaItem(context
					.getPropagationNumber(), tuple, this.agenda, context, rule);
			this.agenda.scheduleItem(item);
			tuple.setActivation(item);
			item.setActivated(true);
			this.getAgendaEventSupport().fireActivationCreated(item);
		} else {
			// -----------------
			// Lazy instantiation and addition to the Agenda of AgendGroup
			// implementations
			// ----------------
			AgendaGroupImpl agendaGroup = null;
			if (rule.getAgendaGroup() == null
					|| rule.getAgendaGroup().equals("")
					|| rule.getAgendaGroup().equals(AgendaGroup.MAIN)) {
				// Is the Rule AgendaGroup undefined? If it is use MAIN, which
				// is added to the Agenda by default
				agendaGroup = (AgendaGroupImpl) this.agenda
						.getAgendaGroup(AgendaGroup.MAIN);
			} else {
				// AgendaGroup is defined, so try and get the AgendaGroup from
				// the Agenda
				agendaGroup = (AgendaGroupImpl) this.agenda.getAgendaGroup(rule
						.getAgendaGroup());
			}

			if (agendaGroup == null) {
				// The AgendaGroup is defined but not yet added to the Agenda,
				// so create the AgendaGroup and add to the Agenda.
				agendaGroup = new AgendaGroupImpl(rule.getAgendaGroup());
				this.agenda.addAgendaGroup(agendaGroup);
			}

			// set the focus if rule autoFocus is true
			if (rule.getAutoFocus()) {
				this.agenda.setFocus(agendaGroup);
			}

			ActivationQueue queue = agendaGroup.getActivationQueue(rule
					.getSalience());
			AgendaItem item = new AgendaItem(context.getPropagationNumber(),
					tuple, context, rule, queue);
			// to take activation of the agenda on retraction
			FactHandle factHandle;
			PostedActivation postedActivation = new PostedActivation(item);
			Set postedActivations;
			FactHandle[] factHandles = tuple.getFactHandles();
			for (int i = 0; i < factHandles.length; i++) {
				factHandle = factHandles[i];
				postedActivations = (Set) this.factHandleActivations
						.get(((FactHandleImpl) factHandle).getId());
				if (postedActivations == null) {
					postedActivations = new HashSet();
					this.factHandleActivations.put(
							((FactHandleImpl) factHandle).getId(),
							postedActivations);
				}
				postedActivations.add(postedActivation);
			}

			queue.add(item);

			// Makes sure the Lifo is added to the AgendaGroup priority queue
			// If the AgendaGroup is already in the priority queue it just
			// returns.
			agendaGroup.addToAgenda(queue);
			tuple.setActivation(item);
			item.setActivated(true);
			this.getAgendaEventSupport().fireActivationCreated(item);
		}
	}

	protected long increamentPropagationIdCounter() {
		return ++this.propagationIdCounter;
	}

	public void removeLogicalDependencies(Activation activation,
			PropagationContext context, Rule rule) throws FactException {
		org.drools.util.LinkedList list = activation.getLogicalDependencies();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node
				.getNext()) {
			FactHandleImpl handle = (FactHandleImpl) node.getFactHandle();
			Set set = (Set) this.justified.get(handle.getId());
			set.remove(node);
			if (set.isEmpty()) {
				this.justified.remove(handle.getId());
				retractObject(handle, false, true, context.getRuleOrigin(),
						context.getActivationOrigin());
			}
		}
	}

	public void removeLogicalDependencies(FactHandle handle)
			throws FactException {
		Set set = (Set) this.justified
				.remove(((FactHandleImpl) handle).getId());
		if (set != null && !set.isEmpty()) {
			for (Iterator it = set.iterator(); it.hasNext();) {
				LogicalDependency node = (LogicalDependency) it.next();
				node.getJustifier().getLogicalDependencies().remove(node);
			}
		}
	}

	public void addLogicalDependency(FactHandle handle, Activation activation,
			PropagationContext context, Rule rule) throws FactException {
		LogicalDependency node = new LogicalDependency(activation, handle);
		activation.addLogicalDependency(node);
		Set set = (Set) this.justified.get(((FactHandleImpl) handle).getId());
		if (set == null) {
			set = new HashSet();
			this.justified.put(((FactHandleImpl) handle).getId(), set);
		}
		set.add(node);
	}

	public void dispose() {
		((RuleBaseImpl) this.ruleBase).disposeWorkingMemory(this);
	}

	/**
	 * Retrieve the rule-firing <code>Agenda</code> for this
	 * <code>WorkingMemory</code>.
	 * 
	 * @return The <code>Agenda</code>.
	 */
	protected Agenda getAgenda() {
		return this.agenda;
	}

	protected Set getFactHandleActivations(FactHandle factHandle) {

		return (Set) this.factHandleActivations
				.get(((FactHandleImpl) factHandle).getId());

	}
}
