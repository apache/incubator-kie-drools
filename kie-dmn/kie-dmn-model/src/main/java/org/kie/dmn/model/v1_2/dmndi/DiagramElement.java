//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.08.06 at 04:40:00 PM CEST 
//


package org.kie.dmn.model.v1_2.dmndi;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

public abstract class DiagramElement implements org.kie.dmn.model.v1x.dmndi.DiagramElement {

    protected org.kie.dmn.model.v1x.dmndi.DiagramElement.Extension extension;
    protected org.kie.dmn.model.v1x.dmndi.Style style;
    protected Object sharedStyle;
    protected String id;

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link DiagramElement.Extension }
     *     
     */
    public org.kie.dmn.model.v1x.dmndi.DiagramElement.Extension getExtension() {
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
    public void setExtension(org.kie.dmn.model.v1x.dmndi.DiagramElement.Extension value) {
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
    public org.kie.dmn.model.v1x.dmndi.Style getStyle() {
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
    public void setStyle(org.kie.dmn.model.v1x.dmndi.Style value) {
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
    public Object getSharedStyle() {
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
    public void setSharedStyle(Object value) {
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



    public static class Extension implements org.kie.dmn.model.v1x.dmndi.DiagramElement.Extension {

        protected List<Object> any;

        public List<Object> getAny() {
            if (any == null) {
                any = new ArrayList<Object>();
            }
            return this.any;
        }

    }

}
