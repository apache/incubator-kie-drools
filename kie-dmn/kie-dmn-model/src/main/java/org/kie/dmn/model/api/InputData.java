package org.kie.dmn.model.api;

public interface InputData extends DRGElement {

    InformationItem getVariable();

    void setVariable(InformationItem value);

}
