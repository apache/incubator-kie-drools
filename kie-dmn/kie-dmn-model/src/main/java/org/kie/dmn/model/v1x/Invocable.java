package org.kie.dmn.model.v1x;

/**
 * @since DMN v1.2
 */
public interface Invocable extends DRGElement {

    /**
     * Gets the value of the variable property.
     * 
     * @return
     *     possible object is
     *     {@link InformationItem }
     *     
     */
    InformationItem getVariable();

    /**
     * Sets the value of the variable property.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationItem }
     *     
     */
    void setVariable(InformationItem value);

}
