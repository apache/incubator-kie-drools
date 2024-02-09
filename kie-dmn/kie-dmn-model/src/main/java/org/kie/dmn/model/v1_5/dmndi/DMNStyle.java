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
package org.kie.dmn.model.v1_5.dmndi;

import org.kie.dmn.model.api.dmndi.AlignmentKind;

public class DMNStyle extends Style implements org.kie.dmn.model.api.dmndi.DMNStyle {

    protected org.kie.dmn.model.api.dmndi.Color fillColor;
    protected org.kie.dmn.model.api.dmndi.Color strokeColor;
    protected org.kie.dmn.model.api.dmndi.Color fontColor;
    protected String fontFamily;
    protected Double fontSize;
    protected Boolean fontItalic;
    protected Boolean fontBold;
    protected Boolean fontUnderline;
    protected Boolean fontStrikeThrough;
    protected AlignmentKind labelHorizontalAlignement;
    protected AlignmentKind labelVerticalAlignment;

    /**
     * Gets the value of the fillColor property.
     * 
     * @return
     *     possible object is
     *     {@link Color }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets the value of the fillColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Color }
     *     
     */
    public void setFillColor(org.kie.dmn.model.api.dmndi.Color value) {
        this.fillColor = value;
    }

    /**
     * Gets the value of the strokeColor property.
     * 
     * @return
     *     possible object is
     *     {@link Color }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Color getStrokeColor() {
        return strokeColor;
    }

    /**
     * Sets the value of the strokeColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Color }
     *     
     */
    public void setStrokeColor(org.kie.dmn.model.api.dmndi.Color value) {
        this.strokeColor = value;
    }

    /**
     * Gets the value of the fontColor property.
     * 
     * @return
     *     possible object is
     *     {@link Color }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Color getFontColor() {
        return fontColor;
    }

    /**
     * Sets the value of the fontColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Color }
     *     
     */
    public void setFontColor(org.kie.dmn.model.api.dmndi.Color value) {
        this.fontColor = value;
    }

    /**
     * Gets the value of the fontFamily property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFontFamily() {
        return fontFamily;
    }

    /**
     * Sets the value of the fontFamily property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFontFamily(String value) {
        this.fontFamily = value;
    }

    /**
     * Gets the value of the fontSize property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getFontSize() {
        return fontSize;
    }

    /**
     * Sets the value of the fontSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setFontSize(Double value) {
        this.fontSize = value;
    }

    /**
     * Gets the value of the fontItalic property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFontItalic() {
        return fontItalic;
    }

    /**
     * Sets the value of the fontItalic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFontItalic(Boolean value) {
        this.fontItalic = value;
    }

    /**
     * Gets the value of the fontBold property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFontBold() {
        return fontBold;
    }

    /**
     * Sets the value of the fontBold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFontBold(Boolean value) {
        this.fontBold = value;
    }

    /**
     * Gets the value of the fontUnderline property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFontUnderline() {
        return fontUnderline;
    }

    /**
     * Sets the value of the fontUnderline property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFontUnderline(Boolean value) {
        this.fontUnderline = value;
    }

    /**
     * Gets the value of the fontStrikeThrough property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFontStrikeThrough() {
        return fontStrikeThrough;
    }

    /**
     * Sets the value of the fontStrikeThrough property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFontStrikeThrough(Boolean value) {
        this.fontStrikeThrough = value;
    }

    /**
     * Gets the value of the labelHorizontalAlignement property.
     * 
     * @return
     *     possible object is
     *     {@link AlignmentKind }
     *     
     */
    public AlignmentKind getLabelHorizontalAlignement() {
        return labelHorizontalAlignement;
    }

    /**
     * Sets the value of the labelHorizontalAlignement property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlignmentKind }
     *     
     */
    public void setLabelHorizontalAlignement(AlignmentKind value) {
        this.labelHorizontalAlignement = value;
    }

    /**
     * Gets the value of the labelVerticalAlignment property.
     * 
     * @return
     *     possible object is
     *     {@link AlignmentKind }
     *     
     */
    public AlignmentKind getLabelVerticalAlignment() {
        return labelVerticalAlignment;
    }

    /**
     * Sets the value of the labelVerticalAlignment property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlignmentKind }
     *     
     */
    public void setLabelVerticalAlignment(AlignmentKind value) {
        this.labelVerticalAlignment = value;
    }

}
