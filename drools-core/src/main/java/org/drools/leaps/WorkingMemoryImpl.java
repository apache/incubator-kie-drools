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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.leaps.conflict.DefaultConflictResolver;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.util.IteratorChain;

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
	
	protected final Agenda agenda;
	/**
	 * Construct.
	 * 
	 * @param ruleBase
	 *            The backing rule-base.
	 */
	public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
		super(ruleBase, ruleBase.newFactHandleFactory());
		this.agenda = new LeapsAgenda(this);
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

	public void clearAgenda()  {
        this.agenda.clearAgenda();
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
	 * @param agendaItem
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
				handle.removeAllLogicalDependencies();
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
			
			// adding logical dependency
			LeapsTuple tuple = (LeapsTuple)activation.getTuple();
			tuple.addLogicalDependency(handle);
			handle.addLogicalDependency(tuple);
		}

		// leaps handle already has object attached
		// handle.setObject( object );

		// this.ruleBase.assertObject( handle,
		// object,
		// propagationContext,
		// this );

		// determine what classes it belongs to put it into the "table" on
		// class name key
		Class objectClass = object.getClass();
		for (Iterator tables = this.getFactTablesList(objectClass).iterator(); tables.hasNext();) {
			FactTable factTable = (FactTable) tables.next(); 
			// adding fact to container
			factTable.add(handle);
			// inspect all tuples for exists and not conditions and activate / deactivate
			// agenda items
			LeapsTuple tuple;
			ColumnConstraints constraint;
			ColumnConstraints[] constraints;
			for (Iterator tuples = factTable.getTuplesIterator(); tuples
					.hasNext();) {
				tuple = (LeapsTuple) tuples.next();
				// check not constraints
				constraints = tuple.getNotConstraints();
				for (int i = 0; i < constraints.length; i++) {
					constraint = constraints[i];
					if (objectClass.isAssignableFrom(((ClassObjectType) constraint.getColumn()
							.getObjectType()).getClassType())
							&& constraint.isAllowed(handle, tuple, this)) {
						tuple.addNotFactHandle(handle, i);
						handle.addNotTuple(tuple, i);
					}
				}
				// check exists constraints
				constraints = tuple.getExistsConstraints();
				for (int i = 0; i < constraints.length; i++) {
					if (constraints[i].isAllowed(handle, tuple, this)) {
						tuple.addExistsFactHandle(handle, i);
						handle.addExistsTuple(tuple, i);
					}
				}
				// check and see if we need deactivate / activate
				if (tuple.isReadyForActivation() && tuple.isActivationNull()) {
					// ready to activate
					this.assertTuple(tuple, rule);
				} else if (!tuple.isReadyForActivation()
						&& !tuple.isActivationNull()) {
					// time to pull from agenda
					this.invalidateActivation(tuple);
				}
			}
		}

		// new leaps stack token
		this.pushTokenOnStack(new Token(this, handle));

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
		// this.ruleBase.retractObject( handle, propagationContext, this );

		/*
		 * leaps specific actions
		 */
		// remove fact from all relevant fact tables container
		for (Iterator it = this.getFactTablesList(
				((FactHandleImpl) handle).getObject().getClass()).iterator(); it
				.hasNext();) {
			((FactTable) it.next()).remove(handle);
		}

		// 0. remove activated tuples
		Iterator tuples = ((FactHandleImpl)handle).getActivatedTuples();
		for(; tuples != null && tuples.hasNext();){
			this.invalidateActivation((LeapsTuple)tuples.next());
		}
		
		// 1. remove fact for nots and exists tuples
		FactHandleTupleAssembly assembly;
		Iterator it;
		it = ((FactHandleImpl) handle).getNotTuples();
		if (it != null) {
			for (; it.hasNext();) {
				assembly = (FactHandleTupleAssembly) it.next();
				assembly.getTuple().removeNotFactHandle(handle,
						assembly.getIndex());
			}
		}
		it = ((FactHandleImpl) handle).getExistsTuples();
		if (it != null) {
			for (; it.hasNext();) {
				assembly = (FactHandleTupleAssembly) it.next();
				assembly.getTuple().removeExistsFactHandle(handle,
						assembly.getIndex());
			}
		}
		// 2. assert all tuples that are ready for activation or cancel ones
		// that are no longer
		LeapsTuple tuple;
		IteratorChain chain = new IteratorChain();
		it = ((FactHandleImpl) handle).getNotTuples();
		if (it != null) {
			chain.addIterator(it);
		}
		it = ((FactHandleImpl) handle).getExistsTuples();
		if (it != null) {
			chain.addIterator(it);
		}
		for (; chain.hasNext();) {
			tuple = ((FactHandleTupleAssembly) chain.next()).getTuple();
			if (tuple.isReadyForActivation() && tuple.isActivationNull()) {
				// ready to activate
				this.assertTuple(tuple, rule);
			} else  {
				// time to pull from agenda
				this.invalidateActivation(tuple);
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

	private void invalidateActivation(LeapsTuple tuple) {
		if (!tuple.isReadyForActivation() && !tuple.isActivationNull()) {
			Activation activation = tuple.getActivation();
			// invalidate agenda agendaItem
			if (activation.isActivated()) {
				activation.remove();
				getAgendaEventSupport().fireActivationCancelled(activation);
			}
			//
			tuple.setActivation(null);
		}
		// remove logical dependency
		FactHandleImpl factHandle;
		Iterator it = tuple.getLogicalDependencies();
		if (it != null) {
			for (; it.hasNext();) {
				factHandle = (FactHandleImpl) it.next();
				factHandle.removeLogicalDependency(tuple);
				if (!factHandle.isLogicalyValid()) {
					this.retractObject(factHandle);
				}
			}
		}
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
								// agendaItem
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
	public void assertTuple(LeapsTuple tuple, Rule rule) {
		PropagationContext context = tuple.getContext();
		// if the current Rule is no-loop and the origin rule is the same then
		// return
		if (rule.getNoLoop() && rule.equals(context.getRuleOrigin())) {
			return;
		}

		Duration dur = rule.getDuration();

		Activation agendaItem;
		if (dur != null && dur.getDuration(tuple) > 0) {
			agendaItem = new ScheduledAgendaItem(context
					.getPropagationNumber(), tuple, this.agenda, context, rule);
			this.agenda.scheduleItem((ScheduledAgendaItem) agendaItem);
			tuple.setActivation(agendaItem);
			agendaItem.setActivated(true);
			this.getAgendaEventSupport().fireActivationCreated(agendaItem);
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
			agendaItem = new AgendaItem(context
					.getPropagationNumber(), tuple, context, rule, queue);

			queue.add(agendaItem);

			// Makes sure the Lifo is added to the AgendaGroup priority queue
			// If the AgendaGroup is already in the priority queue it just
			// returns.
			agendaGroup.addToAgenda(queue);
			tuple.setActivation(agendaItem);
			agendaItem.setActivated(true);
			this.getAgendaEventSupport().fireActivationCreated(agendaItem);
			// retract support
			FactHandleImpl [] factHandles = (FactHandleImpl[])tuple.getFactHandles();
			for(int i = 0; i < factHandles.length; i++){
				factHandles[i].addActivatedTuple(tuple);
			}
		}
	}

	protected long increamentPropagationIdCounter() {
		return ++this.propagationIdCounter;
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
}
