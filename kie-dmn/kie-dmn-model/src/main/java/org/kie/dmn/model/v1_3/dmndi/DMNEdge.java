package org.kie.dmn.model.v1_3.dmndi;

import javax.xml.namespace.QName;

public class DMNEdge extends Edge implements org.kie.dmn.model.api.dmndi.DMNEdge {

    protected org.kie.dmn.model.api.dmndi.DMNLabel dmnLabel;
    protected QName dmnElementRef;
    protected QName sourceElement;
    protected QName targetElement;

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
        return sourceElement;
    }

    @Override
    public void setSourceElement(QName value) {
        this.sourceElement = value;
    }

    @Override
    public QName getTargetElement() {
        return targetElement;
    }

    @Override
    public void setTargetElement(QName value) {
        this.targetElement = value;
    }

}
