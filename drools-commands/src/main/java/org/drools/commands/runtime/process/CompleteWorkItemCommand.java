package org.drools.commands.runtime.process;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CompleteWorkItemCommand implements ExecutableCommand<Void> {

    @XmlAttribute(name="id", required = true)
    private Long workItemId;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="result")
    private Map<String, Object> results = new HashMap<>();

    public CompleteWorkItemCommand() {}

    public CompleteWorkItemCommand(long workItemId) {
        this.workItemId = workItemId;
    }

    public CompleteWorkItemCommand(long workItemId, Map<String, Object> results) {
        this(workItemId);
        this.results = results;
    }

    public long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(long workItemId) {
        this.workItemId = workItemId;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.getWorkItemManager().completeWorkItem(workItemId, results);
        return null;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("session.getWorkItemManager().completeWorkItem(");
        result.append(workItemId);
        result.append(", [");
        if (results != null) {
            int i = 0;
            for (final Map.Entry<String, Object> entry: results.entrySet()) {
                if (i++ > 0) {
                    result.append(", ");
                }
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
            }
        }
        result.append("]);");
        return result.toString();
    }

}
