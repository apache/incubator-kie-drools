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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.RuleIntegrationException;
import org.drools.base.SalienceInteger;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.ReteooRuleBuilder;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Salience;

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
    private static final long                 serialVersionUID = 400L;

    /** The RuleBase */
    private transient InternalRuleBase          ruleBase;

    private transient InternalWorkingMemory[] workingMemories;

    private Map                               rules;

    private transient ReteooRuleBuilder       ruleBuilder;

    private IdGenerator                       idGenerator;
    
    private boolean                           ordered;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    ReteooBuilder(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.rules = new HashMap();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator( 1 );
        this.ruleBuilder = new ReteooRuleBuilder();
    }

    private void readObject(ObjectInputStream stream) throws IOException,
                                                     ClassNotFoundException {
        stream.defaultReadObject();
        this.ruleBase = ((DroolsObjectInputStream) stream).getRuleBase();
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
                                                         this.idGenerator );

        this.rules.put( rule,
                        terminals.toArray( new BaseNode[terminals.size()] ) );
    }
    
    public void order() {
        if ( ordered ) {
            // we should only do this on first call, its expected the RuleBase should not change afterwards.
            return;
        }
        Map map = new HashMap();
        
        for ( Iterator it = this.rules.values().iterator(); it.hasNext(); ) {
            BaseNode[] nodes = (BaseNode[]) it.next();
            for ( int i = 0 ; i < nodes.length; i++ ) {
                if ( nodes[i] instanceof RuleTerminalNode ) {
                    RuleTerminalNode node = ( RuleTerminalNode ) nodes[i];
                    String agendaGroup = node.getRule().getAgendaGroup();
                    if ( agendaGroup == null || agendaGroup.equals( "" ) ) {
                        agendaGroup = "MAIN";
                    }
                    List rules = ( List ) map.get( agendaGroup );
                    if ( rules == null ) {
                        rules = new ArrayList();
                        map.put( agendaGroup, rules );
                    }
                    rules.add( node );
                }
            }
        }
        
        for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = ( Entry ) it.next();
            String agendaGroup = (String) entry.getKey();
            List rules = ( List ) entry.getValue();
            Collections.sort( rules, RuleSequenceComparator.INSTANCE );
            
            int i = 0;
            for ( Iterator listIter = rules.iterator(); listIter.hasNext(); ) {
                RuleTerminalNode node = ( RuleTerminalNode ) listIter.next();
                node.setSequence( i++ );
            }
            
            ruleBase.getAgendaGroupRuleTotals().put( agendaGroup, new Integer( i ) );
        }
        ordered = true;
    }

    public static class RuleSequenceComparator implements Comparator {
        public final static RuleSequenceComparator INSTANCE = new RuleSequenceComparator();

        public int compare(Object o1,
                           Object o2) {
            RuleTerminalNode r1 = (RuleTerminalNode) o1;
            RuleTerminalNode r2 = (RuleTerminalNode) o2;
            
            Salience so1 = r1.getRule().getSalience();
            if (so1 != null && !(so1 instanceof SalienceInteger) ) {
                throw new RuntimeException(r1.getRule().getName() + "must not have a dynamic salience" );
            }
            Salience so2 = r2.getRule().getSalience();
            if (so2 != null && !(so2 instanceof SalienceInteger) ) {
                throw new RuntimeException(r2.getRule().getName() + "must not have a dynamic salience" );
            }
            
            int s1 = so1.getValue( null, null );
            int s2 = so2.getValue( null, null );
            
            if ( s1 >  s2) {                        
                return -1;
            } else if ( s1 < s2 ) {
                return 1;
            } 
            
            int id1 =r1.getId();
            int id2 =r2.getId();
            
            if ( id1 <  id2) {                        
                return -1;
            } else if ( id1 > id2 ) {
                return 1;
            } else {
                return 0;
            }
        }
        
    }
    
    public BaseNode[] getTerminalNodes(final Rule rule) {
        return (BaseNode[]) this.rules.remove( rule );
    }

    public void removeRule(final Rule rule) {
        // reset working memories for potential propagation
        this.workingMemories = (InternalWorkingMemory[]) this.ruleBase.getWorkingMemories();

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

        private static final long serialVersionUID = 400L;

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
