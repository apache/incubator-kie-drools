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

package org.jbpm.process.instance.event;

import java.util.Date;

import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.usertask.UserTaskCommentEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.process.workitem.Comment;

public class UserTaskCommentEventImpl extends UserTaskEventImpl implements UserTaskCommentEvent {

    private static final long serialVersionUID = -7962827076724999755L;
    private Comment oldComment;
    private Comment newComment;

    public UserTaskCommentEventImpl(ProcessInstance instance, HumanTaskNodeInstance nodeInstance, KieRuntime kruntime, String user) {
        super(instance, nodeInstance, kruntime, user);
    }

    @Override
    public String getUserTaskId() {
        return getHumanTaskNodeInstance().getWorkItemId();
    }

    public void setOldComment(Comment oldComment) {
        this.oldComment = oldComment;
    }

    public void setNewComment(Comment newComment) {
        this.newComment = newComment;
    }

    @Override
    public org.kie.api.event.usertask.Comment getNewComment() {
        return wrap(newComment);
    }

    @Override
    public org.kie.api.event.usertask.Comment getOldComment() {
        return wrap(oldComment);
    }

    private org.kie.api.event.usertask.Comment wrap(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new org.kie.api.event.usertask.Comment() {

            @Override
            public String getCommentId() {
                return comment.getId();
            }

            @Override
            public String getCommentContent() {
                return comment.getContent();
            }

            @Override
            public String getUpdatedBy() {
                return comment.getUpdatedBy();
            }

            @Override
            public Date getUpdatedAt() {
                return comment.getUpdatedAt();
            }

        };
    }
}
