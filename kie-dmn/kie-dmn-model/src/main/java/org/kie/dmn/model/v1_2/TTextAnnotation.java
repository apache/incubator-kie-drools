package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.TextAnnotation;

public class TTextAnnotation extends TArtifact implements TextAnnotation {


    private static final String DEFAULT_TEXT_FORMAT = "text/plain";

    private String text;
    private String textFormat;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(final String value) {
        this.text = value;
    }

    @Override
    public String getTextFormat() {
        if (textFormat == null) {
            return DEFAULT_TEXT_FORMAT;
        } else {
            return textFormat;
        }
    }

    @Override
    public void setTextFormat(final String value) {
        this.textFormat = value;
    }

}
