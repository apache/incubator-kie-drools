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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.FactHandleFactory;

/**
 * This base class for the engine and analogous to Drool's RuleBase class. It
 * has a similar interface adapted to the Leaps algorithm
 * 
 * @author Alexander Bagerman
 * 
 */
public class RuleBaseImpl implements RuleBase {
	private static final long serialVersionUID = 1487738104393155409L;

	private HashMap leapsRules = new HashMap();

	/**
	 * The fact handle factory.
	 */
	private final HandleFactory factHandleFactory;

	private Set pkgs;

	private Map applicationData;

	// @todo: replace this with a weak HashSet
	/**
	 * WeakHashMap to keep references of WorkingMemories but allow them to be
	 * garbage collected
	 */
	private final transient Map workingMemories;

	/** Special value when adding to the underlying map. */
	private static final Object PRESENT = new Object();

	/**
	 * Construct.
	 * 
	 * @param rete
	 *            The rete network.
	 * @throws PackageIntegrationException 
	 */
	public RuleBaseImpl() throws PackageIntegrationException {
         this(new HandleFactory(), new HashSet(), new HashMap() );

	}

	/**
	 * Construct.
	 * 
	 * @param rete
	 *            The rete network.
	 * @param conflictResolver
	 *            The conflict resolver.
	 * @param factHandleFactory
	 *            The fact handle factory.
	 * @param pkgs
	 * @param applicationData
	 * @throws PackageIntegrationException 
	 * @throws Exception 
	 */
	public RuleBaseImpl(FactHandleFactory factHandleFactory, Set pkgs,
			Map applicationData) throws PackageIntegrationException {
		// because we can deal only with leaps fact handle factory
		this.factHandleFactory = (HandleFactory) factHandleFactory;
		this.pkgs = pkgs;
		this.applicationData = applicationData;
		this.workingMemories = new WeakHashMap();

		this.pkgs = new HashSet();
		for (Iterator it = pkgs.iterator(); it.hasNext();) {
			this.addPackage((Package) it.next());
		}
	}

	/**
	 * @see RuleBase
	 */
	public WorkingMemory newWorkingMemory() {
		return newWorkingMemory(true);
	}

	/**
	 * @see RuleBase
	 */
	public WorkingMemory newWorkingMemory(boolean keepReference) {
		WorkingMemoryImpl workingMemory = new WorkingMemoryImpl(this);
		// add all rules added so far
		for (Iterator it = this.leapsRules.values().iterator(); it.hasNext();) {
			workingMemory.addLeapsRules((List) it.next());
		}
		//
		if (keepReference) {
			this.workingMemories.put(workingMemory, RuleBaseImpl.PRESENT);
		}
		return workingMemory;
	}

	void disposeWorkingMemory(WorkingMemory workingMemory) {
		this.workingMemories.remove(workingMemory);
	}

	/**
	 * @see RuleBase
	 */
	public FactHandleFactory getFactHandleFactory() {
		return this.factHandleFactory;
	}

	/**
	 * returns NEW fact handle factory because each working memory needs the new
	 * one
	 * 
	 * @see RuleBase
	 */
	public FactHandleFactory newFactHandleFactory() {
		return this.factHandleFactory.newInstance();
	}

	/**
	 * @see RuleBase
	 */
	public Package[] getPackages() {
		return (Package[]) this.pkgs.toArray(new Package[this.pkgs
				.size()]);
	}

	public Map getApplicationData() {
		return this.applicationData;
	}

	/**
	 * Add a <code>Package</code> to the network. Iterates through the
	 * <code>Package</code> adding Each individual <code>Rule</code> to the
	 * network.
	 * 
	 * @param pkg
	 *            The rule-set to add.
	 * @throws PackageIntegrationException 
	 * 
	 * @throws FactException
	 * @throws InvalidPatternException
	 */
	public void addPackage(Package pkg) throws PackageIntegrationException  {
		Map newApplicationData = pkg.getGlobals();

		// Check that the application data is valid, we cannot change the type
		// of an already declared application data variable
		for (Iterator it = newApplicationData.keySet().iterator(); it.hasNext();) {
			String identifier = (String) it.next();
			Class type = (Class) newApplicationData.get(identifier);
			if (this.applicationData.containsKey(identifier)
					&& !this.applicationData.get(identifier).equals(type)) {
				throw new PackageIntegrationException(pkg);
			}
		}
		this.applicationData.putAll(newApplicationData);

		this.pkgs.add(pkg);

		Rule[] rules = pkg.getRules();

		for (int i = 0, length = rules.length; i < length; ++i) {
			addRule(rules[i]);
		}
	}

	/**
	 * Creates leaps rule wrappers and propagate rule to the working memories
	 * 
	 * @param rule
	 * @throws FactException
	 * @throws InvalidPatternException
	 */
	public void addRule(Rule rule) throws FactException,
			InvalidPatternException {
		List rules = Builder.processRule(rule);

		this.leapsRules.put(rule, rules);

		for (Iterator it = this.workingMemories.keySet().iterator(); it
				.hasNext();) {
			((WorkingMemoryImpl) it.next()).addLeapsRules(rules);
		}
	}
	   
    public void removeRule(Rule rule) {
		for (Iterator it = this.workingMemories.keySet().iterator(); it
				.hasNext();) {
			((WorkingMemoryImpl) it.next()).removeRule((List)this.leapsRules.remove(rule));
		}
	}

    public Set getWorkingMemories() {
        return this.workingMemories.keySet();
    }
}
