/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.command.runtime;

import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;

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
        SessionPseudoClock sessionClock = ksession.<SessionPseudoClock>getSessionClock();

        sessionClock.advanceTime( amount, unit );
        long result = sessionClock.getCurrentTime();

        ExecutionResultImpl results = ((RegistryContext)context).lookup( ExecutionResultImpl.class );
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
