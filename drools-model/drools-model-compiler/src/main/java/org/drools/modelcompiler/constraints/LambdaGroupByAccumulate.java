/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.BaseAccumulation;
import org.drools.core.reteoo.AccumulateNode.GroupByContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.core.util.index.TupleList;
import org.drools.model.functions.FunctionN;


public class LambdaGroupByAccumulate extends Accumulate {

    private Accumulate innerAccumulate;
    private Declaration[] groupingDeclarations;
    private FunctionN groupingFunction;
    private boolean propagateAll;

    public LambdaGroupByAccumulate() { }

    public LambdaGroupByAccumulate( Accumulate innerAccumulate, Declaration[] groupingDeclarations, FunctionN groupingFunction, boolean propagateAll ) {
        super(innerAccumulate.getSource(), innerAccumulate.getRequiredDeclarations());
        this.innerAccumulate = innerAccumulate;
        this.groupingDeclarations = groupingDeclarations;
        this.groupingFunction = groupingFunction;
        this.propagateAll = propagateAll;
    }

    private Object getKey( Tuple tuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object[] args = new Object[groupingDeclarations.length];
        for (int i = 0; i < groupingDeclarations.length; i++) {
            Declaration declaration = groupingDeclarations[i];
            Object object = tuple != null && declaration.getOffset() < tuple.size() ? tuple.getObject(declaration.getOffset()) : handle.getObject();
            args[i] = declaration.getValue( workingMemory.getInternalWorkingMemory(), object );
        }
        return groupingFunction.apply( args );
    }

    @Override
    public void readExternal( ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        this.innerAccumulate = (Accumulate) in.readObject();
        this.groupingDeclarations = (Declaration[]) in.readObject();
        this.groupingFunction = (FunctionN) in.readObject();
        this.propagateAll = in.readBoolean();
    }

    @Override
    public void writeExternal( ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(innerAccumulate);
        out.writeObject(groupingDeclarations);
        out.writeObject(groupingFunction);
        out.writeBoolean(propagateAll);
    }

    @Override
    public Accumulator[] getAccumulators() {
        return innerAccumulate.getAccumulators();
    }

    @Override
    public Object createFunctionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init( Object workingMemoryContext, Object context,
                      Tuple leftTuple, WorkingMemory workingMemory ) {
        // do nothing here, it's done when the group is first created
    }

    @Override
    public Object accumulate( Object workingMemoryContext, Object context,
                              Tuple leftTuple, InternalFactHandle handle, WorkingMemory wm ) {
        GroupByContext groupByContext = ( GroupByContext ) context;
        Object key = getKey(leftTuple, handle, wm);
        if (key==null) {
            throw new IllegalStateException("Unable to find group for: " + leftTuple + " : " + handle);
        }

        TupleList<AccumulateContextEntry> tupleList = groupByContext.getGroup(workingMemoryContext, innerAccumulate,
                                                                              leftTuple, handle, key, wm);
        groupByContext.moveToPropagateTupleList(tupleList);

        Object value = innerAccumulate.accumulate( workingMemoryContext, tupleList.getContext(),
                                                   leftTuple, handle, wm );

        groupByContext.setLastTupleList(tupleList);

        return value;
    }

    @Override
    public void reverse(Object workingMemoryContext, Object context, Tuple leftTuple, InternalFactHandle handle,
                        RightTuple rightParent, LeftTuple match, WorkingMemory workingMemory) {
        TupleList<AccumulateContextEntry> memory = match.getMemory();
        AccumulateContextEntry entry = memory.getContext();
        innerAccumulate.reverse(workingMemoryContext, entry, leftTuple, handle, rightParent, match, workingMemory);

        GroupByContext groupByContext = ( GroupByContext ) context;
        groupByContext.moveToPropagateTupleList(match.getMemory());

        memory.remove(match);
        if ( memory.isEmpty()) {
            groupByContext.removeGroup(entry.getKey());
        }
    }

    @Override
    public Object getResult( Object workingMemoryContext, Object context, Tuple leftTuple, WorkingMemory workingMemory ) {
        AccumulateContextEntry entry = (AccumulateContextEntry) context;
        if (!entry.getTupleList().isEmpty()) {
            return innerAccumulate.getResult(workingMemoryContext, context, leftTuple, workingMemory);
        } else {
            return null;
        }
    }

    @Override
    public boolean supportsReverse() {
        return innerAccumulate.supportsReverse();
    }

    @Override
    public Accumulate clone() {
        return new LambdaGroupByAccumulate( innerAccumulate.clone(), groupingDeclarations, groupingFunction, propagateAll );
    }

    @Override
    public Object createWorkingMemoryContext() {
        return innerAccumulate.createWorkingMemoryContext();
    }

    @Override
    public boolean isMultiFunction() {
        return innerAccumulate.isMultiFunction();
    }

    @Override
    public void replaceAccumulatorDeclaration( Declaration declaration, Declaration resolved ) {
        innerAccumulate.replaceAccumulatorDeclaration(declaration, resolved);
    }

    @Override
    public boolean isGroupBy() {
        return true;
    }

//    private static class GroupByContext implements Serializable {
//        private RightTuple lastRightTuple;
//
//        private final Map<Object, RightTuple> contextsByGroup = new HashMap<>();
////        private final Map<Object, Object> changedGroups = new HashMap<>();
//        //private final Map<Long, GroupInfo> reverseSupport;
//
//        private GroupByContext(boolean supportsReverse) {
//            //reverseSupport = supportsReverse ? new HashMap<>() : null;
//        }
//
//        public RightTuple getLastRightTuple() {
//            return lastRightTuple;
//        }
//
//        public void setLastRightTuple(RightTuple lastRightTuple) {
//            this.lastRightTuple = lastRightTuple;
//        }
//
//        RightTuple loadContext(Accumulate innerAccumulate, InternalFactHandle handle, Object key) {
//            RightTuple rightTuple = contextsByGroup.computeIfAbsent( key, k -> {
//                HolderFactHandle holder = new HolderFactHandle(innerAccumulate.createFunctionContext());
//                return new RightTupleImpl(holder);
//            });
//
////            if (reverseSupport != null) {
////                reverseSupport.put( handle.getId(), new GroupInfo( key, groupContext ) );
////            }
//
////            changedGroups.put( key, groupContext );
//            return rightTuple;
//        }
//
//        Object loadContextForReverse(InternalFactHandle handle) {
////            GroupInfo groupInfo = reverseSupport.remove( handle.getId() );
////            changedGroups.put( groupInfo.key, groupInfo.context );
////            return groupInfo.context;
//            return null;
//        }
//
//        public void init() {
////            contextsByGroup.clear();
////            changedGroups.clear();
////            if (reverseSupport != null) {
////                reverseSupport.clear();
////            }
//        }
//
//        public List<Object[]> result( Accumulate innerAccumulate, Object wmCtx, Tuple leftTuple, WorkingMemory wm ) {
////            List<Object[]> results = new ArrayList<>( changedGroups.size() );
////            for (Map.Entry<Object, Object> entry : changedGroups.entrySet()) {
////                results.add( new Object[]{ entry.getKey(), isEmptyContext(entry.getValue()) ? null : innerAccumulate.getResult( wmCtx, entry.getValue(), leftTuple, wm ) } );
////            }
////            changedGroups.clear();
////            return results;
//            return null;
//        }
//
//        private boolean isEmptyContext(Object ctx) {
//            if (ctx instanceof LambdaAccumulator.LambdaAccContext) {
//                return (( LambdaAccumulator.LambdaAccContext ) ctx).isEmpty();
//            }
//            if (ctx instanceof Object[]) {
//                return isEmptyContext( (( Object[] ) ctx)[0] );
//            }
//            return false;
//        }
//    }
//
//    private static class GroupInfo {
//        private final Object key;
//        private final Object context;
//
//        private GroupInfo( Object key, Object context ) {
//            this.key = key;
//            this.context = context;
//        }
//    }


    public static class HolderFactHandle implements InternalFactHandle {
        private Object object;

        public HolderFactHandle(Object object) {
            this.object = object;
        }

        @Override public long getId() {
            throw new UnsupportedOperationException();
        }

        @Override public long getRecency() {
            throw new UnsupportedOperationException();
        }

        @Override public Object getObject() {
            return object;
        }

        @Override public String getObjectClassName() {
            throw new UnsupportedOperationException();
        }

        @Override public void setObject(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override public void setEqualityKey(EqualityKey key) {
            throw new UnsupportedOperationException();
        }

        @Override public EqualityKey getEqualityKey() {
            throw new UnsupportedOperationException();
        }

        @Override public void setRecency(long recency) {
            throw new UnsupportedOperationException();
        }

        @Override public void invalidate() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isValid() {
            throw new UnsupportedOperationException();
        }

        @Override public int getIdentityHashCode() {
            throw new UnsupportedOperationException();
        }

        @Override public int getObjectHashCode() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isDisconnected() {
            return false;
        }

        @Override public boolean isEvent() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isTraitOrTraitable() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isTraitable() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isTraiting() {
            throw new UnsupportedOperationException();
        }

        @Override public TraitTypeEnum getTraitType() {
            throw new UnsupportedOperationException();
        }

        @Override public RightTuple getFirstRightTuple() {
            throw new UnsupportedOperationException();
        }

        @Override public LeftTuple getFirstLeftTuple() {
            throw new UnsupportedOperationException();
        }

        @Override public EntryPointId getEntryPointId() {
            throw new UnsupportedOperationException();
        }

        @Override public WorkingMemoryEntryPoint getEntryPoint(InternalWorkingMemory wm) {
            throw new UnsupportedOperationException();
        }

        @Override public InternalFactHandle clone() {
            throw new UnsupportedOperationException();
        }

        @Override public String toExternalForm() {
            throw new UnsupportedOperationException();
        }

        @Override public void disconnect() {
            throw new UnsupportedOperationException();
        }

        @Override public void addFirstLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void addLastLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void removeLeftTuple(LeftTuple leftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void clearLeftTuples() {
            throw new UnsupportedOperationException();
        }

        @Override public void clearRightTuples() {
            throw new UnsupportedOperationException();
        }

        @Override public void addFirstRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void addLastRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void removeRightTuple(RightTuple rightTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public void addTupleInPosition(Tuple tuple) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isNegated() {
            throw new UnsupportedOperationException();
        }

        @Override public void setNegated(boolean negated) {
            throw new UnsupportedOperationException();
        }

        @Override public <K> K as(Class<K> klass) throws ClassCastException {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isExpired() {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isPendingRemoveFromStore() {
            throw new UnsupportedOperationException();
        }

        @Override public void forEachRightTuple(Consumer<RightTuple> rightTupleConsumer) {
            throw new UnsupportedOperationException();
        }

        @Override public void forEachLeftTuple(Consumer<LeftTuple> leftTupleConsumer) {
            throw new UnsupportedOperationException();
        }

        @Override public RightTuple findFirstRightTuple(Predicate<RightTuple> rightTuplePredicate) {
            throw new UnsupportedOperationException();
        }

        @Override public LeftTuple findFirstLeftTuple(Predicate<LeftTuple> lefttTuplePredicate) {
            throw new UnsupportedOperationException();
        }

        @Override public void setFirstLeftTuple(LeftTuple firstLeftTuple) {
            throw new UnsupportedOperationException();
        }

        @Override public LinkedTuples detachLinkedTuples() {
            throw new UnsupportedOperationException();
        }

        @Override public LinkedTuples detachLinkedTuplesForPartition(int i) {
            throw new UnsupportedOperationException();
        }

        @Override public LinkedTuples getLinkedTuples() {
            throw new UnsupportedOperationException();
        }
    }



}
