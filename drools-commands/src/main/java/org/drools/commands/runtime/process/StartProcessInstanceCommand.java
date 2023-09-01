package org.drools.commands.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class StartProcessInstanceCommand implements ExecutableCommand<ProcessInstance>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = -270933342071833447L;
    
    @XmlAttribute(required = true)
    private String processInstanceId;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public StartProcessInstanceCommand() {
    }

    public StartProcessInstanceCommand(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        final ProcessInstance processInstance = ksession.startProcessInstance(processInstanceId);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processInstance);
        }

        return processInstance;
    }

    public String toString() {
        return "session.startProcessInstance(" + processInstanceId + ");";
    }
}
