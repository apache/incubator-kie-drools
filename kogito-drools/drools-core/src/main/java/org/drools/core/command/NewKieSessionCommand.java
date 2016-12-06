/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.Context;

public class NewKieSessionCommand
    implements
    ExecutableCommand<KieSession> {

    private static final long serialVersionUID = 8748826714594402049L;
    private String sessionId;
    private ReleaseId releaseId;

    public NewKieSessionCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    public NewKieSessionCommand(ReleaseId releaseId, String sessionId) {
        this.sessionId = sessionId;
        this.releaseId = releaseId;
    }

    public KieSession execute(Context context) {
        KieContainer kieContainer;

        if ( releaseId != null ) {
            // use the new API to retrieve the session by ID
            KieServices  kieServices  = KieServices.Factory.get();
            kieContainer = kieServices.newKieContainer(releaseId);
        } else {
            kieContainer = ((RegistryContext)context).lookup( KieContainer.class );
            if ( kieContainer == null ) {
                throw new RuntimeException("ReleaseId was not specfied, nor was an existing KieContainer assigned to the Registry");
            }
        }

        KieSession ksession  = sessionId != null ? kieContainer.newKieSession(sessionId) : kieContainer.newKieSession();

        ((RegistryContext)context).register( KieSession.class, ksession );

        return ksession;
    }

    @Override
    public String toString() {
        return "NewKieSessionCommand{" +
               "sessionId='" + sessionId + '\'' +
               ", releaseId=" + releaseId +
               '}';
    }
}
