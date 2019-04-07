/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.test.listener.process.DefaultCountDownProcessEventListener;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.listener.process.SLAViolationCountDownProcessEventListener;

public class CountDownListenerFactory {

    private static Map<String, DefaultCountDownProcessEventListener> listeners = new ConcurrentHashMap<>();
    
    public static DefaultCountDownProcessEventListener get(String id, String nodeName, int threads) {
        if (listeners.containsKey(id)) {
            return listeners.get(id);
        }
        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener(nodeName, threads);
        listeners.put(id, listener);
        return listener;
    }
    
    public static DefaultCountDownProcessEventListener getSLA(String id, int threads) {
        if (listeners.containsKey(id)) {
            return listeners.get(id);
        }
        SLAViolationCountDownProcessEventListener listener = new SLAViolationCountDownProcessEventListener(threads);
        listeners.put(id, listener);
        return listener;
    }
    
    public static DefaultCountDownProcessEventListener getExisting(String id) {
        return listeners.get(id);
    }
    
    public static DefaultCountDownProcessEventListener removeExisting(String id) {
        return listeners.remove(id);
    }
    
    public static void clear() {
        listeners.clear();
    }
}
