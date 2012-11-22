package org.jbpm.task;

public class MvelFilePath {

    public static final String LoadGroups               = "/org/jbpm/task/LoadGroups.mvel";
    public static final String LoadUsers                = "/org/jbpm/task/LoadUsers.mvel";
    
    public static final String PeopleAssignmentQuerries = "/org/jbpm/task/service/QueryResults_PeopleAssignmentQuerries.mvel";
    public static final String TasksOwned               = "/org/jbpm/task/service/QueryData_TasksOwned.mvel";
    public static final String TasksOwnedInEnglish      = "/org/jbpm/task/service/QueryResults_TasksOwnedInEnglish.mvel";
    public static final String TasksOwnedInGerman       = "/org/jbpm/task/service/QueryResults_TasksOwnedInGerman.mvel";
    public static final String TasksPotentialOwner      = "/org/jbpm/task/service/QueryData_TasksPotentialOwner.mvel";
    public static final String UnescalatedDeadlines     = "/org/jbpm/task/QueryData_UnescalatedDeadlines.mvel";
    
    public static final String DeadlineWithReassignment = "/org/jbpm/task/service/DeadlineWithReassignment.mvel";
    public static final String DeadlineWithNotification = "/org/jbpm/task/service/DeadlineWithNotification.mvel";
    public static final String DeadlineWithNotificationContentSingleObject = "/org/jbpm/task/service/DeadlineWithNotificationContentSingleObject.mvel";
    public static final String DeadlineWithNotificationFromReplyTo = "/org/jbpm/task/service/DeadlineWithNotificationWithFromAndReplyTo.mvel";
    
    public static final String FullyPopulatedTask = "/org/jbpm/task/FullyPopulatedTask.mvel";
}
