package org.drools.commands.runtime.process;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessEventListenersCommand
    implements
    ExecutableCommand<Collection<ProcessEventListener> > {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection<ProcessEventListener> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        Collection<ProcessEventListener> processEventListeners = ksession.getProcessEventListeners();

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processEventListeners);
        }

        return processEventListeners;
    }

    public String toString() {
        return "session.getProcessEventListeners();";
    }
}
