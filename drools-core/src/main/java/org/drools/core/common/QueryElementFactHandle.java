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

package org.drools.core.common;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Tuple;

@XmlAccessorType(XmlAccessType.NONE)
public class QueryElementFactHandle
    implements
    InternalFactHandle {
    private Object object;
    private long id;
    private int identityHashCode;
    private long recency;
    private boolean                 negated;

    protected QueryElementFactHandle() {}

    public QueryElementFactHandle(Object object, long id, long recency) {
        this( object, id, DefaultFactHandle.determineIdentityHashCode( object ), recency );
    }

    public QueryElementFactHandle(Object object, long id, int identityHashCode, long recency) {
        this.object = object;
        this.id = id;
        this.recency = recency;
        this.identityHashCode = identityHashCode;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public long getId() {
        return this.id;
    }

    public int getIdentityHashCode() {
        return this.identityHashCode;
    }

    public int getObjectHashCode() {
        return this.object.hashCode();
    }

    public long getRecency() {
        return this.recency;
    }

    public LeftTuple getLastLeftTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public Object getObject() {
        if ( this.object != null ) {
            return this.object;
        }
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public String getObjectClassName() {
        return this.object != null ? object.getClass().getName() : null;
    }

    public void setObject(Object object) {
        this.object = object;
    }    

    public EntryPointId getEntryPointId() {
        return null;
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint( InternalWorkingMemory wm ) {
        return null;
    }

    public EqualityKey getEqualityKey() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public RightTuple getRightTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void invalidate() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public boolean isEvent() {
        return false;
    }

    public boolean isTraitOrTraitable() {
        return false;
    }

    public boolean isTraitable() {
        return false;
    }

    public boolean isTraiting() {
        return false;
    }

    public TraitTypeEnum getTraitType() {
        return TraitTypeEnum.NON_TRAIT;
    }

    public boolean isValid() {
        return true;
    }

    public void setEntryPoint(WorkingMemoryEntryPoint ep ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setEqualityKey(EqualityKey key) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setFirstLeftTuple(LeftTuple leftTuple) {
    }

    @Override
    public LinkedTuples getLinkedTuples() {
        return null;
    }

    @Override
    public LinkedTuples detachLinkedTuples() {
        return null;
    }

    @Override
    public LinkedTuples detachLinkedTuplesForPartition(int i) {
        return null;
    }

    public void setLastLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void setRecency(long recency) {
        this.recency = recency;
    }

    public void setRightTuple(RightTuple rightTuple) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public InternalFactHandle clone() {
        return new QueryElementFactHandle( object, id, identityHashCode, recency );
    }

    public String toExternalForm() {
        return "QueryElementFactHandl: " + this.object;
    }
    
    @XmlAttribute(name="external-form")
    public String getExternalForm() {
        return toExternalForm();
    }

    
    public LeftTuple getFirstLeftTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    
    public RightTuple getFirstRightTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    
    public RightTuple getLastRightTuple() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    
    public String toTupleTree(int indent) {
        return null;
    }

    public boolean isDisconnected() {
        return true;
    }
    
    public String toString() {
        return "results: " + Arrays.asList( (Object[]) this.object ).toString();
    }

    public void disconnect() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void addFirstLeftTuple(LeftTuple leftTuple) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void addLastLeftTuple( LeftTuple leftTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void removeLeftTuple( LeftTuple leftTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void clearLeftTuples() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void clearRightTuples() {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void addFirstRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void addLastRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void addTupleInPosition( Tuple rightTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    public void removeRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not support this method" );
    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        throw new UnsupportedOperationException( "QueryElementFactHandle does not yet support this method" );
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public boolean isPendingRemoveFromStore() {
        return false;
    }

    public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) { }

    @Override
    public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) { }

    @Override
    public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
        return null;
    }

    @Override
    public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
        return null;
    }
}
