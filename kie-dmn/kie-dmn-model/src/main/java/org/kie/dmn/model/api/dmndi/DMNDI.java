package org.kie.dmn.model.api.dmndi;

import java.util.List;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface DMNDI extends DMNModelInstrumentedBase {

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
    List<DMNDiagram> getDMNDiagram();

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
    List<DMNStyle> getDMNStyle();

    /**
     * Internal model: mutates the current DMNDI to resolve xml's IDREFs
     */
    void normalize();
}
