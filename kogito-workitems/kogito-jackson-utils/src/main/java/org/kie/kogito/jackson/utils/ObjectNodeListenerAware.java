/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jackson.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.kie.kogito.internal.process.event.KogitoObjectListener;
import org.kie.kogito.internal.process.event.KogitoObjectListenerAware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.kie.kogito.jackson.utils.ListenerAwareUtils.handleNull;

public class ObjectNodeListenerAware extends ObjectNode implements KogitoObjectListenerAware {

    private static final long serialVersionUID = 1L;

    private transient Collection<KogitoObjectListener> listeners = new CopyOnWriteArraySet<>();

    public ObjectNodeListenerAware(JsonNodeFactory nc) {
        super(nc);
    }

    @Override
    protected ObjectNode _put(String fieldName, JsonNode value) {
        fireEvent(fieldName, _children.get(fieldName), value, () -> _children.put(fieldName, value));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T set(String propertyName, JsonNode value) {
        fireEvent(propertyName, _children.get(propertyName), value, () -> _children.put(propertyName, value));
        return (T) this;
    }

    @Override
    public JsonNode remove(String propertyName) {
        JsonNode oldValue = _children.get(propertyName);
        fireEvent(propertyName, oldValue, nullNode(), () -> super.remove(propertyName));
        return oldValue;
    }

    @Override
    public void addKogitoObjectListener(KogitoObjectListener listener) {
        listeners.add(listener);
    }

    @Override
    public Collection<KogitoObjectListener> listeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    @Override
    public ObjectNode deepCopy() {
        ObjectNodeListenerAware ret = new ObjectNodeListenerAware(_nodeFactory);
        for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());
        }
        return ret;
    }

    @Override
    public void fireEvent(String propertyName, Object oldValue, Object newValue, Runnable updater) {
        KogitoObjectListenerAware.super.fireEvent(propertyName, handleNull(oldValue), handleNull(newValue), updater);
    }
}
