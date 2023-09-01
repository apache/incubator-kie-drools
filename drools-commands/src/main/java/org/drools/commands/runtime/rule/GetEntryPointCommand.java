package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAttribute;

import org.drools.commands.EntryPointCreator;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.command.RegistryContext;

public class GetEntryPointCommand
    implements
    ExecutableCommand<EntryPoint> {

    private String name;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetEntryPointCommand() {
    }

    public GetEntryPointCommand(String name) {
        this.name = name;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public EntryPoint execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(name);
        if (ep == null) {
            return null;
        }

        final EntryPointCreator epCreator = (EntryPointCreator)context.get(EntryPointCreator.class.getName());
        final EntryPoint entryPoint = epCreator != null ? epCreator.getEntryPoint(name) : ep;

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, entryPoint);
        }

        return entryPoint;
    }

    public String toString() {
        return "session.getEntryPoint( " + name + " );";
    }
}
