/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.model.v1_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

public abstract class DMNModelInstrumentedBase {
    public static final String URI_FEEL = "http://www.omg.org/spec/FEEL/20140401";
    public static final String URI_DMN = "http://www.omg.org/spec/DMN/20151101/dmn.xsd";

    private Map<String, String> nsContext;

    private DMNModelInstrumentedBase parent;
    private final java.util.List<DMNModelInstrumentedBase> children = new ArrayList<>();

    /**
     * Namespace context map as defined at the level of the given element.
     * Please notice it support also default namespace (no prefix) as "" as defined in {@link XMLConstants#DEFAULT_NS_PREFIX} .
     */
    public Map<String, String> getNsContext() {
        if (nsContext == null) {
            nsContext = new HashMap<String, String>();  
        }
        return nsContext;
    }

    public String getNamespaceURI( String prefix ) {
        if( this.nsContext != null && this.nsContext.containsKey( prefix ) ) {
            return this.nsContext.get( prefix );
        }
        if( this.parent != null ) {
            return parent.getNamespaceURI( prefix );
        }
        return null;
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
    public java.util.List<DMNModelInstrumentedBase> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    public void addChildren(DMNModelInstrumentedBase child) {
        this.children.add(child);
    }
}
