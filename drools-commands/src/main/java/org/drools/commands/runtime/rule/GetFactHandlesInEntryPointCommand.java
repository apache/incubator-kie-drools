package org.drools.commands.runtime.rule;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

public class GetFactHandlesInEntryPointCommand
    implements
    ExecutableCommand<Collection<FactHandle>> {

    private ObjectFilter filter = null;
    private boolean      disconnected = false;
    private String       entryPoint;

    public GetFactHandlesInEntryPointCommand(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, ObjectFilter filter) {
        this.entryPoint = entryPoint;
        this.filter = filter;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, ObjectFilter filter, boolean disconnected) {
        this.entryPoint = entryPoint;
        this.filter = filter;
        this.disconnected = disconnected;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, boolean disconnected) {
        this.entryPoint = entryPoint;
        this.disconnected = disconnected;
    }

    public Collection<FactHandle> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Collection<FactHandle> disconnectedFactHandles = new ArrayList<>();
        if ( filter != null ) {
            Collection<InternalFactHandle> factHandles = ep.getFactHandles( this.filter );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                return disconnectedFactHandles;
            }
            else { 
                return ksession.getFactHandles( this.filter );
            }
        } else {
            Collection<InternalFactHandle> factHandles = ep.getFactHandles( );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                return disconnectedFactHandles;
            }
            else { 
                return ksession.getFactHandles();
            }
        }
    }

    public String toString() {
        if ( filter != null ) {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), null, ObjectStoreWrapper.FACT_HANDLE )";
        } else {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), filter, ObjectStoreWrapper.FACT_HANDLE )";
        }
    }
}
