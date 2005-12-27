package org.drools.reteoo;

/*
 * $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.HashMap;
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
import org.drools.conflict.DefaultConflictResolver;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.ConflictResolver;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleBaseContext;

/**
 * Implementation of <code>RuleBase</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
class RuleBaseImpl
    implements
    RuleBase {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The root Rete-OO for this <code>RuleBase</code>. */
    private final Rete              rete;

    private final Builder           builder;

    /** Conflict resolution strategy. */
    private final ConflictResolver  conflictResolver;

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
    RuleBaseImpl(){
        this( DefaultConflictResolver.getInstance(),
              new DefaultFactHandleFactory(),
              null,
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
    RuleBaseImpl(ConflictResolver conflictResolver,
                 FactHandleFactory factHandleFactory,
                 Set ruleSets,
                 Map applicationData,
                 RuleBaseContext ruleBaseContext){
        this.rete = new Rete();
        this.builder = new Builder( this );
        this.factHandleFactory = factHandleFactory;
        this.conflictResolver = conflictResolver;
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
    public WorkingMemory newWorkingMemory(){
        return newWorkingMemory( true );
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory(boolean keepReference){
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( this );
        if ( keepReference ) {
            this.workingMemories.put( workingMemory,
                                      RuleBaseImpl.PRESENT );
        }
        return workingMemory;
    }

    void disposeWorkingMemory(WorkingMemory workingMemory){
        this.workingMemories.remove( workingMemory );
    }

    /**
     * @see RuleBase
     */
    public FactHandleFactory getFactHandleFactory(){
        return this.factHandleFactory;
    }

    /**
     * @see RuleBase
     */
    public ConflictResolver getConflictResolver(){
        return this.conflictResolver;
    }

    /**
     * Retrieve the Rete-OO network for this <code>RuleBase</code>.
     * 
     * @return The RETE-OO network.
     */
    Rete getRete(){
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
                      WorkingMemoryImpl workingMemory) throws FactException{
        getRete().assertObject( object,
                                (FactHandleImpl) handle,
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
                       WorkingMemoryImpl workingMemory) throws FactException{
        getRete().retractObject( (FactHandleImpl) handle,
                                 context,
                                 workingMemory );
    }

    public RuleSet[] getRuleSets(){
        return (RuleSet[]) this.ruleSets.toArray( new RuleSet[this.ruleSets.size()] );
    }

    public Map getApplicationData(){
        return this.applicationData;
    }

    public RuleBaseContext getRuleBaseContext(){
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
                                           InvalidPatternException{
        Map newApplicationData = ruleSet.getApplicationData();

        // Check that the application data is valid, we cannot change the type
        // of an already declared
        // application data variable
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
                                  InvalidPatternException{
        this.builder.addRule( rule );
    }

    public Set getWorkingMemories(){
        return this.workingMemories.keySet();
    }
}
