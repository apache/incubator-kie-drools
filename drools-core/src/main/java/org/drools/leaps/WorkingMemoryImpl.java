package org.drools.leaps;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
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
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.ActivationQueue;
import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.ScheduledAgendaItem;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.ReteooNodeEventListener;
import org.drools.event.ReteooNodeEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.leaps.conflict.DefaultConflictResolver;
import org.drools.leaps.util.TableIterator;
import org.drools.leaps.util.TableOutOfBoundException;
import org.drools.reteoo.PropagationContextImpl;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.util.IdentityMap;
import org.drools.util.PrimitiveLongMap;

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
class WorkingMemoryImpl implements WorkingMemory, PropertyChangeListener {
	// ------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------
	private static final Class[] ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[] { PropertyChangeListener.class };

	// ------------------------------------------------------------
	// Instance members
	// ------------------------------------------------------------

	/** The arguments used when adding/removing a property change listener. */
	private final Object[] addRemovePropertyChangeListenerArgs = new Object[] { this };

	/** Application data which is associated with this memory. */
	private final Map applicationData = new HashMap();

	/** Handle-to-object mapping. */
	private final PrimitiveLongMap objects = new PrimitiveLongMap(32, 8);

	/** Object-to-handle mapping. */
	private final Map identityMap = new IdentityMap();

	private final Map equalsMap = new HashMap();

	private final Map justifiers = new HashMap();

	private final PrimitiveLongMap justified = new PrimitiveLongMap(8, 32);

	private static final String STATED = "STATED";

	/** The eventSupport */
	private final WorkingMemoryEventSupport workingMemoryEventSupport = new WorkingMemoryEventSupport(
			this);

	private final AgendaEventSupport agendaEventSupport = new AgendaEventSupport(
			this);

	private final ReteooNodeEventSupport reteooNodeEventSupport = new ReteooNodeEventSupport(
			this);

	/** The <code>RuleBase</code> with which this memory is associated. */
	private final RuleBaseImpl ruleBase;

	private final HandleFactory handleFactory;

	/** Rule-firing agenda. */
	private final Agenda agenda;

	/** Flag to determine if a rule is currently being fired. */
	private boolean firing;

	private long propagationIdCounter;

	// ------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------

	/**
	 * Construct.
	 * 
	 * @param ruleBase
	 *            The backing rule-base.
	 */
	public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
		this.ruleBase = ruleBase;
		this.agenda = new Agenda(this);
		this.handleFactory = (HandleFactory)this.ruleBase.newFactHandleFactory();
	}

	// ------------------------------------------------------------
	// Instance methods
	// ------------------------------------------------------------

	public void addEventListener(WorkingMemoryEventListener listener) {
		this.workingMemoryEventSupport.addEventListener(listener);
	}

	public void removeEventListener(WorkingMemoryEventListener listener) {
		this.workingMemoryEventSupport.removeEventListener(listener);
	}

	public List getWorkingMemoryEventListeners() {
		return this.workingMemoryEventSupport.getEventListeners();
	}

	public void addEventListener(AgendaEventListener listener) {
		this.agendaEventSupport.addEventListener(listener);
	}

	public void removeEventListener(AgendaEventListener listener) {
		this.agendaEventSupport.removeEventListener(listener);
	}

	public List getAgendaEventListeners() {
		return this.agendaEventSupport.getEventListeners();
	}

	public void addEventListener(ReteooNodeEventListener listener) {
		this.reteooNodeEventSupport.addEventListener(listener);
	}

	public void removeEventListener(ReteooNodeEventListener listener) {
		this.reteooNodeEventSupport.removeEventListener(listener);
	}

	public List getReteooNodeEventListeners() {
		return this.reteooNodeEventSupport.getEventListeners();
	}

	/**
	 * TODO make use of facthandle factory
	 * 
	 * Create a new <code>FactHandle</code>.
	 * 
	 * @return The new fact handle.
	 */
	FactHandle newFactHandle(Object object) {
		return new FactHandleImpl(this.handleFactory.getNextId(), object);
	}

	/**
	 * @see WorkingMemory
	 */
	public Map getApplicationDataMap() {
		return this.applicationData;
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
	public Object getApplicationData(String name) {
		return this.applicationData.get(name);
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

	/**
	 * Clear the Agenda
	 */
	public void clearAgenda() {
		this.agenda.clearAgenda();
	}

	/**
	 * @see WorkingMemory
	 */
	public RuleBase getRuleBase() {
		return this.ruleBase;
	}

	/**
	 * @see WorkingMemory
	 */
	public void fireAllRules() throws FactException {
		fireAllRules(null);
	}

	/**
	 * Returns the fact Object for the given <code>FactHandle</code>. It
	 * actually returns the value from the handle, before retrieving
	 * it from objects map.
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

	/**
	 * @see WorkingMemory
	 */
	public FactHandle getFactHandle(Object object) {
		FactHandle factHandle = (FactHandle) this.identityMap.get(object);

		if (factHandle == null) {
			throw new NoSuchFactHandleException(object);
		}

		return factHandle;
	}

	public List getFactHandles() {
		return new ArrayList(this.identityMap.values());
	}

	/**
	 * @see WorkingMemory
	 */
	public List getObjects() {
		return new ArrayList(this.objects.values());
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
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * TODO find out if you  need to remove fact handles from tables on certain 
	 * logical, dynamic conditions
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param object
	 * @param dynamic
	 * @param logical
	 * @param rule
	 * @param activation
	 * @return
	 * @throws FactException
	 */
	FactHandle assertObject(Object object, boolean dynamic, boolean logical,
			Rule rule, Activation activation) throws FactException {
		/* check if the object already exists in the WM */
		FactHandleImpl handle = (FactHandleImpl) this.identityMap.get(object);

		/* only return if the handle exists and this is a logical assertion */
		if ((handle != null) && (logical)) {
			return handle;
		}

		/* lets see if the object is already logical asserted */
		Object logicalState = this.equalsMap.get(object);

		/* if we have a handle and this STATED fact was previously STATED */
		if ((handle != null) && (!logical)
				&& logicalState == WorkingMemoryImpl.STATED) {
			return handle;
		}

		if (!logical) {
			/*
			 * If this stated assertion already has justifications then we need
			 * to cancel them
			 */
			if (logicalState instanceof FactHandleImpl) {
				handle = (FactHandleImpl) logicalState;
				/*
				 * remove handle from the justified Map and then iterate each of
				 * each Activations. For each Activation remove the handle. If
				 * the Set is empty then remove the activation from justiers.
				 */
				Set activationList = (Set) this.justified
						.remove(((FactHandleImpl) handle).getId());
				for (Iterator it = activationList.iterator(); it.hasNext();) {
					Activation eachActivation = (Activation) it.next();
					Set handles = (Set) this.justifiers.get(eachActivation);
					handles.remove(handle);
					// if an activation has no justified assertions then remove
					// it
					if (handles.isEmpty()) {
						this.justifiers.remove(eachActivation);
					}
				}
			} else {
				handle = (FactHandleImpl) newFactHandle(object);
			}

			putObject(handle, object);

			this.equalsMap.put(object, WorkingMemoryImpl.STATED);

			if (dynamic) {
				addPropertyChangeListener(object);
			}
		} else {
			/* This object is already STATED, we cannot make it justifieable */
			if (logicalState == WorkingMemoryImpl.STATED) {
				return null;
			}

			handle = (FactHandleImpl) logicalState;
			/*
			 * we create a lookup handle for the first asserted equals object
			 * all future equals objects will use that handle
			 */
			if (handle == null) {
				handle = (FactHandleImpl) newFactHandle(object);

				putObject(handle, object);

				this.equalsMap.put(object, handle);
			}
			Set activationList = (Set) this.justified
					.get(((FactHandleImpl) handle).getId());
			if (activationList == null) {
				activationList = new HashSet();
				this.justified.put(((FactHandleImpl) handle).getId(),
						activationList);
			}
			activationList.add(activation);

			Set handles = (Set) this.justifiers.get(activation);
			if (handles == null) {
				handles = new HashSet();
				this.justifiers.put(activation, handles);
			}
			handles.add(handle);
		}

		PropagationContext propagationContext = new PropagationContextImpl(
				++this.propagationIdCounter, PropagationContext.ASSERTION,
				rule, activation);

		//  ret = ret + "\n" +"==> " + factHandle);
		//  determine what classes it belongs to put it into the "table" on
		//  class name key
		List tablesList = this.getFactTablesList(object.getClass());
		for (Iterator it = tablesList.iterator(); it.hasNext();) {
			// adding fact to container
			((FactTable) it.next()).add(handle);
		}
		Token token = new Token(this, (FactHandleImpl) handle, Token.ASSERTED);
		this.pushTokenOnStack(token);

		this.workingMemoryEventSupport.fireObjectAsserted(propagationContext,
				handle, object);
		return handle;
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

	/**
	 * Associate an object with its handle.
	 * 
	 * @param handle
	 *            The handle.
	 * @param object
	 *            The object.
	 */
	Object putObject(FactHandle handle, Object object) {
		Object oldValue = this.objects.put(((FactHandleImpl) handle).getId(),
				object);

		this.identityMap.put(object, handle);

		return oldValue;
	}

	Object removeObject(FactHandle handle) {
		Object object = this.objects.remove(((FactHandleImpl) handle).getId());

		this.identityMap.remove(object);

		return object;
	}

	public void retractObject(FactHandle handle) throws FactException {
		retractObject(handle, true, true, null, null);
	}

	/**
	 * @see WorkingMemory
	 */
	public void retractObject(FactHandle handle, boolean removeLogical,
			boolean updateEqualsMap, Rule rule, Activation activation)
			throws FactException {
		removePropertyChangeListener(handle);

		PropagationContext propagationContext = new PropagationContextImpl(
				++this.propagationIdCounter, PropagationContext.RETRACTION,
				rule, activation);
		//
		// leaps specific actions
		//
		Object object = ((FactHandleImpl) handle).getObject();
		FactHandleImpl retractedFactHandle = new FactHandleImpl(this.handleFactory
				.getNextId(), object);
		// ret = ret + "\n" +"<== " + factHandle);
		// remove fact from all tables
		for (Iterator it = this.getFactTablesList(object.getClass()).iterator(); it
				.hasNext();) {
			// removing fact to container
			((FactTable) it.next()).remove(handle);
		}

		// add fact to all shadow tables
		for (Iterator it = this.getShadowFactTablesList(object.getClass())
				.iterator(); it.hasNext();) {
			// adding fact to container
			((FactTable) it.next()).add(retractedFactHandle);
		}
		// remove it from stack
		this.removeLeapsTokenFromStack(new Token(this, (FactHandleImpl) handle,
				Token.ASSERTED));
		// put the new one back on stack
		Token token = new Token(this, retractedFactHandle, Token.RETRACTED);
		this.pushTokenOnStack(token);
		//
		// end leaps specific actions
		//
		Object oldObject = removeObject(handle);

		/* check to see if this was a logical asserted object */
		if (removeLogical) {
			FactHandleImpl handleImpl = (FactHandleImpl) handle;
			Set activations = (Set) this.justified.remove(handleImpl.getId());
			if (activations != null) {
				for (Iterator it = activations.iterator(); it.hasNext();) {
					this.justifiers.remove(it.next());
				}
			}
			this.equalsMap.remove(oldObject);
		}

		if (updateEqualsMap) {
			this.equalsMap.remove(oldObject);
		}

		this.workingMemoryEventSupport.fireObjectRetracted(propagationContext,
				handle, oldObject);
	}

	public void modifyObject(FactHandle handle, Object object)
			throws FactException {
		modifyObject(handle, object, null, null);
	}

	/**
	 * @see WorkingMemory
	 */
	public void modifyObject(FactHandle handle, Object object, Rule rule,
			Activation activation) throws FactException {

		PropagationContext propagationContext = new PropagationContextImpl(
				++this.propagationIdCounter, PropagationContext.MODIFICATION,
				rule, activation);

		this.retractObject(handle);

		this.assertObject(object);

		/*
		 * this.ruleBase.modifyObject( handle, object, this );
		 */
		this.workingMemoryEventSupport.fireObjectModified(propagationContext,
				handle, ((FactHandleImpl)handle).getObject(), object);
	}

	public WorkingMemoryEventSupport getWorkingMemoryEventSupport() {
		return this.workingMemoryEventSupport;
	}

	public AgendaEventSupport getAgendaEventSupport() {
		return this.agendaEventSupport;
	}

	public ReteooNodeEventSupport getReteooNodeEventSupport() {
		return this.reteooNodeEventSupport;
	}

	/**
	 * Sets the AsyncExceptionHandler to handle exceptions thrown by the Agenda
	 * Scheduler used for duration rules.
	 * 
	 * @param handler
	 */
	public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
		//this.agenda.setAsyncExceptionHandler( handler );
	}

	/*
	 * public void dumpMemory() { Iterator it = this.joinMemories.keySet(
	 * ).iterator( ); while ( it.hasNext( ) ) { ((JoinMemory)
	 * this.joinMemories.get( it.next( ) )).dump( ); } }
	 */

	public void propertyChange(PropertyChangeEvent event) {
		Object object = event.getSource();

		try {
			modifyObject(getFactHandle(object), object);
		} catch (NoSuchFactHandleException e) {
			// Not a fact so unable to process the chnage event
		} catch (FactException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*
	 * public PrimitiveLongMap getJustified() { return this.justified; }
	 * 
	 * public Map getJustifiers() { return this.justifiers; }
	 */
	public void removeLogicalAssertions(TupleKey key,
			PropagationContext context, Rule rule) throws FactException {
		for (Iterator it = this.justifiers.keySet().iterator(); it.hasNext();) {
			AgendaItem item = (AgendaItem) it.next();

//			if (item.getRule() == rule && item.getKey().containsAll(key)) {
//				removeLogicalAssertions(item, context, rule);
//			}
		}

	}

	public void removeLogicalAssertions(Activation activation,
			PropagationContext context, Rule rule) throws FactException {
		Set handles = (Set) this.justifiers.remove(activation);
		/* no justified facts for this activation */
		if (handles == null) {
			return;
		}
		for (Iterator it = handles.iterator(); it.hasNext();) {
			FactHandleImpl handle = (FactHandleImpl) it.next();
			Set activations = (Set) this.justified.get(handle.getId());
			activations.remove(activation);
			if (activations.isEmpty()) {
				this.justified.remove(handle.getId());
				retractObject(handle, false, true, context.getRuleOrigin(),
						context.getActivationOrigin());
			}

		}
	}

	public PrimitiveLongMap getJustified() {
		return this.justified;
	}

	public Map getJustifiers() {
		return this.justifiers;
	}

	public void dispose() {
		this.ruleBase.disposeWorkingMemory(this);
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * leaps section
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	private final String lock = new String("lock");

//	private long idsSequence;

	private long idLastFireAllAt = -1;

	private boolean addedRulesAfterLastFire = false;

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
			table = new FactTable(DefaultConflictResolver.getInstance());
			this.tables.put(c, table);
			// shadow tables created here
			this.shadowTables.put(c, new FactTable(DefaultConflictResolver.getInstance()));
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
					ruleHandle = new RuleHandle(this.handleFactory.getNextId(), rule, i,
							(ClassObjectType) ((ColumnConstraints) rule
									.getColumnConstraintsAtPosition(i))
									.getColumn().getObjectType(), true);
					this.addRuleHandle(ruleHandle);
				}
				for (int i = 0; i < rule.getNumberOfNotColumns(); i++) {
					ruleHandle = new RuleHandle(this.handleFactory.getNextId(), rule, i,
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
						// we put everything on agenda
						// and if there is no modules or anything like it
						// it would fire just activated rule
						while (this.agenda.fireNextItem(agendaFilter)) {
							;
						}
					}
				} catch (TableOutOfBoundException e) {
					new RuntimeException(e);
				}
				if (this.addedRulesAfterLastFire) {
					this.addedRulesAfterLastFire = false;
					// mark when method was called last time
					this.idLastFireAllAt = this.handleFactory.getNextId();
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

	void seek(Token token) throws NoMatchesFoundException, Exception,
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
								// let agenda to do its work
								this.assertTuple(token.getTuple(), propagationContext, token
												.getCurrentRuleHandle()
												.getLeapsRule().getRule());

								done = true;
								found = true;
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
			if (!((LiteralConstraint) alphas.next()).isAllowed(factHandle,
					token, this)) {
				// escape immediately
				return false;
			}
		}
		// finaly beta
		return constraints.getBeta().isAllowed(factHandle, token, this);

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
				found = ((LiteralConstraint) alphas.next()).isAllowed(
						factHandle, token, this);
			}
			if (!found) {
				// finaly beta
				found = constraints.getBeta()
						.isAllowed(factHandle, token, this);
			}
		}

		return found;
	}

	protected long getIdLastFireAllAt() {
		return idLastFireAllAt;
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
     * Assert a new <code>Tuple</code>.
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertTuple(LeapsTuple tuple,
                            PropagationContext context,
                            Rule rule) {
        // if the current Rule is no-loop and the origin rule is the same then
        // return
        if ( rule.getNoLoop() && rule.equals( context.getRuleOrigin() ) ) {
            return;
        }
        Agenda agenda = this.getAgenda();                       

        Duration dur = rule.getDuration();

        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            ScheduledAgendaItem item = new ScheduledAgendaItem( context.getPropagationNumber(),
                    tuple,
                    this.getAgenda(),
                    context,
                    rule );
            agenda.scheduleItem( item );
            tuple.setActivation( item );
            this.getAgendaEventSupport().fireActivationCreated( item );
        } else {
            // -----------------
            // Lazy instantiation and addition to the Agenda of AgendGroup implementations
            // ----------------
            AgendaGroupImpl agendaGroup = null ;
                if (rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) || rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN, which is added to the Agenda by default
                    agendaGroup = (AgendaGroupImpl) agenda.getAgendaGroup( AgendaGroup.MAIN );
                } else {
                    // AgendaGroup is defined, so try and get the AgendaGroup from the Agenda
                    agendaGroup = (AgendaGroupImpl)  agenda.getAgendaGroup( rule.getAgendaGroup() );
                }
                
                if ( agendaGroup == null) {
                    // The AgendaGroup is defined but not yet added to the Agenda, so create the AgendaGroup and add to the Agenda.
                    agendaGroup = new AgendaGroupImpl( rule.getAgendaGroup() );
                    this.getAgenda().addAgendaGroup( agendaGroup );
                }            
            
            // set the focus if rule autoFocus is true 
            if ( rule.getAutoFocus() ) {
                agenda.setFocus( agendaGroup );
            }
            
            ActivationQueue queue = agendaGroup.getActivationQueue( rule.getSalience() );
            AgendaItem item = new AgendaItem( context.getPropagationNumber(),
                                              tuple,
                                              context,
                                              rule,
                                              queue );            

            agendaGroup.getActivationQueue( rule.getSalience() ).add( item );

            // Makes sure the Lifo is added to the AgendaGroup priority queue
            // If the AgendaGroup is already in the priority queue it just returns.
            agendaGroup.addToAgenda( queue );
            tuple.setActivation( item );
            this.getAgendaEventSupport().fireActivationCreated( item );
        }
    }
    
    public void retractTuple(LeapsTuple tuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        Activation activation = tuple.getActivation();
        if ( activation != null ) {
            activation.remove();
            workingMemory.getAgendaEventSupport().fireActivationCancelled(  activation );            
        }        
    }    


}
