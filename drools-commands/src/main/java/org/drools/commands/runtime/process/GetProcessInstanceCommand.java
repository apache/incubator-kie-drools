package org.drools.commands.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstanceCommand implements ExecutableCommand<ProcessInstance>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = 5890677592835087670L;
    
    @XmlAttribute(required = true)
    private String processInstanceId;
    @XmlAttribute
    private boolean readOnly = false;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public GetProcessInstanceCommand() {}

    public GetProcessInstanceCommand(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if (processInstanceId == null) {
            return null;
        }

        final ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId, readOnly);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processInstance);
        }

        return processInstance;
    }

    public String toString() {
        return "session.getProcessInstance(" + processInstanceId + ");";
    }

}
