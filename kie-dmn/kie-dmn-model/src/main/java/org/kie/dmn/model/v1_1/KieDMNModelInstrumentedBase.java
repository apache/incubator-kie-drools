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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.RowLocation;

public abstract class KieDMNModelInstrumentedBase implements DMNModelInstrumentedBase {
    public static final String URI_FEEL = "http://www.omg.org/spec/FEEL/20140401";
    public static final String URI_DMN = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";
    public static final String URI_KIE = "http://www.drools.org/kie/dmn/1.1";

    private Map<String, String> nsContext;

    private DMNModelInstrumentedBase parent;
    private final java.util.List<DMNModelInstrumentedBase> children = new ArrayList<>();
    private Location location;
    private Map<QName, String> additionalAttributes = new HashMap<>();

    @Override
    public String getIdentifierString() {
        if( this instanceof TNamedElement && ((TNamedElement)this).getName() != null ) {
            return ((TNamedElement)this).getName();
        } else if( this instanceof TDMNElement && ((TDMNElement)this).getId() != null ) {
            return ((TDMNElement)this).getId();
        } else {
            return "[unnamed "+getClass().getSimpleName()+"]";
        }
    }

    @Override
    public DMNModelInstrumentedBase getParentDRDElement() {
        if( this instanceof TDRGElement
                || this instanceof TDecisionService // in DMN v1.1 it was a bug in the schema as decision service missed to inherit from DRGElement
                || (this instanceof TArtifact)
                || (this instanceof TItemDefinition && parent != null && parent instanceof TDefinitions)) {
            return this;
        } else if( parent != null ) {
            return parent.getParentDRDElement();
        } else {
            return null;
        }
    }

    /**
     * Namespace context map as defined at the level of the given element.
     * Please notice it support also default namespace (no prefix) as "" as defined in {@link XMLConstants#DEFAULT_NS_PREFIX} .
     */

    public Map<String, String> getNsContext() {
        if (nsContext == null) {
            nsContext = new HashMap<>();  
        }
        return nsContext;
    }

    @Override
    public String getNamespaceURI( String prefix ) {
        if( this.nsContext != null && this.nsContext.containsKey( prefix ) ) {
            return this.nsContext.get( prefix );
        }
        if( this.parent != null ) {
            return parent.getNamespaceURI( prefix );
        }
        return null;
    }
    
    @Override
    public Optional<String> getPrefixForNamespaceURI( String namespaceURI ) {
        if( this.nsContext != null && this.nsContext.containsValue(namespaceURI) ) {
            return this.nsContext.entrySet().stream().filter(kv -> kv.getValue().equals(namespaceURI)).findFirst().map(Map.Entry::getKey);
        }
        if( this.parent != null ) {
            return parent.getPrefixForNamespaceURI( namespaceURI );
        }
        return Optional.empty();
    }
    
    @Override
    public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }
    
    @Override
    public Map<QName, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    @Override
    public DMNModelInstrumentedBase getParent() {
        return parent;
    }

    @Override
    public void setParent(DMNModelInstrumentedBase parent) {
        this.parent = parent;
    }

    /*
     * children element references are populated during deserialization, enabling fast access for Validation.
     */
    @Override
    public java.util.List<DMNModelInstrumentedBase> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    @Override
    public void addChildren(DMNModelInstrumentedBase child) {
        this.children.add(child);
    }

    public void setLocation(Location location) {
        this.location = new RowLocation(location);
    }
    
    /**
     * Returns an approximated location of the XML origin for this DMN Model node.
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public String getURIFEEL() {
        return URI_FEEL;
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
