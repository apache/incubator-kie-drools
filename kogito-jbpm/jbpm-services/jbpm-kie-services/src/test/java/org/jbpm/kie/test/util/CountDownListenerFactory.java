/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.test.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CountDownListenerFactory {

    private static Map<String, CountDownProcessEventListener> listeners = new ConcurrentHashMap<>();
    
    private static Map<String, CountDownTaskEventListener> tasklisteners = new ConcurrentHashMap<>();
    
    public static CountDownProcessEventListener get(String id, String nodeName, int threads) {
        if (listeners.containsKey(id)) {
            return listeners.get(id);
        }
        CountDownProcessEventListener listener = new CountDownProcessEventListener(nodeName, threads);
        listeners.put(id, listener);
        return listener;
    }
    
    public static CountDownTaskEventListener getTask(String id, int threads) {
        if (tasklisteners.containsKey(id)) {
            return tasklisteners.get(id);
        }
        CountDownTaskEventListener listener = new CountDownTaskEventListener(threads, true, true);
        tasklisteners.put(id, listener);
        return listener;
    }
    
    public static CountDownProcessEventListener getExisting(String id) {
        return listeners.get(id);
    }
    
    public static CountDownProcessEventListener removeExisting(String id) {
        return listeners.remove(id);
    }
    
    public static CountDownTaskEventListener getExistingTask(String id) {
        return tasklisteners.get(id);
    }
    
    public static CountDownTaskEventListener removeExistingTask(String id) {
        return tasklisteners.remove(id);
    }
    
    public static void clear() {
        listeners.clear();
        tasklisteners.clear();
    }
}
