/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.task.api;

import java.util.List;
import java.util.Map;

import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;

/**
 * The Task Content Service is intended to handle
 *  the information which is stored inside a Task.
 *  The information inside the Task Content represents
 *   the information required for the Task to be completed.
 *  This information can be divided into:
 *      - Task Input: The data used by the Human Actor
 *                    as a context to do the job
 *      - Task Output: The data entered by the Human Actor
 *                   as the result of the work that is being done.
 */

public interface TaskContentService {

    long setDocumentContent(long taskId, Content content);
    
    long addOutputContent(long taskId, Map<String, Object> params);
    
    void deleteDocumentContent(long taskId, long contentId);

    List<Content> getAllContentByTaskId(long taskId);

    Content getContentById(long contentId);
    
    void addMarshallerContext(String ownerId, ContentMarshallerContext context);
    
    void removeMarshallerContext(String ownerId);
    
    ContentMarshallerContext getMarshallerContext(Task task);
    
    Task loadTaskVariables(Task task);
}
