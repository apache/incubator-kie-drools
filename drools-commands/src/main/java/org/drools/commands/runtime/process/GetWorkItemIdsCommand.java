package org.drools.commands.runtime.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
public class GetWorkItemIdsCommand implements ExecutableCommand<List<Long>> {

    /** generated serial version UID */
    private static final long serialVersionUID = 1471981530823361925L;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public List<Long> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        Set<WorkItem> workItems = ((WorkItemManager) ksession.getWorkItemManager()).getWorkItems();
        List<Long> workItemIds = new ArrayList<>(workItems.size());
        for(WorkItem workItem : workItems ) { 
            workItemIds.add(workItem.getId());
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, workItemIds);
        }

        return workItemIds;
    }

}
