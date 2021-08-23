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

package org.drools.ruleunit.datasources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationEntry.AbstractPropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;
import org.drools.ruleunit.RuleUnit;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getAccessibleProperties;

public class CursoredDataSource<T> implements InternalDataSource<T> {

    private InternalWorkingMemory workingMemory;

    private ObjectStore objectStore = new IdentityObjectStore();

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
    public FactHandle getFactHandleForObject(Object object) {
    	if (objectStore != null) {
    		return (FactHandle)objectStore.getHandleForObject(object);
    	}
    	return null;
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
                       calculatePositiveMask( object.getClass(), asList(modifiedProperties), getAccessibleProperties( workingMemory.getKnowledgeBase(), object.getClass() ) );
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

    public void delete(Object obj) {
        delete( objectStore.getHandleForObject( obj ) );
    }

    @Override
    public void bind(RuleUnit unit, WorkingMemoryEntryPoint ep) {
        setWorkingMemory( ep.getInternalWorkingMemory() );
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
                                        .getOrCreateObjectTypeConf( ep.getEntryPoint(), dsFactHandle.getObject() );

            InternalFactHandle handleForEp = dsFactHandle.createFactHandleFor( ep, typeConf );
            RuleUnit.Identity ruleUnitIdentity = (( RuleUnit ) ep.getRuleUnit()).getUnitIdentity();
            dsFactHandle.childHandles.put( ruleUnitIdentity, handleForEp );

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
                                        .getObjectTypeConf( object );

            RuleUnit.Identity ruleUnitIdentity = (( RuleUnit ) ep.getRuleUnit()).getUnitIdentity();
            InternalFactHandle handle = dsFactHandle.childHandles.get( ruleUnitIdentity );

            PropagationContextFactory pctxFactory = ( (InternalWorkingMemoryEntryPoint) ep ).getPctxFactory();
            PropagationContext context = pctxFactory.createPropagationContext( ep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                               PropagationContext.Type.MODIFICATION,
                                                                               activation == null ? null : activation.getRule(),
                                                                               activation == null ? null : activation.getTuple().getTupleSink(),
                                                                               handle,
                                                                               ep.getEntryPoint(),
                                                                               mask,
                                                                               modifiedClass,
                                                                               null);

            PropagationEntry.Update.execute( handle, context, typeConf, ep.getInternalWorkingMemory() );
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
                                        .getObjectTypeConf( dsFactHandle.getObject() );

            RuleUnit.Identity ruleUnitIdentity = (( RuleUnit ) ep.getRuleUnit()).getUnitIdentity();
            InternalFactHandle handle = dsFactHandle.childHandles.get( ruleUnitIdentity );

            PropagationContextFactory pctxFactory = ( (InternalWorkingMemoryEntryPoint) ep ).getPctxFactory();
            PropagationContext context = pctxFactory.createPropagationContext( ep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                               PropagationContext.Type.DELETION,
                                                                               activation == null ? null : activation.getRule(),
                                                                               activation == null ? null : activation.getTuple().getTupleSink(),
                                                                               handle,
                                                                               ep.getEntryPoint() );

            ep.getEntryPointNode().propagateRetract(handle, context, typeConf, ep.getInternalWorkingMemory());
        }
    }
}
