package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.NamedElement;

public abstract class TNamedElement extends TDMNElement implements NamedElement {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( final String value ) {
        this.name = value;
    }

}
