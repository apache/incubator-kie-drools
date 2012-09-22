/**
 * Copyright 2010 JBoss Inc
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
package org.jbpm.task.service.base.async;

import java.io.StringReader;
import java.util.*;

import org.jbpm.task.*;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.*;
import org.jbpm.task.utils.CollectionUtils;

public abstract class TaskServiceCommentsAndAttachmentsBaseAsyncTest extends BaseTest {

    protected TaskServer server;
    protected AsyncTaskService client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }


    public void testAddRemoveComment() {
        Map<String, Object> vars = fillVariables();

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now}), ";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null, addTaskResponseHandler);

        long taskId = addTaskResponseHandler.getTaskId();

        Comment comment = new Comment();
        Date addedAt = new Date(System.currentTimeMillis());
        comment.setAddedAt(addedAt);
        comment.setAddedBy(users.get("luke"));
        comment.setText("This is my comment1!!!!!");

        BlockingAddCommentResponseHandler addCommentResponseHandler = new BlockingAddCommentResponseHandler();
        client.addComment(taskId, comment, addCommentResponseHandler);
        assertTrue(addCommentResponseHandler.getCommentId() != comment.getId());

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(task.equals(task1));

        List<Comment> comments1 = task1.getTaskData().getComments();
        assertEquals(1, comments1.size());
        Comment returnedComment = comments1.get(0);
        assertEquals("This is my comment1!!!!!", returnedComment.getText());
        assertEquals(addedAt, returnedComment.getAddedAt());
        assertEquals(users.get("luke"), returnedComment.getAddedBy());

        assertEquals((long) addCommentResponseHandler.getCommentId(), (long) returnedComment.getId());

        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setComments(comments1);
        task.getTaskData().setStatus(Status.Created);
        assertEquals(task, task1);

        // test we can have multiple comments
        comment = new Comment();
        addedAt = new Date(System.currentTimeMillis());
        comment.setAddedAt(addedAt);
        comment.setAddedBy(users.get("tony"));
        comment.setText("This is my comment2!!!!!");

        addCommentResponseHandler = new BlockingAddCommentResponseHandler();
        client.addComment(taskId, comment, addCommentResponseHandler);

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        task1 = getTaskResponseHandler.getTask();
        List<Comment> comments2 = task1.getTaskData().getComments();
        assertEquals(2, comments2.size());

        // make two collections the same and compare
        comments1.add(comment);
        assertTrue(CollectionUtils.equals(comments1, comments2));

        BlockingDeleteCommentResponseHandler deleteCommentResponseHandler = new BlockingDeleteCommentResponseHandler();
        client.deleteComment(taskId, addCommentResponseHandler.getCommentId(), deleteCommentResponseHandler);
        deleteCommentResponseHandler.waitTillDone(3000);

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        task1 = getTaskResponseHandler.getTask();
        comments2 = task1.getTaskData().getComments();
        assertEquals(1, comments2.size());

        assertEquals("This is my comment1!!!!!", comments2.get(0).getText());
    }

    public void testAddRemoveAttachment() throws Exception {
        Map<String, Object> vars = fillVariables();

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now}), ";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null, addTaskResponseHandler);

        long taskId = addTaskResponseHandler.getTaskId();

        Attachment attachment = new Attachment();
        Date attachedAt = new Date(System.currentTimeMillis());
        attachment.setAttachedAt(attachedAt);
        attachment.setAttachedBy(users.get("luke"));
        attachment.setName("file1.txt");
        attachment.setAccessType(AccessType.Inline);
        attachment.setContentType("txt");

        byte[] bytes = "Ths is my attachment text1".getBytes();
        Content content = new Content();
        content.setContent(bytes);

        BlockingAddAttachmentResponseHandler addAttachmentResponseHandler = new BlockingAddAttachmentResponseHandler();
        client.addAttachment(taskId, attachment, content, addAttachmentResponseHandler);
        assertTrue(addAttachmentResponseHandler.getAttachmentId() != attachment.getId());
        assertTrue(addAttachmentResponseHandler.getContentId() != attachment.getAttachmentContentId());

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(task.equals(task1));

        List<Attachment> attachments1 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments1.size());
        Attachment returnedAttachment = attachments1.get(0);
        assertEquals(attachedAt, returnedAttachment.getAttachedAt());
        assertEquals(users.get("luke"), returnedAttachment.getAttachedBy());
        assertEquals(AccessType.Inline, returnedAttachment.getAccessType());
        assertEquals("txt", returnedAttachment.getContentType());
        assertEquals("file1.txt", returnedAttachment.getName());
        assertEquals(bytes.length, returnedAttachment.getSize());

        assertEquals((long) addAttachmentResponseHandler.getAttachmentId(), (long) returnedAttachment.getId());
        assertEquals((long) addAttachmentResponseHandler.getContentId(), (long) returnedAttachment.getAttachmentContentId());

        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setAttachments(attachments1);
        task.getTaskData().setStatus(Status.Created);
        assertEquals(task, task1);

        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(returnedAttachment.getAttachmentContentId(), getResponseHandler);
        content = getResponseHandler.getContent();
        assertEquals("Ths is my attachment text1", new String(content.getContent()));

        // test we can have multiple attachments

        attachment = new Attachment();
        attachedAt = new Date(System.currentTimeMillis());
        attachment.setAttachedAt(attachedAt);
        attachment.setAttachedBy(users.get("tony"));
        attachment.setName("file2.txt");
        attachment.setAccessType(AccessType.Inline);
        attachment.setContentType("txt");

        bytes = "Ths is my attachment text2".getBytes();
        content = new Content();
        content.setContent(bytes);

        addAttachmentResponseHandler = new BlockingAddAttachmentResponseHandler();
        client.addAttachment(taskId, attachment, content, addAttachmentResponseHandler);

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(task.equals(task1));

        List<Attachment> attachments2 = task1.getTaskData().getAttachments();
        assertEquals(2, attachments2.size());

        getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(addAttachmentResponseHandler.getContentId(), getResponseHandler);
        content = getResponseHandler.getContent();
        assertEquals("Ths is my attachment text2", new String(content.getContent()));

        // make two collections the same and compare
        attachment.setSize(26);
        attachment.setAttachmentContentId(addAttachmentResponseHandler.getContentId());
        attachments1.add(attachment);
        assertTrue(CollectionUtils.equals(attachments2, attachments1));

        BlockingDeleteAttachmentResponseHandler deleteCommentResponseHandler = new BlockingDeleteAttachmentResponseHandler();
        client.deleteAttachment(taskId, addAttachmentResponseHandler.getAttachmentId(), addAttachmentResponseHandler.getContentId(), deleteCommentResponseHandler);
        deleteCommentResponseHandler.waitTillDone(3000);

        Thread.sleep(3000);

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        task1 = getTaskResponseHandler.getTask();
        attachments2 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments2.size());

        assertEquals("file1.txt", attachments2.get(0).getName());
    }
}
