package org.kie.dmn.model.api;

public interface TextAnnotation extends Artifact {

    String getText();

    void setText(String value);

    String getTextFormat();

    void setTextFormat(String value);

}
