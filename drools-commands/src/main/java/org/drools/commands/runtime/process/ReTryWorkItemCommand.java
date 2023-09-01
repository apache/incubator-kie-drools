package org.drools.commands.runtime.process;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.process.WorkItemManager;
import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ReTryWorkItemCommand implements ExecutableCommand<Void> {
    @XmlAttribute(name="id", required=true)
    private long workItemId;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    private Map<String, Object> params = new HashMap<>();
    
    public ReTryWorkItemCommand() {
        
    }

    public ReTryWorkItemCommand(long workItemId ,Map<String,Object> params) {
        this.workItemId = workItemId;
        this.params = params;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    
    public Map<String, Object> getParams() {
        return params;
    }

    
    public void setParams( Map<String, Object> params ) {
        this.params = params;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ((WorkItemManager)ksession.getWorkItemManager()).retryWorkItem( workItemId, params );
        return null;
    }

    public String toString() {
        return "session.getWorkItemManager().retryWorkItem(" + workItemId + ","+ params+" );";
    }
}
