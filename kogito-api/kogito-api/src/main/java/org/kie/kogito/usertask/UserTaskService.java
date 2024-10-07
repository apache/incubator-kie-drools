/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;
import org.kie.kogito.usertask.view.UserTaskTransitionView;
import org.kie.kogito.usertask.view.UserTaskView;

public interface UserTaskService {

    Optional<UserTaskView> getUserTaskInstance(String taskId, IdentityProvider identity);

    List<UserTaskView> list(IdentityProvider identity);

    Optional<UserTaskView> transition(String taskId, String transitionId, Map<String, Object> data, IdentityProvider identity);

    List<UserTaskTransitionView> allowedTransitions(String taskId, IdentityProvider identity);

    Optional<UserTaskView> setOutputs(String taskId, Map<String, Object> data, IdentityProvider identity);

    Optional<UserTaskView> setInputs(String taskId, Map<String, Object> data, IdentityProvider identity);

    List<Comment> getComments(String taskId, IdentityProvider identity);

    Optional<Comment> getComment(String taskId, String commentId, IdentityProvider identity);

    Optional<Comment> addComment(String taskId, Comment comment, IdentityProvider identity);

    Optional<Comment> updateComment(String taskId, Comment comment, IdentityProvider identity);

    Optional<Comment> removeComment(String taskId, String commentId, IdentityProvider identity);

    List<Attachment> getAttachments(String taskId, IdentityProvider identity);

    Optional<Attachment> getAttachment(String taskId, String commentId, IdentityProvider identity);

    Optional<Attachment> addAttachment(String taskId, Attachment comment, IdentityProvider identity);

    Optional<Attachment> updateAttachment(String taskId, Attachment comment, IdentityProvider identity);

    Optional<Attachment> removeAttachment(String taskId, String commentId, IdentityProvider identity);
}
