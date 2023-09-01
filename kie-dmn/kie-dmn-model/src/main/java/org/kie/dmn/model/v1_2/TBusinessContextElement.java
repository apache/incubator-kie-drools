package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.BusinessContextElement;

public class TBusinessContextElement extends TNamedElement implements BusinessContextElement {

    protected String uri;

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public void setURI(String value) {
        this.uri = value;
    }

}
