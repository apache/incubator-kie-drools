/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands;

import java.util.function.BiFunction;

import org.kie.api.builder.ReleaseId;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.command.RegistryContext;

public class NewKieSessionCommand extends AbstractNewKieContainerCommand
        implements
        ExecutableCommand<KieSession> {

    private static final long serialVersionUID = 8748826714594402049L;
    private String sessionName;
    private ReleaseId releaseId;
    private BiFunction<String, KieContainer, KieSessionConfiguration> customizeSessionConfiguration =
            (sessionName, kieContainer) -> kieContainer.getKieSessionConfiguration(sessionName);

    public NewKieSessionCommand(String sessionName) {
        this.sessionName = sessionName;
    }

    public NewKieSessionCommand(ReleaseId releaseId, String sessionName) {
        this.sessionName = sessionName;
        this.releaseId = releaseId;
    }

    @Override
    public KieSession execute(Context context) {

        KieContainer kieContainer = getKieContainer((RegistryContext) context, releaseId);

        KieSessionConfiguration kieSessionConfiguration = customizeSessionConfiguration.apply(sessionName, kieContainer);

        KieSession ksession = kieContainer.newKieSession(sessionName, kieSessionConfiguration);

        ((RegistryContext) context).register(KieSession.class, ksession);

        return ksession;
    }

    public NewKieSessionCommand setCustomizeSessionConfiguration(BiFunction<String, KieContainer, KieSessionConfiguration> customizeSessionConfiguration) {
        this.customizeSessionConfiguration = customizeSessionConfiguration;
        return this;
    }

    @Override
    public String toString() {
        return "NewKieSessionCommand{" +
                "sessionName='" + sessionName + '\'' +
                ", releaseId=" + releaseId +
                '}';
    }
}
