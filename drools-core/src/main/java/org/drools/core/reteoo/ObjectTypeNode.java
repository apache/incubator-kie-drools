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
import java.util.Iterator;
import java.util.List;

import org.drools.core.InitialFact;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ValueType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.FactHandleClassStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.impl.WorkingMemoryReteExpireAction;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
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
public class ObjectTypeNode extends ObjectSource implements ObjectSink, MemoryFactory<ObjectTypeNode.ObjectTypeNodeMemory> {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    /**
     * The <code>ObjectType</code> semantic module.
     */
    protected ObjectType objectType;

    private boolean objectMemoryEnabled;

    private long                            expirationOffset = -1;

    private boolean queryNode;

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
              context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
              source,
              context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold(),
              context.getRuleBase().getConfiguration().getAlphaNodeRangeIndexThreshold());
        this.objectType = objectType;
        idGenerator = new IdGenerator(id);

        setObjectMemoryEnabled(context.isObjectTypeNodeMemoryEnabled());

        if (ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom(objectType)) {
            queryNode = true;
        }

        this.dirty = true;

        hashcode = calculateHashCode();

        if (objectType != ClassObjectType.InitialFact_ObjectType && context.getRuleBase().getConfiguration().isMultithreadEvaluation()) {
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
    public BitMask calculateDeclaredMask(ObjectType modifiedType, List<String> settableProperties) {
        return EmptyBitMask.get();
    }

    public boolean isAssignableFrom(final ObjectType objectType) {
        return this.objectType.isAssignableFrom(objectType);
    }

    public void assertInitialFact(final InternalFactHandle factHandle,
                                  final PropagationContext context,
                                  final ReteEvaluator reteEvaluator) {
        if (objectMemoryEnabled) {
            InitialFactObjectTypeNodeMemory memory = (InitialFactObjectTypeNodeMemory) reteEvaluator.getNodeMemory(this);
            memory.add(factHandle);
        }

        checkDirty();
        propagateAssert(factHandle, context, reteEvaluator);
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
     * @param reteEvaluator The working memory session.
     */
    @Override
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final ReteEvaluator reteEvaluator) {
    }

    public void propagateAssert(InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator) {
        checkDirty();
        this.sink.propagateAssertObject(factHandle, context, reteEvaluator);
    }

    /**
     * Retract the <code>FactHandleimpl</code> from the <code>Rete</code> network. Also remove the
     * <code>FactHandleImpl</code> from the node memory.
     *
     * @param factHandle    The fact handle.
     * @param context       The propagation context.
     * @param reteEvaluator The working memory session.
     */
    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final ReteEvaluator reteEvaluator) {
        checkDirty();

        doRetractObject( factHandle, context, reteEvaluator);
    }

    public void retractObject(final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final ReteEvaluator reteEvaluator,
                              int partition) {
        checkDirty();

        retractRightTuples( factHandle, context, reteEvaluator, partition );
        retractLeftTuples( factHandle, context, reteEvaluator, partition );
    }

    public static void doRetractObject(final InternalFactHandle factHandle,
                                       final PropagationContext context,
                                       final ReteEvaluator reteEvaluator) {
        retractRightTuples( factHandle, context, reteEvaluator );
        retractLeftTuples( factHandle, context, reteEvaluator );
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

    public static void retractLeftTuples( InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator ) {
        factHandle.forEachLeftTuple( lt -> {
            LeftTupleSink sink = lt.getTupleSink();
            ((LeftInputAdapterNode) sink.getLeftTupleSource()).retractLeftTuple(lt, context, reteEvaluator);
        } );
        factHandle.clearLeftTuples();
    }

    public static void retractLeftTuples( InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator, int partition ) {
        DefaultFactHandle.CompositeLinkedTuples linkedTuples = ( (DefaultFactHandle.CompositeLinkedTuples) factHandle.getLinkedTuples() );
        linkedTuples.forEachLeftTuple( partition, lt -> {
            LeftTupleSink sink = lt.getTupleSink();
            ((LeftInputAdapterNode) sink.getLeftTupleSource()).retractLeftTuple(lt, context, reteEvaluator);
        } );
        linkedTuples.clearLeftTuples(partition);
    }

    public static void retractRightTuples( InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator ) {
        factHandle.forEachRightTuple( rt -> rt.retractTuple( context, reteEvaluator) );
        factHandle.clearRightTuples();
    }

    public static void retractRightTuples( InternalFactHandle factHandle, PropagationContext context, ReteEvaluator reteEvaluator, int partition ) {
        DefaultFactHandle.CompositeLinkedTuples linkedTuples = ( (DefaultFactHandle.CompositeLinkedTuples) factHandle.getLinkedTuples() );
        linkedTuples.forEachRightTuple( partition, rt -> rt.retractTuple( context, reteEvaluator) );
        linkedTuples.clearRightTuples(partition);
    }

    protected void resetIdGenerator() {
        idGenerator.reset();
    }

    @Override
    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             ReteEvaluator reteEvaluator) {
        checkDirty();

        this.sink.propagateModifyObject(factHandle,
                                        modifyPreviousTuples,
                                        context.adaptModificationMaskForObjectType(objectType, reteEvaluator),
                                        reteEvaluator);
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
            sink.assertObject(it.next(), context, workingMemory);
        }
    }

    /**
     * Rete needs to know that this ObjectTypeNode has been added
     */
    @Override
    public void doAttach(BuildContext context) {
        super.doAttach(context);
        this.source.addObjectSink(this);

        EntryPointNode epn = context.getRuleBase().getRete().getEntryPointNode( ((EntryPointNode) source).getEntryPoint() );
        if (epn == null) {
            return;
        }

        ObjectTypeConf objectTypeConf = epn.getTypeConfReg().getConfForObjectType( objectType );
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
    public ObjectTypeNodeMemory createMemory(final RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        if (objectType.isTemplate()) {
            throw new UnsupportedOperationException("this method is used only for the initial fact and during incremental compilation which is not supported yet for fact templates");
        }
        Class<?> classType = ((ClassObjectType) objectType).getClassType();
        if (InitialFact.class.isAssignableFrom(classType)) {
            return new InitialFactObjectTypeNodeMemory(classType);
        }
        return new ObjectTypeNodeMemory(classType, reteEvaluator);
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
            context.reteEvaluator.addPropagation(context.expireAction, true);
            context.getExpireAction().getFactHandle().removeJob( context.getJobHandle());
        }
    }

    public static class ExpireJobContext
            implements
            JobContext,
            Externalizable {
        public final WorkingMemoryReteExpireAction expireAction;
        public final ReteEvaluator         reteEvaluator;
        public JobHandle                     handle;

        public ExpireJobContext(WorkingMemoryReteExpireAction expireAction,
                                ReteEvaluator reteEvaluator) {
            super();
            this.expireAction = expireAction;
            this.reteEvaluator = reteEvaluator;
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

        public ReteEvaluator getReteEvaluator() {
            return reteEvaluator;
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

    @Override
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       ReteEvaluator reteEvaluator) {
        throw new UnsupportedOperationException("This should never get called, as the PropertyReactive first happens at the AlphaNode");
    }


    public static class ObjectTypeNodeMemory implements Memory {
        private FactHandleClassStore store;
        private Class<?> classType;

        ObjectTypeNodeMemory(Class<?> classType) {
            this.classType = classType;
        }

        ObjectTypeNodeMemory(Class<?> classType, ReteEvaluator reteEvaluator) {
            this(classType);
            store = reteEvaluator.getStoreForClass(classType);
        }

        @Override
        public short getNodeType() {
            return NodeTypeEnums.ObjectTypeNode;
        }

        public Iterator<InternalFactHandle> iterator() {
            return store.iterator();
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
