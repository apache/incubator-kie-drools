package org.kie.dmn.model.api.dmndi;

import java.util.List;


public interface Edge extends DiagramElement {

    public List<Point> getWaypoint();
}
