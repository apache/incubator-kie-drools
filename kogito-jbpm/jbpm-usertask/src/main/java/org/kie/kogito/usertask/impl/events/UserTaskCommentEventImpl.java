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

package org.kie.kogito.usertask.impl.events;

import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.events.UserTaskCommentEvent;
import org.kie.kogito.usertask.model.Comment;

public class UserTaskCommentEventImpl extends UserTaskEventImpl implements UserTaskCommentEvent {

    private static final long serialVersionUID = -7962827076724999755L;
    private Comment oldComment;
    private Comment newComment;

    public UserTaskCommentEventImpl(UserTaskInstance usertaskInstance, String user) {
        super(usertaskInstance, user);
    }

    public void setOldComment(Comment oldComment) {
        this.oldComment = oldComment;
    }

    public void setNewComment(Comment newComment) {
        this.newComment = newComment;
    }

    @Override
    public Comment getNewComment() {
        return newComment;
    }

    @Override
    public Comment getOldComment() {
        return oldComment;
    }

    @Override
    public String toString() {
        return "UserTaskCommentEventImpl [oldComment=" + oldComment + ", newComment=" + newComment + "]";
    }

}
