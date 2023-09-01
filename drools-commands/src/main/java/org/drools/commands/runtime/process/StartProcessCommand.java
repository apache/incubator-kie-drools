package org.drools.commands.runtime.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.jaxb.JaxbMapAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class StartProcessCommand implements ExecutableCommand<ProcessInstance>, IdentifiableResult {

    @XmlAttribute(required = true)
    private String processId;

    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="parameter")
    private Map<String, Object> parameters = new HashMap<>();

    @XmlElementWrapper(name="data")
    private List<Object> data = null;
    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    @XmlAnyElement(lax = true)
    private AgendaFilter agendaFilter = null;

    public StartProcessCommand() {
    }

    public StartProcessCommand(String processId) {
        this.processId = processId;
    }

    public StartProcessCommand(String processId, String outIdentifier) {
        this(processId);
        this.outIdentifier = outIdentifier;
    }

    public StartProcessCommand(String processId, Map<String, Object> parameters) {
        this(processId);
        this.parameters = parameters;
    }

    public StartProcessCommand(String processId, Map<String, Object> parameters, String outIdentifier) {
        this(processId, outIdentifier);
        this.parameters = parameters;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public void putParameter(String key, Object value) {
        getParameters().put(key, value);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if (data != null) {
            for (Object o: data) {
                ksession.insert(o);
            }
        }
        ProcessInstance processInstance = ksession.startProcess(processId, parameters, agendaFilter);
        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processInstance.getId());
        }
        return processInstance;
    }

    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("session.startProcess(");
        result.append(processId);
        result.append(", [");
        if (parameters != null) {
            int i = 0;
            for (final Map.Entry<String, Object> entry: parameters.entrySet()) {
                if (i++ > 0) {
                    result.append(", ");
                }
                result.append(entry.getKey());
                result.append("=");
                result.append(entry.getValue());
            }
        }
        result.append("]");
        if (agendaFilter != null) {
            result.append(", " + agendaFilter);
        }
        result.append(");");
        return result.toString();
    }
}
