/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_3.dmndi;

import javax.xml.namespace.QName;

public class DMNShape extends Shape implements org.kie.dmn.model.api.dmndi.DMNShape {

    protected org.kie.dmn.model.api.dmndi.DMNLabel dmnLabel;
    protected org.kie.dmn.model.api.dmndi.DMNDecisionServiceDividerLine dmnDecisionServiceDividerLine;
    protected QName dmnElementRef;
    protected Boolean isListedInputData;
    protected Boolean isCollapsed;

    /**
     * Gets the value of the dmnLabel property.
     * 
     * @return
     *     possible object is
     *     {@link DMNLabel }
     *     
     */
    public org.kie.dmn.model.api.dmndi.DMNLabel getDMNLabel() {
        return dmnLabel;
    }

    /**
     * Sets the value of the dmnLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link DMNLabel }
     *     
     */
    public void setDMNLabel(org.kie.dmn.model.api.dmndi.DMNLabel value) {
        this.dmnLabel = value;
    }

    /**
     * Gets the value of the dmnDecisionServiceDividerLine property.
     * 
     * @return
     *     possible object is
     *     {@link DMNDecisionServiceDividerLine }
     *     
     */
    public org.kie.dmn.model.api.dmndi.DMNDecisionServiceDividerLine getDMNDecisionServiceDividerLine() {
        return dmnDecisionServiceDividerLine;
    }

    /**
     * Sets the value of the dmnDecisionServiceDividerLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link DMNDecisionServiceDividerLine }
     *     
     */
    public void setDMNDecisionServiceDividerLine(org.kie.dmn.model.api.dmndi.DMNDecisionServiceDividerLine value) {
        this.dmnDecisionServiceDividerLine = value;
    }

    /**
     * Gets the value of the dmnElementRef property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getDmnElementRef() {
        return dmnElementRef;
    }

    /**
     * Sets the value of the dmnElementRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setDmnElementRef(QName value) {
        this.dmnElementRef = value;
    }

    /**
     * Gets the value of the isListedInputData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsListedInputData() {
        return isListedInputData;
    }

    /**
     * Sets the value of the isListedInputData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsListedInputData(Boolean value) {
        this.isListedInputData = value;
    }

    /**
     * Gets the value of the isCollapsed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsCollapsed() {
        if (isCollapsed == null) {
            return false;
        } else {
            return isCollapsed;
        }
    }

    /**
     * Sets the value of the isCollapsed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCollapsed(Boolean value) {
        this.isCollapsed = value;
    }

}
