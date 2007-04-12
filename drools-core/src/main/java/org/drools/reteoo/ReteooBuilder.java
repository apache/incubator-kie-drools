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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuleIntegrationException;
import org.drools.common.BaseNode;
import org.drools.reteoo.builder.ReteooRuleBuilder;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;

/**
 * Builds the Rete-OO network for a <code>Package</code>.
 * 
 * @see org.drools.rule.Package
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 * 
 */
public class ReteooBuilder
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long               serialVersionUID = 1737643968218792944L;

    /** The RuleBase */
    private transient ReteooRuleBase        ruleBase;

    private transient ReteooWorkingMemory[] workingMemories;

    /** Nodes that have been attached. */
    private final Map                       attachedNodes;

    private Map                             rules;

    private transient ReteooRuleBuilder     ruleBuilder;

    private IdGenerator                     idGenerator;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    ReteooBuilder(final ReteooRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.attachedNodes = new HashMap();
        this.rules = new HashMap();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator( 1 );
        this.ruleBuilder = new ReteooRuleBuilder();
    }

    /**
     * Allow this to be settable, otherwise we get infinite recursion on serialisation
     * @param ruleBase
     */
    void setRuleBase(final ReteooRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    /**
     * Allow this to be settable, otherwise we get infinite recursion on serialisation
     * @param ruleBase
     */
    void setRete(final Rete rete) {

    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add a <code>Rule</code> to the network.
     * 
     * @param rule
     *            The rule to add.
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws InvalidPatternException
     */
    void addRule(final Rule rule) throws InvalidPatternException {
        final List terminals = this.ruleBuilder.addRule( rule,
                                                   this.ruleBase,
                                                   this.attachedNodes,
                                                   this.idGenerator );

        this.rules.put( rule,
                        terminals.toArray( new BaseNode[terminals.size()] ) );
    }

    public BaseNode[] getTerminalNodes(final Rule rule) {
        return (BaseNode[]) this.rules.remove( rule );
    }

    public void removeRule(final Rule rule) {
        // reset working memories for potential propagation
        this.workingMemories = (ReteooWorkingMemory[]) this.ruleBase.getWorkingMemories().toArray( new ReteooWorkingMemory[this.ruleBase.getWorkingMemories().size()] );

        final Object object = this.rules.get( rule );

        final BaseNode[] nodes = (BaseNode[]) object;
        for ( int i = 0, length = nodes.length; i < length; i++ ) {
            final BaseNode node = nodes[i];
            node.remove( null,
                         this.workingMemories );
        }
    }

    public static class IdGenerator
        implements
        Serializable {

        private static final long serialVersionUID = -5909710713463187779L;

        private int               nextId;

        public IdGenerator(final int firstId) {
            this.nextId = firstId;
        }

        public int getNextId() {
            return this.nextId++;
        }

        public void releaseLastId() {
            this.nextId--;
        }

    }

}
