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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public abstract class DMNElement extends DMNModelInstrumentedBase {

    private String description;
    private DMNElement.ExtensionElements extensionElements;
    private String id;
    private String label;
    private Map<QName, String> otherAttributes = new HashMap<>();

    public String getDescription() {
        return description;
    }

    public void setDescription( final String value ) {
        this.description = value;
    }

    public DMNElement.ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements( final DMNElement.ExtensionElements value ) {
        this.extensionElements = value;
    }

    public String getId() {
        return id;
    }

    public void setId( final String value ) {
        this.id = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( final String value ) {
        this.label = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public static class ExtensionElements
            extends DMNModelInstrumentedBase {

        protected List<Object> any;

        public List<Object> getAny() {
            if ( any == null ) {
                any = new ArrayList<>();
            }
            return this.any;
        }

    }

}
