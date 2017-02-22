/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationEntry.AbstractPropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;

import static java.util.Arrays.asList;
import static org.drools.core.common.DefaultFactHandle.determineIdentityHashCode;
import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getSettableProperties;

public class CursoredDataSource<T> implements InternalDataSource<T> {

    private InternalWorkingMemory workingMemory;

    private ObjectStore objectStore = new ClassAwareObjectStore( AssertBehaviour.IDENTITY, null );

    private Map<RuleUnit.Identity, PropagationList> propagationsMap = new HashMap<>();

    private RuleUnit.Identity currentUnit;
    private EntryPoint currentEntryPoint;

    private List<T> inserted;

    public CursoredDataSource() { }

    public CursoredDataSource( InternalWorkingMemory workingMemory ) {
        this.workingMemory = workingMemory;
    }

    public void setWorkingMemory( InternalWorkingMemory workingMemory ) {
        this.workingMemory = workingMemory;
        if (inserted != null) {
            inserted.forEach( this::insertIntoWm );
            inserted = null;
        }
    }

    @Override
    public FactHandle insert( T object ) {
        if (workingMemory != null) {
            return insertIntoWm( object );
        }

        if (inserted == null) {
            inserted = new ArrayList<>();
        }
        inserted.add(object);
        return null;
    }

    private FactHandle insertIntoWm( T object ) {
        FactHandleFactory fhFactory = workingMemory.getFactHandleFactory();
        DataSourceFactHandle factHandle = new DataSourceFactHandle( this, fhFactory.getNextId(), fhFactory.getNextRecency(), object );
        objectStore.addHandle( factHandle, object );
        propagate( () -> new Insert( factHandle ) );
        return factHandle;
    }

    @Override
    public void update(FactHandle handle, T object, String... modifiedProperties) {
        BitMask mask = modifiedProperties == null || modifiedProperties.length == 0 ?
                       allSetButTraitBitMask() :
                       calculatePositiveMask(asList(modifiedProperties), getSettableProperties(workingMemory.getKnowledgeBase(), object.getClass()));
        internalUpdate((DataSourceFactHandle) handle, object, mask, Object.class, null);
    }

    @Override
    public void update(FactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation) {
        DataSourceFactHandle dataSourceFactHandle = ( (DataSourceFactHandle) ( (InternalFactHandle) fh ).getParentHandle() );
        internalUpdate( dataSourceFactHandle, obj, mask, modifiedClass, activation );
    }

    private void internalUpdate( DataSourceFactHandle dataSourceFactHandle, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation ) {
        propagate( () -> new Update( dataSourceFactHandle, obj, mask, modifiedClass, activation ) );
    }

    private void propagate( Supplier<AbstractDataSourcePropagation> s ) {
        propagationsMap.forEach( (ruId, list) -> {
            if (ruId.equals( currentUnit )) {
                workingMemory.getPropagationList().addEntry( s.get().setEntryPoint( currentEntryPoint ) );
            } else {
                list.addEntry( s.get() );
            }
        } );
    }

    @Override
    public void delete(FactHandle fh) {
        DataSourceFactHandle dsFh = (DataSourceFactHandle) fh;
        objectStore.removeHandle( dsFh );
        propagate( () -> new Delete( dsFh, null ) );
    }

    @Override
    public void bind(RuleUnit unit, EntryPoint ep) {
        PropagationList propagationList = propagationsMap.get( unit.getUnitIdentity() );
        if (propagationList != null) {
            flush( ep, propagationList.takeAll() );
        } else {
            Iterator<InternalFactHandle> fhs = objectStore.iterateFactHandles();
            while (fhs.hasNext()) {
                new Insert( (DataSourceFactHandle) fhs.next() ).execute( ep );
            }
            propagationsMap.put( unit.getUnitIdentity(), new SynchronizedPropagationList( ((WorkingMemoryEntryPoint)ep).getInternalWorkingMemory()) );
        }
        currentUnit = unit.getUnitIdentity();
        currentEntryPoint = ep;
    }

    private void flush( EntryPoint ep, PropagationEntry currentHead ) {
        for (PropagationEntry entry = currentHead; entry != null; entry = entry.getNext()) {
            ((AbstractDataSourcePropagation)entry).execute(ep);
        }
    }

    @Override
    public void unbind(RuleUnit unit) {
        currentUnit = null;
        currentEntryPoint = null;
    }

    @Override
    public Iterator<T> iterator() {
        return inserted != null ? inserted.iterator() : (Iterator<T>) objectStore.iterateObjects();
    }

    static abstract class AbstractDataSourcePropagation extends AbstractPropagationEntry {

        private EntryPoint ep;

        public AbstractDataSourcePropagation setEntryPoint( EntryPoint ep ) {
            this.ep = ep;
            return this;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            execute( ep );
        }

        public abstract void execute( EntryPoint ep );
    }

    static class Insert extends AbstractDataSourcePropagation {

        private final DataSourceFactHandle dsFactHandle;

        Insert( DataSourceFactHandle factHandle ) {
            this.dsFactHandle = factHandle;
        }

        @Override
        public void execute( EntryPoint entryPoint ) {
            WorkingMemoryEntryPoint ep = (WorkingMemoryEntryPoint) entryPoint;
            ObjectTypeConf typeConf = ep.getObjectTypeConfigurationRegistry()
                                        .getObjectTypeConf( ep.getEntryPoint(), dsFactHandle.getObject() );

            InternalFactHandle handleForEp = dsFactHandle.createFactHandleFor( ep, typeConf );
            RuleUnit ruleUnit = ep.getInternalWorkingMemory().getRuleUnitExecutor().getCurrentRuleUnit();
            dsFactHandle.childHandles.put( ruleUnit.getUnitIdentity(), handleForEp );

            PropagationContextFactory pctxFactory = ( (InternalWorkingMemoryEntryPoint) ep ).getPctxFactory();
            PropagationContext context = pctxFactory.createPropagationContext( ep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                               PropagationContext.Type.INSERTION,
                                                                               null,
                                                                               null,
                                                                               handleForEp,
                                                                               ep.getEntryPoint() );
            for ( ObjectTypeNode otn : typeConf.getObjectTypeNodes() ) {
                otn.propagateAssert( handleForEp, context, ep.getInternalWorkingMemory() );
            }
        }
    }

    static class Update extends AbstractDataSourcePropagation {

        private final DataSourceFactHandle dsFactHandle;
        private final Object object;
        private final BitMask mask;
        private final Class<?> modifiedClass;
        private final Activation activation;

        Update( DataSourceFactHandle factHandle, Object object, BitMask mask, Class<?> modifiedClass, Activation activation ) {
            this.dsFactHandle = factHandle;
            this.object = object;
            this.mask = mask;
            this.modifiedClass = modifiedClass;
            this.activation = activation;
        }

        @Override
        public void execute( EntryPoint entryPoint ) {
            WorkingMemoryEntryPoint ep = (WorkingMemoryEntryPoint) entryPoint;
            ObjectTypeConf typeConf = ep.getObjectTypeConfigurationRegistry()
                                        .getObjectTypeConf( ep.getEntryPoint(), object );

            RuleUnit ruleUnit = ep.getInternalWorkingMemory().getRuleUnitExecutor().getCurrentRuleUnit();
            InternalFactHandle handle = dsFactHandle.childHandles.get(ruleUnit.getUnitIdentity() );

            PropagationContextFactory pctxFactory = ( (InternalWorkingMemoryEntryPoint) ep ).getPctxFactory();
            PropagationContext context = pctxFactory.createPropagationContext( ep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                               PropagationContext.Type.MODIFICATION,
                                                                               activation == null ? null : activation.getRule(),
                                                                               activation == null ? null : activation.getTuple(),
                                                                               handle,
                                                                               ep.getEntryPoint(),
                                                                               mask,
                                                                               modifiedClass,
                                                                               null);

            EntryPointNode.propagateModify( handle, context, typeConf, ep.getInternalWorkingMemory() );
        }
    }

    static class Delete extends AbstractDataSourcePropagation {

        private final DataSourceFactHandle dsFactHandle;
        private final Activation activation;

        Delete( DataSourceFactHandle factHandle, Activation activation ) {
            this.dsFactHandle = factHandle;
            this.activation = activation;
        }

        @Override
        public void execute( EntryPoint entryPoint ) {
            WorkingMemoryEntryPoint ep = (WorkingMemoryEntryPoint) entryPoint;
            ObjectTypeConf typeConf = ep.getObjectTypeConfigurationRegistry()
                                        .getObjectTypeConf( ep.getEntryPoint(), dsFactHandle.getObject() );

            RuleUnit ruleUnit = ep.getInternalWorkingMemory().getRuleUnitExecutor().getCurrentRuleUnit();
            InternalFactHandle handle = dsFactHandle.childHandles.get(ruleUnit.getUnitIdentity() );

            PropagationContextFactory pctxFactory = ( (InternalWorkingMemoryEntryPoint) ep ).getPctxFactory();
            PropagationContext context = pctxFactory.createPropagationContext( ep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                               PropagationContext.Type.DELETION,
                                                                               activation == null ? null : activation.getRule(),
                                                                               activation == null ? null : activation.getTuple(),
                                                                               handle,
                                                                               ep.getEntryPoint() );

            ep.getEntryPointNode().propagateRetract(handle, context, typeConf, ep.getInternalWorkingMemory());
        }
    }

    public static class DataSourceFactHandle implements InternalFactHandle {

        private final InternalDataSource<?> dataSource;
        private Object object;

        private final Map<RuleUnit.Identity, InternalFactHandle> childHandles = new HashMap<>();

        private final int id;
        private long recency;

        private final int identityHashCode;

        private boolean negated = false;

        DataSourceFactHandle( InternalDataSource<?> dataSource, int id, long recency, Object object ) {
            this.dataSource = dataSource;
            this.id = id;
            this.recency = recency;
            this.object = object;
            identityHashCode = determineIdentityHashCode( object );
        }

        InternalFactHandle createFactHandleFor(WorkingMemoryEntryPoint ep, ObjectTypeConf conf) {
            InternalFactHandle fh = ep.getHandleFactory().newFactHandle( id, object, recency, conf, ep.getInternalWorkingMemory(), ep );
            fh.setNegated( negated );
            fh.setParentHandle( this );
            return fh;
        }

        @Override
        public InternalDataSource<?> getDataSource() {
            return dataSource;
        }

        @Override
        public int getId() {
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
        public WorkingMemoryEntryPoint getEntryPoint() {
            throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.getEntryPoint -> TODO" );

        }

        @Override
        public void setEntryPoint( WorkingMemoryEntryPoint ep ) {
            throw new UnsupportedOperationException( "org.drools.core.datasources.CursoredDataSource.DataSourceFactHandle.setEntryPoint -> TODO" );

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
}
