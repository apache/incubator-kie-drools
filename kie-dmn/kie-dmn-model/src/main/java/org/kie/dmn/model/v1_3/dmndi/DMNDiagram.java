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

public class DMNDiagram extends Diagram implements org.kie.dmn.model.api.dmndi.DMNDiagram {

    protected org.kie.dmn.model.api.dmndi.Dimension size;
    protected List<org.kie.dmn.model.api.dmndi.DiagramElement> dmnDiagramElement;

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Dimension }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Dimension getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dimension }
     *     
     */
    public void setSize(org.kie.dmn.model.api.dmndi.Dimension value) {
        this.size = value;
    }

    /**
     * Gets the value of the dmnDiagramElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmnDiagramElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDMNDiagramElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DMNShape }{@code >}
     * {@link JAXBElement }{@code <}{@link DiagramElement }{@code >}
     * {@link JAXBElement }{@code <}{@link DMNEdge }{@code >}
     * 
     * 
     */
    public List<org.kie.dmn.model.api.dmndi.DiagramElement> getDMNDiagramElement() {
        if (dmnDiagramElement == null) {
            dmnDiagramElement = new ArrayList<>();
        }
        return this.dmnDiagramElement;
    }

    @Override
    public Boolean getUseAlternativeInputDataShape() {
        throw new UnsupportedOperationException("Since DMNv1.5");
    }

    @Override
    public void setUseAlternativeInputDataShape(Boolean value) {
        throw new UnsupportedOperationException("Since DMNv1.5");
    }

}
