package org.drools;


public class WorkItemHandlerNotFoundException extends RuntimeDroolsException {

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
