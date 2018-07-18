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


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionClock;

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
        SessionClock sessionClock = ksession.<SessionClock>getSessionClock();

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

    public String toString() {
        return "session.getSessionClock().getCurrentTime();";
    }
}
