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

package org.drools.ruleunit.datasources;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Tuple;
import org.drools.ruleunit.RuleUnit;

import static org.drools.core.common.DefaultFactHandle.determineIdentityHashCode;

public class DataSourceFactHandle implements InternalFactHandle {

    private final InternalDataSource<?> dataSource;
    private Object object;

    final Map<RuleUnit.Identity, InternalFactHandle> childHandles = new HashMap<>();

    private final long id;
    private long recency;

    private final int identityHashCode;

    private boolean negated = false;

    DataSourceFactHandle( InternalDataSource<?> dataSource, long id, long recency, Object object ) {
        this.dataSource = dataSource;
        this.id = id;
        this.recency = recency;
        this.object = object;
        identityHashCode = determineIdentityHashCode( object );
    }

    InternalFactHandle createFactHandleFor( WorkingMemoryEntryPoint ep, ObjectTypeConf conf) {
        InternalFactHandle fh = ep.getHandleFactory().newFactHandle( id, object, recency, conf, ep.getInternalWorkingMemory(), ep );
        fh.setNegated( negated );
        fh.setParentHandle( this );
        return fh;
    }

    public InternalDataSource<?> getDataSource() {
        return dataSource;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public boolean isNegated() {
        return negated;
    }

    @Override
    public void setNegated( boolean negated ) {
        this.negated = negated;
    }

    @Override
    public int getIdentityHashCode() {
        return identityHashCode;
    }

    @Override
    public long getRecency() {
        return recency;
    }

    @Override
    public void setRecency( long recency ) {
        this.recency = recency;
    }

    @Override
    public String getObjectClassName() {
        return object.getClass().getName();
    }

    @Override
    public void setObject( Object object ) {
        this.object = object;
    }

    @Override
    public void setEqualityKey( EqualityKey key ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.setEqualityKey -> TODO" );

    }

    @Override
    public EqualityKey getEqualityKey() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getEqualityKey -> TODO" );

    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.invalidate -> TODO" );

    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isValid -> TODO" );

    }

    @Override
    public int getObjectHashCode() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getObjectHashCode -> TODO" );

    }

    @Override
    public boolean isDisconnected() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isDisconnected -> TODO" );

    }

    @Override
    public boolean isEvent() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isEvent -> TODO" );

    }

    @Override
    public boolean isTraitOrTraitable() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isTraitOrTraitable -> TODO" );

    }

    @Override
    public boolean isTraitable() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isTraitable -> TODO" );

    }

    @Override
    public boolean isTraiting() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isTraiting -> TODO" );

    }

    @Override
    public TraitTypeEnum getTraitType() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getTraitType -> TODO" );

    }

    @Override
    public RightTuple getFirstRightTuple() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getFirstRightTuple -> TODO" );

    }

    @Override
    public LeftTuple getFirstLeftTuple() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getFirstLeftTuple -> TODO" );

    }

    @Override
    public EntryPointId getEntryPointId() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getEntryPoint -> TODO" );

    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint( InternalWorkingMemory wm ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getEntryPoint -> TODO" );

    }

    @Override
    public InternalFactHandle clone() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.clone -> TODO" );

    }

    @Override
    public String toExternalForm() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.toExternalForm -> TODO" );

    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.disconnect -> TODO" );

    }

    @Override
    public void addFirstLeftTuple( LeftTuple leftTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.addFirstLeftTuple -> TODO" );

    }

    @Override
    public void addLastLeftTuple( LeftTuple leftTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.addLastLeftTuple -> TODO" );

    }

    @Override
    public void removeLeftTuple( LeftTuple leftTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.removeLeftTuple -> TODO" );

    }

    @Override
    public void clearLeftTuples() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.clearLeftTuples -> TODO" );

    }

    @Override
    public void clearRightTuples() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.clearRightTuples -> TODO" );

    }

    @Override
    public void addFirstRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.addFirstRightTuple -> TODO" );

    }

    @Override
    public void addLastRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.addLastRightTuple -> TODO" );

    }

    @Override
    public void removeRightTuple( RightTuple rightTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.removeRightTuple -> TODO" );

    }

    @Override
    public void addTupleInPosition( Tuple tuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.addTupleInPosition -> TODO" );

    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.as -> TODO" );

    }

    @Override
    public boolean isExpired() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isExpired -> TODO" );

    }

    @Override
    public boolean isPendingRemoveFromStore() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.isPendingRemoveFromStore -> TODO" );

    }

    @Override
    public void forEachRightTuple( Consumer<RightTuple> rightTupleConsumer ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.forEachRightTuple -> TODO" );

    }

    @Override
    public void forEachLeftTuple( Consumer<LeftTuple> leftTupleConsumer ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.forEachLeftTuple -> TODO" );

    }

    @Override
    public RightTuple findFirstRightTuple( Predicate<RightTuple> rightTuplePredicate ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.findFirstRightTuple -> TODO" );

    }

    @Override
    public LeftTuple findFirstLeftTuple( Predicate<LeftTuple> lefttTuplePredicate ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.findFirstLeftTuple -> TODO" );

    }

    @Override
    public void setFirstLeftTuple( LeftTuple firstLeftTuple ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.setFirstLeftTuple -> TODO" );

    }

    @Override
    public LinkedTuples detachLinkedTuples() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.detachLinkedTuples -> TODO" );

    }

    @Override
    public LinkedTuples detachLinkedTuplesForPartition( int i ) {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.detachLinkedTuplesForPartition -> TODO" );

    }

    @Override
    public LinkedTuples getLinkedTuples() {
        throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getLinkedTuples -> TODO" );

    }
}

