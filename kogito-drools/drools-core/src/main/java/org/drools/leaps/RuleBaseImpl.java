package org.drools.leaps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.leaps.conflict.DefaultConflictResolver;
import org.drools.reteoo.DefaultFactHandleFactory;
import org.drools.rule.DuplicateRuleNameException;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.RuleBaseContext;

/**
 * This base class for the engine and analogous to Drool's RuleBase class. It
 * has a similar interface adapted to the Leaps algorithm
 * 
 * @author Alexander Bagerman
 * 
 */
public class RuleBaseImpl implements RuleBase, Serializable {
	private static final long serialVersionUID = 0L;

	// to store rules added just as addRule rather than as a part of ruleSet
	private final static String defaultRuleSet = "___default___rule___set___";

	private HashMap ruleSets;

	private Map applicationData;

	private RuleBaseContext ruleBaseContext;

	private ConflictResolver conflictResolver;

	private Builder builder;

	private HashMap leapsRules = new HashMap();

	/**
	 * TODO we do not need it here. and it references RETEoo class
	 * 
	 * The fact handle factory.
	 */
	private final FactHandleFactory factHandleFactory;

	/* @todo: replace this with a weak HashSet */
	private final transient Map workingMemories;

	/** Special value when adding to the underlying map. */
	private static final Object PRESENT = new Object();

	/**
	 * constractor that supplies default conflict resolution
	 * 
	 * @see LeapsDefaultConflictResolver
	 */
	public RuleBaseImpl() throws DuplicateRuleNameException,
			InvalidPatternException, InvalidRuleException {
		this(DefaultConflictResolver.getInstance(),
				new DefaultFactHandleFactory(), new HashSet(), new HashMap(),
				new RuleBaseContext());

	}

	public RuleBaseImpl(ConflictResolver conflictResolver,
			FactHandleFactory factHandleFactory, Set ruleSets,
			Map applicationData, RuleBaseContext ruleBaseContext)
			throws DuplicateRuleNameException, InvalidPatternException,
			InvalidRuleException {
		this.factHandleFactory = factHandleFactory;
		this.conflictResolver = conflictResolver;
		this.applicationData = applicationData;
		this.ruleBaseContext = ruleBaseContext;
		this.workingMemories = new WeakHashMap();
		this.builder = new Builder();

		this.ruleSets = new HashMap();
		if (ruleSets != null) {
			int i = 0;
			RuleSet ruleSet;
			for (Iterator it = ruleSets.iterator(); it.hasNext(); i++) {
				ruleSet = (RuleSet) it.next();
				this.ruleSets.put(new Integer(i), ruleSet);
				Rule[] rules = ruleSet.getRules();
				for (int k = 0; k < rules.length; k++) {
					this.addRule(rules[k]);
				}
			}
		}
		// default one to collect standalone rules
		this.ruleSets.put(defaultRuleSet, new RuleSet(defaultRuleSet,
				this.ruleBaseContext));
	}

	/**
	 * constractor. Takes conflict resolution class that for each fact and rule
	 * sides must not return 0 if o1 != 02
	 */

	public RuleBaseImpl(ConflictResolver conflictResolver)
			throws DuplicateRuleNameException, InvalidPatternException,
			InvalidRuleException {
		this(conflictResolver, new DefaultFactHandleFactory(), new HashSet(),
				new HashMap(), new RuleBaseContext());

	}

	/**
	 * factory method for new working memory. will keep reference by default.
	 * <b>Note:</b> references kept in a week hashmap.
	 * 
	 * @return new working memory instance
	 * 
	 * @see LeapsWorkingMemory
	 */
	public WorkingMemory newWorkingMemory() {
		return this.newWorkingMemory(true);
	}

	/**
	 * factory method for new working memory. will keep reference by default.
	 * <b>Note:</b> references kept in a week hashmap.
	 * 
	 * @param keepReference
	 * @return new working memory instance
	 * 
	 * @see LeapsWorkingMemory
	 */
	public WorkingMemory newWorkingMemory(boolean keepReference) {
		WorkingMemory workingMemory = new WorkingMemoryImpl(this);
		// process existing rules
		for (Iterator it = this.leapsRules.values().iterator(); it.hasNext();) {
			((WorkingMemoryImpl) workingMemory).addLeapsRules((List) it.next());
		}
		if (keepReference) {
			this.workingMemories.put(workingMemory, PRESENT);
		}
		return workingMemory;
	}

	void disposeWorkingMemory(WorkingMemory workingMemory) {
		this.workingMemories.remove(workingMemory);
	}

	public Set getWorkingMemories() {
		return this.workingMemories.keySet();
	}

	/**
	 * TODO clash with leaps conflict resolver
	 * 
	 * @see RuleBase
	 */
	public org.drools.spi.ConflictResolver getConflictResolver() {
		return (org.drools.spi.ConflictResolver) null;
	}

	public ConflictResolver getLeapsConflictResolver() {
		return this.conflictResolver;
	}

	/**
	 * @see RuleBase
	 */
	public RuleSet[] getRuleSets() {
		return (RuleSet[]) this.ruleSets.values().toArray(
				new RuleSet[this.ruleSets.size()]);
	}

	/**
	 * Creates leaps rule wrappers and propagate rule to the working memories
	 * 
	 * @param rule
	 * @throws DuplicateRuleNameException
	 * @throws InvalidRuleException
	 * @throws InvalidPatternException
	 */
	public void addRule(Rule rule) throws DuplicateRuleNameException,
			InvalidRuleException, InvalidPatternException {
		List rules = this.builder.processRule(rule);

		this.leapsRules.put(rule, rules);

		for(Iterator it = this.workingMemories.keySet().iterator(); it.hasNext();) {
			((WorkingMemoryImpl)it.next()).addLeapsRules(rules);
		}
	}

	/**
	 * @see RuleBase
	 */
	public Map getApplicationData() {
		return this.applicationData;
	}

	/**
	 * @see RuleBase
	 */
	public RuleBaseContext getRuleBaseContext() {
		return this.ruleBaseContext;
	}

	/**
	 * TODO do not understand its location here
	 * 
	 * @see RuleBase
	 */
	public FactHandleFactory getFactHandleFactory() {
		return this.factHandleFactory;
	}
}
