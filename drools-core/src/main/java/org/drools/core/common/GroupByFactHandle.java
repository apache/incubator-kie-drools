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

package org.drools.core.common;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Tuple;

public class GroupByFactHandle implements InternalFactHandle {
    private final InternalFactHandle handle;
    private final Object groupKey;

    public GroupByFactHandle( InternalFactHandle handle, Object groupKey ) {
        this.handle = handle;
        this.groupKey = groupKey;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    @Override
    public String toString() {
        return "GroupByFactHandle{" +
                "groupKey=" + groupKey +
                ", handle=" + handle +
                '}';
    }

    @Override
    public long getId() {
        return handle.getId();
    }

    @Override
    public long getRecency() {
        return handle.getRecency();
    }

    @Override
    public Object getObject() {
        return handle.getObject();
    }

    @Override
    public String getObjectClassName() {
        return handle.getObjectClassName();
    }

    @Override
    public void setObject( Object object ) {
        handle.setObject( object );
    }

    @Override
    public void setEqualityKey( EqualityKey key ) {
        handle.setEqualityKey( key );
    }

    @Override
    public EqualityKey getEqualityKey() {
        return handle.getEqualityKey();
    }

    @Override
    public void setRecency( long recency ) {
        handle.setRecency( recency );
    }

    @Override
    public void invalidate() {
        handle.invalidate();
    }

    @Override
    public boolean isValid() {
        return handle.isValid();
    }

    @Override
    public int getIdentityHashCode() {
        return handle.getIdentityHashCode();
    }

    @Override
    public int getObjectHashCode() {
        return handle.getObjectHashCode();
    }

    @Override
    public boolean isDisconnected() {
        return handle.isDisconnected();
    }

    @Override
    public boolean isEvent() {
        return handle.isEvent();
    }

    @Override
    public boolean isTraitOrTraitable() {
        return handle.isTraitOrTraitable();
    }

    @Override
    public boolean isTraitable() {
        return handle.isTraitable();
    }

    @Override
    public boolean isTraiting() {
        return handle.isTraiting();
    }

    @Override
    public TraitTypeEnum getTraitType() {
        return handle.getTraitType();
    }

    @Override
    public RightTuple getFirstRightTuple() {
        return handle.getFirstRightTuple();
    }

    @Override
    public LeftTuple getFirstLeftTuple() {
        return handle.getFirstLeftTuple();
    }

    @Override
    public InternalWorkingMemory getWorkingMemory() {
        return handle.getWorkingMemory();
    }

    @Override
    public EntryPointId getEntryPointId() {
        return handle.getEntryPointId();
    }

    @Override
    public String getEntryPointName() {
        return handle.getEntryPointName();
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint( InternalWorkingMemory wm ) {
        return handle.getEntryPoint( wm );
    }

    @Override
    public InternalFactHandle clone() {
        return handle.clone();
    }

    @Override
    public String toExternalForm() {
        return handle.toExternalForm();
    }

    @Override
    public void disconnect() {
        handle.disconnect();
    }

    @Override
    public void addFirstLeftTuple( LeftTuple leftTuple ) {
        handle.addFirstLeftTuple( leftTuple );
    }

    @Override
    public void addLastLeftTuple( LeftTuple leftTuple ) {
        handle.addLastLeftTuple( leftTuple );
    }

    @Override
    public void removeLeftTuple( LeftTuple leftTuple ) {
        handle.removeLeftTuple( leftTuple );
    }

    @Override
    public void clearLeftTuples() {
        handle.clearLeftTuples();
    }

    @Override
    public void clearRightTuples() {
        handle.clearRightTuples();
    }

    @Override
    public void addFirstRightTuple( RightTuple rightTuple ) {
        handle.addFirstRightTuple( rightTuple );
    }

    @Override
    public void addLastRightTuple( RightTuple rightTuple ) {
        handle.addLastRightTuple( rightTuple );
    }

    @Override
    public void removeRightTuple( RightTuple rightTuple ) {
        handle.removeRightTuple( rightTuple );
    }

    @Override
    public void addTupleInPosition( Tuple tuple ) {
        handle.addTupleInPosition( tuple );
    }

    @Override
    public boolean isNegated() {
        return handle.isNegated();
    }

    @Override
    public void setNegated( boolean negated ) {
        handle.setNegated( negated );
    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        return handle.as( klass );
    }

    @Override
    public boolean isExpired() {
        return handle.isExpired();
    }

    @Override
    public boolean isPendingRemoveFromStore() {
        return handle.isPendingRemoveFromStore();
    }

    @Override
    public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) {
        handle.forEachRightTuple( rightTupleConsumer );
    }

    @Override
    public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) {
        handle.forEachLeftTuple( leftTupleConsumer );
    }

    @Override
    public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
        return handle.findFirstRightTuple( rightTuplePredicate );
    }

    @Override
    public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
        return handle.findFirstLeftTuple( lefttTuplePredicate );
    }

    @Override
    public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
        handle.setFirstLeftTuple( firstLeftTuple );
    }

    @Override
    public LinkedTuples detachLinkedTuples() {
        return handle.detachLinkedTuples();
    }

    @Override
    public LinkedTuples detachLinkedTuplesForPartition( int i ) {
        return handle.detachLinkedTuplesForPartition( i );
    }

    @Override
    public LinkedTuples getLinkedTuples() {
        return handle.getLinkedTuples();
    }

    @Override
    public InternalFactHandle getParentHandle() {
        return handle.getParentHandle();
    }

    @Override
    public void setParentHandle( InternalFactHandle parentHandle ) {
        handle.setParentHandle( parentHandle );
    }
}