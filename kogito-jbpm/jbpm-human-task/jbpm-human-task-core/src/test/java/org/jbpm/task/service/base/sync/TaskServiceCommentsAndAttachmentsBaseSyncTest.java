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
package org.jbpm.task.service.base.sync;


import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.AccessType;
import org.jbpm.task.Attachment;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Comment;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.utils.CollectionUtils;

public abstract class TaskServiceCommentsAndAttachmentsBaseSyncTest extends BaseTest {

    protected TaskServer server;
    protected TaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }

    public void testAddRemoveComment() {
        Map vars = fillVariables(users, groups);

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now}), ";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);

        long taskId = task.getId();

        Comment comment = new Comment();
        Date addedAt = new Date(System.currentTimeMillis());
        comment.setAddedAt(addedAt);
        comment.setAddedBy(users.get("luke"));
        comment.setText("This is my comment1!!!!!");

        
        client.addComment(taskId, comment);
        
        long commentId = comment.getId();
        
        
        Task task1 = client.getTask(taskId);
        // We are reusing this for local clients where the object is the same
        //assertNotSame(task, task1);
        //assertFalse(task.equals(task1));

        List<Comment> comments1 = task1.getTaskData().getComments();
        assertEquals(1, comments1.size());
        Comment returnedComment = comments1.get(0);
        assertEquals("This is my comment1!!!!!", returnedComment.getText());
        assertEquals(addedAt, returnedComment.getAddedAt());
        assertEquals(users.get("luke"), returnedComment.getAddedBy());

        assertEquals(commentId, (long) returnedComment.getId());

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

        
        client.addComment(taskId, comment);
        long commentId2 = comment.getId();

       
        
        task1 = client.getTask(taskId);
        List<Comment> comments2 = task1.getTaskData().getComments();
        assertEquals(2, comments2.size());

        // make two collections the same and compare
        comments1.add(comment);
        assertTrue(CollectionUtils.equals(comments1, comments2));

        client.deleteComment(taskId, commentId2);

        
        
        task1 = client.getTask(taskId);
        comments2 = task1.getTaskData().getComments();
        assertEquals(1, comments2.size());

        assertEquals("This is my comment1!!!!!", comments2.get(0).getText());
    }

    public void testAddRemoveAttachment() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now}), ";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);

        long taskId = task.getId();

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

        
        client.addAttachment(taskId, attachment, content);
        

        Task task1 = client.getTask(taskId);
        // For local clients this will be the same.. that's why is commented
        //assertNotSame(task, task1);
        //assertFalse(task.equals(task1));

        List<Attachment> attachments1 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments1.size());
        Attachment returnedAttachment = attachments1.get(0);
        assertEquals(attachedAt, returnedAttachment.getAttachedAt());
        assertEquals(users.get("luke"), returnedAttachment.getAttachedBy());
        assertEquals(AccessType.Inline, returnedAttachment.getAccessType());
        assertEquals("txt", returnedAttachment.getContentType());
        assertEquals("file1.txt", returnedAttachment.getName());
        assertEquals(bytes.length, returnedAttachment.getSize());

        assertEquals((long)attachment.getId(), (long) returnedAttachment.getId());
        assertEquals((long)content.getId(), (long) returnedAttachment.getAttachmentContentId());

        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setAttachments(attachments1);
        task.getTaskData().setStatus(Status.Created);
        assertEquals(task, task1);

        
        
        content = client.getContent(returnedAttachment.getAttachmentContentId());
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

        
        client.addAttachment(taskId, attachment, content);

        
        
        task1 = client.getTask(taskId);
        // In local clients this will be the same object and we are reusing the tests
        //assertNotSame(task, task1);
        //assertFalse(task.equals(task1));

        List<Attachment> attachments2 = task1.getTaskData().getAttachments();
        assertEquals(2, attachments2.size());

        
        
        content = client.getContent(content.getId() );
        assertEquals("Ths is my attachment text2", new String(content.getContent()));

        // make two collections the same and compare
        attachment.setSize(26);
        attachment.setAttachmentContentId(content.getId());
        attachments1.add(attachment);
        assertTrue(CollectionUtils.equals(attachments2, attachments1));

        
        client.deleteAttachment(taskId, attachment.getId(), content.getId());
        

        
        
        task1 = client.getTask(taskId);
        attachments2 = task1.getTaskData().getAttachments();
        assertEquals(1, attachments2.size());

        assertEquals("file1.txt", attachments2.get(0).getName());
    }
}
