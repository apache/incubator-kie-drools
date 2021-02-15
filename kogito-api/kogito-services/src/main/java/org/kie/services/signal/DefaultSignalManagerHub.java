/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.services.signal;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.signal.SignalManager;
import org.kie.kogito.signal.SignalManagerHub;


public class DefaultSignalManagerHub implements SignalManagerHub {
    
    private ConcurrentHashMap<String, Set<SignalManager>> signalManagers = new ConcurrentHashMap<>();

    @Override
    public void publish(String type, Object signalData) {
        Set<SignalManager> list = signalManagers.getOrDefault(type, Collections.emptySet());
        for (SignalManager sm : list) {
            sm.signalEvent(type, signalData);
        }
        
        if (signalData instanceof ProcessInstance) {
            list = signalManagers.getOrDefault(((ProcessInstance) signalData).getProcessId(), Collections.emptySet());
            
            for (SignalManager sm : list) {
                sm.signalEvent(type, signalData);
            }
        }
    }

    @Override
    public void publishTargeting(String id, String type, Object signalData) {
        signalManagers.getOrDefault(type, Collections.emptySet())
            .forEach(e -> e.signalEvent(id, type, signalData));
    }

    @Override
    public void subscribe(String type, SignalManager signalManager) {
        this.signalManagers.compute(type, (k, v) -> {
            if (v == null) {
                v = new CopyOnWriteArraySet<>();
            }
            v.add(signalManager);
            return v;
        });
    }

    @Override
    public void unsubscribe(String type, SignalManager signalManager) {
        this.signalManagers.computeIfPresent(type, (k, v) -> {
            v.remove(signalManager);
            if (v.isEmpty()) {
                signalManagers.remove(type);
            }
            return v;
        });
    }

}
