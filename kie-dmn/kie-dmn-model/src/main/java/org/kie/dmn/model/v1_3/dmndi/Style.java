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

import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;
import org.w3c.dom.Element;


public abstract class Style extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.Style {

    protected org.kie.dmn.model.api.dmndi.Style.Extension extension;
    protected String id;

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link Style.Extension }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Style.Extension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link Style.Extension }
     *     
     */
    public void setExtension(org.kie.dmn.model.api.dmndi.Style.Extension value) {
        this.extension = value;
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


    public static class Extension implements org.kie.dmn.model.api.dmndi.Style.Extension {

        protected List<Object> any;

        /**
         * Gets the value of the any property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the any property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAny().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * {@link Element }
         * 
         * 
         */
        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<>();
            }
            return this.any;
        }

    }

    public static class IDREFStubStyle extends Style {

        public IDREFStubStyle(String id) {
            this.id = id;
        }
    }

}
