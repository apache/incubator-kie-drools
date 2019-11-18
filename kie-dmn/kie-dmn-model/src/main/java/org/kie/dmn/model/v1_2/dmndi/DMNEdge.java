/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_2.dmndi;

import javax.xml.namespace.QName;

public class DMNEdge extends Edge implements org.kie.dmn.model.api.dmndi.DMNEdge {

    protected org.kie.dmn.model.api.dmndi.DMNLabel dmnLabel;
    protected QName dmnElementRef;

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

    @Override
    public QName getSourceElement() {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }

    @Override
    public void setSourceElement(QName value) {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }

    @Override
    public QName getTargetElement() {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }

    @Override
    public void setTargetElement(QName value) {
        throw new UnsupportedOperationException("Since DMNv1.3");
    }

}
