package org.drools.commands.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.commands.IdentifiableResult;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetGlobalCommand
    implements
    ExecutableCommand<Object>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute (required=true)
    private String identifier;
    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetGlobalCommand() {
    }

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );

        Object object = ksession.getGlobal( identifier );
        ExecutionResults results = ((RegistryContext)context).lookup( ExecutionResults.class );
        if ( results != null ) {
            results.getResults().put( (this.outIdentifier != null) ? this.outIdentifier : this.identifier, object );
        }
        return object;
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
