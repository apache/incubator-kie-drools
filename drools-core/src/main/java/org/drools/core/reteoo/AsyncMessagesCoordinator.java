/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AsyncMessagesCoordinator {

    private AsyncMessagesCoordinator() { }

    public static class Holder {
        private static final AsyncMessagesCoordinator INSTANCE = new AsyncMessagesCoordinator();
    }

    public static AsyncMessagesCoordinator get() {
        return Holder.INSTANCE;
    }

    private final Map<String, List<Consumer<AsyncMessage>>> listeners = new HashMap<>();

    public void propagate(String messageId, AsyncMessage leftTuple) {
        listeners.getOrDefault( messageId, Collections.emptyList() ).forEach( c -> c.accept( leftTuple ) );
    }

    synchronized void registerReceiver(String messageId, Consumer<AsyncMessage> receiver) {
        listeners.computeIfAbsent( messageId, s -> new ArrayList<>() ).add( receiver );
    }

    synchronized void deregisterReceiver(String messageId, Consumer<AsyncMessage> receiver) {
        List<Consumer<AsyncMessage>> consumers = listeners.get( messageId );
        consumers.remove( receiver );
        if (consumers.isEmpty()) {
            listeners.remove( messageId );
        }
    }

    public Map<String, List<Consumer<AsyncMessage>>> getListeners() {
        return listeners;
    }
}
