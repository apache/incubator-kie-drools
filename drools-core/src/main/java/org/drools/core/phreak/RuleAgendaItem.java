/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.AbstractTerminalNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.consequence.Consequence;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleAgendaItem implements LinkedListNode<RuleAgendaItem>, AgendaItem {

    private static final Logger log = LoggerFactory.getLogger(RuleAgendaItem.class);
    private static final long serialVersionUID = 510l;

    private transient RuleExecutor executor;
    private RuleAgendaItem previous;
    private RuleAgendaItem next;
    private PathMemory pmem;
    private boolean declarativeAgendaEnabled;
    /**
     * The tuple.
     */
    private Tuple                                                    tuple;
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

    private           int                                            index;
    private           boolean                                        queued;
    private transient InternalAgendaGroup                            agendaGroup;
    private           ActivationGroupNode                            activationGroupNode;
    private           ActivationNode                                 activationNode;
    private           InternalFactHandle                             factHandle;
    private transient boolean                                        canceled;
    private           boolean                                        matched;
    private           boolean                                        active;
    private Runnable callback;

    public RuleAgendaItem() {

    }

    public RuleAgendaItem(final long activationNumber,
                          final Tuple tuple,
                          final int salience,
                          final PropagationContext context,
                          final PathMemory pmem,
                          final TerminalNode rtn,
                          boolean declarativeAgendaEnabled,
                          InternalAgendaGroup agendaGroup) {
        this.pmem = pmem;
        this.declarativeAgendaEnabled = declarativeAgendaEnabled;

        this.tuple = tuple;
        this.context = context;
        this.salience = salience;
        this.rtn = rtn;
        this.activationNumber = activationNumber;
        this.index = -1;
        this.matched = true;
        this.agendaGroup = agendaGroup;
    }

    public RuleExecutor getRuleExecutor() {
        if (executor == null) {
            executor = new RuleExecutor(pmem, this, declarativeAgendaEnabled);
        }
        return executor;
    }

    public boolean isRuleAgendaItem() {
        return true;
    }

    public RuleAgendaItem getPrevious() {
        return previous;
    }

    public void setPrevious(RuleAgendaItem previous) {
        this.previous = previous;
    }

    public RuleAgendaItem getNext() {
        return next;
    }

    public void setNext(RuleAgendaItem next) {
        this.next = next;
    }

    public boolean isInList( LinkedList<RuleAgendaItem> list ) {
        return previous != null || next != null || list.getFirst() == this;
    }

    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public PropagationContext getPropagationContext() {
        return this.context;
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

    public RuleBasePartitionId getPartition() {
        return this.rtn.getPartitionId();
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
    public Tuple getTuple() {
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
    public InternalFactHandle getActivationFactHandle() {
        return factHandle;
    }

    @Override
    public void setActivationFactHandle( InternalFactHandle factHandle ) {
        this.factHandle = factHandle;
    }

    @Override
    public RuleAgendaItem getRuleAgendaItem() {
        return null;
    }

    @Override
    public long getActivationNumber() {
        return this.activationNumber;
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

    @Override
    public void setQueueIndex(final int index) {
        this.index = index;
    }

    @Override
    public int getQueueIndex() {
        return this.index;
    }

    @Override
    public void dequeue() {
        if (this.agendaGroup != null) {
            this.agendaGroup.remove(this);
        }
        this.queued = false;
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
    public List<FactHandle> getFactHandles() {
        return getFactHandles(this.tuple);
    }

    @Override
    public String toExternalForm() {
        return "[ " + this.getRule().getName() + " active=" + this.queued + " ]";
    }

    @Override
    public List<Object> getObjects() {
        return getObjects(this.tuple);
    }

    @Override
    public List<Object> getObjectsDeep() {
        return getObjectsDeep((LeftTuple) this.tuple);
    }

    @Override
    public Object getDeclarationValue(String variableName) {
        Declaration decl = this.rtn.getSubRule().getOuterDeclarations().get(variableName);
        // need to double check, but the working memory reference is only used for resolving globals, right?
        return decl.getValue(tuple);
    }

    @Override
    public List<String> getDeclarationIds() {
        Declaration[] declArray = ((AbstractTerminalNode) this.tuple.getTupleSink()).getAllDeclarations();
        List<String> declarations = new ArrayList<>();
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

    public boolean isRuleInUse() {
        return rtn.getLeftTupleSource() != null;
    }

    @Override
    public Runnable getCallback() {
        return callback;
    }

    @Override
    public void setCallback( Runnable callback ) {
        this.callback = callback;
    }
}
