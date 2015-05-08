package org.jbpm.kie.services.impl.cmd;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.command.runtime.process.StartProcessInstanceCommand;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class StartProcessInstanceWithParentCommand  implements GenericCommand<ProcessInstance> {
    @XmlAttribute(
            required = true
    )
    private Long processInstanceId;
    @XmlAttribute(
            required = true
    )
    private Long parentProcessInstanceId;

    public StartProcessInstanceWithParentCommand(){}

    public StartProcessInstanceWithParentCommand(Long processInstanceId, Long parentProcessInstanceId) {
        this.processInstanceId = processInstanceId;
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    @Override
    public ProcessInstance execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext)context).getKieSession();
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId.longValue());
        if(parentProcessInstanceId > 0){
            ((ProcessInstanceImpl)processInstance).setMetaData("ParentProcessInstanceId", parentProcessInstanceId);
        }

        return ksession.startProcessInstance(processInstanceId.longValue());
    }

    public Long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(Long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
}