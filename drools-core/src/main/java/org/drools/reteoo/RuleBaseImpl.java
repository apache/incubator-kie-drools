package org.drools.reteoo;

/*
 * Copyright 2005 JBoss Inc
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
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleIntegrationException;
import org.drools.RuleSetIntegrationException;
import org.drools.WorkingMemory;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.ClassObjectTypeResolver;
import org.drools.spi.ConflictResolver;
import org.drools.spi.ObjectTypeResolver;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleBaseContext;

/**
 * Implementation of <code>RuleBase</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
public class RuleBaseImpl
    implements
    RuleBase {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The root Rete-OO for this <code>RuleBase</code>. */
    private final Rete              rete;

    private final Builder           builder;

    /** The fact handle factory. */
    private final FactHandleFactory factHandleFactory;

    private Set                     ruleSets;

    private Map                     applicationData;

    private RuleBaseContext         ruleBaseContext;

    // @todo: replace this with a weak HashSet
    /**
     * WeakHashMap to keep references of WorkingMemories but allow them to be
     * garbage collected
     */
    private final transient Map     workingMemories;

    /** Special value when adding to the underlying map. */
    private static final Object     PRESENT = new Object();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    
    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     */
    public RuleBaseImpl() {
        this( new DefaultFactHandleFactory(),
              new HashSet(),
              new HashMap(),
              new RuleBaseContext() );
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
     * @param ruleSets
     * @param applicationData
     */
    public RuleBaseImpl(FactHandleFactory factHandleFactory,
                        Set ruleSets,
                        Map applicationData,
                        RuleBaseContext ruleBaseContext) {
        ObjectTypeResolver resolver = new ClassObjectTypeResolver();
        this.rete = new Rete( resolver );
        this.builder = new Builder( this,
                                    resolver );
        this.factHandleFactory = factHandleFactory;
        this.ruleSets = ruleSets;
        this.applicationData = applicationData;
        this.ruleBaseContext = ruleBaseContext;
        this.workingMemories = new WeakHashMap();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory() {
        return newWorkingMemory( true );
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory(boolean keepReference) {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( this );
        if ( keepReference ) {
            this.workingMemories.put( workingMemory,
                                      RuleBaseImpl.PRESENT );
        }
        return workingMemory;
    }

    void disposeWorkingMemory(WorkingMemory workingMemory) {
        this.workingMemories.remove( workingMemory );
    }

    /**
     * @see RuleBase
     */
    public FactHandleFactory getFactHandleFactory() {
        return this.factHandleFactory;
    }

    public FactHandleFactory newFactHandleFactory() {
        return this.factHandleFactory.newInstance();
    }

    /**
     * Retrieve the Rete-OO network for this <code>RuleBase</code>.
     * 
     * @return The RETE-OO network.
     */
    Rete getRete() {
        return this.rete;
    }

    /**
     * Assert a fact object.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The fact.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the assertion.
     */
    void assertObject(FactHandle handle,
                      Object object,
                      PropagationContext context,
                      WorkingMemoryImpl workingMemory) throws FactException {
        getRete().assertObject( (FactHandleImpl) handle,
                                context,
                                workingMemory );
    }

    /**
     * Retract a fact object.
     * 
     * @param handle
     *            The handle.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the retraction.
     */
    void retractObject(FactHandle handle,
                       PropagationContext context,
                       WorkingMemoryImpl workingMemory) throws FactException {
        getRete().retractObject( (FactHandleImpl) handle,
                                 context,
                                 workingMemory );
    }

    public RuleSet[] getRuleSets() {
        return (RuleSet[]) this.ruleSets.toArray( new RuleSet[this.ruleSets.size()] );
    }

    public Map getApplicationData() {
        return this.applicationData;
    }

    public RuleBaseContext getRuleBaseContext() {
        return this.ruleBaseContext;
    }

    /**
     * Add a <code>RuleSet</code> to the network. Iterates through the
     * <code>RuleSet</code> adding Each individual <code>Rule</code> to the
     * network.
     * 
     * @param ruleSet
     *            The rule-set to add.
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws FactException
     * @throws InvalidPatternException
     */
    public void addRuleSet(RuleSet ruleSet) throws RuleIntegrationException,
                                           RuleSetIntegrationException,
                                           FactException,
                                           InvalidPatternException {
        Map newApplicationData = ruleSet.getApplicationData();

        // Check that the application data is valid, we cannot change the type
        // of an already declared application data variable
        for ( Iterator it = newApplicationData.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            Class type = (Class) newApplicationData.get( identifier );
            if ( this.applicationData.containsKey( identifier ) && !this.applicationData.get( identifier ).equals( type ) ) {
                throw new RuleSetIntegrationException( ruleSet );
            }
        }
        this.applicationData.putAll( newApplicationData );

        this.ruleSets.add( ruleSet );

        Rule[] rules = ruleSet.getRules();

        for ( int i = 0; i < rules.length; ++i ) {
            addRule( rules[i] );
        }
    }

    public void addRule(Rule rule) throws FactException,
                                  RuleIntegrationException,
                                  InvalidPatternException {
        this.builder.addRule( rule );
    }

    public Set getWorkingMemories() {
        return this.workingMemories.keySet();
    }
}
