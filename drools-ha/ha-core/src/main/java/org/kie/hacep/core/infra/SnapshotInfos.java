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
package org.kie.hacep.core.infra;

import java.time.LocalDateTime;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.hacep.consumer.FactHandlesManager;

public class SnapshotInfos {

    private KieSession kieSession;
    private KieContainer kieContainer;
    private FactHandlesManager fhManager;
    private String keyDuringSnaphot;
    private long offsetDuringSnapshot;
    private LocalDateTime time;
    private String kJarGAV;

    public SnapshotInfos(KieSession kieSession,
                         KieContainer kieContainer,
                         FactHandlesManager fhManager,
                         String keyDuringSnaphot,
                         long offsetDuringSnapshot,
                         LocalDateTime time,
                         String kjarGAV) {
        this.kieSession = kieSession;
        this.kieContainer = kieContainer;
        if (fhManager == null || fhManager.getFhMapKeys().isEmpty()) {
            this.fhManager = new FactHandlesManager();
            this.fhManager.initFromKieSession(kieSession);
        } else {
            this.fhManager = fhManager.initFromKieSession(kieSession);
        }
        this.keyDuringSnaphot = keyDuringSnaphot;
        this.offsetDuringSnapshot = offsetDuringSnapshot;
        this.time = time;
        this.kJarGAV = kjarGAV;
    }

    public KieSession getKieSession() {
        return kieSession;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public FactHandlesManager getFhManager() {
        return fhManager;
    }

    public String getKeyDuringSnaphot() {
        return keyDuringSnaphot;
    }

    public long getOffsetDuringSnapshot() {
        return offsetDuringSnapshot;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getkJarGAV() {
        return kJarGAV;
    }
}
