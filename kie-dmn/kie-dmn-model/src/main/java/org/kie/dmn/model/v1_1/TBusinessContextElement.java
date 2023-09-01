package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.BusinessContextElement;

public abstract class TBusinessContextElement extends TNamedElement implements BusinessContextElement {

    private String uri;

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public void setURI( final String value ) {
        this.uri = value;
    }

}
