package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.DMNElementReference;

public class TDMNElementReference extends KieDMNModelInstrumentedBase implements DMNElementReference {

    private String href;

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref( final String value ) {
        this.href = value;
    }

}
