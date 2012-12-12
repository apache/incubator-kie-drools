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
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.ValueType;
import org.drools.common.AbstractRuleBase;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.UpdateContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.ObjectHashSet.ObjectEntry;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.marshalling.impl.PersisterEnums;
import org.drools.marshalling.impl.ProtobufMessages;
import org.drools.marshalling.impl.ProtobufMessages.Timers.ExpireTimer;
import org.drools.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.marshalling.impl.TimersInputMarshaller;
import org.drools.marshalling.impl.TimersOutputMarshaller;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteExpireAction;
import org.drools.reteoo.RuleRemovalContext.CleanupAdapter;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.compiled.CompiledNetwork;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.EvalCondition;
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
 * @see Rete
 */
public class ObjectTypeNode extends ObjectSource
        implements
        ObjectSink,
        Externalizable,
        MemoryFactory
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long               serialVersionUID = 510l;

    /**
     * The <code>ObjectType</code> semantic module.
     */
    private ObjectType                      objectType;

    private boolean                         objectMemoryEnabled;

    private long                            expirationOffset = -1;

    public static final transient ExpireJob job              = new ExpireJob();

    private boolean                         queryNode;

    private CompiledNetwork                 compiledNetwork;

    /* always dirty after serialisation */
    private transient boolean               dirty;

    /* reset counter when dirty */
    private transient int                   otnIdCounter;

    public int getOtnIdCounter() {
        return otnIdCounter;
    }

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

        setObjectMemoryEnabled( context.isObjectTypeNodeMemoryEnabled() );

        if ( ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom( objectType ) ) {
            queryNode = true;
        }

        this.dirty = true;
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

        objectMemoryEnabled = in.readBoolean();
        expirationOffset = in.readLong();
        queryNode = in.readBoolean();
        dirty = true;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( objectType );
        out.writeBoolean( objectMemoryEnabled );
        out.writeLong( expirationOffset );
        out.writeBoolean( queryNode );
    }
    
    public short getType() {
        return NodeTypeEnums.ObjectTypeNode;
    }    

    /**
     * Retrieve the semantic <code>ObjectType</code> differentiator.
     *
     * @return The semantic <code>ObjectType</code> differentiator.
     */
    public ObjectType getObjectType() {
        return this.objectType;
    }

    @Override
    public long calculateDeclaredMask(List<String> settableProperties) {
        return 0;
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
        if ( dirty ) {
            otnIdCounter = 0;
            updateTupleSinkId( this, this );
            dirty = false;
        }

        if ( objectMemoryEnabled && !(queryNode && !((DroolsQuery) factHandle.getObject()).isOpen()) ) {
            final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory( this );
            memory.memory.add( factHandle,
                               false );
        }

        if ( compiledNetwork != null ) {
            compiledNetwork.assertObject( factHandle,
                                          context,
                                          workingMemory );
        } else {

            this.sink.propagateAssertObject( factHandle,
                                             context,
                                             workingMemory );
        }

        if ( context.getReaderContext() == null && this.objectType.isEvent() && this.expirationOffset >= 0 && this.expirationOffset != Long.MAX_VALUE ) {
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
        if ( dirty ) {
            otnIdCounter = 0;
            updateTupleSinkId( this, this );
            dirty = false;
        }

        if ( objectMemoryEnabled && !(queryNode && !((DroolsQuery) factHandle.getObject()).isOpen()) ) {
            final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory( this );
            memory.memory.remove( factHandle );            
        }

        for ( RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; rightTuple = rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              workingMemory );
        }
        factHandle.clearRightTuples();

        for ( LeftTuple leftTuple = factHandle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getLeftParentNext() ) {
            // must go via the LiaNode, so that the fact counter is updated, for linking
//            leftTuple.getLeftTupleSink().getLeftTupleSource().retractLeftTuple( leftTuple,
//                                                                                context,
//                                                                                workingMemory );            
            ((LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource()).retractLeftTuple( leftTuple,
                                                                                                         context,
                                                                                                         workingMemory );
//            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
//                                                           context,
//                                                           workingMemory );            
        }
        factHandle.clearLeftTuples();
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        if ( dirty ) {
            otnIdCounter = 0;
            updateTupleSinkId( this, this );
            dirty = false;
        }

        context.setObjectType( objectType );
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
        // Regular updateSink
        final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory( this );
        Iterator it = memory.memory.iterator();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            sink.assertObject( (InternalFactHandle) entry.getValue(),
                               context,
                               workingMemory );
        }
    }

    /**
     * Rete needs to know that this ObjectTypeNode has been added
     */
    public void attach( BuildContext context ) {
        this.source.addObjectSink( this );
        if (context == null) {
            return;
        }

        // we need to call updateSink on Rete, because someone
        // might have already added facts matching this ObjectTypeNode
        // to working memories
        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
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

    public void networkUpdated(UpdateContext updateContext) {
        this.dirty = true;
    }

    private static void updateTupleSinkId( ObjectTypeNode otn,
                                           ObjectSource source ) {
        for ( ObjectSink sink : source.sink.getSinks() ) {
            if ( sink instanceof BetaNode ) {
                ((BetaNode) sink).setRightInputOtnId( otn.nextOtnId() );
            } else if ( sink instanceof LeftInputAdapterNode ) {
                for ( LeftTupleSink liaChildSink : ((LeftInputAdapterNode) sink).getSinkPropagator().getSinks() ) {
                    liaChildSink.setLeftInputOtnId( otn.nextOtnId() );
                }
            } else if ( sink instanceof AlphaNode ) {
                updateTupleSinkId( otn, (AlphaNode) sink );
            }
        }
    }

    public int nextOtnId() {
        return otnIdCounter++;
    }

    /**
     * OTN needs to override remove to avoid releasing the node ID, since OTN are
     * never removed from the rulebase in the current implementation
     *
     * @inheritDoc
     * @see org.kie.common.BaseNode#remove(org.kie.reteoo.RuleRemovalContext, org.kie.reteoo.ReteooBuilder, org.kie.common.BaseNode, org.kie.common.InternalWorkingMemory[])
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
                final ObjectTypeNodeMemory memory = (ObjectTypeNodeMemory) workingMemory.getNodeMemory( this );
                Iterator it = memory.memory.iterator();
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
    public Memory createMemory(final RuleBaseConfiguration config) {
        return new ObjectTypeNodeMemory();
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

    public static class ExpireJobContextTimerOutputMarshaller
            implements
            TimersOutputMarshaller {
        public void write(JobContext jobCtx,
                          MarshallerWriteContext outputCtx) throws IOException {
            outputCtx.writeShort( PersisterEnums.EXPIRE_TIMER );

            // ExpireJob, no state            
            ExpireJobContext ejobCtx = (ExpireJobContext) jobCtx;
            WorkingMemoryReteExpireAction expireAction = ejobCtx.getExpireAction();
            outputCtx.writeInt( expireAction.getFactHandle().getId() );
            outputCtx.writeUTF( expireAction.getNode().getEntryPoint().getEntryPointId() );

            outputCtx.writeUTF( ((ClassObjectType) expireAction.getNode().getObjectType()).getClassType().getName() );

            DefaultJobHandle jobHandle = (DefaultJobHandle) ejobCtx.getJobHandle();
            PointInTimeTrigger trigger = (PointInTimeTrigger) jobHandle.getTimerJobInstance().getTrigger();
            outputCtx.writeLong( trigger.hasNextFireTime().getTime() );

        }

        public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx,
                                                       MarshallerWriteContext outputCtx) {
            // ExpireJob, no state            
            ExpireJobContext ejobCtx = ( ExpireJobContext ) jobCtx;
            WorkingMemoryReteExpireAction expireAction = ejobCtx.getExpireAction();
            DefaultJobHandle jobHandle = ( DefaultJobHandle ) ejobCtx.getJobHandle();
            PointInTimeTrigger trigger = ( PointInTimeTrigger ) jobHandle.getTimerJobInstance().getTrigger();
            
            return ProtobufMessages.Timers.Timer.newBuilder()
                    .setType( ProtobufMessages.Timers.TimerType.EXPIRE )
                    .setExpire( ProtobufMessages.Timers.ExpireTimer.newBuilder()
                                .setHandleId( expireAction.getFactHandle().getId() )
                                .setEntryPointId( expireAction.getNode().getEntryPoint().getEntryPointId() )
                                .setClassName( ((ClassObjectType)expireAction.getNode().getObjectType()).getClassType().getName() )
                                .setNextFireTimestamp( trigger.hasNextFireTime().getTime() )
                                .build() )
                    .build();
        }
    }

    public static class ExpireJobContextTimerInputMarshaller
            implements
            TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException,
                                                       ClassNotFoundException {

            InternalFactHandle factHandle = inCtx.handles.get( inCtx.readInt() );

            String entryPointId = inCtx.readUTF();
            EntryPointNode epn = ((ReteooRuleBase) inCtx.wm.getRuleBase()).getRete().getEntryPointNode( new EntryPoint( entryPointId ) );

            String className = inCtx.readUTF();
            Class< ? > cls = ((ReteooRuleBase) inCtx.wm.getRuleBase()).getRootClassLoader().loadClass( className );
            ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( cls ) );

            long nextTimeStamp = inCtx.readLong();

            TimerService clock = inCtx.wm.getTimerService();

            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction( factHandle, otn ),
                                                      inCtx.wm );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  new PointInTimeTrigger( nextTimeStamp,
                                                                          null,
                                                                          null ) );
            jobctx.setJobHandle( handle );

        }
        
        public void deserialize(MarshallerReaderContext inCtx,
                                Timer _timer) throws ClassNotFoundException {
            ExpireTimer _expire = _timer.getExpire();
            InternalFactHandle factHandle = inCtx.handles.get( _expire.getHandleId() );
            EntryPointNode epn = ((ReteooRuleBase)inCtx.wm.getRuleBase()).getRete().getEntryPointNode( new EntryPoint( _expire.getEntryPointId() ) );
            Class<?> cls = ((ReteooRuleBase)inCtx.wm.getRuleBase()).getRootClassLoader().loadClass( _expire.getClassName() );
            ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType( cls ) );
            
            TimerService clock = inCtx.wm.getTimerService();
            
            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction(factHandle, otn),
                                                      inCtx.wm );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  new PointInTimeTrigger( _expire.getNextFireTimestamp(),
                                                                          null,
                                                                          null ) );
            jobctx.setJobHandle( handle );
        }
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException( "This should never get called, as the PropertyReactive first happens at the AlphaNode" );
    }

    
    public static class ObjectTypeNodeMemory implements Memory {
        public ObjectHashSet memory = new ObjectHashSet();

        public short getNodeType() {
            return NodeTypeEnums.ObjectTypeNode;
        }

        public SegmentMemory getSegmentMemory() {
            throw new UnsupportedOperationException();
        }

        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        public void setPrevious(Memory previous) {
            throw new UnsupportedOperationException();
        }

        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }
    }
}
