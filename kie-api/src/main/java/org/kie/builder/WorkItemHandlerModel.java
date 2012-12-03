package org.kie.builder;

public interface WorkItemHandlerModel {

    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
