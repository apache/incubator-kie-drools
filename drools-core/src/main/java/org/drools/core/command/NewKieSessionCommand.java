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

import java.util.function.BiFunction;

import org.drools.core.command.impl.RegistryContext;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class NewKieSessionCommand extends AbstractNewKieContainerCommand
        implements
        ExecutableCommand<KieSession> {

    private static final long serialVersionUID = 8748826714594402049L;
    private String sessionId;
    private ReleaseId releaseId;
    private BiFunction<String, KieContainer, KieContainer> beforeSessionCreation = (a, b) -> b;

    public NewKieSessionCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    public NewKieSessionCommand(ReleaseId releaseId, String sessionId) {
        this.sessionId = sessionId;
        this.releaseId = releaseId;
    }

    @Override
    public KieSession execute(Context context) {

        KieContainer kieContainer = getKieContainer((RegistryContext) context, releaseId);

        kieContainer = beforeSessionCreation.apply(sessionId, kieContainer);

        KieSession ksession = sessionId != null ? kieContainer.newKieSession(sessionId) : kieContainer.newKieSession();

        ((RegistryContext) context).register(KieSession.class, ksession);

        return ksession;
    }

    public NewKieSessionCommand setBeforeSessionCreation(BiFunction<String, KieContainer, KieContainer> beforeSessionCreation) {
        this.beforeSessionCreation = beforeSessionCreation;
        return this;
    }

    @Override
    public String toString() {
        return "NewKieSessionCommand{" +
                "sessionId='" + sessionId + '\'' +
                ", releaseId=" + releaseId +
                '}';
    }
}
