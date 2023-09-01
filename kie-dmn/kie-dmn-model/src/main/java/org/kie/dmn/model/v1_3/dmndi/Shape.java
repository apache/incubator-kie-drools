package org.kie.dmn.model.v1_3.dmndi;

public abstract class Shape extends DiagramElement implements org.kie.dmn.model.api.dmndi.Shape {

    protected org.kie.dmn.model.api.dmndi.Bounds bounds;

    /**
     * the optional bounds of the shape relative to the origin of its nesting plane.
     * 
     * @return
     *     possible object is
     *     {@link Bounds }
     *     
     */
    public org.kie.dmn.model.api.dmndi.Bounds getBounds() {
        return bounds;
    }

    /**
     * Sets the value of the bounds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Bounds }
     *     
     */
    public void setBounds(org.kie.dmn.model.api.dmndi.Bounds value) {
        this.bounds = value;
    }

}
