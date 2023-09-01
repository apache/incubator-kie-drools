package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteCommand
        implements ExecutableCommand<Void> {

    @XmlElement(name="handle")
    private DisconnectedFactHandle handle;

    private String factHandle;

    @XmlElement
    private FactHandle.State fhState = FactHandle.State.ALL;

    public DeleteCommand() {
    }

    public DeleteCommand(FactHandle handle) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
    }

    public DeleteCommand(FactHandle handle, FactHandle.State fhState) {
        this(handle);
        this.fhState = fhState;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }

    public void setHandle(DisconnectedFactHandle factHandle) {
        this.handle = factHandle;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        handle = new DisconnectedFactHandle(factHandleId);
    }

    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.getEntryPoint( handle.getEntryPointName() ).delete( handle, fhState );
        return null;
    }

    public String toString() {
        return "session.retract( " + handle + " );";
    }

}
