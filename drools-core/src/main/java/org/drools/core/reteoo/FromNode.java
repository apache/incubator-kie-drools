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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.From;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.TupleList;

import static org.drools.core.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getAccessibleProperties;
import static org.drools.core.reteoo.PropertySpecificUtil.isPropertyReactive;
import static org.drools.core.util.ClassUtils.areNullSafeEquals;

public class FromNode<T extends FromNode.FromMemory> extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory<T> {
    private static final long          serialVersionUID = 510l;

    protected DataProvider               dataProvider;
    protected AlphaNodeFieldConstraint[] alphaConstraints;
    protected BetaConstraints            betaConstraints;

    protected LeftTupleSinkNode          previousTupleSinkNode;
    protected LeftTupleSinkNode          nextTupleSinkNode;
    
    protected From                       from;
    protected Class<?>                   resultClass;

    protected boolean                    tupleMemoryEnabled;

    protected transient ObjectTypeConf   objectTypeConf;

    public FromNode() {
    }

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final LeftTupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder,
                    final boolean tupleMemoryEnabled,
                    final BuildContext context,
                    final From from) {
        super(id, context);
        this.dataProvider = dataProvider;
        setLeftTupleSource(tupleSource);
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.betaConstraints.init(context, getType());
        this.tupleMemoryEnabled = tupleMemoryEnabled;
        this.from = from;
        resultClass = this.from.getResultClass();

        initMasks(context, tupleSource);

        hashcode = calculateHashCode();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        dataProvider = (DataProvider) in.readObject();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        from = (From) in.readObject();
        resultClass = from.getResultClass();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dataProvider );
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( from );
    }

    private int calculateHashCode() {
        int hash = ( 23 * leftInput.hashCode() ) + ( 29 * dataProvider.hashCode() );
        if (from.getResultPattern() != null) {
            hash += 31 * from.getResultPattern().hashCode();
        }
        if (alphaConstraints != null) {
            hash += 37 * Arrays.hashCode( alphaConstraints );
        }
        if (betaConstraints != null) {
            hash += 41 * betaConstraints.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals( Object object ) {
        if (this == object) {
            return true;
        }

        if (object == null || !(object instanceof FromNode ) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        FromNode other = (FromNode) object;

        return this.leftInput.getId() == other.leftInput.getId() &&
               dataProvider.equals( other.dataProvider ) &&
               areNullSafeEquals(from.getResultPattern(), other.from.getResultPattern() ) &&
               Arrays.equals( alphaConstraints, other.alphaConstraints ) &&
               betaConstraints.equals( other.betaConstraints );
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public AlphaNodeFieldConstraint[] getAlphaConstraints() {
        return alphaConstraints;
    }

    public BetaConstraints getBetaConstraints() {
        return betaConstraints;
    }

    @Override
    protected void initDeclaredMask(BuildContext context,
                                    LeftTupleSource leftInput) {
        super.initDeclaredMask(context, leftInput);

        if ( leftDeclaredMask.isAllSet() ) {
            return;
        }

        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            leftDeclaredMask = AllSetBitMask.get();
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[1];
        if ( pattern == null ) {
            return;
        }

        ObjectType objectType = pattern.getObjectType();

        if ( objectType instanceof ClassObjectType ) {
            Class objectClass = (( ClassWireable ) objectType).getClassType();
            // if pattern is null (e.g. for eval or query nodes) we cannot calculate the mask, so we set it all
            if ( isPropertyReactive( context, objectClass ) ) {
                Collection<String> leftListenedProperties = pattern.getListenedProperties();
                List<String> accessibleProperties = getAccessibleProperties( context.getKnowledgeBase(), objectClass );
                leftDeclaredMask = leftDeclaredMask.setAll( calculatePositiveMask( objectClass, leftListenedProperties, accessibleProperties ) );
                leftNegativeMask = leftNegativeMask.setAll( calculateNegativeMask( objectClass, leftListenedProperties, accessibleProperties ) );
            }
        }
    }

    @Override
    protected Pattern getLeftInputPattern( BuildContext context ) {
        return context.getLastBuiltPatterns()[0];
    }

    @Override
    protected BitMask setNodeConstraintsPropertyReactiveMask( BitMask mask, Class objectClass, List<String> accessibleProperties) {
        for (int i = 0; i < alphaConstraints.length; i++) {
            mask = mask.setAll(alphaConstraints[i].getListenedPropertyMask(objectClass, accessibleProperties));
        }
        return mask;
    }

    public Class< ? > getResultClass() {
        return resultClass;
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    @SuppressWarnings("unchecked")
    public RightTuple createRightTuple( final LeftTuple leftTuple,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory,
                                        final Object object ) {
        return new RightTupleImpl( createFactHandle( leftTuple, context, workingMemory, object ) );
    }

    public InternalFactHandle createFactHandle( Tuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory, Object object ) {
        if ( objectTypeConf == null ) {
            // use default entry point and object class. Notice that at this point object is assignable to resultClass
            objectTypeConf = new ClassObjectTypeConf( workingMemory.getEntryPoint(), resultClass, workingMemory.getKnowledgeBase() );
        }

        return workingMemory.getFactHandleFactory().newFactHandle(object,
                                                                  objectTypeConf,
                                                                  workingMemory,
                                                                  null );
    }


    public void addToCreatedHandlesMap(final Map<Object, RightTuple> matches,
                                       final RightTuple rightTuple) {
        if ( rightTuple.getFactHandle().isValid() ) {
            Object object = rightTuple.getFactHandle().getObject();
            // keeping a list of matches
            RightTuple existingMatch = matches.get( object );
            if ( existingMatch != null ) {
                // this is for the obscene case where two or more objects returned by "from"
                // have the same hash code and evaluate equals() to true, so we need to preserve
                // all of them to avoid leaks
                rightTuple.setNext( existingMatch );
            }
            matches.put( object,
                         rightTuple );
        }
    }


    public T createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        BetaMemory beta = new BetaMemory( new TupleList(),
                                          null,
                                          this.betaConstraints.createContext(),
                                          NodeTypeEnums.FromNode );
        return (T) new FromMemory( beta,
                                   this.dataProvider );
    }
   

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        FromNodeLeftTuple peer = new FromNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }    

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.FromNode;
    } 

    public static class FromMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Serializable,
        SegmentNodeMemory {
        private static final long serialVersionUID = 510l;

        private DataProvider      dataProvider;

        private final BetaMemory         betaMemory;
        public Object                    providerContext;

        public FromMemory(BetaMemory betaMemory,
                          DataProvider dataProvider) {
            this.betaMemory = betaMemory;
            this.dataProvider = dataProvider;
            this.providerContext = dataProvider.createContext();
        }

        public short getNodeType() {
            return NodeTypeEnums.FromNode;
        }

        public SegmentMemory getSegmentMemory() {
            return betaMemory.getSegmentMemory();
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            betaMemory.setSegmentMemory(segmentMemory);
        }

        public BetaMemory getBetaMemory() {
            return betaMemory;
        }

        public void reset() {
            this.betaMemory.reset();
            this.providerContext = dataProvider.createContext();
        }

        @Override
        public long getNodePosMaskBit() {
            return betaMemory.getNodePosMaskBit();
        }

        @Override
        public void setNodePosMaskBit( long segmentPos ) {
            betaMemory.setNodePosMaskBit( segmentPos );
        }

        @Override
        public void setNodeDirtyWithoutNotify() {
            betaMemory.setNodeDirtyWithoutNotify();
        }

        @Override
        public void setNodeCleanWithoutNotify() {
            betaMemory.setNodeCleanWithoutNotify();
        }
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new FromNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, sink, pctx,
                                     leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    public void attach( BuildContext context ) {
        this.leftInput.addTupleSink( this, context );
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {

        if ( !this.isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            return true;
        }
        return false;
    }

}
