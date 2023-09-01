package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAttribute;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

public class GetFactHandleCommand
    implements
    ExecutableCommand<FactHandle> {

    private Object object;
    private boolean disconnected;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetFactHandleCommand() {
    }

    public GetFactHandleCommand(Object object) {
        this.object = object;
        this.disconnected = false;
    }
    
    public GetFactHandleCommand(Object object, boolean disconnected) {
        this.object = object;
        this.disconnected = disconnected;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public FactHandle execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        InternalFactHandle factHandle = (InternalFactHandle) ksession.getFactHandle( object );
        if ( factHandle != null ){
            InternalFactHandle handle = factHandle.clone();
            if ( disconnected ) {
                handle.disconnect();
            }

            if ( this.outIdentifier != null ) {
                ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, handle);
            }

            return handle;
        }
        return null;
    }

    public String toString() {
        return "ksession.getFactHandle( " + object + " );";
    }
}
