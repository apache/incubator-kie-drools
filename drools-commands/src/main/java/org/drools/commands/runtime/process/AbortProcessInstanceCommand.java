package org.drools.commands.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.ProcessInstanceIdCommand;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class AbortProcessInstanceCommand implements ExecutableCommand<Void>, ProcessInstanceIdCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = 2482483392339538998L;
    
    @XmlAttribute
    private String processInstanceId;

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if (processInstanceId == null) {
            return null;
        }
        ksession.abortProcessInstance(processInstanceId);
        return null;
    }

    public String toString() {
        return "session.abortProcessInstance(" + processInstanceId + ");";
    }

}
