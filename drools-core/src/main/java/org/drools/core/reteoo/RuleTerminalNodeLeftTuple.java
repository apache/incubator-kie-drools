/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.rule.ActivationUnMatchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleTerminalNodeLeftTuple<T extends ModedAssertion<T>> extends BaseLeftTuple implements
                                                                    AgendaItem<T> {
    private static final long serialVersionUID = 540l;
    /**
     * The salience
     */
    private           int                                            salience;
    /**
     * The activation number
     */
    private           long                                           activationNumber;
    private           int                                            queueIndex;
    private           boolean                                        queued;
    private           LinkedList<LogicalDependency<T>>               justified;
    private           LinkedList<LogicalDependency<SimpleMode>>      blocked;
    private           LinkedList<SimpleMode>                         blockers;
    private           InternalAgendaGroup                            agendaGroup;
    private           ActivationGroupNode                            activationGroupNode;
    private           ActivationNode                                 activationNode;
    private           InternalFactHandle                             activationFactHandle;
    private transient boolean                                        canceled;
    private           boolean                                        matched;
    private           boolean                                        active;
    private           ActivationUnMatchListener                      activationUnMatchListener;
    private           RuleAgendaItem                                 ruleAgendaItem;

    public RuleTerminalNodeLeftTuple() {
        // constructor needed for serialisation
    }

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public RuleTerminalNodeLeftTuple(final InternalFactHandle factHandle,
                                     final Sink sink,
                                     final boolean leftTupleMemoryEnabled) {
        super(factHandle,
              sink,
              leftTupleMemoryEnabled);
    }

    public RuleTerminalNodeLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        super(factHandle, leftTuple, sink);
    }

    public RuleTerminalNodeLeftTuple(final LeftTuple leftTuple,
                                     final Sink sink,
                                     final PropagationContext pctx,
                                     final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              sink,
              pctx,
              leftTupleMemoryEnabled);
    }

    public RuleTerminalNodeLeftTuple(final LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        super(leftTuple,
              rightTuple,
              sink);
    }

    public RuleTerminalNodeLeftTuple(final LeftTuple leftTuple,
                                     final RightTuple rightTuple,
                                     final Sink sink,
                                     final boolean leftTupleMemoryEnabled) {
        this( leftTuple,
              rightTuple,
              null,
              null,
              sink,
              leftTupleMemoryEnabled );
    }

    public RuleTerminalNodeLeftTuple(final LeftTuple leftTuple,
                                     final RightTuple rightTuple,
                                     final LeftTuple currentLeftChild,
                                     final LeftTuple currentRightChild,
                                     final Sink sink,
                                     final boolean leftTupleMemoryEnabled) {
        super(leftTuple,
              rightTuple,
              currentLeftChild,
              currentRightChild,
              sink,
              leftTupleMemoryEnabled);
    }

    public void init(final long activationNumber,
                     final int salience,
                     final PropagationContext pctx,
                     final RuleAgendaItem ruleAgendaItem,
                     InternalAgendaGroup agendaGroup) {
        setPropagationContext(pctx);
        this.salience = salience;
        this.activationNumber = activationNumber;
        this.queueIndex = -1;
        this.matched = true;
        this.ruleAgendaItem = ruleAgendaItem;
        this.agendaGroup = agendaGroup;
    }

    public void update(final int salience,
                        final PropagationContext pctx) {
        setPropagationContext(pctx);
        this.salience = salience;
        this.matched = true;
    }

    /**
     * Retrieve the rule.
     *
     * @return The rule.
     */
    public RuleImpl getRule() {
        return getTerminalNode().getRule();
    }

    public Consequence getConsequence() {
        String consequenceName = ((RuleTerminalNode) getTerminalNode()).getConsequenceName();
        return consequenceName.equals(RuleImpl.DEFAULT_CONSEQUENCE_NAME) ? getTerminalNode().getRule().getConsequence() : getTerminalNode().getRule().getNamedConsequence(consequenceName);
    }

    /**
     * Retrieve the tuple.
     *
     * @return The tuple.
     */
    public LeftTuple getTuple() {
        return this;
    }

    public int getSalience() {
        return this.salience;
    }

    public void setSalience(int salience) {
        this.salience = salience;
    }

    public InternalFactHandle getActivationFactHandle() {
        return activationFactHandle;
    }

    public void setActivationFactHandle( InternalFactHandle factHandle ) {
        this.activationFactHandle = factHandle;
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return ruleAgendaItem;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.kie.spi.Activation#getActivationNumber()
         */
    public long getActivationNumber() {
        return this.activationNumber;
    }

    public void addBlocked(final LogicalDependency<SimpleMode> dep) {
        // Adds the blocked to the blockers list
        if (this.blocked == null) {
            this.blocked = new LinkedList<LogicalDependency<SimpleMode>>();
        }

        this.blocked.add(dep);

        // now ad the blocker to the blocked's list - we need to check that references are null first
        RuleTerminalNodeLeftTuple blocked = (RuleTerminalNodeLeftTuple) dep.getJustified();
        if (blocked.blockers == null) {
            blocked.blockers = new LinkedList<SimpleMode>();
            blocked.blockers.add(dep.getMode());
        } else if (dep.getMode().getNext() == null && dep.getMode().getPrevious() == null && blocked.getBlockers().getFirst() != dep.getMode()) {
            blocked.blockers.add(dep.getMode());
        }
    }

    public void removeAllBlockersAndBlocked(InternalAgenda agenda) {
        if (this.blockers != null) {
            // Iterate and remove this node's logical dependency list from each of it's blockers
            for (SimpleMode node = blockers.getFirst(); node != null; node = node.getNext()) {
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
                RuleTerminalNodeLeftTuple justified = (RuleTerminalNodeLeftTuple) dep.getJustified();
                if (justified.getBlockers().isEmpty() && justified.isActive()) {
                    agenda.stageLeftTuple(ruleAgendaItem, justified);

                }
                dep = tmp;
            }
        }
        this.blocked = null;
    }

    public void removeBlocked(final LogicalDependency<SimpleMode> dep) {
        this.blocked.remove(dep);

        RuleTerminalNodeLeftTuple blocked = (RuleTerminalNodeLeftTuple) dep.getJustified();
        blocked.blockers.remove(dep.getMode());
    }

    public LinkedList<LogicalDependency<SimpleMode>> getBlocked() {
        return this.blocked;
    }

    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {
        this.blocked = justified;
    }

    public LinkedList<SimpleMode> getBlockers() {
        return this.blockers;
    }

    public void addLogicalDependency(final LogicalDependency<T> node) {
        if (this.justified == null) {
            this.justified = new LinkedList<LogicalDependency<T>>();
        }

        this.justified.add(node);
    }

    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return this.justified;
    }

    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {
        this.justified = justified;
    }

    public boolean isQueued() {
        return this.queued;
    }

    public void setQueued(final boolean queued) {
        this.queued = queued;
        if (queued) {
            setActive(true);
        }
    }

    public void setQueueIndex(final int queueIndex) {
        this.queueIndex = queueIndex;
    }

    public void dequeue() {
        if (this.agendaGroup != null) {
            this.agendaGroup.remove(this);
        }
        setQueued(false);
    }

    public int getQueueIndex() {
        return this.queueIndex;
    }

    public void remove() {
        dequeue();
    }

    public ActivationGroupNode getActivationGroupNode() {
        return this.activationGroupNode;
    }

    public void setActivationGroupNode(final ActivationGroupNode activationNode) {
        this.activationGroupNode = activationNode;
    }

    public InternalAgendaGroup getAgendaGroup() {
        return this.agendaGroup;
    }


    public ActivationNode getActivationNode() {
        return this.activationNode;
    }

    public void setActivationNode(final ActivationNode activationNode) {
        this.activationNode = activationNode;
    }

    public GroupElement getSubRule() {
        return getTerminalNode().getSubRule();
    }

    public TerminalNode getTerminalNode() {
        return (TerminalNode) getTupleSink();
    }

    public ActivationUnMatchListener getActivationUnMatchListener() {
        return activationUnMatchListener;
    }

    public void setActivationUnMatchListener(ActivationUnMatchListener activationUnMatchListener) {
        this.activationUnMatchListener = activationUnMatchListener;
    }

    public List<FactHandle> getFactHandles() {
        FactHandle[] factHandles = toFactHandles();
        List<FactHandle> list = new ArrayList<FactHandle>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(factHandle);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public String toExternalForm() {
        return "[ " + this.getRule().getName() + " active=" + this.queued + " ]";
    }

    public List<Object> getObjects() {
        FactHandle[] factHandles = toFactHandles();
        List<Object> list = new ArrayList<Object>(factHandles.length);
        for (FactHandle factHandle : factHandles) {
            Object o = ((InternalFactHandle) factHandle).getObject();
            if (!(o instanceof QueryElementFactHandle)) {
                list.add(o);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public Object getDeclarationValue(String variableName) {
        Declaration decl = getTerminalNode().getSubRule().getOuterDeclarations().get(variableName);
        InternalFactHandle handle = get(decl);
        // need to double check, but the working memory reference is only used for resolving globals, right?
        return decl.getValue(null, handle.getObject());
    }

    public List<String> getDeclarationIds() {
        Declaration[] declArray = ((org.drools.core.reteoo.RuleTerminalNode) getTupleSink()).getAllDeclarations();
        List<String> declarations = new ArrayList<String>();
        for (Declaration decl : declArray) {
            declarations.add(decl.getIdentifier());
        }
        return Collections.unmodifiableList(declarations);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRuleAgendaItem() {
        return false;
    }
    
    @Override
    public String toString() {
        return "["+toExternalForm()+" [ " + super.toString()+ " ] ]";
    }
}
