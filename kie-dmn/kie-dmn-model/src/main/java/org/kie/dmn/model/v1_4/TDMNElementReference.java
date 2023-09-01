package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.DMNElementReference;

public class TDMNElementReference extends KieDMNModelInstrumentedBase implements DMNElementReference {

    private String href;

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String value) {
        this.href = value;
    }

}
