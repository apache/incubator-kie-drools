package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.NamedElement;

public class TNamedElement extends TDMNElement implements NamedElement {

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

}
