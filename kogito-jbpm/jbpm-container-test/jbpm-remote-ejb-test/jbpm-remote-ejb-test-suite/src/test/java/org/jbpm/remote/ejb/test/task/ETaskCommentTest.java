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

package org.jbpm.remote.ejb.test.task;

import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.TaskSummary;


public class ETaskCommentTest extends RemoteEjbTest {

    private static final String COMMENT = "This is a comment";
    private static final Date TODAY = new Date();

    @Test
    public void testUserComment() {
        ProcessInstance pi = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK);
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> taskSummaryList = ejb.getTasksAssignedAsPotentialOwner(userId);
        Assertions.assertThat(taskSummaryList.size()).isEqualTo(1);

        // Add a comment
        Long taskId = taskSummaryList.get(0).getId();
        ejb.addComment(taskId, COMMENT, userId, TODAY);

        // Get the comment
        Comment comment = getComment(taskId);
        Assertions.assertThat(comment.getText()).isEqualTo(COMMENT);
        Assertions.assertThat(comment.getAddedBy().getId()).isEqualTo(userId);
        // Note: must ignore millis because some databases ignore them by default which is the same
        //       as setting them to 0. This will do a very small difference in the time and equals
        //       will fail. Moreover, some databases are rounding this value, therefore we cannot use
        //       isEqualToIgnoringMillis() method.
        Assertions.assertThat(comment.getAddedAt()).isCloseTo(TODAY, 1000);

        // Delete the comment
        ejb.deleteComment(taskId, comment.getId());

        comment = getComment(taskId);
        Assertions.assertThat(comment).isNull();
    }

    @Test
    public void testMultipleUserComments() {
        ProcessInstance pi = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK);
        Assertions.assertThat(pi).isNotNull();
        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> taskSummaryList = ejb.getTasksAssignedAsPotentialOwner(userId);
        Assertions.assertThat(taskSummaryList.size()).isEqualTo(1);

        Long taskId = taskSummaryList.get(0).getId();
        for (int i = 1; i <= 50; i++) {
            ejb.addComment(taskId, COMMENT + " with sequence number #" + i, userId, new Date());
        }

        List<Comment> commentList = getComments(taskId);
        Assertions.assertThat(commentList).hasSize(50);
    }

    private List<Comment> getComments(Long taskId) {
        List<Comment> commentList = ejb.getCommentsByTaskId(taskId);
        Assertions.assertThat(commentList).isNotNull();

        return commentList;
    }

    private Comment getComment(Long taskId) {
        List<Comment> commentList = getComments(taskId);
        Comment comment = null;
        if (!commentList.isEmpty()) {
            comment = commentList.get(0);
        }
        return comment;
    }

}
