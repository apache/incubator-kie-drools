package org.drools.commands.runtime.process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.CorrelationKeyCommand;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.jaxb.CorrelationKeyXmlAdapter;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessInstanceByCorrelationKeyCommand implements ExecutableCommand<ProcessInstance>, CorrelationKeyCommand {

    /** Generated serial version UID */
    private static final long serialVersionUID = -211522165088235065L;
    
    @XmlElement(name = "correlation-key", required = true)
    @XmlJavaTypeAdapter(value = CorrelationKeyXmlAdapter.class)
    private CorrelationKey correlationKey;

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public GetProcessInstanceByCorrelationKeyCommand() {}

    public GetProcessInstanceByCorrelationKeyCommand(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @Override
    public CorrelationKey getCorrelationKey() {
        return correlationKey;
    }

    @Override
    public void setCorrelationKey(CorrelationKey correlationKey) {
        this.correlationKey = correlationKey;
    }

    public ProcessInstance execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if (correlationKey == null) {
            return null;
        }

        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(correlationKey);

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, processInstance);
        }

        return processInstance;
    }

    public String toString() {
        return "session.getProcessInstance(" + correlationKey + ");";
    }

}
