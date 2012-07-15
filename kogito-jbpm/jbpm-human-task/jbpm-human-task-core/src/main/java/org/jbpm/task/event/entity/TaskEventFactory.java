package org.jbpm.task.event.entity;

public class TaskEventFactory {

   public static TaskUserEvent createCreatedEvent(long taskId, String userId) { 
      return new TaskCreatedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createClaimedEvent(long taskId, String userId) { 
      return new TaskClaimedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createCompletedEvent(long taskId, String userId) { 
      return new TaskCompletedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createFailedEvent(long taskId, String userId) { 
      return new TaskFailedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createForwardedEvent(long taskId, String userId) { 
      return new TaskForwardedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createReleasedEvent(long taskId, String userId) { 
      return new TaskReleasedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createSkippedEvent(long taskId, String userId) { 
      return new TaskSkippedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createStartedEvent(long taskId, String userId) { 
       return new TaskStartedEvent(taskId, userId);
   }
   
   public static TaskUserEvent createStoppedEvent(long taskId, String userId) { 
      return new TaskStoppedEvent(taskId, userId);
   }

}
