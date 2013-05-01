package org.kie.api.builder.model;

/**
 * WorkItemHandlerModel is a model allowing to programmatically define a WorkItemHandler and wire it to a KieSession
 */
public interface WorkItemHandlerModel {

    /**
     * Returns the name of the work item that the handler is for
     * @return
     */
    String getName();
    /**
     * Returns the type of this WorkItemHandlerModel
     * (i.e. the name of the class implementing the WorkItemHandler)
     */
    String getType();

    QualifierModel getQualifierModel();

    QualifierModel newQualifierModel(String type);
}
