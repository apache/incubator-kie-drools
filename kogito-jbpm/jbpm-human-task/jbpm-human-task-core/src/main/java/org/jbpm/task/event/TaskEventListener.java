/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.task.event;

import java.util.EventListener;

import org.jbpm.task.event.entity.TaskUserEvent;
/* 
 * Events Defined by the WS-HT Specification 1.1 Page 61
 */
public interface TaskEventListener extends EventListener {
    /* 
     * State Changed : Yes
     * Owner Changed : Maybe
     */ 
    void taskCreated(TaskUserEvent event);
    /* 
     * State Changed : Yes
     * Owner Changed : Yes
     */ 
    void taskClaimed(TaskUserEvent event);
    /*
     * State Changed : Yes
     * Owner Changed : Maybe
     */
    void taskStarted(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     */ 
    void taskStopped(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     * Owner Changed : Yes
     */
    void taskReleased(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     */
    void taskCompleted(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     */
    void taskFailed(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     */
    void taskSkipped(TaskUserEvent event);
    /* 
     * State Changed : Yes 
     * Owner Changed : Maybe
     */
    void taskForwarded(TaskUserEvent event);
    
    // void taskSuspended(TaskSuspendedEvent event);
    // void taskSuspendedUntil(TaskSuspendedUntilEvent event);
    // void taskResumed(TaskResumedEvent event);   
    // void taskRemoved(TaskRemovedEvent event); 
    // void taskPrioritySet(TaskPrioritySetEvent event); 
    // void taskAddedAttachment(TaskAddedAttachmentEvent event); 
    // void taskDeletededAttachment(TaskDeletedAttachmentEvent event);
    // void taskAddedComment(TaskAddedCommentEvent event);
    // void taskDeletedComment(TaskDeletedCommentEvent event);
    // void taskDeleageted(TaskDelegatedEvent event);
    // void taskOutputSet(TaskOutputSetEvent event);
    // void taskDeletedOutput(TaskDeletedOutputEvent event);
    // void taskFaultSet(TaskFaultSetEvent event);
    // void taskDeletedFault(TaskDeletedFaultEvent event);
    // void taskActivated(TaskActivatedEvent event);
    // void taskNominated(TaskNominatedEvent event);
    // void taskGenericHumanRoleSet(TaskGenericHumanRoleSetEvent event); ??
    // void taskExpired(TaskExpiredEvent event);
    // void taskEscalated(TaskEscalatedEvent event);
    // void taskCanceled(TaskCanceledEvent event);
}
