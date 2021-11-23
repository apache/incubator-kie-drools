/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.core.correlation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Correlation implements Serializable {

    private static final long serialVersionUID = 4214786559199084264L;

    private String id;
    private String name;

    private Map<String, CorrelationProperties> correlationPropertiesByMessage;
    private CorrelationProperties processCorrelationProperties;

    public Correlation(String id, String name) {
        this.id = id;
        this.name = name;
        this.correlationPropertiesByMessage = new HashMap<>();
    }

    public CorrelationProperties getMessageCorrelationFor(String messageRef) {
        return correlationPropertiesByMessage.computeIfAbsent(messageRef, ref -> new CorrelationProperties());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void subscribe() {
        if (isSubscribed()) {
            return;
        }
        processCorrelationProperties = new CorrelationProperties();
    }

    public boolean isSubscribed() {
        return processCorrelationProperties != null;
    }

    public CorrelationProperties getProcessSubscription() {
        return processCorrelationProperties;
    }

    public boolean hasCorrelationFor(String messageRef) {
        return correlationPropertiesByMessage.containsKey(messageRef);
    }
}