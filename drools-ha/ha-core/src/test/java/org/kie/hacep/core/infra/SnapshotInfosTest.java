/*
 * Copyright 2019 Red Hat
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
package org.kie.hacep.core.infra;

import java.time.LocalDateTime;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.hacep.EnvConfig;
import org.kie.hacep.consumer.FactHandlesManager;
import org.kie.hacep.consumer.KieContainerUtils;

import static org.junit.Assert.*;

public class SnapshotInfosTest {

    @Test
    public void constructorTest(){
        EnvConfig config = EnvConfig.getDefaultEnvConfig();
        KieServices ks = KieServices.get();
        KieContainer kieContainer = KieContainerUtils.getKieContainer(config, ks);
        KieSession kieSession = kieContainer.newKieSession();
        FactHandlesManager fhManager = new FactHandlesManager(kieSession);

        String keyDuringSnapshot = "111";
        long offsetDuringSnapshot = 10l;
        LocalDateTime time = LocalDateTime.now();
        String kjarGAV = "org.kie:fake:1.0.0.Snapshot";
        SnapshotInfos infos = new SnapshotInfos(kieSession,
                                                kieContainer, fhManager, keyDuringSnapshot, offsetDuringSnapshot,
                                                time, kjarGAV);
        assertNotNull(infos);
        assertTrue(infos.getFhManager().getFhMapKeys().size() == fhManager.getFhMapKeys().size());
        assertEquals(infos.getKeyDuringSnaphot(), keyDuringSnapshot);
        assertEquals(infos.getKieContainer(), kieContainer);
        assertEquals(infos.getKieSession(), kieSession);
        assertEquals(infos.getOffsetDuringSnapshot(), offsetDuringSnapshot);
        assertEquals(infos.getTime(), time);
        assertEquals(infos.getkJarGAV(), kjarGAV);
    }
}
