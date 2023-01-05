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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import org.kie.kogito.internal.process.event.KogitoObjectListener;
import org.kie.kogito.internal.process.event.KogitoObjectListenerAware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static org.kie.kogito.jackson.utils.ListenerAwareUtils.handleNull;

public class ArrayNodeListenerAware extends ArrayNode implements KogitoObjectListenerAware {

    private static final long serialVersionUID = 1L;

    private transient Collection<KogitoObjectListener> listeners = new CopyOnWriteArraySet<>();

    public ArrayNodeListenerAware(JsonNodeFactory nf) {
        super(nf);
    }

    public ArrayNodeListenerAware(JsonNodeFactory nf, int capacity) {
        super(nf, capacity);
    }

    public ArrayNodeListenerAware(JsonNodeFactory nf, List<JsonNode> children) {
        super(nf, children);
    }

    @Override
    public void addKogitoObjectListener(KogitoObjectListener listener) {
        listeners.add(listener);
    }

    @Override
    protected ArrayNode _set(int index, JsonNode node) {
        processNode(index, super.get(index), node, () -> super._set(index, node));
        return this;
    }

    private void processNode(int index, JsonNode oldValue, JsonNode newValue, Runnable updater) {
        String propertyName = "[" + index + "]";
        fireEvent(propertyName, oldValue, newValue, updater);
    }

    @Override
    protected ArrayNode _add(JsonNode node) {
        processNode(size(), nullNode(), node, () -> super._add(node));
        return this;
    }

    @Override
    protected ArrayNode _insert(int index, JsonNode node) {
        processNode(index, nullNode(), node, () -> super._insert(index, node));
        return this;
    }

    @Override
    public JsonNode remove(int index) {
        JsonNode oldValue = get(index);
        processNode(index, oldValue, nullNode(), () -> super.remove(index));
        return oldValue;
    }

    @Override
    public Collection<KogitoObjectListener> listeners() {
        return listeners;
    }

    @Override
    public ArrayNode deepCopy() {
        List<JsonNode> nodes = new ArrayList<>();
        Iterator<JsonNode> iter = super.elements();
        while (iter.hasNext()) {
            nodes.add(iter.next().deepCopy());
        }
        return new ArrayNodeListenerAware(_nodeFactory, nodes);
    }

    @Override
    public void fireEvent(String propertyName, Object oldValue, Object newValue, Runnable updater) {
        KogitoObjectListenerAware.super.fireEvent(propertyName, handleNull(oldValue), handleNull(newValue), updater);
    }
}
