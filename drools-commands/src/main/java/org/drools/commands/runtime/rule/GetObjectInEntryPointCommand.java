package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.drools.commands.IdentifiableResult;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectInEntryPointCommand
    implements
    ExecutableCommand<Object>, IdentifiableResult {

    private FactHandle factHandle;
    private String     outIdentifier;

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    public GetObjectInEntryPointCommand() { }

    public GetObjectInEntryPointCommand(FactHandle factHandle, String entryPoint) {
        this.factHandle = factHandle;
        this.entryPoint = entryPoint;
    }

    public GetObjectInEntryPointCommand(FactHandle factHandle, String entryPoint, String outIdentifier) {
        this.factHandle = factHandle;
        this.entryPoint = entryPoint;
        this.outIdentifier = outIdentifier;
    }

    @XmlAttribute(name="out-identifier", required=true)
    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        factHandle = DefaultFactHandle.createFromExternalFormat(factHandleId);
    }

    public String getFactHandleFromString() {
        return factHandle.toExternalForm();
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Object object = ep.getObject( factHandle );

        if (this.outIdentifier != null) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, object );
        }

        return object;
    }

    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).getObject( " + factHandle + " );";
    }

}
