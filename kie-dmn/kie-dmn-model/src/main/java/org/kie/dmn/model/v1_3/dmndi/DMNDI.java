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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.model.api.dmndi.DMNDiagram;
import org.kie.dmn.model.api.dmndi.DMNStyle;
import org.kie.dmn.model.api.dmndi.DiagramElement;
import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_3.dmndi.Style.IDREFStubStyle;


public class DMNDI extends KieDMNModelInstrumentedBase implements org.kie.dmn.model.api.dmndi.DMNDI {

    protected List<DMNDiagram> dmnDiagram;
    protected List<DMNStyle> dmnStyle;

    /**
     * Gets the value of the dmnDiagram property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmnDiagram property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDMNDiagram().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DMNDiagram }
     * 
     * 
     */
    @Override
    public List<DMNDiagram> getDMNDiagram() {
        if (dmnDiagram == null) {
            dmnDiagram = new ArrayList<>();
        }
        return this.dmnDiagram;
    }

    /**
     * Gets the value of the dmnStyle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dmnStyle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDMNStyle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DMNStyle }
     * 
     * 
     */
    @Override
    public List<DMNStyle> getDMNStyle() {
        if (dmnStyle == null) {
            dmnStyle = new ArrayList<>();
        }
        return this.dmnStyle;
    }

    @Override
    public void normalize() {
        if (dmnStyle == null || dmnDiagram == null) {
            return;
        }
        Map<String, DMNStyle> styleById = dmnStyle.stream().collect(Collectors.toMap(DMNStyle::getId, Function.identity()));
        for (DMNDiagram diagram : dmnDiagram) {
            for (DiagramElement element : diagram.getDMNDiagramElement()) {
                replaceSharedStyleIfStubbed(element, styleById);
                if (element instanceof DMNShape) {
                    DMNShape dmnShape = (DMNShape) element;
                    replaceSharedStyleIfStubbed(dmnShape.getDMNLabel(), styleById);
                }
            }
        }
    }

    private void replaceSharedStyleIfStubbed(DiagramElement element, Map<String, DMNStyle> styleById) {
        if (element.getSharedStyle() instanceof IDREFStubStyle) {
            DMNStyle locatedStyle = styleById.get(element.getSharedStyle().getId());
            element.setSharedStyle(locatedStyle);
        }
    }

}
