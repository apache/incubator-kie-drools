/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.rule.EntryPointId;
import org.kie.api.runtime.rule.RuleUnit;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicEntryPoint extends NamedEntryPoint {

    private final Map<RuleUnit, ObjectTypeConfigurationRegistry> boundUnits = new WeakHashMap<>();

    public DynamicEntryPoint( EntryPointId entryPoint, StatefulKnowledgeSessionImpl wm ) {
        this( entryPoint, wm, new ReentrantLock() );
    }

    private DynamicEntryPoint( EntryPointId entryPoint, StatefulKnowledgeSessionImpl wm, ReentrantLock lock ) {
        super( entryPoint, wm, new ReteooFactHandleFactory(), lock, new ClassAwareObjectStore( RuleBaseConfiguration.AssertBehaviour.IDENTITY, lock ) );
    }

    public void bindRuleBase( InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
        this.pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.entryPointNode = this.kBase.getRete().getEntryPointNode( entryPoint );
        this.typeConfReg = new ObjectTypeConfigurationRegistry( this.kBase );
        boundUnits.clear();
        propagateAll();
    }

    private boolean isBoundToRuleBase() {
        return kBase != null;
    }

    private void propagateAll() {
        // TODO: when binding a different kbase check if it is possible to reuse the object store of the previous entry-point
        Iterator<InternalFactHandle> i = objectStore.iterateFactHandles();
        while (i.hasNext()) {
            propagateInsert( i.next() );
        }
    }

    private void propagateInsert( InternalFactHandle handle ) {
        ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPointNode.getEntryPoint(), handle.getObject() );

        PropagationContext pctx = pctxFactory.createPropagationContext( this.wm.getNextPropagationIdCounter(),
                                                                        PropagationContext.Type.INSERTION,
                                                                        null, null, handle, this.entryPoint );
        entryPointNode.assertObject( handle, pctx, typeConf, wm );
    }

    @Override
    public FactHandle insert( Object object, boolean dynamic, RuleImpl rule, Activation activation ) {
        if (isBoundToRuleBase()) {
            return super.insert( object, dynamic, rule, activation );
        }

        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        try {
            this.wm.startOperation();

            InternalFactHandle handle = null;

            try {
                this.lock.lock();
                // check if the object already exists in the WM
                handle = this.objectStore.getHandleForObject( object );

                // TMS not enabled for this object type
                if ( handle != null ) {
                    return handle;
                }
                handle = createHandle( object );

                this.objectStore.addHandle( handle, object );
            } finally {
                this.lock.unlock();
            }
            return handle;
        } finally {
            this.wm.endOperation();
        }
    }

    private InternalFactHandle createHandle(Object object) {
        return this.handleFactory.newFactHandle( object, null, this.wm, this );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, Activation activation, FactHandle.State fhState ) {
        if (isBoundToRuleBase()) {
            super.delete( factHandle, rule, activation, fhState );
        }

        try {
            this.lock.lock();
            this.wm.startOperation();

            InternalFactHandle handle = (InternalFactHandle) factHandle;

            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
            }

            if ( handle.getEntryPoint() != this ) {
                throw new IllegalArgumentException( "Invalid Entry Point. You updated the FactHandle on entry point '" + handle.getEntryPoint().getEntryPointId() + "' instead of '" + getEntryPointId() + "'" );
            }

            this.objectStore.removeHandle( handle );
        } finally {
            this.wm.endOperation();
            this.lock.unlock();
        }
    }
}
