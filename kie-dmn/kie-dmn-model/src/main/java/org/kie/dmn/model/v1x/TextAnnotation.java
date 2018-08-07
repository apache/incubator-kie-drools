package org.kie.dmn.model.v1x;

public interface TextAnnotation extends Artifact {

    String getText();

    void setText(String value);

    String getTextFormat();

    void setTextFormat(String value);

}
