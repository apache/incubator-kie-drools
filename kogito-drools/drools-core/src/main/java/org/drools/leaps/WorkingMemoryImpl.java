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
import java.util.Arrays;
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
import org.drools.common.InternalFactHandle;
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
import org.drools.util.*;

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
	// 
	private final PrimitiveLongMap retracts;

	/**
	 * Construct.
	 * 
	 * @param ruleBase
	 *            The backing rule-base.
	 */
	public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
		super(ruleBase, ruleBase.newFactHandleFactory());
		this.retracts = new PrimitiveLongMap();
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
		Iterator it;
		RetractAssembly info = (RetractAssembly) this.retracts
				.remove(((FactHandleImpl) handle).getId());
		if (info != null) {
			// we can have three types of facts being retracted
			// 1. part of the tuple
			// 2. part of not condition
			// 3. part of exists condition
			// each of this conditions can apply to pending agendaItem (waiting
			// on not facts disappear)
			// or posted agendaItem

			// pending agendaItem
			PendingTuple pendingTuple;
			// first set of iterations to try to invalidate
			// 1. part of the tuple
			// 2. part of not condition
			// 3. part of exists condition

			// first we try invalidate core and exists related tuples
			for (it = info.getPendingTuples(); it.hasNext();) {
				pendingTuple = (PendingTuple) it.next();
				if (pendingTuple.isValid()) {
					invalidatePendingTuple(pendingTuple);
				}
			}
			for (it = info.getPendingExists(); it.hasNext();) {
				pendingTuple = (PendingTuple) it.next();
				if (pendingTuple.isValid()) {
					pendingTuple.decrementExistsCount();
					if (!pendingTuple.isValid()) {
						invalidatePendingTuple(pendingTuple);
					}
				}
			}
			// then we decrement not and see if we can submit it
			for (it = info.getPendingNots(); it.hasNext();) {
				pendingTuple = (PendingTuple) it.next();
				if (pendingTuple.isValid()) {
					pendingTuple.decrementNotCount();
					if (!pendingTuple.isValid()) {
						invalidatePendingTuple(pendingTuple);
					} else {
						assertTuple(pendingTuple.getTuple(), new ArrayList(),
								pendingTuple.getExistsQualifiers(),
								pendingTuple.getContext(), pendingTuple
										.getRule());
						// and let everybody know that we submited it
						pendingTuple.setSubmited();
					}
				}
			}

			// posted agendaItem
			// 1. part of the tuple
			// 2. part of not condition
			// 3. part of exists condition
			PostedActivation postedActivation;
			for (it = info.getPostedActivations(); it.hasNext();) {
				postedActivation = (PostedActivation) it.next();
				if (postedActivation.isValid()) {
					// no need to take actions. just invalidate
					invalidatePostedActivation(postedActivation);
				}
			}
			for (it = info.getPostedExists(); it.hasNext();) {
				postedActivation = (PostedActivation) it.next();
				if (postedActivation.isValid()) {
					postedActivation.decrementExistsCount();
					if (!postedActivation.isValid()) {
						invalidatePostedActivation(postedActivation);
					}
				}
			}
			// not is irrelevant for posted activations
//			for (it = info.getPostedNots(); it.hasNext();) {
//				postedActivation = (PostedActivation) it.next();
//				if (postedActivation.isValid()) {
//					postedActivation.decrementNotCount();
//					if (!postedActivation.isValid()) {
//						invalidatePostedActivation(postedActivation);
//					}
//				}
//			}
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

	private void invalidatePendingTuple(PendingTuple pendingTuple){
		// invalidate token 
		pendingTuple.invalidate();
	}

	private void invalidatePostedActivation(PostedActivation postedActivation){
		// invalidate token 
		postedActivation.invalidate();
		// invalidate agenda agendaItem
		Activation agendaItem = postedActivation.getAgendaItem();
		if (!((LeapsTuple) agendaItem.getTuple())
				.isActivationNull()
				&& agendaItem.isActivated()) {
			agendaItem.remove();
			getAgendaEventSupport().fireActivationCancelled(
					agendaItem);
		}
		removeLogicalDependencies(agendaItem, agendaItem.getPropagationContext(), agendaItem.getRule());

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
	public void assertTuple(LeapsTuple tuple, List blockingFactHandles
			, List existsEnablingFactHandles,
			PropagationContext context, Rule rule) {
		FactHandleImpl factHandle;
		// if the current Rule is no-loop and the origin rule is the same then
		// return
		if (rule.getNoLoop() && rule.equals(context.getRuleOrigin())) {
			return;
		}
		// see if there are blocking facts and put it on pending activations
		// list without submitting to agenda
		if (blockingFactHandles.size() != 0) {
			PendingTuple pendingTuple = new PendingTuple(
					tuple, existsEnablingFactHandles,context, rule,
					(existsEnablingFactHandles != null)
							&& (existsEnablingFactHandles.size() > 0),
					(existsEnablingFactHandles != null)?existsEnablingFactHandles.size():0,
					(blockingFactHandles != null)
							&& (blockingFactHandles.size() > 0),
							(blockingFactHandles != null)?blockingFactHandles.size():0);
			// exists qualifiers
			addPendingTuple(pendingTuple, existsEnablingFactHandles, blockingFactHandles);
			//
			return;
		}

		Duration dur = rule.getDuration();

		Activation agendaItem;
		if (dur != null && dur.getDuration(tuple) > 0) {
			 agendaItem = new ScheduledAgendaItem(context
					.getPropagationNumber(), tuple, this.agenda, context, rule);
			this.agenda.scheduleItem((ScheduledAgendaItem)agendaItem);
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
			agendaItem = new AgendaItem(context.getPropagationNumber(),
					tuple, context, rule, queue);

			queue.add(agendaItem);

			// Makes sure the Lifo is added to the AgendaGroup priority queue
			// If the AgendaGroup is already in the priority queue it just
			// returns.
			agendaGroup.addToAgenda(queue);
			tuple.setActivation(agendaItem);
			agendaItem.setActivated(true);
			this.getAgendaEventSupport().fireActivationCreated(agendaItem);
			// added relationships to use during retract
			
			PostedActivation postedActivation = new PostedActivation(
					(AgendaItem) agendaItem,
					(existsEnablingFactHandles != null)
							&& (existsEnablingFactHandles.size() > 0),
					(existsEnablingFactHandles != null) ? existsEnablingFactHandles
							.size()
							: -1);
			// to take agendaItem of the agenda on retraction
			addPostedActivation(postedActivation, existsEnablingFactHandles);
		}
	}

	/**
	 * add link between exists enabling facts and token/agendaItem depending if
	 * any not blocking facts are present
	 * 
	 * @param agendaItem
	 * @param enablingFactHandles
	 */
	private void addPendingTuple(PendingTuple tuple,
			List existsFactHandles, List notFactHandles) {
		long id;
		RetractAssembly assembly;
		// 1. core facts
		FactHandle []factHandles = tuple.getTuple().getFactHandles();
		for(int i = 0; i < factHandles.length; i++ ){
			id = ((FactHandleImpl)factHandles[i]).getId();
			assembly = (RetractAssembly)this.retracts.get(id);
			if(assembly == null) {
				assembly = new RetractAssembly();
				this.retracts.put(id, assembly);
			}
			assembly.addPendingTuple(tuple);
		}
		// 2. exists
		if(existsFactHandles != null){
			for(Iterator it = existsFactHandles.iterator(); it.hasNext();){
				id = ((FactHandleImpl)it.next()).getId();
				assembly = (RetractAssembly)this.retracts.get(id);
				if(assembly == null) {
					assembly = new RetractAssembly();
					this.retracts.put(id, assembly);
				}
				assembly.addPendingExists(tuple);
				
			}
		}
		// 3. exists
		if(notFactHandles != null){
			for(Iterator it = notFactHandles.iterator(); it.hasNext();){
				id = ((FactHandleImpl)it.next()).getId();
				assembly = (RetractAssembly)this.retracts.get(id);
				if(assembly == null) {
					assembly = new RetractAssembly();
					this.retracts.put(id, assembly);
				}
				assembly.addPendingNot(tuple);
				
			}
		}
	}

	private void addPostedActivation(PostedActivation activation,
			List existsFactHandles) {
		long id;
		RetractAssembly assembly;
		// 1. core facts
		FactHandle []factHandles = activation.getAgendaItem().getTuple().getFactHandles();
		for(int i = 0; i < factHandles.length; i++ ){
			id = ((FactHandleImpl)factHandles[i]).getId();
			assembly = (RetractAssembly)this.retracts.get(id);
			if(assembly == null) {
				assembly = new RetractAssembly();
				this.retracts.put(id, assembly);
			}
			assembly.addPostedActivation(activation);
		}
		// 2. exists
		if(existsFactHandles != null){
			for(Iterator it = existsFactHandles.iterator(); it.hasNext();){
				id = ((FactHandleImpl)it.next()).getId();
				assembly = (RetractAssembly)this.retracts.get(id);
				if(assembly == null) {
					assembly = new RetractAssembly();
					this.retracts.put(id, assembly);
				}
				assembly.addPostedExist(activation);
				
			}
		}
		// 3. (not is irrelevant for posted activations
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

	protected Iterator getFactHandleActivations(FactHandle factHandle) {
		Iterator ret = null;
		RetractAssembly assembly = (RetractAssembly) this.retracts
				.get(((FactHandleImpl) factHandle).getId());
		if (assembly != null) {
			ret = assembly.getPostedActivations();
		}
		return ret;
	}
}
