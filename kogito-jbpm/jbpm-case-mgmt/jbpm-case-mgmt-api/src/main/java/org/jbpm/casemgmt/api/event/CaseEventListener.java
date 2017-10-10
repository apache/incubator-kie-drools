/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

    default void beforeCaseStarted(CaseStartEvent event) {        
    };

    default void afterCaseStarted(CaseStartEvent event) {        
    };
    
    default void beforeCaseClosed(CaseCloseEvent event) {        
    };
    
    default void afterCaseClosed(CaseCloseEvent event) {        
    };

    default void beforeCaseCancelled(CaseCancelEvent event) {        
    };
    
    default void afterCaseCancelled(CaseCancelEvent event) {        
    };

    default void beforeCaseDestroyed(CaseDestroyEvent event) {        
    };
    
    default void afterCaseDestroyed(CaseDestroyEvent event) {        
    };
    
    default void beforeCaseReopen(CaseReopenEvent event) {        
    };

    default void afterCaseReopen(CaseReopenEvent event) {        
    };

    default void beforeCaseCommentAdded(CaseCommentEvent event) {        
    };

    default void afterCaseCommentAdded(CaseCommentEvent event) {        
    };
    
    default void beforeCaseCommentUpdated(CaseCommentEvent event) {        
    };
    
    default void afterCaseCommentUpdated(CaseCommentEvent event) {        
    };
    
    default void beforeCaseCommentRemoved(CaseCommentEvent event) {        
    };
    
    default void afterCaseCommentRemoved(CaseCommentEvent event) {        
    };

    default void beforeCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {        
    };
    
    default void afterCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {        
    };
    
    default void beforeCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {        
    };
    
    default void afterCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {        
    };

    default void beforeCaseDataAdded(CaseDataEvent event) {        
    };
    
    default void afterCaseDataAdded(CaseDataEvent event) {        
    };
    
    default void beforeCaseDataRemoved(CaseDataEvent event) {        
    };
    
    default void afterCaseDataRemoved(CaseDataEvent event) {        
    };

    default void beforeDynamicTaskAdded(CaseDynamicTaskEvent event) {        
    };
    
    default void afterDynamicTaskAdded(CaseDynamicTaskEvent event) {        
    };

    default void beforeDynamicProcessAdded(CaseDynamicSubprocessEvent event) {        
    };
    
    default void afterDynamicProcessAdded(CaseDynamicSubprocessEvent event) {        
    };
}
