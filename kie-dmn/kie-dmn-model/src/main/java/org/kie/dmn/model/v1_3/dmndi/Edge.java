package org.kie.dmn.model.v1_3.dmndi;

import java.util.ArrayList;
import java.util.List;

public abstract class Edge extends DiagramElement implements org.kie.dmn.model.api.dmndi.Edge {

    protected List<org.kie.dmn.model.api.dmndi.Point> waypoint;

    /**
     * Gets the value of the waypoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the waypoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWaypoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Point }
     * 
     * 
     */
    public List<org.kie.dmn.model.api.dmndi.Point> getWaypoint() {
        if (waypoint == null) {
            waypoint = new ArrayList<>();
        }
        return this.waypoint;
    }

}
