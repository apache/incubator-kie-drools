package org.kie.dmn.model.api.dmndi;

public interface Diagram extends DiagramElement {

    public String getName();

    public void setName(String value);

    public String getDocumentation();

    public void setDocumentation(String value);

    public Double getResolution();

    public void setResolution(Double value);

}
