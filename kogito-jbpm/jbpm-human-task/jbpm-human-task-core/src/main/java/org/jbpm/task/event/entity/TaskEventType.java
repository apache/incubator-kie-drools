package org.jbpm.task.event.entity;

public enum TaskEventType {
    Create("cr"),   
    Claim("cl"), 
    Stop("st"),   // finished
    Complete("co"), 
    Fail("fa"), 
    Forward("fo"), 
    Release("re"), 
    Skipped("sk"),  
    
    Started("be"),  // begun
    Suspended("ss"),  
    SuspendedUntil("su"), 
    Resume("rs"), 
    Removed("rm"),
    SetPriority("sp"),
    
    AddedAttachment("aa"),
    DeletedAttachment("da"), 
    AddedComment("ac"),
    UpdatedComment("uc"),
    
    Delegated("de"),
    SetOutput("so"),
    DeleteOutput("do"),
    SetFault("sf"), 
    DeleteFault("df"),
    Activate("at"),
    Nominate("no"),
    SetGenericHumanRole("sr"),
    Expire("ex"),
    Escalated("es"),
    Cancel("ca"),
    
    UnknownUserEvent("us");
    
    private String type;
    
    private TaskEventType(String t) {
        type = t;
    }
    
    public String getValue() { 
        return type;
    }

    public static TaskEventType getTypeFromValue(String type) {
        int hashCode = type.hashCode();
        switch(hashCode) {
          case 3123:
              return TaskEventType.Activate;
          case 3104:
              return TaskEventType.AddedAttachment;
          case 3106:
              return TaskEventType.AddedComment;
          case 3166:
              return TaskEventType.Cancel;
          case 3177:
              return TaskEventType.Claim;
          case 3180:
              return TaskEventType.Complete;
          case 3183:
              return TaskEventType.Create;
          case 3201:
              return TaskEventType.Delegated;
          case 3202:
              return TaskEventType.DeleteFault;
          case 3211:
              return TaskEventType.DeleteOutput;
          case 3197:
              return TaskEventType.DeletedAttachment;
          case 3246:
              return TaskEventType.Escalated;
          case 3251:
              return TaskEventType.Expire;
          case 3259:
              return TaskEventType.Fail;
          case 3273:
              return TaskEventType.Forward;
          case 3521:
              return TaskEventType.Nominate;
          case 3635:
              return TaskEventType.Release;
          case 3643:
              return TaskEventType.Removed;
          case 3649:
              return TaskEventType.Resume;
          case 3667:
              return TaskEventType.SetFault;
          case 3679:
              return TaskEventType.SetGenericHumanRole;
          case 3676:
              return TaskEventType.SetOutput;
          case 3677:
              return TaskEventType.SetPriority;
          case 3672:
              return TaskEventType.Skipped;
          case 3139:
              return TaskEventType.Started;
          case 3681:
              return TaskEventType.Stop;
          case 3680:
              return TaskEventType.Suspended;
          case 3682:
              return TaskEventType.SuspendedUntil;
          case 3742:
              return TaskEventType.UnknownUserEvent;
          case 3726:
              return TaskEventType.UpdatedComment;
          default:
            throw new IllegalStateException("Unknown type: " + type );
        }
    }
}
