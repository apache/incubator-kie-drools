package org.drools.commands.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetFactCountCommand
    implements
    ExecutableCommand<Long> {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Long execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        final Long factCount = ksession.getFactCount();

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, factCount);
        }
        
        return factCount;
    }

    public String toString() {
        return "ksession.getFactCount();";
    }

}
