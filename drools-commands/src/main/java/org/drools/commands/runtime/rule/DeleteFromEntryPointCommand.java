package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class DeleteFromEntryPointCommand
        implements ExecutableCommand<Void> {

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    private DisconnectedFactHandle handle;

    private FactHandle.State fhState = FactHandle.State.ALL;

    public DeleteFromEntryPointCommand() {
    }

    public DeleteFromEntryPointCommand(FactHandle handle, String entryPoint) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.entryPoint = entryPoint;
    }

    public DeleteFromEntryPointCommand(FactHandle handle, String entryPoint, FactHandle.State fhState) {
        this(handle, entryPoint);
        this.fhState = fhState;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.getEntryPoint( entryPoint ).delete( handle, fhState );
        return null;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        handle = new DisconnectedFactHandle(factHandleId);
    }

    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).retract( " + handle + " );";
    }

}
