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
import java.util.List;

import org.kie.dmn.model.api.DMNElement;

public abstract class TDMNElement extends KieDMNModelInstrumentedBase implements DMNElement {

    private String description;
    private ExtensionElements extensionElements;
    private String id;
    private String label;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription( final String value ) {
        this.description = value;
    }

    @Override
    public ExtensionElements getExtensionElements() {
        return extensionElements;
    }

    @Override
    public void setExtensionElements(final ExtensionElements value) {
        this.extensionElements = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId( final String value ) {
        this.id = value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel( final String value ) {
        this.label = value;
    }

    public static class TExtensionElements extends KieDMNModelInstrumentedBase implements ExtensionElements {

        private List<Object> any;

        @Override
        public List<Object> getAny() {
            if ( any == null ) {
                any = new ArrayList<>();
            }
            return this.any;
        }

    }

}
