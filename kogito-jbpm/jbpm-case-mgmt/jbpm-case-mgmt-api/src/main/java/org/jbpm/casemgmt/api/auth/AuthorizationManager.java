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

package org.jbpm.casemgmt.api.auth;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;

/**
 * Responsible for authorizing access to case instances based on the context.
 *
 */
public interface AuthorizationManager {
    
    public static final String PUBLIC_GROUP = "_public_";
    
    public static final String OWNER_ROLE = "owner";
    public static final String ADMIN_ROLE = "admin";

    public static final String UNKNOWN_USER = "unknown";
    
    public enum ProtectedOperation {
        CLOSE_CASE,
        CANCEL_CASE,
        DESTROY_CASE,
        REOPEN_CASE,
        ADD_TASK_TO_CASE,
        ADD_PROCESS_TO_CASE,
        ADD_DATA,
        REMOVE_DATA,
        MODIFY_ROLE_ASSIGNMENT,
        MODIFY_COMMENT
    }

    /**
     * Checks if the caller (based on identity provider) is authorized to work with a case identified by caseId
     * @param caseId unique id of the case
     * @throws SecurityException thrown when caller is not authorized to access the case instance
     */
    void checkAuthorization(String caseId) throws SecurityException;
    
    /**
     * Checks if the caller (based on identity provider) is authorized to perform given operation on a case.
     * @param caseId unique id of the case
     * @param operation operations that is being invoked
     * @throws SecurityException thrown when caller is not authorized to access the case instance
     */
    void checkOperationAuthorization(String caseId, ProtectedOperation operation) throws SecurityException;
    
    /**
     * Filters provided data by data restrictions. This guarantees only data authorized for given user will be returned.
     * @param caseId unique id of the case
     * @param caseFileInstance case file associated with given case instance
     * @param data actual data to be filtered
     * @return returns filtered map of data if any restriction applied
     */
    Map<String, Object> filterByDataAuthorization(String caseId, CaseFileInstance caseFileInstance, Map<String, Object> data);
    
    /**
     * Check if caller (based on identity provider) is authorized to manipulate given data
     * @param caseId unique id of the case
     * @param caseFileInstance case file associated with given case instance
     * @param dataNames data names to be manipulated/put into the case instance
     * @throws SecurityException thrown when caller is not authorized to manipulate any of the given data
     */
    void checkDataAuthorization(String caseId, CaseFileInstance caseFileInstance, Collection<String> dataNames);
    
    /**
     * Filters provided comments by their restrictions. This guarantees only comments authorized to be seen by user will be returned.
     * @param caseId unique id of the case
     * @param caseFileInstance case file associated with given case instance
     * @param comments comments to be filtered
     * @return filtered comments if any restrictions applied or same as given as argument
     */
    List<CommentInstance> filterByCommentAuthorization(String caseId, CaseFileInstance caseFileInstance, List<CommentInstance> comments);
    
    /**
     * Check if caller (based on identity provider) is authorized to manipulate given comment
     * @param caseId unique id of the case
     * @param caseFileInstance case file associated with given case instance
     * @param commentInstance comment that is about to be manipulated (updated or removed)
     * @throws SecurityException thrown when caller is not authorized to manipulate given comment
     */
    void checkCommentAuthorization(String caseId, CaseFileInstance caseFileInstance, CommentInstance commentInstance);
}
