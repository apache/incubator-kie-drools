package org.drools.compiler.kie.builder.impl;

import java.util.Optional;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.reteoo.EntryPointNode;
import org.kie.api.runtime.rule.EntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KieBaseUpdater implements Runnable {

    protected final KieBaseUpdateContext ctx;

    public KieBaseUpdater( KieBaseUpdateContext ctx ) {
        this.ctx = ctx;
    }

    Logger log = LoggerFactory.getLogger(KieBaseUpdaterImpl.class );

    protected void clearInstancesOfModifiedClass( Class<?> cls ) {
        // remove all ObjectTypeNodes for the modified classes
        ClassObjectType objectType = new ClassObjectType( cls );
        for ( EntryPointNode epn : ctx.kBase.getRete().getEntryPointNodes().values() ) {
            epn.removeObjectType( objectType );
        }

        // remove all instance of the old class from the object stores
        for (InternalWorkingMemory wm : ctx.kBase.getWorkingMemories()) {
            for (EntryPoint ep : wm.getEntryPoints()) {
                InternalWorkingMemoryEntryPoint wmEp = (InternalWorkingMemoryEntryPoint) wm.getWorkingMemoryEntryPoint( ep.getEntryPointId() );
                ClassAwareObjectStore store = ( (ClassAwareObjectStore) wmEp.getObjectStore() );
                if ( store.clearClassStore( cls ) ) {
                    log.warn( "Class " + cls.getName() + " has been modified and therfore its old instances will no longer match" );
                }
            }
        }
    }

    public Optional<InternalKnowledgeBuilder> getKnowledgeBuilder() {
        return Optional.empty();
    }
}
