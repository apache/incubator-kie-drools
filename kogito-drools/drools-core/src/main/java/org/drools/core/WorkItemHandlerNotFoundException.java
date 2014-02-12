package org.drools.core;


public class WorkItemHandlerNotFoundException extends RuntimeException {

    private String workItemName;

    public WorkItemHandlerNotFoundException(String message,
                                            String workItemName) {
        super( message );
        this.workItemName = workItemName;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public void setWorkName(String workName) {
        this.workItemName = workName;
    }

}
