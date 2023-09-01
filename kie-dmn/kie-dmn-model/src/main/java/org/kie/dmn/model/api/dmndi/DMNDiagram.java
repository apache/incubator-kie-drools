package org.kie.dmn.model.api.dmndi;

import java.util.List;


public interface DMNDiagram extends Diagram {

    public Dimension getSize();

    public void setSize(Dimension value);

    public List<DiagramElement> getDMNDiagramElement();

}
