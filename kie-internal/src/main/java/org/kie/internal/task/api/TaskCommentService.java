/**
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
package org.kie.internal.task.api;

import java.util.List;

import org.kie.api.task.model.Comment;

/**
 * The Task Comment Service will handle all the
 *  operations related with the Comments associated with
 *  a Task
 */
public interface TaskCommentService {

    long addComment(long taskId, Comment comment);

    void deleteComment(long taskId, long commentId);

    List<Comment> getAllCommentsByTaskId(long taskId);

    Comment getCommentById(long commentId);
}
