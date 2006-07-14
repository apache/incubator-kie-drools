package org.drools.leaps;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.common.AbstractRuleBase;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

/**
 * This base class for the engine and analogous to Drool's RuleBase class. It
 * has a similar interface adapted to the Leaps algorithm
 * 
 * @author Alexander Bagerman
 * 
 */
public class LeapsRuleBase extends AbstractRuleBase {
    private static final long serialVersionUID = 1487738104393155409L;

    private Map               leapsRules;

    /**
     * Default constructor - for Externalizable. This should never be used by a user, as it 
     * will result in an invalid state for the instance.
     */
    public LeapsRuleBase() {

    }

    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     */
    public LeapsRuleBase(final String id) {
        this( id,
              null,
              new LeapsFactHandleFactory() );
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
    public LeapsRuleBase(final String id,
                         final RuleBaseConfiguration config,
                         final FactHandleFactory factHandleFactory) {
        super( id,
               config,
               factHandleFactory );
        this.leapsRules = new HashMap();
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory() {
        return newWorkingMemory( true );
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory(final boolean keepReference) {
        final LeapsWorkingMemory workingMemory = new LeapsWorkingMemory( this.workingMemoryCounter++, 
                                                                         this );
        // add all rules added so far
        for ( final Iterator it = this.leapsRules.values().iterator(); it.hasNext(); ) {
            workingMemory.addLeapsRules( (List) it.next() );
        }
        //
        super.addWorkingMemory( workingMemory,
                                keepReference );

        return workingMemory;
    }

    /**
     * Creates leaps rule wrappers and propagate rule to the working memories
     * 
     * @param rule
     * @throws FactException
     * @throws InvalidPatternException
     */
    public void addRule(final Rule rule) throws FactException,
                                        InvalidPatternException {
        super.addRule( rule );

        final List rules = Builder.processRule( rule );

        this.leapsRules.put( rule,
                             rules );

        for ( final Iterator it = this.getWorkingMemories().iterator(); it.hasNext(); ) {
            ((LeapsWorkingMemory) it.next()).addLeapsRules( rules );
        }

        // Iterate each workingMemory and attempt to fire any rules, that were
        // activated as a result of the new rule addition
        for ( final Iterator it = this.getWorkingMemories().iterator(); it.hasNext(); ) {
            final LeapsWorkingMemory workingMemory = (LeapsWorkingMemory) it.next();
            workingMemory.fireAllRules();
        }
    }

    public void removeRule(final Rule rule) {
        for ( final Iterator it = this.getWorkingMemories().iterator(); it.hasNext(); ) {
            ((LeapsWorkingMemory) it.next()).removeRule( (List) this.leapsRules.remove( rule ) );
        }
    }

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     * 
     */
    public void writeExternal(final ObjectOutput stream) throws IOException {
        doWriteExternal( stream,
                         new Object[0] );
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     * 
     */
    public void readExternal(final ObjectInput stream) throws IOException,
                                                      ClassNotFoundException {
        doReadExternal( stream,
                        new Object[0] );
        
        this.leapsRules = new HashMap();
        
        for ( int i = 0; i < this.getPackages().length; i++ ) {
            final Rule[] rules = this.getPackages()[i].getRules();

            for ( int j = 0; j < rules.length; ++j ) {
                addRule( rules[j] );
            }
        }
    }

    public void assertObject(final FactHandle handle,
                             final Object object,
                             final PropagationContext context,
                             final ReteooWorkingMemory workingMemory) throws FactException {
        // do nothing as reteoo specific

    }

    public void modifyObject(final FactHandle handle,
                             final PropagationContext context,
                             final ReteooWorkingMemory workingMemory) throws FactException {
        // do nothing as reteoo specific

    }

    public void retractObject(final FactHandle handle,
                              final PropagationContext context,
                              final ReteooWorkingMemory workingMemory) throws FactException {
        // do nothing as reteoo specific

    }

}