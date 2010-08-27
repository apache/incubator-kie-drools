/**
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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.RuleFlowGroupImpl.DeactivateCallback;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.LinkedListNode;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.runtime.process.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConsequenceException;
import org.drools.spi.ConsequenceExceptionHandler;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.spi.Tuple;

/**
 * Rule-firing Agenda.
 * 
 * <p>
 * Since many rules may be matched by a single assertObject(...) all scheduled
 * actions are placed into the <code>Agenda</code>.
 * </p>
 * 
 * <p>
 * While processing a scheduled action, it may update or retract objects in
 * other scheduled actions, which must then be removed from the agenda.
 * Non-invalidated actions are left on the agenda, and are executed in turn.
 * </p>
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class DefaultAgenda
    implements
    Externalizable,
    InternalAgenda {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long                                   serialVersionUID = 510l;

    /** Working memory of this Agenda. */
    private InternalWorkingMemory                               workingMemory;

    private org.drools.core.util.LinkedList                          scheduledActivations;

    /** Items time-delayed. */

    private Map<String, InternalAgendaGroup>                    agendaGroups;

    private Map<String, ActivationGroup>                        activationGroups;

    private Map<String, RuleFlowGroup>                          ruleFlowGroups;

    private LinkedList<AgendaGroup>                             focusStack;

    private InternalAgendaGroup                                 currentModule;

    private InternalAgendaGroup                                 main;

    private AgendaGroupFactory                                  agendaGroupFactory;

    private KnowledgeHelper                                     knowledgeHelper;

    public int                                                  activeActivations;

    public int                                                  dormantActivations;

    private ConsequenceExceptionHandler                         legacyConsequenceExceptionHandler;

    private org.drools.runtime.rule.ConsequenceExceptionHandler consequenceExceptionHandler;

    protected volatile AtomicBoolean                            halt             = new AtomicBoolean( false );

    private int                                                 activationCounter;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public DefaultAgenda() {
    }

    /**
     * Construct.
     * 
     * @param workingMemory
     *            The <code>WorkingMemory</code> of this agenda.
     * @param conflictResolver
     *            The conflict resolver.
     */
    public DefaultAgenda(InternalRuleBase rb) {
        this( rb,
              true );
    }

    /**
     * Construct.
     * 
     * @param workingMemory
     *            The <code>WorkingMemory</code> of this agenda.
     * @param conflictResolver
     *            The conflict resolver.
     */
    public DefaultAgenda(InternalRuleBase rb,
                         boolean initMain) {

        this.agendaGroups = new HashMap<String, InternalAgendaGroup>();
        this.activationGroups = new HashMap<String, ActivationGroup>();
        this.ruleFlowGroups = new HashMap<String, RuleFlowGroup>();
        this.focusStack = new LinkedList<AgendaGroup>();
        this.scheduledActivations = new org.drools.core.util.LinkedList();
        this.agendaGroupFactory = rb.getConfiguration().getAgendaGroupFactory();

        if ( initMain ) {
            // MAIN should always be the first AgendaGroup and can never be
            // removed
            this.main = agendaGroupFactory.createAgendaGroup( AgendaGroup.MAIN,
                                                              rb );

            this.agendaGroups.put( AgendaGroup.MAIN,
                                   this.main );

            this.focusStack.add( this.main );
        }
        Object object = ClassUtils.instantiateObject( rb.getConfiguration().getConsequenceExceptionHandler(),
                                                      rb.getConfiguration().getClassLoader() );
        if ( object instanceof ConsequenceExceptionHandler ) {
            this.legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) object;
        } else {
            this.consequenceExceptionHandler = (org.drools.runtime.rule.ConsequenceExceptionHandler) object;
        }
    }

    public AgendaItem createAgendaItem(final Tuple tuple,
                                       final int salience,
                                       final PropagationContext context,
                                       final Rule rule,
                                       final GroupElement subrule) {
        return new AgendaItem( activationCounter++,
                               tuple,
                               salience,
                               context,
                               rule,
                               subrule );
    }

    public ScheduledAgendaItem createScheduledAgendaItem(final Tuple tuple,
                                                         final PropagationContext context,
                                                         final Rule rule,
                                                         final GroupElement subrule) {
        return new ScheduledAgendaItem( activationCounter++,
                                        tuple,
                                        this,
                                        context,
                                        rule,
                                        subrule );
    }

    public void setWorkingMemory(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
        if ( ((InternalRuleBase) this.workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
            this.knowledgeHelper = new SequentialKnowledgeHelper( this.workingMemory );
        } else {
            this.knowledgeHelper = new DefaultKnowledgeHelper( this.workingMemory );
        }
    }

    //    public void write(WMSerialisationOutContext context) throws IOException {
    //        BinaryHeapQueueAgendaGroup[] agendaGroups = (BinaryHeapQueueAgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    //        Arrays.sort( agendaGroups,
    //                     AgendaGroupSorter.instance );
    //
    //        for ( BinaryHeapQueueAgendaGroup group : agendaGroups ) {
    //            context.writeInt( PersisterEnums.AGENDA_GROUP );
    //            group.write( context );
    //        }
    //        context.writeInt( PersisterEnums.END );
    //
    //        for ( ListIterator it = this.focusStack.listIterator( this.focusStack.size() - 1 ); it.hasPrevious(); ) {
    //            AgendaGroup group = (AgendaGroup) it.previous();
    //            context.writeInt( PersisterEnums.AGENDA_GROUP );
    //            context.writeUTF( group.getName() );
    //        }
    //        context.writeInt( PersisterEnums.END );
    //
    //        RuleFlowGroupImpl[] ruleFlowGroups = (RuleFlowGroupImpl[]) this.ruleFlowGroups.values().toArray( new RuleFlowGroupImpl[this.ruleFlowGroups.size()] );
    //        Arrays.sort( agendaGroups,
    //                     AgendaGroupSorter.instance );
    //
    //        for ( BinaryHeapQueueAgendaGroup group : agendaGroups ) {
    //            context.writeInt( PersisterEnums.RULE_FLOW_GROUP );
    //            group.write( context );
    //        }
    //        context.writeInt( PersisterEnums.END );
    //    }

    //    public static class AgendaGroupSorter
    //        implements
    //        Comparator<AgendaGroup> {
    //        public static final AgendaGroupSorter instance = new AgendaGroupSorter();
    //
    //        public int compare(AgendaGroup group1,
    //                           AgendaGroup group2) {
    //            return group1.getName().compareTo( group2.getName() );
    //        }
    //    }
    //
    //    public static class RuleFlowGroupSorter
    //        implements
    //        Comparator<AgendaGroup> {
    //        public static final AgendaGroupSorter instance = new AgendaGroupSorter();
    //
    //        public int compare(AgendaGroup group1,
    //                           AgendaGroup group2) {
    //            return group1.getName().compareTo( group2.getName() );
    //        }
    //    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        scheduledActivations = (org.drools.core.util.LinkedList) in.readObject();
        agendaGroups = (Map) in.readObject();
        activationGroups = (Map) in.readObject();
        ruleFlowGroups = (Map) in.readObject();
        focusStack = (LinkedList) in.readObject();
        currentModule = (InternalAgendaGroup) in.readObject();
        main = (InternalAgendaGroup) in.readObject();
        agendaGroupFactory = (AgendaGroupFactory) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        activeActivations = in.readInt();
        dormantActivations = in.readInt();
        legacyConsequenceExceptionHandler = (ConsequenceExceptionHandler) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeObject( scheduledActivations );
        out.writeObject( agendaGroups );
        out.writeObject( activationGroups );
        out.writeObject( ruleFlowGroups );
        out.writeObject( focusStack );
        out.writeObject( currentModule );
        out.writeObject( main );
        out.writeObject( agendaGroupFactory );
        out.writeObject( knowledgeHelper );
        out.writeInt( activeActivations );
        out.writeInt( dormantActivations );
        out.writeObject( legacyConsequenceExceptionHandler );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getWorkingMemory()
     */
    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    /**
     * Schedule an agenda item for delayed firing.
     * 
     * @param item
     *            The item to schedule.
     */
    public void scheduleItem(final ScheduledAgendaItem item, final InternalWorkingMemory wm) {

        Scheduler.scheduleAgendaItem( item,
                                      this,
                                      wm );
        this.scheduledActivations.add( item );

        // adds item to activation group if appropriate
        addItemToActivationGroup( item );

    }

    /**
     * If the item belongs to an activation group, add it
     * 
     * @param item
     */
    private void addItemToActivationGroup(final AgendaItem item) {
        String group = item.getRule().getActivationGroup();
        if ( group != null && group.length() > 0 ) {
            this.getActivationGroup( group ).addActivation( item );
        }
    }

    /**
     * @inheritDoc
     */
    public boolean addActivation(final AgendaItem activation) {
        // set the focus if rule autoFocus is true
        if ( activation.getRule().getAutoFocus() ) {
            this.setFocus( activation.getRule().getAgendaGroup() );
        }

        // adds item to activation group if appropriate
        addItemToActivationGroup( activation );

        InternalAgendaGroup agendaGroup = (InternalAgendaGroup) this.getAgendaGroup( activation.getRule().getAgendaGroup() );
        activation.setAgendaGroup( agendaGroup );

        if ( activation.getRule().getRuleFlowGroup() == null ) {
            // No RuleFlowNode so add it directly to the Agenda

            // do not add the activation if the rule is "lock-on-active" and the
            // AgendaGroup is active
            if ( activation.getRule().isLockOnActive() && agendaGroup.isActive() ) {
                return false;
            }

            agendaGroup.add( activation );
        } else {
            // There is a RuleFlowNode so add it there, instead of the Agenda
            InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( activation.getRule().getRuleFlowGroup() );

            // do not add the activation if the rule is "lock-on-active" and the
            // RuleFlowGroup is active
            if ( activation.getRule().isLockOnActive() && rfg.isActive() ) {
                return false;
            }

            rfg.addActivation( activation );
        }

        // making sure we re-evaluate agenda in case we are waiting for activations
        synchronized ( this.halt ) {
            this.halt.notifyAll();
        }
        return true;

    }

    public void removeScheduleItem(final ScheduledAgendaItem item) {
        this.scheduledActivations.remove( item );
        Scheduler.removeAgendaItem( item,
                                    this );
    }

    public void addAgendaGroup(final AgendaGroup agendaGroup) {
        this.agendaGroups.put( agendaGroup.getName(),
                               (InternalAgendaGroup) agendaGroup );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setFocus(org.drools.spi.AgendaGroup)
     */
    public boolean setFocus(final AgendaGroup agendaGroup) {
        // Set the focus to the agendaGroup if it doesn't already have the focus
        if ( this.focusStack.getLast() != agendaGroup ) {
            ((InternalAgendaGroup) this.focusStack.getLast()).setActive( false );
            this.focusStack.add( agendaGroup );
            ((InternalAgendaGroup) agendaGroup).setActive( true );
            final EventSupport eventsupport = (EventSupport) this.workingMemory;
            eventsupport.getAgendaEventSupport().fireAgendaGroupPushed( agendaGroup,
                                                                        this.workingMemory );
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setFocus(java.lang.String)
     */
    public void setFocus(final String name) {
        AgendaGroup agendaGroup = getAgendaGroup( name );
        setFocus( agendaGroup );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getFocus()
     */
    public AgendaGroup getFocus() {
        return (AgendaGroup) this.focusStack.getLast();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getNextFocus()
     */
    public AgendaGroup getNextFocus() {
        InternalAgendaGroup agendaGroup = null;
        // Iterate until we find a populate AgendaModule or we reach the MAIN,
        // default, AgendaGroup
        while ( true ) {
            agendaGroup = (InternalAgendaGroup) this.focusStack.getLast();

            final boolean empty = agendaGroup.isEmpty();

            // No populated queues found so pop the focusStack and repeat
            if ( empty && (this.focusStack.size() > 1) ) {
                agendaGroup.setActive( false );
                this.focusStack.removeLast();
                final EventSupport eventsupport = (EventSupport) this.workingMemory;
                eventsupport.getAgendaEventSupport().fireAgendaGroupPopped( agendaGroup,
                                                                            this.workingMemory );
            } else {
                agendaGroup = (empty) ? null : agendaGroup;
                break;
            }
        }

        if ( agendaGroup != null ) {
            agendaGroup.setActive( true );
        }
        return agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#setCurrentAgendaGroup(org.drools.spi.AgendaGroup)
     */
    public void setCurrentAgendaGroup(final InternalAgendaGroup agendaGroup) {
        this.currentModule = agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getCurrentAgendaGroup()
     */
    public AgendaGroup getCurrentAgendaGroup() {
        return this.currentModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getAgendaGroup(java.lang.String)
     */
    public AgendaGroup getAgendaGroup(final String name) {
        String groupName = (name == null || name.length() == 0) ? AgendaGroup.MAIN : name;

        AgendaGroup agendaGroup = (AgendaGroup) this.agendaGroups.get( groupName );
        if ( agendaGroup == null ) {
            // The AgendaGroup is defined but not yet added to the
            // Agenda, so create the AgendaGroup and add to the Agenda.
            agendaGroup = agendaGroupFactory.createAgendaGroup( name,
                                                                ((InternalRuleBase) this.workingMemory.getRuleBase()) );
            addAgendaGroup( agendaGroup );
        }
        return agendaGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getAgendaGroups()
     */
    public AgendaGroup[] getAgendaGroups() {
        return (AgendaGroup[]) this.agendaGroups.values().toArray( new AgendaGroup[this.agendaGroups.size()] );
    }

    public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
        return this.agendaGroups;
    }

    public InternalAgendaGroup getMainAgendaGroup() {
        if ( this.main == null ) {
            this.main = (InternalAgendaGroup) getAgendaGroup( AgendaGroup.MAIN );
        }

        return this.main;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getStack()
     */
    public AgendaGroup[] getStack() {
        return (AgendaGroup[]) this.focusStack.toArray( new AgendaGroup[this.focusStack.size()] );
    }

    public LinkedList<AgendaGroup> getStackList() {
        return this.focusStack;
    }

    public Map<String, RuleFlowGroup> getRuleFlowGroupsMap() {
        return this.ruleFlowGroups;
    }

    public Map<String, ActivationGroup> getActivationGroupsMap() {
        return this.activationGroups;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getActivationGroup(java.lang.String)
     */
    public ActivationGroup getActivationGroup(final String name) {
        ActivationGroupImpl activationGroup = (ActivationGroupImpl) this.activationGroups.get( name );
        if ( activationGroup == null ) {
            activationGroup = new ActivationGroupImpl( name );
            this.activationGroups.put( name,
                                       activationGroup );
        }
        return activationGroup;
    }

    public RuleFlowGroup getRuleFlowGroup(final String name) {
        RuleFlowGroup ruleFlowGroup = (RuleFlowGroup) this.ruleFlowGroups.get( name );
        if ( ruleFlowGroup == null ) {
            ruleFlowGroup = new RuleFlowGroupImpl( name );
            ((InternalRuleFlowGroup) ruleFlowGroup).setWorkingMemory( (InternalWorkingMemory) getWorkingMemory() );
            this.ruleFlowGroups.put( name,
                                     ruleFlowGroup );
        }
        return ruleFlowGroup;
    }

    public void activateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( true );
    }

    public void activateRuleFlowGroup(final String name, long processInstanceId, String nodeInstanceId) {
    	InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) getRuleFlowGroup( name );
    	ruleFlowGroup.addNodeInstance(processInstanceId, nodeInstanceId);
        ruleFlowGroup.setActive( true );
    }

    public void deactivateRuleFlowGroup(final String name) {
        ((InternalRuleFlowGroup) getRuleFlowGroup( name )).setActive( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#focusStackSize()
     */
    public int focusStackSize() {
        int size = 0;
        for ( final java.util.Iterator iterator = this.focusStack.iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) iterator.next();
            size += group.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#agendaSize()
     */
    public int agendaSize() {
        int size = 0;
        for ( final java.util.Iterator iterator = this.agendaGroups.values().iterator(); iterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) iterator.next();
            size += group.size();
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getActivations()
     */
    public Activation[] getActivations() {
        final List list = new ArrayList();
        for ( final java.util.Iterator it = this.agendaGroups.values().iterator(); it.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) it.next();
            list.addAll( Arrays.asList( group.getActivations() ) );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#getScheduledActivations()
     */
    public Activation[] getScheduledActivations() {
        final List list = new ArrayList( this.scheduledActivations.size() );
        for ( LinkedListNode node = this.scheduledActivations.getFirst(); node != null; node = node.getNext() ) {
            list.add( node );
        }
        return (Activation[]) list.toArray( new Activation[list.size()] );
    }

    public org.drools.core.util.LinkedList getScheduledActivationsLinkedList() {
        return this.scheduledActivations;
    }

    public void clear() {
        // reset focus stack
        this.focusStack.clear();
        this.focusStack.add( getMainAgendaGroup() );

        // reset scheduled activations
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                Scheduler.removeAgendaItem( item,
                                            this );
            }
        }

        //reset all agenda groups
        for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
            group.clear();
        }

        // reset all ruleflows
        for ( RuleFlowGroup group : this.ruleFlowGroups.values() ) {
            group.clear();
        }

        // reset all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            group.clear();
        }
    }

    /** (non-Javadoc)
     * @see org.drools.common.AgendaI#clearAgenda()
     */
    public void clearAndCancel() {
        // Cancel all items and fire a Cancelled event for each Activation
        for ( final java.util.Iterator agendaGroupIterator = this.agendaGroups.values().iterator(); agendaGroupIterator.hasNext(); ) {
            final AgendaGroup group = (AgendaGroup) agendaGroupIterator.next();
            clearAndCancelAgendaGroup( group );
        }

        final EventSupport eventsupport = (EventSupport) this.workingMemory;
        if ( !this.scheduledActivations.isEmpty() ) {
            for ( ScheduledAgendaItem item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst(); item != null; item = (ScheduledAgendaItem) this.scheduledActivations.removeFirst() ) {
                Scheduler.removeAgendaItem( item,
                                            this );
                eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                              this.workingMemory,
                                                                              ActivationCancelledCause.CLEAR );
            }
        }

        // cancel all ruleflows
        for ( RuleFlowGroup group : this.ruleFlowGroups.values() ) {
            clearAndCancelAndCancel( group );
        }

        // cancel all activation groups.
        for ( ActivationGroup group : this.activationGroups.values() ) {
            clearAndCancelActivationGroup( group );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearAgendaGroup(java.lang.String)
     */
    public void clearAndCancelAgendaGroup(final String name) {
        final AgendaGroup agendaGroup = (AgendaGroup) this.agendaGroups.get( name );
        if ( agendaGroup != null ) {
            clearAndCancelAgendaGroup( agendaGroup );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearAgendaGroup(org.drools.common.AgendaGroupImpl)
     */
    public void clearAndCancelAgendaGroup(final AgendaGroup agendaGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        final Activation[] queueable = ((InternalAgendaGroup) agendaGroup).getQueue();
        for ( int i = 0, length = queueable.length; i < length; i++ ) {
            final AgendaItem item = (AgendaItem) queueable[i];
            if ( item == null ) {
                continue;
            }

            // this must be set false before removal from the activationGroup.
            // Otherwise the activationGroup will also try to cancel the
            // Actvation
            item.setActivated( false );

            if ( item.getActivationGroupNode() != null ) {
                item.getActivationGroupNode().getActivationGroup().removeActivation( item );
            }

            if ( item.getActivationNode() != null ) {
                final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) item.getActivationNode().getParentContainer();
                ruleFlowGroup.removeActivation( item );
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          ActivationCancelledCause.CLEAR );
        }
        ((InternalAgendaGroup) agendaGroup).clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearActivationGroup(java.lang.String)
     */
    public void clearAndCancelActivationGroup(final String name) {
        final ActivationGroup activationGroup = (ActivationGroup) this.activationGroups.get( name );
        if ( activationGroup != null ) {
            clearAndCancelActivationGroup( activationGroup );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.common.AgendaI#clearActivationGroup(org.drools.spi.ActivationGroup)
     */
    public void clearAndCancelActivationGroup(final ActivationGroup activationGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        for ( final Iterator it = activationGroup.iterator(); it.hasNext(); ) {
            final ActivationGroupNode node = (ActivationGroupNode) it.next();
            final Activation activation = node.getActivation();
            activation.setActivationGroupNode( null );

            if ( activation.isActivated() ) {
                activation.setActivated( false );
                activation.remove();

                if ( activation.getActivationNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                    ruleFlowGroup.removeActivation( activation );
                }

                eventsupport.getAgendaEventSupport().fireActivationCancelled( activation,
                                                                              this.workingMemory,
                                                                              ActivationCancelledCause.CLEAR );
            }
        }
        activationGroup.clear();
    }

    public void clearAndCancelRuleFlowGroup(final String name) {
        final RuleFlowGroup ruleFlowGrlup = (RuleFlowGroup) this.ruleFlowGroups.get( name );
        if ( ruleFlowGrlup != null ) {
            clearAndCancelAndCancel( ruleFlowGrlup );
        }
    }

    public void clearAndCancelAndCancel(final RuleFlowGroup ruleFlowGroup) {
        final EventSupport eventsupport = (EventSupport) this.workingMemory;

        for ( Iterator it = ruleFlowGroup.iterator(); it.hasNext(); ) {
            ActivationNode node = (ActivationNode) it.next();
            AgendaItem item = (AgendaItem) node.getActivation();
            if ( item != null ) {
                item.setActivated( false );
                item.remove();

                if ( item.getActivationGroupNode() != null ) {
                    item.getActivationGroupNode().getActivationGroup().removeActivation( item );
                }
            }

            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                          this.workingMemory,
                                                                          ActivationCancelledCause.CLEAR );
        }

        ((InternalRuleFlowGroup) ruleFlowGroup).clear();

        if ( ruleFlowGroup.isActive() && ruleFlowGroup.isAutoDeactivate() ) {
            // deactivate callback
            WorkingMemoryAction action = new DeactivateCallback( (InternalRuleFlowGroup) ruleFlowGroup );
            this.workingMemory.queueWorkingMemoryAction( action );
        }
    }

    /**
     * Fire the next scheduled <code>Agenda</code> item, skipping items
     * that are not allowed by the agenda filter.
     * 
     * @return true if an activation was fired. false if no more activations 
     *              to fire
     * 
     * @throws ConsequenceException
     *             If an error occurs while firing an agenda item.
     */
    public boolean fireNextItem(final AgendaFilter filter) throws ConsequenceException {
        boolean tryagain, result;
        try {
            do {
                this.workingMemory.prepareToFireActivation();
                tryagain = result = false;
                final InternalAgendaGroup group = (InternalAgendaGroup) getNextFocus();
                // if there is a group with focus
                if ( group != null ) {
                    final AgendaItem item = (AgendaItem) group.getNext();
                    // if there is an item to fire from that group
                    if ( item != null ) {
                        // if that item is allowed to fire
                        if ( filter == null || filter.accept( item ) ) {
                            // fire it
                            fireActivation( item );
                            result = true;
                        } else {
                            // otherwise cancel it and try the next
                            final EventSupport eventsupport = (EventSupport) this.workingMemory;

                            eventsupport.getAgendaEventSupport().fireActivationCancelled( item,
                                                                                          this.workingMemory,
                                                                                          ActivationCancelledCause.FILTER );
                            tryagain = true;
                        }
                    }
                }
            } while ( tryagain );
        } finally {
            this.workingMemory.activationFired();
        }
        return result;
    }

    /**
     * Fire this item.
     * 
     * @param workingMemory
     *            The working memory context.
     * 
     * @throws ConsequenceException
     *             If an error occurs while attempting to fire the consequence.
     */
    public synchronized void fireActivation(final Activation activation) throws ConsequenceException {
        // We do this first as if a node modifies a fact that causes a recursion
        // on an empty pattern
        // we need to make sure it re-activates
        this.workingMemory.startOperation();
        try {
            increaseDormantActivations();

            final EventSupport eventsupport = (EventSupport) this.workingMemory;

            eventsupport.getAgendaEventSupport().fireBeforeActivationFired( activation,
                                                                            this.workingMemory );

            if ( activation.getActivationGroupNode() != null ) {
                // We know that this rule will cancel all other activations in the group
                // so lets remove the information now, before the consequence fires
                final ActivationGroup activationGroup = activation.getActivationGroupNode().getActivationGroup();
                activationGroup.removeActivation( activation );
                clearAndCancelActivationGroup( activationGroup );
            }
            activation.setActivated( false );

            InternalRuleFlowGroup ruleFlowGroup = null;
            if ( activation.getActivationNode() != null ) {
                ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                // it is possible that the ruleflow group is no longer active if it was
                // cleared during execution of this activation
                ruleFlowGroup.removeActivation( activation );
            }

            try {
                this.knowledgeHelper.setActivation( activation );
                activation.getRule().getConsequence().evaluate( this.knowledgeHelper,
                                                                this.workingMemory );
                this.knowledgeHelper.cancelRemainingPreviousLogicalDependencies();
                this.knowledgeHelper.reset();
            } catch ( final Exception e ) {
                if ( this.legacyConsequenceExceptionHandler != null ) {
                    this.legacyConsequenceExceptionHandler.handleException( activation,
                                                                            this.workingMemory,
                                                                            e );
                } else if ( this.consequenceExceptionHandler != null ) {
                    this.consequenceExceptionHandler.handleException( activation,
                                                                      new StatefulKnowledgeSessionImpl( (ReteooWorkingMemory) this.workingMemory ),
                                                                      e );
                } else {
                    throw new RuntimeException( e );
                }
            }
            
            if( ruleFlowGroup != null ) {
                ruleFlowGroup.deactivateIfEmpty();
            }

            // if the tuple contains expired events 
            for ( LeftTuple tuple = (LeftTuple) activation.getTuple(); tuple != null; tuple = tuple.getParent() ) {
                if ( tuple.getLastHandle().isEvent() ) {
                    EventFactHandle handle = (EventFactHandle) tuple.getLastHandle();
                    // handles "expire" only in stream mode.
                    if ( handle.isExpired() ) {
                        // decrease the activation count for the event
                        handle.decreaseActivationsCount();
                        if ( handle.getActivationsCount() == 0 ) {
                            // and if no more activations, retract the handle
                            handle.getEntryPoint().retract( handle );
                        }
                    }
                }
            }

            eventsupport.getAgendaEventSupport().fireAfterActivationFired( activation,
                                                                           this.workingMemory );
        } finally {
            this.workingMemory.endOperation();
        }
    }

    public void increaseActiveActivations() {
        this.activeActivations++;
    }

    public void decreaseActiveActivations() {
        this.activeActivations--;
    }

    public void increaseDormantActivations() {
        this.activeActivations--;
        this.dormantActivations++;
    }

    public void decreaseDormantActivations() {
        this.dormantActivations--;
    }

    public int getActiveActivations() {
        return this.activeActivations;
    }

    public int getDormantActivations() {
        return this.dormantActivations;
    }

    /**
     * @inheritDoc
     */
    public boolean isRuleActiveInRuleFlowGroup(String ruleflowGroupName,
                                               String ruleName,
                                               long processInstanceId) {

        RuleFlowGroup systemRuleFlowGroup = this.getRuleFlowGroup( ruleflowGroupName );

        for ( Iterator<ActivationNode> activations = systemRuleFlowGroup.iterator(); activations.hasNext(); ) {
            Activation activation = activations.next().getActivation();
            if ( ruleName.equals( activation.getRule().getName() ) ) {
                if ( checkProcessInstance( activation,
                                           processInstanceId ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkProcessInstance(Activation activation,
                                         long processInstanceId) {
        final Map< ? , ? > declarations = activation.getSubRule().getOuterDeclarations();
        for ( Iterator< ? > it = declarations.values().iterator(); it.hasNext(); ) {
            Declaration declaration = (Declaration) it.next();
            if ( "processInstance".equals( declaration.getIdentifier() ) ) {
                Object value = declaration.getValue( workingMemory,
                                                     ((InternalFactHandle) activation.getTuple().get( declaration )).getObject() );
                if ( value instanceof ProcessInstance ) {
                    return ((ProcessInstance) value).getId() == processInstanceId;
                }
            }
        }
        return true;
    }

    public void addRuleFlowGroupListener(String ruleFlowGroup,
                                         RuleFlowGroupListener listener) {
        InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( ruleFlowGroup );
        rfg.addRuleFlowGroupListener( listener );
    }

    public void removeRuleFlowGroupListener(String ruleFlowGroup,
                                            RuleFlowGroupListener listener) {
        InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) this.getRuleFlowGroup( ruleFlowGroup );
        rfg.removeRuleFlowGroupListener( listener );
    }

    public String getFocusName() {
        return this.getFocus().getName();
    }

    public void fireUntilHalt() {
        fireUntilHalt( null );
    }

    private AtomicBoolean missedNotifyAll = new AtomicBoolean(false);
    
    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        this.halt.set( false );
        while ( continueFiring( -1 ) ) {
            boolean fired = fireNextItem( agendaFilter );
            fired = fired || !((AbstractWorkingMemory) this.workingMemory).getActionQueue().isEmpty();
            this.workingMemory.executeQueuedActions();
            if ( !fired && !missedNotifyAll.get()) {
                try {
                    synchronized ( this.halt ) {
                        this.halt.wait();
                    }
                } catch ( InterruptedException e ) {
                    this.halt.set( true );
                }
                this.missedNotifyAll.set( false );
            } else {
                this.workingMemory.executeQueuedActions();
            }
        }
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) {
        this.halt.set( false );
        int fireCount = 0;
        while ( continueFiring( fireLimit ) && fireNextItem( agendaFilter ) ) {
            fireCount++;
            fireLimit = updateFireLimit( fireLimit );
            this.workingMemory.executeQueuedActions();
        }
        if ( this.focusStack.size() == 1 && getMainAgendaGroup().isEmpty() ) {
            // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
            getMainAgendaGroup().setActive( false );
        }
        return fireCount;
    }

    private final boolean continueFiring(final int fireLimit) {
        return (!halt.get()) && (fireLimit != 0);
    }

    private final int updateFireLimit(final int fireLimit) {
        return fireLimit > 0 ? fireLimit - 1 : fireLimit;
    }

    public void notifyHalt() {
        synchronized ( this.halt ) {
            this.missedNotifyAll.set( true );
            this.halt.notifyAll();
        }
    }

    public void halt() {
        this.halt.set( true );
        notifyHalt();
    }

    public ConsequenceExceptionHandler getConsequenceExceptionHandler() {
        return this.legacyConsequenceExceptionHandler;
    }
}
