package org.drools.commands.runtime;

import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.commands.IdentifiableResult;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.command.RegistryContext;

@XmlRootElement(name="advance-session-time")
@XmlAccessorType(XmlAccessType.NONE)
public class AdvanceSessionTimeCommand implements ExecutableCommand<Long>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute(name="out-identifier", required=true)
    private String outIdentifier;

    @XmlAttribute(name="amount")
    private long amount;

    @XmlAttribute(name="unit")
    private TimeUnit unit;

    public AdvanceSessionTimeCommand() {
        this("session-advancecurrenttime", 0L, TimeUnit.MILLISECONDS);
    }

    public AdvanceSessionTimeCommand(long amount, TimeUnit unit) {
        this("session-advancecurrenttime", amount, unit);
    }

    public AdvanceSessionTimeCommand(String outIdentifier, long amount, TimeUnit unit) {
        this.outIdentifier = outIdentifier;
        this.amount = amount;
        this.unit = unit;
    }

    @Override
    public Long execute(Context context ) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );
        SessionPseudoClock sessionClock = ksession.getSessionClock();

        sessionClock.advanceTime( amount, unit );
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

    public long getAmount() {
        return amount;
    }

    public void setAmount( long amount ) {
        this.amount = amount;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit( TimeUnit unit ) {
        this.unit = unit;
    }

    public String toString() {
        return "session.getSessionClock().advanceTime( " + amount + ", " + unit + " );";
    }
}
