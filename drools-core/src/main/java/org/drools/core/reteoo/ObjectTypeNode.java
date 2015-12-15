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

package org.drools.core.reteoo;

import org.drools.core.InitialFact;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
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
import org.drools.core.reteoo.RuleRemovalContext.CleanupAdapter;
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    protected transient boolean dirty;

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
              context.getPartitionId(),
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
            if (o == null || !(o instanceof Id)) return false;

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

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(objectType);
        out.writeBoolean(objectMemoryEnabled);
        out.writeLong(expirationOffset);
        out.writeBoolean(queryNode);
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
    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        return EmptyBitMask.get();
    }

    public boolean isAssignableFrom(final ObjectType objectType) {
        return this.objectType.isAssignableFrom(objectType);
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

    private void checkDirty() {
        if (dirty) {
            resetIdGenerator();
            updateTupleSinkId(this, this);
            dirty = false;
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

    public static void doRetractObject(final InternalFactHandle factHandle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory) {
        for (RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; ) {
            RightTuple nextRightTuple = rightTuple.getHandleNext();
            rightTuple.getTupleSink().retractRightTuple(rightTuple,
                                                             context,
                                                             workingMemory);
            rightTuple = nextRightTuple;
        }
        factHandle.clearRightTuples();

        for (LeftTuple leftTuple = factHandle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getHandleNext()) {
            // must go via the LiaNode, so that the fact counter is updated, for linking
            ((LeftInputAdapterNode) leftTuple.getTupleSink().getLeftTupleSource()).retractLeftTuple(leftTuple,
                                                                                                        context,
                                                                                                        workingMemory);
        }
        factHandle.clearLeftTuples();
    }

    protected void resetIdGenerator() {
        idGenerator.reset();
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        checkDirty();

        context.setObjectType(objectType);
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
    public void attach(BuildContext context) {
        this.source.addObjectSink(this);

        InternalWorkingMemory[] workingMemories = context.getWorkingMemories();
        InternalWorkingMemory workingMemory = workingMemories.length > 0 ? workingMemories[0] : null;
        if ( workingMemory != null ) {
            InternalWorkingMemoryEntryPoint wmEntryPoint = (InternalWorkingMemoryEntryPoint) workingMemory.getWorkingMemoryEntryPoint(((EntryPointNode) source).getEntryPoint().getEntryPointId());
            ObjectTypeConf objectTypeConf = wmEntryPoint.getObjectTypeConfigurationRegistry().getObjectTypeConfByClass( ((ClassObjectType) objectType).getClassType() );
            if (objectTypeConf != null) {
                objectTypeConf.resetCache();
            }
        }
    }

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
    public boolean remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       InternalWorkingMemory[] workingMemories) {
        return doRemove(context,
                        builder,
                        workingMemories);
    }

    /**
     * OTN needs to override remove to avoid releasing the node ID, since OTN are
     * never removed from the rulebase in the current implementation
     */
    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        if (!context.getKnowledgeBase().getConfiguration().isPhreakEnabled() && context.getCleanupAdapter() != null) {
            for (InternalWorkingMemory workingMemory : workingMemories) {
                CleanupAdapter adapter = context.getCleanupAdapter();
                final ObjectTypeNodeMemory memory = workingMemory.getNodeMemory(this);
                Iterator<InternalFactHandle> it = memory.iterator();
                while (it.hasNext()) {
                    InternalFactHandle handle = it.next();
                    for (LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = leftTuple.getHandleNext()) {
                        adapter.cleanUp(leftTuple,
                                        workingMemory);
                    }
                }
            }
            context.setCleanupAdapter(null);
        }
        return false;
    }

    /**
     * Creates memory for the node using PrimitiveLongMap as its optimised for storage and reteivals of Longs.
     * However PrimitiveLongMap is not ideal for spase data. So it should be monitored incase its more optimal
     * to switch back to a standard HashMap.
     */
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

    public String toString() {
        return "[ObjectTypeNode(" + this.id + ")::" + ((EntryPointNode) this.source).getEntryPoint() + " objectType=" + this.objectType + " expiration=" + this.getExpirationOffset() + "ms ]";
    }

    /**
     * Uses he hashCode() of the underlying ObjectType implementation.
     */
    public int hashCode() {
        return this.objectType.hashCode() ^ this.source.hashCode();
    }

    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || !(object instanceof ObjectTypeNode)) {
            return false;
        }

        final ObjectTypeNode other = (ObjectTypeNode) object;

        return this.objectType.equals(other.objectType) && this.source.equals(other.source);
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

    public static class ExpireJob
            implements
            Job {

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

        public JobHandle getJobHandle() {
            return this.handle;
        }

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

            long nextTimeStamp = inCtx.readLong();

            TimerService clock = inCtx.wm.getTimerService();

            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction( (EventFactHandle) factHandle ),
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

            TimerService clock = inCtx.wm.getTimerService();

            JobContext jobctx = new ExpireJobContext( new WorkingMemoryReteExpireAction((EventFactHandle)factHandle),
                                                      inCtx.wm );
            JobHandle jobHandle = clock.scheduleJob( job,
                                                     jobctx,
                                                     new PointInTimeTrigger( _expire.getNextFireTimestamp(), null, null ) );
            jobctx.setJobHandle( jobHandle );
            ((EventFactHandle) factHandle).addJob(jobHandle);
        }
    }

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

        public short getNodeType() {
            return NodeTypeEnums.ObjectTypeNode;
        }

        public Iterator<InternalFactHandle> iterator() {
            return store.factHandlesIterator(true);
        }

        public SegmentMemory getSegmentMemory() {
            return null;
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            throw new UnsupportedOperationException();
        }

        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        public void setPrevious(Memory previous) {
            throw new UnsupportedOperationException();
        }

        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }

        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        public void reset() { }

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
