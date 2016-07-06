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

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieService;

import java.util.Map;

public class NewKieSessionCommand
    implements
    GenericCommand<KieSession> {

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
            kieContainer = (KieContainer) ((Map<String, Object>)context.get(ContextImpl.REGISTRY)).get(KieContainer.class.getName());
            if ( kieContainer == null ) {
                throw new RuntimeException("ReleaseId was not specfied, nor was an existing KieContainer assigned to the Registry");
            }
        }

        KieSession ksession  = sessionId != null ? kieContainer.newKieSession(sessionId) : kieContainer.newKieSession();

        ((Map<String, Object>)context.get(ContextImpl.REGISTRY)).put(KieSession.class.getName(), ksession);

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
