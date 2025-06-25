/*
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
package org.kie.dmn.model.impl;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.RowLocation;

public abstract class AbstractKieDMNModelInstrumentedBase implements DMNModelInstrumentedBase {

    private Map<String, String> nsContext;

    private DMNModelInstrumentedBase parent;
    private final List<DMNModelInstrumentedBase> children = new ArrayList<>();
    private Location location;
    private Map<QName, String> additionalAttributes = new HashMap<>();

    public String getIdentifierString() {
        if (this instanceof AbstractTNamedElement && ((AbstractTNamedElement) this).getName() != null) {
            return ((AbstractTNamedElement) this).getName();
        } else if (this instanceof AbstractTDMNElement && ((AbstractTDMNElement) this).getId() != null) {
            return ((AbstractTDMNElement) this).getId();
        } else {
            return "[unnamed " + getClass().getSimpleName() + "]";
        }
    }

    public DMNModelInstrumentedBase getParentDRDElement() {
        if (this instanceof AbstractTDRGElement
                || (this instanceof AbstractTArtifact)
                || (this instanceof AbstractTItemDefinition && parent != null && parent instanceof AbstractTDefinitions)) {
            return this;
        } else if (parent != null) {
            return parent.getParentDRDElement();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String> getNsContext() {
        if (nsContext == null) {
            nsContext = new HashMap<>();
        }
        return nsContext;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (this.nsContext != null && this.nsContext.containsKey(prefix)) {
            return this.nsContext.get(prefix);
        }
        if (this.parent != null) {
            return parent.getNamespaceURI(prefix);
        }
        return null;
    }

    public Optional<String> getPrefixForNamespaceURI(String namespaceURI) {
        if (this.nsContext != null && this.nsContext.containsValue(namespaceURI)) {
            return this.nsContext.entrySet().stream().filter(kv -> kv.getValue().equals(namespaceURI)).findFirst().map(Map.Entry::getKey);
        }
        if (this.parent != null) {
            return parent.getPrefixForNamespaceURI(namespaceURI);
        }
        return Optional.empty();
    }

    public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public Map<QName, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public DMNModelInstrumentedBase getParent() {
        return parent;
    }

    public void setParent(DMNModelInstrumentedBase parent) {
        this.parent = parent;
    }

    /*
     * children element references are populated during deserialization, enabling fast access for Validation.
     */
    public List<DMNModelInstrumentedBase> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChildren(DMNModelInstrumentedBase child) {
        this.children.add(child);
    }

    @Override
    public void setLocation(Location location) {
        this.location = new RowLocation(location);
    }

    /**
     * Returns an approximated location of the XML origin for this DMN Model node.
     */
    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public <T extends DMNModelInstrumentedBase> List<? extends T> findAllChildren(Class<T> clazz) {
        if (clazz.isInstance(this)) {
            T obj = (T) this;
            return Collections.singletonList(obj);
        }
        List<T> results = new ArrayList<>();
        for (DMNModelInstrumentedBase c : getChildren()) {
            results.addAll(c.findAllChildren(clazz));
        }
        return results;
    }
}
