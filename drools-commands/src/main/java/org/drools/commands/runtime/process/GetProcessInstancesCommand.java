package org.drools.commands.runtime.process;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstancesCommand
    implements
    ExecutableCommand<Collection<ProcessInstance>> {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection<ProcessInstance> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        Collection<ProcessInstance> instances = ksession.getProcessInstances();
        Collection<ProcessInstance> result = new ArrayList<>();

        result.addAll(instances);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, new ArrayList<>(result));
        }

        return result;
    }

    public String toString() {
        return "session.getProcessInstances();";
    }

}
