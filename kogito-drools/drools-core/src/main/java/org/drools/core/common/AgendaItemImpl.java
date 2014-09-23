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

package org.drools.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.rule.ActivationUnMatchListener;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Item entry in the <code>Agenda</code>.
 */
public class AgendaItemImpl<T extends ModedAssertion<T>>  implements  AgendaItem<T> {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;
    /**
     * The tuple.
     */
    private           LeftTuple                                      tuple;
    /**
     * The salience
     */
    private           int                                            salience;
    /**
     * Rule terminal node, gives access to SubRule *
     */
    private           TerminalNode                                   rtn;
    /**
     * The propagation context
     */
    private           PropagationContext                             context;
    /**
     * The activation number
     */
    private           long                                           activationNumber;
    private volatile  int                                            index;
    private volatile  boolean                                        queued;
    private           LinkedList<LogicalDependency<T>>  justified;
    private           LinkedList<LogicalDependency<SimpleMode>>      blocked;
    private           LinkedList<SimpleMode>                         blockers;
    private           InternalAgendaGroup                            agendaGroup;
    private           ActivationGroupNode                            activationGroupNode;
    private           ActivationNode                                 activationNode;
    private           InternalFactHandle                             factHandle;
    private transient boolean                                        canceled;
    private           boolean                                        matched;
    private           boolean                                        active;
    private           ActivationUnMatchListener                      activationUnMatchListener;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public AgendaItemImpl() {

    }

    /**
     * Construct.
     *
     * @param tuple          The tuple.
     * @param agendaGroup
     */
    public AgendaItemImpl(final long activationNumber,
                          final LeftTuple tuple,
                          final int salience,
                          final PropagationContext context,
                          final TerminalNode rtn,
                          final InternalAgendaGroup agendaGroup) {
        this.tuple = tuple;
        this.context = context;
        this.salience = salience;
        this.rtn = rtn;
        this.activationNumber = activationNumber;
        this.index = -1;
        this.matched = true;
        this.agendaGroup = agendaGroup;
    }

    @Override
    public PropagationContext getPropagationContext() {
        return this.context;
    }

    @Override
    public void setPropagationContext(PropagationContext context) {
        this.context = context;
    }

    /**
     * Retrieve the rule.
     *
     * @return The rule.
     */
    @Override
    public RuleImpl getRule() {
        return this.rtn.getRule();
    }

    @Override
    public Consequence getConsequence() {
        String consequenceName = ((RuleTerminalNode) rtn).getConsequenceName();
        return consequenceName.equals(RuleImpl.DEFAULT_CONSEQUENCE_NAME) ? rtn.getRule().getConsequence() : rtn.getRule().getNamedConsequence(consequenceName);
    }

    /**
     * Retrieve the tuple.
     *
     * @return The tuple.
     */
    @Override
    public LeftTuple getTuple() {
        return this.tuple;
    }

    @Override
    public int getSalience() {
        return this.salience;
    }

    @Override
    public void setSalience(int salience) {
        this.salience = salience;
    }

    @Override
    public InternalFactHandle getFactHandle() {
        return factHandle;
    }

    @Override
    public void setFactHandle(InternalFactHandle factHandle) {
        this.factHandle = factHandle;
    }

    @Override
    public RuleAgendaItem getRuleAgendaItem() {
        return null;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.kie.spi.Activation#getActivationNumber()
         */
    @Override
    public long getActivationNumber() {
        return this.activationNumber;
    }

    @Override
    public void addBlocked(final LogicalDependency<SimpleMode> dep) {
        // Adds the blocked to the blockers list
        if (this.blocked == null) {
            this.blocked = new LinkedList<LogicalDependency<SimpleMode>>();
        }

        this.blocked.add(dep);

        // now ad the blocker to the blocked's list - we need to check that references are null first
        AgendaItemImpl blocked = (AgendaItemImpl) dep.getJustified();
        if (blocked.blockers == null) {
            blocked.blockers = new LinkedList<SimpleMode>();
            blocked.blockers.add( dep.getMode());
        } else if (dep.getMode().getNext() == null && dep.getMode().getPrevious() == null && blocked.getBlockers().getFirst() != dep.getMode()) {
            blocked.blockers.add(dep.getMode());
        }
    }

    @Override
    public void removeAllBlockersAndBlocked(InternalAgenda agenda) {
        if (this.blockers != null) {
            // Iterate and remove this node's logical dependency list from each of it's blockers
            for (LinkedListEntry<SimpleMode, LogicalDependency<SimpleMode>> node = blockers.getFirst(); node != null; node = node.getNext()) {
                LogicalDependency dep = node.getObject();
                dep.getJustifier().getBlocked().remove(dep);
            }
        }
        this.blockers = null;

        if (this.blocked != null) {
            // Iterate and remove this node's logical dependency list from each of it's blocked
            for (LogicalDependency<SimpleMode> dep = blocked.getFirst(); dep != null; ) {
                LogicalDependency<SimpleMode> tmp = dep.getNext();
                removeBlocked(dep);
                AgendaItem justified = (AgendaItem) dep.getJustified();
                if (justified.getBlockers().isEmpty()) {
                    agenda.stageLeftTuple(null,justified);
                }
                dep = tmp;
            }
        }
        this.blocked = null;
    }

    @Override
    public void removeBlocked(final LogicalDependency<SimpleMode> dep) {
        this.blocked.remove(dep);

        AgendaItemImpl blocked = (AgendaItemImpl) dep.getJustified();
        blocked.blockers.remove(dep.getMode());
    }

    @Override
    public LinkedList<LogicalDependency<SimpleMode>> getBlocked() {
        return this.blocked;
    }

    @Override
    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {
        this.blocked = justified;
    }

    @Override
    public LinkedList<SimpleMode> getBlockers() {
        return this.blockers;
    }

    @Override
    public void addLogicalDependency(final LogicalDependency<T> node) {
        if (this.justified == null) {
            this.justified = new LinkedList<LogicalDependency<T>>();
        }

        this.justified.add(node);
    }

    @Override
    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return this.justified;
    }

    @Override
    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {
        this.justified = justified;
    }

    @Override
    public boolean isQueued() {
        return this.queued;
    }

    @Override
    public void setQueued(final boolean queued) {
        this.queued = queued;
    }

    @Override
    public String toString() {
        return "[Activation rule=" + this.rtn.getRule().getName() + ", act#=" + this.activationNumber + ", salience=" + this.salience + ", tuple=" + this.tuple + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof AgendaItemImpl)) {
            return false;
        }

        final AgendaItem otherItem = (AgendaItem) object;

        return (this.rtn.getRule().equals(otherItem.getRule()) && this.tuple.equals(otherItem.getTuple()));
    }

    /**
     * Return the hashCode of the
     * <code>TupleKey<code> as the hashCode of the AgendaItem
     *
     * @return
     */
    @Override
    public int hashCode() {
        return this.tuple.hashCode();
    }

    @Override
    public void setQueueIndex(final int index) {
        this.index = index;
    }

    @Override
    public void dequeue() {
        if (this.agendaGroup != null) {
            this.agendaGroup.remove(this);
        }
        this.queued = false;
    }

    @Override
    public int getQueueIndex() {
        return this.index;
    }

    @Override
    public void remove() {
        dequeue();
    }

    @Override
    public ActivationGroupNode getActivationGroupNode() {
        return this.activationGroupNode;
    }

    @Override
    public void setActivationGroupNode(final ActivationGroupNode activationNode) {
        this.activationGroupNode = activationNode;
    }

    @Override
    public InternalAgendaGroup getAgendaGroup() {
        return this.agendaGroup;
    }

    @Override
    public ActivationNode getActivationNode() {
        return this.activationNode;
    }

    @Override
    public void setActivationNode(final ActivationNode activationNode) {
        this.activationNode = activationNode;
    }

    @Override
    public GroupElement getSubRule() {
        return this.rtn.getSubRule();
    }

    @Override
    public TerminalNode getTerminalNode() {
        return this.rtn;
    }

    @Override
    public ActivationUnMatchListener getActivationUnMatchListener() {
        return activationUnMatchListener;
    }

    @Override
    public void setActivationUnMatchListener(ActivationUnMatchListener activationUnMatchListener) {
        this.activationUnMatchListener = activationUnMatchListener;
    }

    @Override
    public List<FactHandle> getFactHandles() {
        FactHandle[] factHandles = this.tuple.toFactHandles();
        List<FactHandle> list = new ArrayList<FactHandle>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(factHandle);
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public String toExternalForm() {
        return "[ " + this.getRule().getName() + " active=" + this.queued + " ]";
    }

    @Override
    public List<Object> getObjects() {
        FactHandle[] factHandles = this.tuple.toFactHandles();
        List<Object> list = new ArrayList<Object>(factHandles.length);
        int j = 0;
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            list.set( j++, o instanceof QueryElementFactHandle ? null : o );
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public Object getDeclarationValue(String variableName) {
        Declaration decl = this.rtn.getSubRule().getOuterDeclarations().get(variableName);
        InternalFactHandle handle = this.tuple.get(decl);
        // need to double check, but the working memory reference is only used for resolving globals, right?
        return decl.getValue(null, handle.getObject());
    }

    @Override
    public List<String> getDeclarationIds() {
        Declaration[] declArray = ((org.drools.core.reteoo.RuleTerminalNode) this.tuple.getLeftTupleSink()).getDeclarations();
        List<String> declarations = new ArrayList<String>();
        for (Declaration decl : declArray) {
            declarations.add(decl.getIdentifier());
        }
        return Collections.unmodifiableList(declarations);
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

    @Override
    public boolean isMatched() {
        return matched;
    }

    @Override
    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isRuleAgendaItem() {
        return false;
    }

}
