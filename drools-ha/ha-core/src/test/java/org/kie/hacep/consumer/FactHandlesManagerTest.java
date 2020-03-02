/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.hacep.consumer;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.hacep.EnvConfig;
import org.kie.remote.RemoteFactHandle;
import org.kie.remote.impl.RemoteFactHandleImpl;

import static org.junit.Assert.*;

public class FactHandlesManagerTest {

    @Test
    public void getFactHandleByIdTest(){
        KieServices ks = KieServices.get();
        EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
        KieContainer kieContainer = KieContainerUtils.getKieContainer(envConfig, ks);
        KieSession kieSession = kieContainer.newKieSession();
        FactHandlesManager manager = new FactHandlesManager(kieSession);
        String myObject = "myObject";
        RemoteFactHandle remoteFactHandle = new RemoteFactHandleImpl(myObject);
        FactHandle factHandle = kieSession.getEntryPoint("DEFAULT").insert(myObject);
        manager.registerHandle(remoteFactHandle, factHandle);
        assertNotNull(manager.getFactHandleById(remoteFactHandle));
        assertNotNull(manager.toString());
    }

    @Test
    public void getFactHandleByIdSecondTest(){
        KieServices ks = KieServices.get();
        EnvConfig envConfig = EnvConfig.getDefaultEnvConfig();
        KieContainer kieContainer = KieContainerUtils.getKieContainer(envConfig, ks);
        KieSession kieSession = kieContainer.newKieSession();
        FactHandlesManager manager = new FactHandlesManager();
        manager.initFromKieSession(kieSession);
        String myObject = "myObject";
        RemoteFactHandle remoteFactHandle = new RemoteFactHandleImpl(myObject);
        FactHandle factHandle = kieSession.getEntryPoint("DEFAULT").insert(myObject);
        manager.registerHandle(remoteFactHandle, factHandle);
        assertNotNull(manager.getFactHandleById(remoteFactHandle));
        assertNotNull(manager.toString());
    }
}
