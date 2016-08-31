/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.api.event;

import java.util.EventListener;

/**
 * Case event listener that is invoked upon various
 * operation related to a case.
 *
 */
public interface CaseEventListener extends EventListener {

    void beforeCaseStarted(CaseStartEvent event);

    void afterCaseStarted(CaseStartEvent event);

    void beforeCaseCancelled(CaseCancelEvent event);
    
    void afterCaseCancelled(CaseCancelEvent event);

    void beforeCaseDestroyed(CaseDestroyEvent event);
    
    void afterCaseDestroyed(CaseDestroyEvent event);

    void beforeCaseCommentAdded(CaseCommentEvent event);

    void afterCaseCommentAdded(CaseCommentEvent event);
    
    void beforeCaseCommentUpdated(CaseCommentEvent event);
    
    void afterCaseCommentUpdated(CaseCommentEvent event);
    
    void beforeCaseCommentRemoved(CaseCommentEvent event);
    
    void afterCaseCommentRemoved(CaseCommentEvent event);

    void beforeCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event);
    
    void afterCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event);
    
    void beforeCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event);
    
    void afterCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event);

    void beforeCaseDataAdded(CaseDataEvent event);
    
    void afterCaseDataAdded(CaseDataEvent event);
    
    void beforeCaseDataRemoved(CaseDataEvent event);
    
    void afterCaseDataRemoved(CaseDataEvent event);

    void beforeDynamicTaskAdded(CaseDynamicTaskEvent event);
    
    void afterDynamicTaskAdded(CaseDynamicTaskEvent event);

    void beforeDynamicProcessAdded(CaseDynamicSubprocessEvent event);
    
    void afterDynamicProcessAdded(CaseDynamicSubprocessEvent event);
}
