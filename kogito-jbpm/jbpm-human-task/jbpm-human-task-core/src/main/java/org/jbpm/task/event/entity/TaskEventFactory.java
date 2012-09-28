package org.jbpm.task.event.entity;

public class TaskEventFactory {

   public static TaskUserEvent createCreatedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskCreatedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createClaimedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskClaimedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createCompletedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskCompletedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createFailedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskFailedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createForwardedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskForwardedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createReleasedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskReleasedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createSkippedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskSkippedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createStartedEvent(long taskId, String userId, final int sessionId) { 
       return new TaskStartedEvent(taskId, userId, sessionId);
   }
   
   public static TaskUserEvent createStoppedEvent(long taskId, String userId, final int sessionId) { 
      return new TaskStoppedEvent(taskId, userId, sessionId);
   }

}
