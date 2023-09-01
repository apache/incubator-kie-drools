package org.drools.commands.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.process.WorkItem;
import org.drools.core.process.WorkItemManager;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetWorkItemCommand implements ExecutableCommand<WorkItem> {

    @XmlAttribute(required = true)
    private long workItemId;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public GetWorkItemCommand() {
    }

    public GetWorkItemCommand(long workItemId) {
        this.workItemId = workItemId;
    }
        
    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public WorkItem execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        final WorkItem workItem = ((WorkItemManager) ksession.getWorkItemManager()).getWorkItem(workItemId);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, workItem);
        }

        return workItem;
    }

    public String toString() {
        return "((org.drools.core.process.instance.WorkItemManager) session.getWorkItemManager()).getWorkItem("
            + workItemId +  ");";
    }

}
