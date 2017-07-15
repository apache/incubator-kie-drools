/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;

public class TaskCommentTest extends HumanTaskServicesBaseTest{
        private PoolingDataSource pds;
        private EntityManagerFactory emf;

        @Before
        public void setup() {
            pds = setupPoolingDataSource();
            emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

            this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                                                    .entityManagerFactory(emf)
                                                    .getTaskService();
        }

        @After
        public void clean() {
            super.tearDown();
            if (emf != null) {
                emf.close();
            }
            if (pds != null) {
                pds.close();
            }
        }

        @Test
        public void testTaskComment() throws Exception {
            String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
            str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
            str += "name =  'This is my task name' })";
            Task task = TaskFactory.evalTask(new StringReader(str));
            taskService.addTask(task, new HashMap<String, Object>());
            List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");

            String txt = "brainwashArmitageRecruitCaseGetPasswordFromLady3JaneAscentToStraylightIcebreakerUniteWithNeuromancer";

            assertEquals(1, tasks.size());
            TaskSummary taskSum = tasks.get(0);

            Comment comment = TaskModelProvider.getFactory().newComment();
            Date date = new Date();
            ((InternalComment)comment).setAddedAt(date);
            User user = TaskModelProvider.getFactory().newUser();
            ((InternalOrganizationalEntity) user).setId("Troll");
            ((InternalComment)comment).setAddedBy(user);
            ((InternalComment)comment).setText(txt);

            Long commentId = taskService.addComment(taskSum.getId().longValue(), comment);
            assertNotNull(commentId);
            assertTrue(commentId.longValue() > 0l);

            Comment commentById = taskService.getCommentById(commentId.longValue());
            assertNotNull(commentById);
            assertEquals(commentId, commentById.getId());
            assertEquals(date, commentById.getAddedAt());
            assertEquals(user, commentById.getAddedBy());
            assertEquals(txt, commentById.getText());

            Comment comment2 = TaskModelProvider.getFactory().newComment();
            ((InternalComment)comment2).setAddedAt(new Date());
            User user2 = TaskModelProvider.getFactory().newUser();
            ((InternalOrganizationalEntity) user2).setId("Master");
            ((InternalComment)comment2).setAddedBy(user2);
            ((InternalComment)comment2).setText(txt+"asdf");

            Long commentId2 = taskService.addComment(taskSum.getId(), comment2);
            assertNotNull(commentId2);
            assertTrue(commentId2.longValue() > 0l);
            assertNotEquals(commentId, commentId2);

            Comment commentById2 = taskService.getCommentById(commentId2.longValue());
            assertNotNull(commentById2);
            assertNotEquals(commentById, commentById2);

            List<Comment> allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
            assertEquals(2, allCommentList.size());

            //check id
            assertEquals(commentId, allCommentList.get(0).getId());
            assertEquals(commentId2, allCommentList.get(1).getId());

            taskService.deleteComment(taskSum.getId(), commentId2);
            assertFalse(taskService.getAllCommentsByTaskId(taskSum.getId()).isEmpty());
            //one item
            allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
            assertEquals(1, allCommentList.size());

            taskService.deleteComment(taskSum.getId(), commentId);
            assertTrue(taskService.getAllCommentsByTaskId(taskSum.getId()).isEmpty());

        }

        @Test
        public void testTaskCommentsOrder() {
            int commentsCount = 50;
            String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
            str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
            str += "name =  'This is my task name' })";
            Task task = TaskFactory.evalTask(new StringReader((str)));
            taskService.addTask(task, new HashMap<String, Object>());

            List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
            TaskSummary taskSum = tasks.get(0);

            String[] messages = new String[commentsCount];
            Long[] commentId = new Long[commentsCount];

            for(int i = 0; i < commentsCount; i++) {
                Comment comment = TaskModelProvider.getFactory().newComment();
                messages[i] = "Comment "+i+".";
                ((InternalComment)comment).setAddedAt(new Date());
                User user = TaskModelProvider.getFactory().newUser();
                ((InternalOrganizationalEntity) user).setId("Troll");
                ((InternalComment)comment).setAddedBy(user);
                ((InternalComment)comment).setText(messages[i]);

                commentId[i] = taskService.addComment(taskSum.getId(), comment);
                assertNotNull(commentId[i]);
            }

            List<Comment> allCommentList = taskService.getAllCommentsByTaskId(taskSum.getId());
            assertEquals(commentsCount, allCommentList.size());

            for(int i = 0; i < commentsCount; i++) {
                Comment comment = allCommentList.get(i);
                assertNotNull(comment);
                assertEquals(commentId[i], comment.getId());
                assertNotNull(comment.getAddedAt());
                assertEquals(messages[i], comment.getText());
                assertEquals("Troll", comment.getAddedBy().getId());
            }
        }
}
