package org.kie.builder;

public interface ListenerModel {

    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
