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
package org.drools.reliability.test.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.api.prototype.PrototypeEvent;
import org.kie.api.prototype.PrototypeEventInstance;
import org.kie.api.prototype.PrototypeFact;

import static org.kie.api.prototype.PrototypeBuilder.prototype;

public class PrototypeUtils {

    public static final String DEFAULT_PROTOTYPE_NAME = "DROOLS_PROTOTYPE";
    public static final String SYNTHETIC_PROTOTYPE_NAME = "DROOLS_SYNTHETIC_PROTOTYPE";

    private static final Map<String, PrototypeFact> prototypeFacts = new HashMap<>();
    private static final Map<String, PrototypeEvent> prototypeEvents = new HashMap<>();

    private PrototypeUtils() {
        // It is not allowed to create instances of util classes.
    }

    public static PrototypeEventInstance createControlEvent() {
        return getPrototypeEvent(SYNTHETIC_PROTOTYPE_NAME).newInstance();
    }

    public static PrototypeFact getPrototypeFact(String name) {
        return prototypeFacts.computeIfAbsent(name, n -> prototype(n).asFact());
    }

    public static PrototypeEvent getPrototypeEvent(String name) {
        return prototypeEvents.computeIfAbsent(name, n -> prototype(n).asEvent());
    }

    public static void processResults(List<Object> globalResults, List<PrototypeEventInstance> controlResults) {
        List<Object> events = controlResults.stream().map(r -> r.get("event")).collect(Collectors.toList());
        globalResults.addAll(events);
    }

    public static PrototypeEventInstance createEvent() {
        return getPrototypeEvent(DEFAULT_PROTOTYPE_NAME).newInstance();
    }
}
