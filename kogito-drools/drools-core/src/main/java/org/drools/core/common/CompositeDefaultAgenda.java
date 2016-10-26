/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.ExecutableEntry;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.spi.Tuple;
import org.drools.core.util.CompositeIterator;
import org.drools.core.util.LinkedList;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.concurrent.ExecutorProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class CompositeDefaultAgenda implements Externalizable, InternalAgenda {

    protected static final transient Logger log = LoggerFactory.getLogger( CompositeDefaultAgenda.class );

    private static final Executor EXECUTOR = ExecutorProviderFactory.getExecutorProvider().getExecutor();

    private final DefaultAgenda[] agendas = new DefaultAgenda[RuleBasePartitionId.PARALLEL_PARTITIONS_NUMBER];

    private final DefaultAgenda.ExecutionStateMachine executionStateMachine = new DefaultAgenda.ExecutionStateMachine();

    private PropagationList propagationList;

    public CompositeDefaultAgenda() { }

    public CompositeDefaultAgenda(InternalKnowledgeBase kBase) {
        this( kBase, true );
    }

    public CompositeDefaultAgenda(InternalKnowledgeBase kBase, boolean initMain) {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i] = new PartitionedDefaultAgenda(kBase, initMain, executionStateMachine, i);
        }
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        for ( DefaultAgenda agenda : agendas ) {
            out.writeObject( agenda );
        }
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i] = (DefaultAgenda) in.readObject();
        }
    }

    public DefaultAgenda getPartitionedAgenda(int partitionNr) {
        return agendas[partitionNr];
    }

    public DefaultAgenda getPartitionedAgendaForNode(NetworkNode node) {
        return getPartitionedAgenda( node.getPartitionId().getParallelEvaluationSlot() );
    }

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return agendas[0].getWorkingMemory();
    }

    @Override
    public void setWorkingMemory( InternalWorkingMemory workingMemory ) {
        Stream.of( agendas ).forEach( a -> a.setWorkingMemory( workingMemory ) );
        // this composite agenda and the first partitioned one share the same propagation list
        this.propagationList = agendas[0].getPropagationList();
    }

    @Override
    public int fireAllRules( AgendaFilter agendaFilter, int fireLimit ) {
        if (!executionStateMachine.toFireAllRules()) {
            return 0;
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Starting Fire All Rules");
        }

        int iterationFireCount = 1;
        int fireCount = 0;
        boolean limitReached = false;

        try {
            while ( iterationFireCount > 0 && !limitReached && hasPendingPropagations() ) {
                iterationFireCount = parallelFire( agendaFilter, fireLimit - fireCount );
                fireCount += iterationFireCount;
                limitReached = ( fireLimit > 0 && fireCount >= fireLimit );
            }
        } finally {
            executionStateMachine.immediateHalt(propagationList);
        }

        if ( log.isTraceEnabled() ) {
            log.trace("Ending Fire All Rules");
        }

        return fireCount;
    }

    private int parallelFire( AgendaFilter agendaFilter, int fireLimit ) {
        CompletableFuture<Integer>[] results = new CompletableFuture[agendas.length-1];
        for (int i = 0; i < results.length; i++) {
            final int j = i;
            results[j] = supplyAsync( () -> agendas[j].internalFireAllRules( agendaFilter, fireLimit, false ), EXECUTOR );
        }

        int result = agendas[agendas.length-1].internalFireAllRules( agendaFilter, fireLimit, false );
        for (int i = 0; i < results.length; i++) {
            result += results[i].join();
        }
        return result;
    }

    @Override
    public RuleAgendaItem createRuleAgendaItem( int salience, PathMemory rs, TerminalNode rtn ) {
        return getPartitionedAgendaForNode(rtn).createRuleAgendaItem( salience, rs, rtn );
    }

    @Override
    public AgendaItem createAgendaItem( RuleTerminalNodeLeftTuple rtnLeftTuple, int salience, PropagationContext context, RuleAgendaItem ruleAgendaItem, InternalAgendaGroup agendaGroup ) {
        return getPartitionedAgendaForNode(ruleAgendaItem.getTerminalNode()).createAgendaItem( rtnLeftTuple, salience, context, ruleAgendaItem, agendaGroup );
    }

    @Override
    public int unstageActivations() {
        // Not used by phreak, but still called by some generic code.
        return 0;
    }

    @Override
    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    @Override
    public void fireUntilHalt( AgendaFilter agendaFilter ) {
        if ( log.isTraceEnabled() ) {
            log.trace("Starting Fire Until Halt");
        }
        executionStateMachine.toFireUntilHalt();
        try {
            while (isFiring()) {
                CompletableFuture<Void>[] futures = new CompletableFuture[agendas.length-1];
                for (int i = 0; i < futures.length; i++) {
                    final int j = i;
                    futures[j] = runAsync( () -> agendas[j].internalFireUntilHalt( agendaFilter, false ), EXECUTOR );
                }

                agendas[agendas.length-1].internalFireUntilHalt( agendaFilter, false );

                for (int i = 0; i < futures.length; i++) {
                    futures[i].join();
                }
            }
        } finally {
            executionStateMachine.immediateHalt(propagationList);
        }
        if ( log.isTraceEnabled() ) {
            log.trace("Ending Fire Until Halt");
        }
    }

    @Override
    public boolean dispose(InternalWorkingMemory wm) {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].getPropagationList().dispose();
        }
        return executionStateMachine.dispose( wm );
    }

    @Override
    public boolean isAlive() {
        return executionStateMachine.isAlive();
    }

    @Override
    public void halt() {
        if ( isFiring() ) {
            propagationList.addEntry(new CompositeHalt( executionStateMachine, this ) );
        }
    }

    static class CompositeHalt extends DefaultAgenda.Halt {

        private final CompositeDefaultAgenda compositeAgenda;

        protected CompositeHalt( DefaultAgenda.ExecutionStateMachine executionStateMachine, CompositeDefaultAgenda compositeAgenda ) {
            super( executionStateMachine );
            this.compositeAgenda = compositeAgenda;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            super.execute( wm );
            compositeAgenda.notifyWaitOnRest();
        }
    }

    @Override
    public boolean isFiring() {
        return executionStateMachine.isFiring();
    }

    @Override
    public void addPropagation( PropagationEntry propagationEntry ) {
        if (propagationEntry.isPartitionSplittable()) {
            for ( int i = 0; i < agendas.length; i++ ) {
                agendas[i].addPropagation( propagationEntry.getSplitForPartition( i ) );
            }
        } else {
            propagationList.addEntry( propagationEntry );
        }
    }

    @Override
    public void flushPropagations() {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].flushPropagations();
        }
    }

    @Override
    public void notifyWaitOnRest() {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].notifyWaitOnRest();
        }
    }

    @Override
    public Iterator<PropagationEntry> getActionsIterator() {
        return new CompositeIterator<>( Stream.of( agendas ).map( DefaultAgenda::getActionsIterator ).toArray(Iterator[]::new) );
    }

    @Override
    public boolean hasPendingPropagations() {
        for ( int i = 0; i < agendas.length; i++ ) {
            if (agendas[i].hasPendingPropagations()) {
                return true;
            }
        }
        return false;
    }

    public void handleException(InternalWorkingMemory wm, Activation activation, Exception e) {
        agendas[0].handleException( wm, activation, e );
    }

    @Override
    public void clear() {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].clear();
        }
    }

    @Override
    public void reset() {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].reset();
        }
    }

    @Override
    public void executeTask( ExecutableEntry executable ) {
        agendas[0].executeTask( executable );
    }

    @Override
    public void executeFlush() {
        if (!executionStateMachine.toExecuteTaskState()) {
            return;
        }

        try {
            for ( int i = 0; i < agendas.length; i++ ) {
                agendas[i].flushPropagations();
            }
        } finally {
            executionStateMachine.immediateHalt(propagationList);
        }
    }

    @Override
    public void activate() {
        agendas[0].activate();
    }

    @Override
    public void deactivate() {
        agendas[0].deactivate();
    }

    @Override
    public boolean tryDeactivate() {
        return agendas[0].tryDeactivate();
    }

    @Override
    public void activateRuleFlowGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.activateRuleFlowGroup -> TODO" );
    }

    @Override
    public void activateRuleFlowGroup( String name, long processInstanceId, String nodeInstanceId ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.activateRuleFlowGroup -> TODO" );
    }

    @Override
    public void deactivateRuleFlowGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.deactivateRuleFlowGroup -> TODO" );
    }

    @Override
    public AgendaGroup[] getAgendaGroups() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getAgendaGroups -> TODO" );
    }

    @Override
    public AgendaGroup[] getStack() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getStack -> TODO" );
    }

    @Override
    public int focusStackSize() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.focusStackSize -> TODO" );
    }

    @Override
    public int agendaSize() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.agendaSize -> TODO" );
    }

    @Override
    public Activation[] getActivations() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getActivations -> TODO" );
    }

    @Override
    public Activation[] getScheduledActivations() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getScheduledActivations -> TODO" );
    }

    @Override
    public void clearAndCancel() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancel -> TODO" );
    }

    @Override
    public void clearAndCancelAgendaGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancelAgendaGroup -> TODO" );
    }

    @Override
    public void clearAndCancelAgendaGroup( InternalAgendaGroup agendaGroup ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancelAgendaGroup -> TODO" );
    }

    @Override
    public void clearAndCancelActivationGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancelActivationGroup -> TODO" );
    }

    @Override
    public void clearAndCancelActivationGroup( InternalActivationGroup activationGroup ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancelActivationGroup -> TODO" );
    }

    @Override
    public void clearAndCancelRuleFlowGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.clearAndCancelRuleFlowGroup -> TODO" );
    }

    @Override
    public String getFocusName() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getFocusName -> TODO" );
    }

    @Override
    public boolean fireTimedActivation( Activation activation ) throws ConsequenceException {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.fireTimedActivation -> TODO" );
    }

    @Override
    public void removeScheduleItem( ScheduledAgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.removeScheduleItem -> TODO" );
    }

    @Override
    public <T extends ModedAssertion<T>> LinkedList<ScheduledAgendaItem<T>> getScheduledActivationsLinkedList() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getScheduledActivationsLinkedList -> TODO" );
    }

    @Override
    public int fireNextItem( AgendaFilter filter, int fireCount, int fireLimit ) throws ConsequenceException {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.fireNextItem -> TODO" );
    }

    @Override
    public void scheduleItem( ScheduledAgendaItem item, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.scheduleItem -> TODO" );
    }

    @Override
    public boolean createActivation( Tuple tuple, PropagationContext context, InternalWorkingMemory workingMemory, TerminalNode rtn ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.createActivation -> TODO" );
    }

    @Override
    public void cancelActivation( Tuple leftTuple, PropagationContext context, Activation activation, TerminalNode rtn ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.cancelActivation -> TODO" );
    }

    @Override
    public boolean addActivation( AgendaItem activation ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addActivation -> TODO" );
    }

    @Override
    public void removeActivation( AgendaItem activation ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.removeActivation -> TODO" );
    }

    @Override
    public void modifyActivation( AgendaItem activation, boolean previouslyActive ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.modifyActivation -> TODO" );
    }

    @Override
    public void addAgendaGroup( AgendaGroup agendaGroup ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addAgendaGroup -> TODO" );
    }

    @Override
    public boolean isDeclarativeAgenda() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.isDeclarativeAgenda -> TODO" );
    }

    @Override
    public boolean isRuleInstanceAgendaItem( String ruleflowGroupName, String ruleName, long processInstanceId ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.isRuleInstanceAgendaItem -> TODO" );
    }

    @Override
    public AgendaGroup getAgendaGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getAgendaGroup -> TODO" );
    }

    @Override
    public AgendaGroup getAgendaGroup( String name, InternalKnowledgeBase kBase ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getAgendaGroup -> TODO" );
    }

    @Override
    public void setFocus( String name ) {
        for ( int i = 0; i < agendas.length; i++ ) {
            agendas[i].setFocus( name );
        }
    }

    @Override
    public boolean setFocus( AgendaGroup agendaGroup ) {
        boolean result = true;
        for ( int i = 0; i < agendas.length; i++ ) {
            result = agendas[i].setFocus( agendaGroup ) || result;
        }
        return result;
    }

    @Override
    public InternalActivationGroup getActivationGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getActivationGroup -> TODO" );
    }

    @Override
    public RuleFlowGroup getRuleFlowGroup( String name ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getRuleFlowGroup -> TODO" );
    }

    @Override
    public void setActivationsFilter( ActivationsFilter filter ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.setActivationsFilter -> TODO" );
    }

    @Override
    public ActivationsFilter getActivationsFilter() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getActivationsFilter -> TODO" );
    }

    @Override
    public RuleAgendaItem peekNextRule() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.peekNextRule -> TODO" );
    }

    @Override
    public void insertAndStageActivation( AgendaItem activation ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.insertAndStageActivation -> TODO" );
    }

    @Override
    public void addAgendaItemToGroup( AgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addAgendaItemToGroup -> TODO" );
    }

    @Override
    public void addEagerRuleAgendaItem( RuleAgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addEagerRuleAgendaItem -> TODO" );
    }

    @Override
    public void removeEagerRuleAgendaItem( RuleAgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.removeEagerRuleAgendaItem -> TODO" );
    }

    @Override
    public void addQueryAgendaItem( RuleAgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addQueryAgendaItem -> TODO" );
    }

    @Override
    public void removeQueryAgendaItem( RuleAgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.removeQueryAgendaItem -> TODO" );
    }

    @Override
    public void stageLeftTuple( RuleAgendaItem ruleAgendaItem, AgendaItem justified ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.stageLeftTuple -> TODO" );
    }

    @Override
    public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getAgendaGroupsMap -> TODO" );
    }

    @Override
    public void addAgendaGroupOnStack( AgendaGroup agendaGroup ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addAgendaGroupOnStack -> TODO" );
    }

    @Override
    public void evaluateEagerList() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.evaluateEagerList -> TODO" );
    }

    @Override
    public Map<String, InternalActivationGroup> getActivationGroupsMap() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getActivationGroupsMap -> TODO" );
    }

    @Override
    public InternalAgendaGroup getNextFocus() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getNextFocus -> TODO" );
    }

    @Override
    public java.util.LinkedList<AgendaGroup> getStackList() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getStackList -> TODO" );
    }

    @Override
    public AgendaGroup getFocus() {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.getFocus -> TODO" );
    }

    @Override
    public int sizeOfRuleFlowGroup( String s ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.sizeOfRuleFlowGroup -> TODO" );
    }

    @Override
    public void addItemToActivationGroup( AgendaItem item ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.addItemToActivationGroup -> TODO" );
    }

    @Override
    public boolean createPostponedActivation( LeftTuple postponedTuple, PropagationContext propagationContext, InternalWorkingMemory workingMemory, TerminalNode terminalNode ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.createPostponedActivation -> TODO" );
    }

    @Override
    public boolean isRuleActiveInRuleFlowGroup( String ruleflowGroupName, String ruleName, long processInstanceId ) {
        throw new UnsupportedOperationException( "org.drools.core.common.CompositeDefaultAgenda.isRuleActiveInRuleFlowGroup -> TODO" );
    }

    @Override
    public void registerExpiration( PropagationContext expirationContext ) {
        throw new UnsupportedOperationException( "This method has to be called on the single partitioned agendas" );
    }
}
