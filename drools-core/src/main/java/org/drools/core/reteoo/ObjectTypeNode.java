/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.core.InitialFact;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryReteExpireAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterEnums;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.ExpireTimer;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.compiled.CompiledNetwork;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.DefaultJobHandle;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;

import static org.drools.core.rule.TypeDeclaration.NEVER_EXPIRES;

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
        MemoryFactory<ObjectTypeNode.ObjectTypeNodeMemory> {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    /**
     * The <code>ObjectType</code> semantic module.
     */
    protected ObjectType objectType;

    private boolean objectMemoryEnabled;

    private static final transient ExpireJob job = new ExpireJob();

    private long                            expirationOffset = -1;

    private boolean queryNode;

    protected CompiledNetwork compiledNetwork;

    /* always dirty after serialisation */
    private transient volatile boolean dirty;

    /* reset counter when dirty */
    protected transient IdGenerator idGenerator;

    public int getOtnIdCounter() {
        return idGenerator.otnIdCounter;
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
        super(id,
              RuleBasePartitionId.MAIN_PARTITION,
              context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
              source,
              context.getKnowledgeBase().getConfiguration().getAlphaNodeHashingThreshold());
        this.objectType = objectType;
        idGenerator = new IdGenerator(id);

        setObjectMemoryEnabled(context.isObjectTypeNodeMemoryEnabled());

        if (ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom(objectType)) {
            queryNode = true;
        }

        this.dirty = true;

        hashcode = calculateHashCode();

        if (objectType != ClassObjectType.InitialFact_ObjectType && context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation()) {
            this.sink = new CompositePartitionAwareObjectSinkAdapter();
        }

        initMemoryId( context );
    }

    private static class IdGenerator {
        private final int otnId;
        private int otnIdCounter;

        private IdGenerator(int otnId) {
            this.otnId = otnId;
        }

        private Id nextId() {
            return new Id(otnId, otnIdCounter++);
        }

        private void reset() {
            otnIdCounter = 0;
        }
    }

    public static final Id DEFAULT_ID = new Id(-1, 0);

    public static class Id {

        private final int otnId;
        private final int id;

        public Id(int otnId, int id) {
            this.otnId = otnId;
            this.id = id;
        }

        @Override
        public String toString() {
            return "ObjectTypeNode.Id[" + otnId + "#" + id + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;

            Id otherId = (Id) o;
            return id == otherId.id && otnId == otherId.otnId;
        }

        @Override
        public int hashCode() {
            return 31 * otnId + 37 * id;
        }

        public boolean before(Id otherId) {
            return otherId != null && (otnId < otherId.otnId || (otnId == otherId.otnId && id < otherId.id));
        }

        public int getId() {
            return id;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        objectType = (ObjectType) in.readObject();

        // this is here as not all objectTypeNodes used ClassObjectTypes in packages (i.e. rules with those nodes did not exist yet)
        // and thus have no wiring targets
        if (objectType instanceof ClassObjectType) {
            objectType = ((DroolsObjectInputStream) in).getKnowledgeBase().getClassFieldAccessorCache().getClassObjectType((ClassObjectType) objectType, true);
        }

        objectMemoryEnabled = in.readBoolean();
        expirationOffset = in.readLong();
        queryNode = in.readBoolean();
        dirty = true;
        idGenerator = new IdGenerator(id);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(objectType);
        out.writeBoolean(objectMemoryEnabled);
        out.writeLong(expirationOffset);
        out.writeBoolean(queryNode);
    }

    @Override
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

    /**
     * Returns the partition ID for which this node belongs to
     */
    @Override
    public RuleBasePartitionId getPartitionId() {
        return RuleBasePartitionId.MAIN_PARTITION;
    }

    @Override
    public BitMask calculateDeclaredMask(Class modifiedClass, List<String> settableProperties) {
        return EmptyBitMask.get();
    }

    public boolean isAssignableFrom(final ObjectType objectType) {
        return this.objectType.isAssignableFrom(objectType);
    }

    public CompiledNetwork getCompiledNetwork() {
        return this.compiledNetwork;
    }

    public void setCompiledNetwork(CompiledNetwork compiledNetwork) {
        this.compiledNetwork = compiledNetwork;

        this.compiledNetwork.setObjectTypeNode(this);
    }

    public void assertInitialFact(final InternalFactHandle factHandle,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
        if (objectMemoryEnabled) {
            InitialFactObjectTypeNodeMemory memory = (InitialFactObjectTypeNodeMemory) workingMemory.getNodeMemory(this);
            memory.add(factHandle);
        }

        checkDirty();
        propagateAssert(factHandle, context, workingMemory);
    }

    protected void checkDirty() {
        if (dirty) {
            synchronized (this) {
                if (dirty) {
                    resetIdGenerator();
                    updateTupleSinkId( this, this );
                    dirty = false;
                }
            }
        }
    }

    /**
     * Propagate the <code>FactHandleimpl</code> through the <code>Rete</code> network. All
     * <code>FactHandleImpl</code> should be remembered in the node memory, so that later runtime rule attachmnents
     * can have the matched facts propagated to them.
     *
     * @param factHandle    The fact handle.
     * @param context       The propagation context.
     * @param workingMemory The working memory session.
     */
    @Override
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
    }

    public void propagateAssert(InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory) {
        checkDirty();
        if (compiledNetwork != null) {
            compiledNetwork.assertObject(factHandle,
                                         context,
                                         workingMemory);
        } else {
            this.sink.propagateAssertObject(factHandle,
                                            context,
                                            workingMemory);
        }
    }

    /**
     * Retract the <code>FactHandleimpl</code> from the <code>Rete</code> network. Also remove the
     * <code>FactHandleImpl</code> from the node memory.
     *
     * @param factHandle    The fact handle.
     * @param context       The propagation context.
     * @param workingMemory The working memory session.
     */
    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        checkDirty();

        doRetractObject( factHandle, context, workingMemory);
    }

    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory,
                              int partition) {
        checkDirty();

        retractRightTuples( factHandle, context, workingMemory, partition );
        retractLeftTuples( factHandle, context, workingMemory, partition );
    }

    public static void doRetractObject(final InternalFactHandle factHandle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory) {
        retractRightTuples( factHandle, context, workingMemory );
        retractLeftTuples( factHandle, context, workingMemory );
    }

    public static void expireLeftTuple(LeftTuple leftTuple) {
        if (!leftTuple.isExpired()) {
            leftTuple.setExpired();
            for ( LeftTuple child = leftTuple.getFirstChild(); child != null; child = child.getHandleNext() ) {
                expireLeftTuple(child);
            }
            for ( LeftTuple peer = leftTuple.getPeer(); peer != null; peer = peer.getPeer() ) {
                expireLeftTuple(peer);
            }
        }
    }

    public static void expireRightTuple(RightTuple rightTuple) {
        for ( LeftTuple child = rightTuple.getFirstChild(); child != null; child = child.getHandleNext() ) {
            expireLeftTuple(child);
        }
    }

    public static void retractLeftTuples( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        factHandle.forEachLeftTuple( lt -> {
            LeftTupleSink sink = lt.getTupleSink();
            ((LeftInputAdapterNode) sink.getLeftTupleSource()).retractLeftTuple(lt,
                                                                                context,
                                                                                workingMemory);
        } );
        factHandle.clearLeftTuples();
    }

    public static void retractLeftTuples( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory, int partition ) {
        DefaultFactHandle.CompositeLinkedTuples linkedTuples = ( (DefaultFactHandle.CompositeLinkedTuples) factHandle.getLinkedTuples() );
        linkedTuples.forEachLeftTuple( partition, lt -> {
            LeftTupleSink sink = lt.getTupleSink();
            ((LeftInputAdapterNode) sink.getLeftTupleSource()).retractLeftTuple(lt,
                                                                                context,
                                                                                workingMemory);
        } );
        linkedTuples.clearLeftTuples(partition);
    }

    public static void retractRightTuples( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        factHandle.forEachRightTuple( rt -> rt.retractTuple( context, workingMemory) );
        factHandle.clearRightTuples();
    }

    public static void retractRightTuples( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory, int partition ) {
        DefaultFactHandle.CompositeLinkedTuples linkedTuples = ( (DefaultFactHandle.CompositeLinkedTuples) factHandle.getLinkedTuples() );
        linkedTuples.forEachRightTuple( partition, rt -> rt.retractTuple( context, workingMemory) );
        linkedTuples.clearRightTuples(partition);
    }

    protected void resetIdGenerator() {
        idGenerator.reset();
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        checkDirty();

        if (compiledNetwork != null) {
            compiledNetwork.modifyObject(factHandle,
                                         modifyPreviousTuples,
                                         context.adaptModificationMaskForObjectType(objectType, workingMemory),
                                         workingMemory);
        } else {
            this.sink.propagateModifyObject(factHandle,
                                            modifyPreviousTuples,
                                            context.adaptModificationMaskForObjectType(objectType, workingMemory),
                                            workingMemory);
        }
    }

    @Override
    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        checkDirty();

        // Regular updateSink
        final ObjectTypeNodeMemory memory = workingMemory.getNodeMemory(this);
        Iterator<InternalFactHandle> it = memory.iterator();

        while (it.hasNext()) {
            sink.assertObject(it.next(),
                              context,
                              workingMemory);
        }
    }

    /**
     * Rete needs to know that this ObjectTypeNode has been added
     */
    @Override
    public void attach(BuildContext context) {
        this.source.addObjectSink(this);

        Class<?> nodeTypeClass = objectType.getClassType();
        if (nodeTypeClass == null) {
            return;
        }

        EntryPointNode epn = context.getKnowledgeBase().getRete().getEntryPointNode( ((EntryPointNode) source).getEntryPoint() );
        if (epn == null) {
            return;
        }

        ObjectTypeConf objectTypeConf = epn.getTypeConfReg().getObjectTypeConfByClass( nodeTypeClass );
        if ( objectTypeConf != null ) {
            objectTypeConf.resetCache();
        }
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
        this.dirty = true;
    }

    protected static void updateTupleSinkId(ObjectTypeNode otn,
                                            ObjectSource source) {
        for (ObjectSink sink : source.sink.getSinks()) {
            if (sink instanceof BetaNode) {
                ((BetaNode) sink).setRightInputOtnId(otn.nextOtnId());
            } else if (sink instanceof LeftInputAdapterNode) {
                for (LeftTupleSink liaChildSink : ((LeftInputAdapterNode) sink).getSinkPropagator().getSinks()) {
                    liaChildSink.setLeftInputOtnId(otn.nextOtnId());
                }
            } else if (sink instanceof WindowNode) {
                ((WindowNode) sink).setRightInputOtnId(otn.nextOtnId());
                updateTupleSinkId(otn, (WindowNode) sink);
            } else if (sink instanceof AlphaNode) {
                updateTupleSinkId(otn, (AlphaNode) sink);
            }
        }
    }

    public Id nextOtnId() {
        return idGenerator.nextId();
    }

    /**
    * OTN needs to override remove to avoid releasing the node ID, since OTN are
    * never removed from the rulebase in the current implementation
    */
    @Override
    public boolean remove(RuleRemovalContext context, ReteooBuilder builder) {
            return doRemove(context, builder);
    }

    /**
     * OTN needs to override remove to avoid releasing the node ID, since OTN are
     * never removed from the rulebase in the current implementation
     */
    @Override
    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return false;
    }

    /**
     * Creates memory for the node using PrimitiveLongMap as its optimised for storage and reteivals of Longs.
     * However PrimitiveLongMap is not ideal for spase data. So it should be monitored incase its more optimal
     * to switch back to a standard HashMap.
     */
    @Override
    public ObjectTypeNodeMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        Class<?> classType = ((ClassObjectType) getObjectType()).getClassType();
        if (InitialFact.class.isAssignableFrom(classType)) {
            return new InitialFactObjectTypeNodeMemory(classType);
        }
        return new ObjectTypeNodeMemory(classType, wm);
    }

    public boolean isObjectMemoryEnabled() {
        return this.objectMemoryEnabled;
    }

    public void setObjectMemoryEnabled(boolean objectMemoryEnabled) {
        this.objectMemoryEnabled = objectMemoryEnabled;
    }

    @Override
    public String toString() {
        return "[ObjectTypeNode(" + this.id + ")::" + ((EntryPointNode) this.source).getEntryPoint() + " objectType=" + this.objectType + " expiration=" + this.getExpirationOffset() + "ms ]";
    }

    private int calculateHashCode() {
        return (this.objectType != null ? this.objectType.hashCode() : 0) * 37 + (this.source != null ? this.source.hashCode() : 0) * 31;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if ( !(object instanceof ObjectTypeNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        ObjectTypeNode other = (ObjectTypeNode)object;
        return this.source.getId() == other.source.getId() && this.objectType.equals( other.objectType );
    }

    /**
     * @return the entryPoint
     */
    public EntryPointId getEntryPoint() {
        return ((EntryPointNode) this.source).getEntryPoint();
    }

    public long getExpirationOffset() {
        return expirationOffset;
    }

    public void setExpirationOffset(long expirationOffset) {
        this.expirationOffset = expirationOffset;
        if (!this.objectType.getValueType().equals(ValueType.QUERY_TYPE)) {
            if (expirationOffset > 0) {
                // override memory enabled settings
                this.setObjectMemoryEnabled(true);
            } else if (expirationOffset == 0) {
                // disable memory
                this.setObjectMemoryEnabled(false);
            }
        }
    }

    public void mergeExpirationOffset(ObjectTypeNode other) {
        setExpirationOffset( expirationOffset == NEVER_EXPIRES || other.expirationOffset == NEVER_EXPIRES ?
                             NEVER_EXPIRES :
                             Math.max(expirationOffset, other.expirationOffset) );
    }

    public static class ExpireJob
            implements
            Job {

        @Override
        public void execute(JobContext ctx) {
            ExpireJobContext context = (ExpireJobContext) ctx;
            context.workingMemory.queueWorkingMemoryAction(context.expireAction);
            context.getExpireAction().getFactHandle().removeJob( context.getJobHandle());
        }
    }

    public static class ExpireJobContext
            implements
            JobContext,
            Externalizable {
        public final WorkingMemoryReteExpireAction expireAction;
        public final InternalWorkingMemory         workingMemory;
        public JobHandle                     handle;

        public ExpireJobContext(WorkingMemoryReteExpireAction expireAction,
                                InternalWorkingMemory workingMemory) {
            super();
            this.expireAction = expireAction;
            this.workingMemory = workingMemory;
        }

        @Override
        public JobHandle getJobHandle() {
            return this.handle;
        }

        @Override
        public void setJobHandle(JobHandle jobHandle) {
            this.handle = jobHandle;
        }

        public WorkingMemoryReteExpireAction getExpireAction() {
            return expireAction;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return workingMemory;
        }

        public JobHandle getHandle() {
            return handle;
        }

        public void setHandle(JobHandle handle) {
            this.handle = handle;
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException,
                                                        ClassNotFoundException {
            //this.behavior = (O)
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // TODO Auto-generated method stub

        }
    }

    public static class ExpireJobContextTimerOutputMarshaller
            implements
            TimersOutputMarshaller {

        public void write(JobContext jobCtx,
                          MarshallerWriteContext outputCtx) throws IOException {
            // ExpireJob, no state
            ExpireJobContext ejobCtx = (ExpireJobContext) jobCtx;
            DefaultJobHandle jobHandle = (DefaultJobHandle) ejobCtx.getJobHandle();
            PointInTimeTrigger trigger = (PointInTimeTrigger) jobHandle.getTimerJobInstance().getTrigger();
            // There is no reason to serialize a timer when it has no future execution time.
            Date nextFireTime = trigger.hasNextFireTime();
            if (nextFireTime != null) {
                outputCtx.writeShort(PersisterEnums.EXPIRE_TIMER);
                outputCtx.writeLong(ejobCtx.getExpireAction().getFactHandle().getId());
                outputCtx.writeLong(nextFireTime.getTime());
            }
        }

        @Override
        public ProtobufMessages.Timers.Timer serialize(JobContext jobCtx,
                                                       MarshallerWriteContext outputCtx) {
            // ExpireJob, no state
            ExpireJobContext ejobCtx = (ExpireJobContext) jobCtx;
            WorkingMemoryReteExpireAction expireAction = ejobCtx.getExpireAction();
            DefaultJobHandle jobHandle = (DefaultJobHandle) ejobCtx.getJobHandle();
            PointInTimeTrigger trigger = (PointInTimeTrigger) jobHandle.getTimerJobInstance().getTrigger();
            Date nextFireTime = trigger.hasNextFireTime();
            if (nextFireTime != null) {
                return ProtobufMessages.Timers.Timer.newBuilder()
                        .setType(ProtobufMessages.Timers.TimerType.EXPIRE)
                        .setExpire(ProtobufMessages.Timers.ExpireTimer.newBuilder()
                                           .setHandleId(expireAction.getFactHandle().getId())
                                           .setNextFireTimestamp(nextFireTime.getTime())
                                           .build())
                        .build();
            } else {
                // There is no reason to serialize a timer when it has no future execution time.
                return null;
            }
        }
    }

    public static class ExpireJobContextTimerInputMarshaller
            implements
            TimersInputMarshaller {
        public void read(MarshallerReaderContext inCtx) throws IOException,
                                                               ClassNotFoundException {

            InternalFactHandle factHandle = inCtx.handles.get( inCtx.readLong() );

            long nextTimeStamp = inCtx.readLong();

            TimerService clock = inCtx.wm.getTimerService();

            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction( (EventFactHandle) factHandle ),
                                                      inCtx.wm );
            JobHandle handle = clock.scheduleJob( job,
                                                  jobctx,
                                                  PointInTimeTrigger.createPointInTimeTrigger( nextTimeStamp, null ) );
            jobctx.setJobHandle( handle );

        }

        @Override
        public void deserialize(MarshallerReaderContext inCtx,
                                Timer timer) throws ClassNotFoundException {
            ExpireTimer expire = timer.getExpire();
            InternalFactHandle factHandle = inCtx.handles.get( expire.getHandleId() );

            TimerService clock = inCtx.wm.getTimerService();

            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction((EventFactHandle)factHandle),
                                                      inCtx.wm );
            JobHandle jobHandle = clock.scheduleJob( job,
                                                     jobctx,
                                                     PointInTimeTrigger.createPointInTimeTrigger( expire.getNextFireTimestamp(), null ) );
            jobctx.setJobHandle( jobHandle );
            ((EventFactHandle) factHandle).addJob(jobHandle);
        }
    }

    @Override
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException("This should never get called, as the PropertyReactive first happens at the AlphaNode");
    }


    public static class ObjectTypeNodeMemory implements Memory {
        private ClassAwareObjectStore.SingleClassStore store;
        private Class<?> classType;

        ObjectTypeNodeMemory(Class<?> classType) {
            this.classType = classType;
        }

        ObjectTypeNodeMemory(Class<?> classType, InternalWorkingMemory wm) {
            this(classType);
            store = ((ClassAwareObjectStore) wm.getObjectStore()).getOrCreateClassStore(classType);
        }

        @Override
        public short getNodeType() {
            return NodeTypeEnums.ObjectTypeNode;
        }

        public Iterator<InternalFactHandle> iterator() {
            return store.factHandlesIterator(true);
        }

        @Override
        public SegmentMemory getSegmentMemory() {
            return null;
        }

        @Override
        public void setSegmentMemory(SegmentMemory segmentMemory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPrevious(Memory previous) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void reset() { }

        @Override
        public String toString() {
            return "ObjectTypeMemory for " + classType;
        }
    }

    public static class InitialFactObjectTypeNodeMemory extends ObjectTypeNodeMemory {
        private List<InternalFactHandle> list = Collections.emptyList();

        InitialFactObjectTypeNodeMemory(Class<?> classType) {
            super(classType);
        }

        public void add(InternalFactHandle factHandle) {
            list = Collections.singletonList( factHandle );
        }

        @Override
        public Iterator<InternalFactHandle> iterator() {
            return list.iterator();
        }

        @Override
        public void reset() {
            list = Collections.emptyList();
        }
    }
}
