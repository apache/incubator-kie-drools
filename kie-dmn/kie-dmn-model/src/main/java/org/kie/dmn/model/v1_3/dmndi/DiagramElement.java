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
package org.kie.dmn.model.v1_3.dmndi;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;

import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;

public abstract class DiagramElement extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.DiagramElement {

    protected org.kie.dmn.model.api.dmndi.DiagramElement.Extension extension;
    protected org.kie.dmn.model.api.dmndi.Style style;
    protected org.kie.dmn.model.api.dmndi.Style sharedStyle;
    protected String id;

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link DiagramElement.Extension }
     *     
     */
    public org.kie.dmn.model.api.dmndi.DiagramElement.Extension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiagramElement.Extension }
     *     
     */
    public void setExtension(org.kie.dmn.model.api.dmndi.DiagramElement.Extension value) {
        this.extension = value;
    }

    /**
     * an optional locally-owned style for this diagram element.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DMNStyle }{@code >}
     *     {@link JAXBElement }{@code <}{@link Style }{@code >}
     *     
     */
    public org.kie.dmn.model.api.dmndi.Style getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DMNStyle }{@code >}
     *     {@link JAXBElement }{@code <}{@link Style }{@code >}
     *     
     */
    public void setStyle(org.kie.dmn.model.api.dmndi.Style value) {
        this.style = value;
    }

    /**
     * Gets the value of the sharedStyle property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Style getSharedStyle() {
        return sharedStyle;
    }

    /**
     * Sets the value of the sharedStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSharedStyle(org.kie.dmn.model.api.dmndi.Style value) {
        this.sharedStyle = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }



    public static class Extension extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.DiagramElement.Extension {

        protected List<Object> any;

        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }

    }

}
