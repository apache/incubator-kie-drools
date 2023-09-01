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
import org.kie.api.time.SessionClock;
import org.kie.internal.command.RegistryContext;

@XmlRootElement(name="get-session-time")
@XmlAccessorType(XmlAccessType.NONE)
public class GetSessionTimeCommand implements ExecutableCommand<Long>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name="out-identifier", required=true)
    private String outIdentifier;

    public GetSessionTimeCommand() {
        this("session-currenttime");
    }

    public GetSessionTimeCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @Override
    public Long execute(Context context ) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );
        SessionClock sessionClock = ksession.getSessionClock();

        long result = sessionClock.getCurrentTime();

        ExecutionResults results = ((RegistryContext)context).lookup( ExecutionResults.class );
        if ( results != null ) {
            results.getResults().put( this.outIdentifier, result );
        }
        return result;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String toString() {
        return "session.getSessionClock().getCurrentTime();";
    }
}
