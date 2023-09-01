package org.drools.commands.runtime.process;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetProcessIdsCommand
    implements
    ExecutableCommand<List<String>> {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public List<String> execute(Context context) {
    	List<String> result = new ArrayList<>();
        for (Process p: ((RegistryContext) context).lookup( KieSession.class ).getKieBase().getProcesses()) {
        	result.add(p.getId());
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, new ArrayList<>(result));
        }

        return result;
    }

    public String toString() {
        return "session.getKieBase().getProcesses();";
    }

}
