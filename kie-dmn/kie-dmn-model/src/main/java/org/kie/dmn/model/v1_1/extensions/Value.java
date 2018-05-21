package org.kie.dmn.model.v1_1.extensions;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;

public class Value extends DMNModelInstrumentedBase {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
