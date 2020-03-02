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
package org.kie.remote.impl.consumer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.remote.exceptions.StopConsumeException;

import static org.kie.remote.CommonConfig.SKIP_LISTENER_AUTOSTART;
import static org.kie.remote.util.ConfigurationUtil.readBoolean;

public class Listener {

    private final Map<String, CompletableFuture<Object>> requestsStore = new ConcurrentHashMap<>();
    private final ListenerThread listenerThread;
    private Thread t;

    public Listener(Properties configuration, ListenerThread listenerThread) {
        this.listenerThread = listenerThread;
        this.listenerThread.init(requestsStore);
        if (!readBoolean(configuration, SKIP_LISTENER_AUTOSTART)) {
            start();
        }
    }

    public Listener start() {
        t = new Thread(listenerThread);
        t.setDaemon(true);
        t.start();
        return this;
    }

    public Map<String, CompletableFuture<Object>> getRequestsStore() {
        return requestsStore;
    }

    public void stopConsumeEvents() {
        listenerThread.stop();
        requestsStore.clear();
        if (t != null) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                t.interrupt();
                throw new StopConsumeException(ex.getMessage(), ex);
            }
        }
    }
}
