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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.ValueType;
import org.drools.common.AbstractRuleBase;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.RightTupleKey;
import org.drools.marshalling.impl.TimersInputMarshaller;
import org.drools.marshalling.impl.TimersOutputMarshaller;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteExpireAction;
import org.drools.reteoo.RuleRemovalContext.CleanupAdapter;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.compiled.CompiledNetwork;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.EvalCondition;
import org.drools.rule.SlidingTimeWindow;
import org.drools.rule.SlidingTimeWindow.BehaviorJobContext;
import org.drools.spi.Constraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.time.Job;
import org.drools.time.JobContext;
import org.drools.time.JobHandle;
import org.drools.time.TimerService;
import org.drools.time.impl.DefaultJobHandle;
import org.drools.time.impl.PointInTimeTrigger;

/**
 * <code>ObjectTypeNodes<code> are responsible for filtering and propagating the matching
 * fact assertions propagated from the <code>Rete</code> node using <code>ObjectType</code> interface.
 * <p/>
 * The assert and retract methods do not attempt to filter as this is the role of the <code>Rete</code>
 * node which builds up a cache of matching <code>ObjectTypdeNodes</code>s for each asserted object, using
 * the <code>matches(Object object)</code> method. Incorrect propagation in these methods is not checked and
 * will result in <code>ClassCastExpcections</code> later on in the network.
 * <p/>
 * Filters <code>Objects</code> coming from the <code>Rete</code> using a
 * <code>ObjectType</code> semantic module.
 *
 * @see ObjectType
 * @see Rete
 */
public class ObjectTypeNode extends ObjectSource
    implements
    ObjectSink,
    Externalizable,
    NodeMemory
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long   serialVersionUID = 510l;

    /**
     * The <code>ObjectType</code> semantic module.
     */
    private ObjectType          objectType;

    private boolean             skipOnModify     = false;

    private boolean             objectMemoryEnabled;

    private long                expirationOffset = -1;

    public static final transient ExpireJob job              = new ExpireJob();
    
    private boolean             queryNode;

    private CompiledNetwork     compiledNetwork;

    /** @see LRUnlinkingOption */
    private boolean lrUnlinkingEnabled = false;
    
    public ObjectTypeNode() {

    }

    /**
     * Construct given a semantic <code>ObjectType</code> and the provided
     * unique id. All <code>ObjectTypdeNode</code> have node memory.
     *
     * @param id         The unique id for the node.
     * @param objectType The semantic object-type differentiator.
     */
    public ObjectTypeNode(final int id,
                          final EntryPointNode source,
                          final ObjectType objectType,
                          final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               source,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.objectType = objectType;
        this.lrUnlinkingEnabled = context.getRuleBase().getConfiguration().isLRUnlinkingEnabled();
        setObjectMemoryEnabled( context.isObjectTypeNodeMemoryEnabled() );
        
        if ( ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom( objectType )) {
            queryNode = true;
        }        
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        objectType = (ObjectType) in.readObject();

        // this is here as not all objectTypeNodes used ClassObjectTypes in packages (i.e. rules with those nodes did not exist yet)
        // and thus have no wiring targets
        if ( objectType instanceof ClassObjectType ) {
            objectType = ((AbstractRuleBase) ((DroolsObjectInputStream) in).getRuleBase()).getClassFieldAccessorCache().getClassObjectType( (ClassObjectType) objectType );
        }

        skipOnModify = in.readBoolean();
        objectMemoryEnabled = in.readBoolean();
        expirationOffset = in.readLong();
        lrUnlinkingEnabled = in.readBoolean();
        queryNode = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( objectType );
        out.writeBoolean( skipOnModify );
        out.writeBoolean( objectMemoryEnabled );
        out.writeLong( expirationOffset );
        out.writeBoolean( lrUnlinkingEnabled );
        out.writeBoolean( queryNode );
    }

    /**
     * Retrieve the semantic <code>ObjectType</code> differentiator.
     *
     * @return The semantic <code>ObjectType</code> differentiator.
     */
    public ObjectType getObjectType() {
        return this.objectType;
    }

    public boolean isAssignableFrom(final ObjectType objectType) {
        return this.objectType.isAssignableFrom( objectType );
    }

    public void setCompiledNetwork(CompiledNetwork compiledNetwork) {
        this.compiledNetwork = compiledNetwork;

        this.compiledNetwork.setObjectTypeNode( this );
    }
    
    /**
     * Propagate the <code>FactHandleimpl</code> through the <code>Rete</code> network. All
     * <code>FactHandleImpl</code> should be remembered in the node memory, so that later runtime rule attachmnents
     * can have the matched facts propagated to them.
     *
     * @param factHandle    The fact handle.
     * @param object        The object to assert.
     * @param workingMemory The working memory session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        
        if ( objectMemoryEnabled && !(queryNode && !((DroolsQuery)factHandle.getObject()).isOpen() ) ) {
            final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( this );
            memory.add( factHandle,
                        false );            
        }
        
        if ( compiledNetwork != null ) {
            compiledNetwork.assertObject( factHandle,
                                          context,
                                          workingMemory );
        } else {
            
            context.setCurrentPropagatingOTN( this );
            this.sink.propagateAssertObject( factHandle,
                                             context,
                                             workingMemory );
        }

        if ( this.objectType.isEvent() && this.expirationOffset >= 0 && this.expirationOffset != Long.MAX_VALUE ) {
            // schedule expiration
            WorkingMemoryReteExpireAction expire = new WorkingMemoryReteExpireAction( factHandle,
                                                                                      this );
            TimerService clock = workingMemory.getTimerService();

            long nextTimestamp = Math.max( clock.getCurrentTime() + this.expirationOffset,
                                           ((EventFactHandle) factHandle).getStartTimestamp() + this.expirationOffset );
            JobContext jobctx = new ExpireJobContext( expire,
                                                      workingMemory );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  new PointInTimeTrigger( nextTimestamp,
                                                                          null,
                                                                          null ) );
            jobctx.setJobHandle( handle );
        }

    }

    /**
     * Retract the <code>FactHandleimpl</code> from the <code>Rete</code> network. Also remove the
     * <code>FactHandleImpl</code> from the node memory.
     *
     * @param rightTuple    The fact handle.
     * @param object        The object to assert.
     * @param workingMemory The working memory session.
     */
    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        if ( objectMemoryEnabled && !(queryNode && !((DroolsQuery)factHandle.getObject()).isOpen() ) ) {
            final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( this );
            memory.remove( factHandle );            
        }

        for ( RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              workingMemory );
        }
        factHandle.setFirstRightTuple( null );
        factHandle.setLastRightTuple( null );

        for ( LeftTuple leftTuple = factHandle.getFirstLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                           context,
                                                           workingMemory );
        }
        factHandle.setFirstLeftTuple( null );
        factHandle.setLastLeftTuple( null );
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        if ( this.skipOnModify && context.getDormantActivations() == 0  ) {
            // we do this after the shadowproxy update, just so that its up to date for the future
            return;
        }

        if ( compiledNetwork != null ) {
            compiledNetwork.modifyObject( factHandle,
                                          modifyPreviousTuples,
                                          context,
                                          workingMemory );
        } else {
            this.sink.propagateModifyObject( factHandle,
                                             modifyPreviousTuples,
                                             context,
                                             workingMemory );
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        if (lrUnlinkingEnabled) {
            // Update sink taking into account L&R unlinking peculiarities
            updateLRUnlinking(sink, context, workingMemory);
            
        } else {
            // Regular updateSink
            final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( this );
            Iterator it = memory.iterator();
    
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                sink.assertObject( (InternalFactHandle) entry.getValue(),
                        context,
                        workingMemory );
            }
        }

    }
    
    /**
     *  When L&R Unlinking is enabled, updateSink() is used to populate 
     *  a node's memory, but it has to take into account if it's propagating.
     */
    private void updateLRUnlinking(final ObjectSink sink,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
        
        final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( this );
        
         Iterator it = memory.iterator();
            
        
        InternalFactHandle ctxHandle = (InternalFactHandle)context.getFactHandle(); 
        
        if (!context.isPropagating( this ) || 
                (context.isPropagating( this ) && context.shouldPropagateAll())){
            
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                // Assert everything
                sink.assertObject( (InternalFactHandle) entry.getValue(),
                        context,
                        workingMemory );
            }
            
        } else {
            
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                InternalFactHandle handle = (InternalFactHandle) entry.getValue();
                // Exclude the current fact propagation
                if (handle.getId() != ctxHandle.getId()) {
                    sink.assertObject( handle,
                            context,
                            workingMemory );
                }
            }
        }
    }

    
    /**
     * Rete needs to know that this ObjectTypeNode has been added
     */
    public void attach() {
        this.source.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        // we need to call updateSink on Rete, because someone
        // might have already added facts matching this ObjectTypeNode
        // to working memories
        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContextImpl propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                          PropagationContext.RULE_ADDITION,
                                                                                          null,
                                                                                          null,
                                                                                          null );
            propagationContext.setEntryPoint( ((EntryPointNode) this.source).getEntryPoint() );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }

    public void networkUpdated() {
        this.skipOnModify = canSkipOnModify( this.sink.getSinks(),
                                             true );
    }

    /**
     * OTN needs to override remove to avoid releasing the node ID, since OTN are
     * never removed from the rulebase in the current implementation
     *
     * @inheritDoc
     * @see org.drools.common.BaseNode#remove(org.drools.reteoo.RuleRemovalContext, org.drools.reteoo.ReteooBuilder, org.drools.common.BaseNode, org.drools.common.InternalWorkingMemory[])
     */
    public void remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       BaseNode node,
                       InternalWorkingMemory[] workingMemories) {
        doRemove( context,
                  builder,
                  node,
                  workingMemories );
    }

    /**
     * OTN needs to override remove to avoid releasing the node ID, since OTN are
     * never removed from the rulebase in the current implementation
     */
    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( context.getCleanupAdapter() != null ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                CleanupAdapter adapter = context.getCleanupAdapter();
                final ObjectHashSet memory = (ObjectHashSet) workingMemory.getNodeMemory( this );
                Iterator it = memory.iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    InternalFactHandle handle = (InternalFactHandle) entry.getValue();
                    for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
                        adapter.cleanUp( leftTuple,
                                         workingMemory );
                    }
                }
            }
            context.setCleanupAdapter( null );
        }
        if ( !node.isInUse() ) {
            removeObjectSink( (ObjectSink) node );
        }
    }

    /**
     * Creates memory for the node using PrimitiveLongMap as its optimised for storage and reteivals of Longs.
     * However PrimitiveLongMap is not ideal for spase data. So it should be monitored incase its more optimal
     * to switch back to a standard HashMap.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return new ObjectHashSet();
    }

    public boolean isObjectMemoryEnabled() {
        return this.objectMemoryEnabled;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        this.objectMemoryEnabled = objectMemoryEnabled;
    }

    public String toString() {
        return "[ObjectTypeNode(" + this.id + ")::" + ((EntryPointNode) this.source).getEntryPoint() + " objectType=" + this.objectType + " expiration=" + this.expirationOffset + "ms ]";
    }

    /**
     * Uses he hashCode() of the underlying ObjectType implementation.
     */
    public int hashCode() {
        return this.objectType.hashCode() ^ this.source.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ObjectTypeNode) ) {
            return false;
        }

        final ObjectTypeNode other = (ObjectTypeNode) object;

        return this.objectType.equals( other.objectType ) && this.source.equals( other.source );
    }

    /**
     * Checks if a modify action on this object type may
     * be skipped because no constraint is applied to it
     *
     * @param sinks
     * @return
     */
    private boolean canSkipOnModify(final Sink[] sinks,
                                    final boolean rootCall) {
        // If we have no alpha or beta node with constraints on this ObjectType, we can just skip modifies
        boolean hasConstraints = false;
        for ( int i = 0; i < sinks.length && !hasConstraints; i++ ) {
            if ( sinks[i] instanceof AlphaNode || sinks[i] instanceof AccumulateNode || sinks[i] instanceof FromNode ) {
                hasConstraints = true;
            } else if ( sinks[i] instanceof BetaNode && ((BetaNode) sinks[i]).getConstraints().length > 0 ) {
                hasConstraints = rootCall || this.usesDeclaration( ((BetaNode) sinks[i]).getConstraints() );
            } else if ( sinks[i] instanceof EvalConditionNode ) {
                hasConstraints = this.usesDeclaration( ((EvalConditionNode) sinks[i]).getCondition() );
            }
            if ( !hasConstraints && sinks[i] instanceof ObjectSource ) {
                hasConstraints = !this.canSkipOnModify( ((ObjectSource) sinks[i]).getSinkPropagator().getSinks(),
                                                        false );
            } else if ( !hasConstraints && sinks[i] instanceof LeftTupleSource ) {
                hasConstraints = !this.canSkipOnModify( ((LeftTupleSource) sinks[i]).getSinkPropagator().getSinks(),
                                                        false );
            }
        }

        // Can only skip if we have no constraints
        return !hasConstraints;
    }

    private boolean usesDeclaration(final Constraint[] constraints) {
        boolean usesDecl = false;
        for ( int i = 0; !usesDecl && i < constraints.length; i++ ) {
            usesDecl = this.usesDeclaration( constraints[i] );
        }
        return usesDecl;
    }

    private boolean usesDeclaration(final Constraint constraint) {
        boolean usesDecl = false;
        final Declaration[] declarations = constraint.getRequiredDeclarations();
        for ( int j = 0; !usesDecl && j < declarations.length; j++ ) {
            usesDecl = (declarations[j].getPattern().getObjectType() == this.objectType);
        }
        return usesDecl;
    }

    private boolean usesDeclaration(final EvalCondition condition) {
        boolean usesDecl = false;
        final Declaration[] declarations = condition.getRequiredDeclarations();
        for ( int j = 0; !usesDecl && j < declarations.length; j++ ) {
            usesDecl = (declarations[j].getPattern().getObjectType() == this.objectType);
        }
        return usesDecl;
    }

    /**
     * @return the entryPoint
     */
    public EntryPoint getEntryPoint() {
        return ((EntryPointNode) this.source).getEntryPoint();
    }

    public long getExpirationOffset() {
        return expirationOffset;
    }

    public void setExpirationOffset(long expirationOffset) {
        this.expirationOffset = expirationOffset;
        if ( !this.objectType.getValueType().equals( ValueType.QUERY_TYPE ) ) {
            if ( this.expirationOffset > 0 ) {
                // override memory enabled settings
                this.setObjectMemoryEnabled( true );
            } else if ( this.expirationOffset == 0 ) {
                // disable memory
                this.setObjectMemoryEnabled( false );
            }
        }
    }

    public static class ExpireJob
        implements
        Job {

        public void execute(JobContext ctx) {
            ExpireJobContext context = (ExpireJobContext) ctx;
            context.workingMemory.queueWorkingMemoryAction( context.expireAction );
        }

    }

    public static class ExpireJobContext
        implements
        JobContext,
        Externalizable {
        public WorkingMemoryReteExpireAction expireAction;
        public InternalWorkingMemory         workingMemory;
        public JobHandle                     handle;

        /**
         * @param workingMemory
         * @param behavior
         * @param behaviorContext
         */
        public ExpireJobContext(WorkingMemoryReteExpireAction expireAction,
                                InternalWorkingMemory workingMemory) {
            super();
            this.expireAction = expireAction;
            this.workingMemory = workingMemory;
        }

        public JobHandle getJobHandle() {
            return this.handle;
        }

        public void setJobHandle(JobHandle jobHandle) {
            this.handle = jobHandle;
        }

        public WorkingMemoryReteExpireAction getExpireAction() {
            return expireAction;
        }

        public void setExpireAction(WorkingMemoryReteExpireAction expireAction) {
            this.expireAction = expireAction;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return workingMemory;
        }

        public void setWorkingMemory(InternalWorkingMemory workingMemory) {
            this.workingMemory = workingMemory;
        }

        public JobHandle getHandle() {
            return handle;
        }

        public void setHandle(JobHandle handle) {
            this.handle = handle;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            //this.behavior = (O)
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }
    }
    
    public static class ExpireJobContextTimerOutputMarshaller implements TimersOutputMarshaller {
        public void write(JobContext jobCtx,
                        MarshallerWriteContext outputCtx) throws IOException {   
            outputCtx.writeShort( PersisterEnums.EXPIRE_TIMER );
            
            // ExpireJob, no state            
            ExpireJobContext ejobCtx = ( ExpireJobContext ) jobCtx;
            WorkingMemoryReteExpireAction expireAction = ejobCtx.getExpireAction();
            outputCtx.writeInt( expireAction.getFactHandle().getId() );
            outputCtx.writeUTF( expireAction.getNode().getEntryPoint().getEntryPointId() );
            
            outputCtx.writeUTF( ((ClassObjectType)expireAction.getNode().getObjectType()).getClassType().getName() );
            
            DefaultJobHandle jobHandle = ( DefaultJobHandle ) ejobCtx.getJobHandle();
            PointInTimeTrigger trigger = ( PointInTimeTrigger ) jobHandle.getTimerJobInstance().getTrigger();
            outputCtx.writeLong( trigger.hasNextFireTime().getTime() );           
            
        }
    }
    
    public static class ExpireJobContextTimerInputMarshaller implements TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException, ClassNotFoundException {
            
            InternalFactHandle factHandle = inCtx.handles.get( inCtx.readInt() );
            
            String entryPointId = inCtx.readUTF();            
            EntryPointNode epn = ((ReteooRuleBase)inCtx.wm.getRuleBase()).getRete().getEntryPointNode( new EntryPoint( entryPointId ) );
            
            String className = inCtx.readUTF();
            Class cls = ((ReteooRuleBase)inCtx.wm.getRuleBase()).getRootClassLoader().loadClass( className );
            ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( cls ) );
            
            long nextTimeStamp = inCtx.readLong();
            
            TimerService clock = inCtx.wm.getTimerService();
            
            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction(factHandle, otn),
                                                      inCtx.wm );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  new PointInTimeTrigger( nextTimeStamp,
                                                                          null,
                                                                          null ) );
            jobctx.setJobHandle( handle );
            
            
//            SlidingTimeWindow beh = ( SlidingTimeWindow) inCtx.readObject();
//            
//            SlidingTimeWindowContext slCtx = new SlidingTimeWindowContext();
//            if ( inCtx.readBoolean() ) {
//                if ( inCtx.readBoolean() ) {
//                    int sinkId = inCtx.readInt();
//                    int factHandleId = inCtx.readInt();
//                    
//                    RightTupleSink sink =(RightTupleSink) inCtx.sinks.get( sinkId );                    
//                    RightTupleKey key = new RightTupleKey( factHandleId,
//                                                           sink );  
//                    slCtx.expiringTuple = inCtx.rightTuples.get( key );
//                }
//                
//                if ( inCtx.readBoolean() ) {
//                    int size = inCtx.readInt();
//                    for ( int i = 0; i < size; i++ ) {
//                        int sinkId = inCtx.readInt();
//                        int factHandleId = inCtx.readInt();
//                        
//                        RightTupleSink sink =(RightTupleSink) inCtx.sinks.get( sinkId );                    
//                        RightTupleKey key = new RightTupleKey( factHandleId,
//                                                               sink ); 
//                        slCtx.queue.add( inCtx.rightTuples.get( key ) );
//                    }
//                }
//                
//                if ( slCtx.queue.peek() != null ) {
//                    updateNextExpiration( ( RightTuple) slCtx.queue.peek(),
//                                          inCtx.wm,
//                                          beh,
//                                          slCtx );
//                }              
//            }
        }
    }        
}
