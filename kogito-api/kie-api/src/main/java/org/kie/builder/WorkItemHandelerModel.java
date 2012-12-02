package org.kie.builder;

public interface WorkItemHandelerModel {

    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
