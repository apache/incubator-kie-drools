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

package org.drools.reteoo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import org.drools.RuleIntegrationException;
import org.drools.base.SalienceInteger;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.builder.ReteooRuleBuilder;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Rule;
import org.drools.spi.Salience;

/**
 * Builds the Rete-OO network for a <code>Package</code>.
 *
 * @see org.drools.rule.Package
 */
public class ReteooBuilder
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long           serialVersionUID = 510l;

    /** The RuleBase */
    private transient InternalRuleBase  ruleBase;

    private Map<Rule, BaseNode[]>       rules;

    private transient ReteooRuleBuilder ruleBuilder;

    private IdGenerator                 idGenerator;

    private boolean                     ordered;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public ReteooBuilder() {

    }

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    ReteooBuilder(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.rules = new HashMap<Rule, BaseNode[]>();

        //Set to 1 as Rete node is set to 0
        this.idGenerator = new IdGenerator( 1 );
        this.ruleBuilder = new ReteooRuleBuilder();
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
    synchronized void addRule(final Rule rule) throws InvalidPatternException {
        final List<TerminalNode> terminals = this.ruleBuilder.addRule( rule,
                                                                       this.ruleBase,
                                                                       this.idGenerator );

        this.rules.put( rule,
                        terminals.toArray( new BaseNode[terminals.size()] ) );
    }
    
    public void addEntryPoint( String id ) {
        this.ruleBuilder.addEntryPoint( id,
                                        this.ruleBase,
                                        this.idGenerator );
    }

    public IdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    protected void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public synchronized void order() {
        if ( ordered ) {
            // we should only do this on first call, its expected the RuleBase should not change afterwards.
            return;
        }
        Map map = new HashMap();

        for ( Iterator it = this.rules.values().iterator(); it.hasNext(); ) {
            BaseNode[] nodes = (BaseNode[]) it.next();
            for ( int i = 0; i < nodes.length; i++ ) {
                if ( nodes[i] instanceof RuleTerminalNode ) {
                    RuleTerminalNode node = (RuleTerminalNode) nodes[i];
                    String agendaGroup = node.getRule().getAgendaGroup();
                    if ( agendaGroup == null || agendaGroup.equals( "" ) ) {
                        agendaGroup = "MAIN";
                    }
                    List rules = (List) map.get( agendaGroup );
                    if ( rules == null ) {
                        rules = new ArrayList();
                        map.put( agendaGroup,
                                 rules );
                    }
                    rules.add( node );
                }
            }
        }

        for ( Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            String agendaGroup = (String) entry.getKey();
            List rules = (List) entry.getValue();
            Collections.sort( rules,
                              RuleSequenceComparator.INSTANCE );

            int i = 0;
            for ( Iterator listIter = rules.iterator(); listIter.hasNext(); ) {
                RuleTerminalNode node = (RuleTerminalNode) listIter.next();
                node.setSequence( i++ );
            }

            ruleBase.getAgendaGroupRuleTotals().put( agendaGroup,
                    i );
        }
        ordered = true;
    }

    public static class RuleSequenceComparator
        implements
        Comparator {
        public final static RuleSequenceComparator INSTANCE = new RuleSequenceComparator();

        public int compare(Object o1,
                           Object o2) {
            RuleTerminalNode r1 = (RuleTerminalNode) o1;
            RuleTerminalNode r2 = (RuleTerminalNode) o2;

            Salience so1 = r1.getRule().getSalience();
            if ( so1 != null && !(so1 instanceof SalienceInteger) ) {
                throw new RuntimeException( r1.getRule().getName() + "must not have a dynamic salience" );
            }
            Salience so2 = r2.getRule().getSalience();
            if ( so2 != null && !(so2 instanceof SalienceInteger) ) {
                throw new RuntimeException( r2.getRule().getName() + "must not have a dynamic salience" );
            }

            int s1 = so1.getValue( null,
                                   null,
                                   null );
            int s2 = so2.getValue( null,
                                   null,
                                   null );

            if ( s1 > s2 ) {
                return -1;
            } else if ( s1 < s2 ) {
                return 1;
            }

            int id1 = r1.getId();
            int id2 = r2.getId();

            if ( id1 < id2 ) {
                return -1;
            } else if ( id1 > id2 ) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    public synchronized BaseNode[] getTerminalNodes(final Rule rule) {
        return this.rules.get( rule );
    }

    public synchronized Map<Rule, BaseNode[]> getTerminalNodes() {
        return this.rules;
    }
    
    public synchronized void removeRule(final Rule rule) {
        // reset working memories for potential propagation
        InternalWorkingMemory[] workingMemories = this.ruleBase.getWorkingMemories();

        final Object object = this.rules.remove( rule );

        final BaseNode[] nodes = (BaseNode[]) object;
        final RuleRemovalContext context = new RuleRemovalContext( rule );
        for ( int i = 0, length = nodes.length; i < length; i++ ) {
            final BaseNode node = nodes[i];
            node.remove( context,
                         this,
                         null,
                         workingMemories );
        }
    }

    public static class IdGenerator
        implements
        Externalizable {

        private static final long serialVersionUID = 510l;

        private Queue<Integer>    recycledIds;
        private int               nextId;

        public IdGenerator() {
        }

        public IdGenerator(final int firstId) {
            this.nextId = firstId;
            this.recycledIds = new LinkedList<Integer>();
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            recycledIds = (Queue<Integer>) in.readObject();
            nextId = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( recycledIds );
            out.writeInt( nextId );
        }

        public synchronized int getNextId() {
            Integer id = this.recycledIds.poll();
            return ( id == null ) ? this.nextId++ : id.intValue();
        }

        public synchronized void releaseId(int id) {
            this.recycledIds.add( id );
        }

        public int getLastId() {
            return this.nextId - 1;
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        boolean isDrools = out instanceof DroolsObjectOutputStream;
        DroolsObjectOutputStream droolsStream;
        ByteArrayOutputStream bytes;

        if ( isDrools ) {
            bytes = null;
            droolsStream = (DroolsObjectOutputStream) out;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream( bytes );
        }
        droolsStream.writeObject( rules );
        droolsStream.writeObject( idGenerator );
        droolsStream.writeBoolean( ordered );
        if ( !isDrools ) {
            droolsStream.flush();
            droolsStream.close();
            bytes.close();
            out.writeInt( bytes.size() );
            out.writeObject( bytes.toByteArray() );
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        boolean isDrools = in instanceof DroolsObjectInputStream;
        DroolsObjectInputStream droolsStream;
        ByteArrayInputStream bytes;

        if ( isDrools ) {
            bytes = null;
            droolsStream = (DroolsObjectInputStream) in;
        } else {
            bytes = new ByteArrayInputStream( (byte[]) in.readObject() );
            droolsStream = new DroolsObjectInputStream( bytes );
        }
        
        this.rules = (Map<Rule, BaseNode[]>) droolsStream.readObject();
        this.idGenerator = (IdGenerator) droolsStream.readObject();
        this.ordered = droolsStream.readBoolean();
        if ( !isDrools ) {
            droolsStream.close();
            bytes.close();
        }

        this.ruleBuilder = new ReteooRuleBuilder();
    }

    public void setRuleBase(ReteooRuleBase reteooRuleBase) {
        this.ruleBase = reteooRuleBase;
    }

}
