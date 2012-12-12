package org.jbpm.task.service.persistence;

import static org.jbpm.task.service.test.impl.TestServerUtil.deserialize;
import static org.jbpm.task.service.test.impl.TestServerUtil.serialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.Attachment;
import org.jbpm.task.BaseTest;
import org.jbpm.task.BooleanExpression;
import org.jbpm.task.Comment;
import org.jbpm.task.I18NText;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskService;
import org.kie.SystemEventListenerFactory;

public class DataModelTest extends BaseTest {

    private final static String EMPTY_STRING = "";
   
    @Override
    protected EntityManagerFactory createEntityManagerFactory() { 
        Map<String, String> properties = new HashMap<String, String>();
        properties.put( "hibernate.hbm2ddl.auto", "update");
        return Persistence.createEntityManagerFactory("org.jbpm.task", properties);
    }
    
    public void testOracleEmptyStringNullTask() throws Exception { 
        Properties dsProps = loadDataSourceProperties();
        String driverClassName = (String) dsProps.get("driverClassName");
        if( ! driverClassName.startsWith("oracle") ) { 
            return;
        }
        
        long workItemId = new Date().getTime();
        
        // Scope to make sure no references from setup present later
        {
            User user = new User();
            user.setId("mriet"); // ;P
        
            taskSession.addUser(user);
        
            Task task = new Task();
            ContentData contentData = new ContentData();
            TaskData taskData = new TaskData();
            taskData.setActualOwner(user);
            task.setTaskData(taskData);
            taskData.setWorkItemId(workItemId);

            // Attachments
            Attachment attachment = new Attachment();
            attachment.setName(EMPTY_STRING);
            attachment.setContentType(EMPTY_STRING);
            attachment.setAttachedBy(user);
            ArrayList<Attachment> attachments = new ArrayList<Attachment>();
            attachments.add(attachment);
            taskData.setAttachments(attachments);

            // Comments
            Comment comment = new Comment();
            comment.setText(EMPTY_STRING);
            comment.setAddedBy(user);
            ArrayList<Comment> comments = new ArrayList<Comment>();
            comments.add(comment);
            taskData.setComments(comments);

            // Descriptions, names, subjects
            I18NText text = new I18NText();
            text.setLanguage(EMPTY_STRING);
            text.setText(EMPTY_STRING);

            ArrayList<I18NText> i18nTexts = new ArrayList<I18NText>();
            i18nTexts.add(text);
            task.setDescriptions(i18nTexts);
            task.setNames(i18nTexts);
            task.setSubjects(i18nTexts);
        
            taskSession.addTask(task, contentData);
            taskSession.dispose();
            emf.close();
        }
        
        emf = createEntityManagerFactory();
        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();
        
        // Retrieve task
        Task task = taskSession.getTaskByWorkItemId(workItemId);
        assertTrue( "mriet".equals(task.getTaskData().getActualOwner().getId()) );
        String nullMessage = "Empty string not converted to null.";
        assertTrue( nullMessage, null == task.getTaskData().getAttachments().get(0).getContentType() );
        assertTrue( nullMessage, null == task.getTaskData().getAttachments().get(0).getName() );
        assertTrue( nullMessage, null == task.getTaskData().getComments().get(0).getText() );
        assertTrue( nullMessage, null == task.getNames().get(0).getText() );
        assertTrue( nullMessage, null == task.getNames().get(0).getLanguage() );
        assertTrue( nullMessage, null == task.getSubjects().get(0).getText() );
        assertTrue( nullMessage, null == task.getSubjects().get(0).getLanguage() );
        assertTrue( nullMessage, null == task.getDescriptions().get(0).getText() );
        assertTrue( nullMessage, null == task.getDescriptions().get(0).getLanguage() );
        
        // Serialize, deserialize
        byte [] serializedData = serialize(task);
        task = (Task) deserialize(serializedData);
        
        // Test
        assertTrue( "mriet".equals(task.getTaskData().getActualOwner().getId()) );
        nullMessage = "Null not converted to empty string.";
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getTaskData().getAttachments().get(0).getContentType()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getTaskData().getAttachments().get(0).getName()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getTaskData().getComments().get(0).getText()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getNames().get(0).getText()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getNames().get(0).getLanguage()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getSubjects().get(0).getText()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getSubjects().get(0).getLanguage()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getDescriptions().get(0).getText()) );
        assertTrue( nullMessage, EMPTY_STRING.equals( task.getDescriptions().get(0).getLanguage()) );
    }
 
    public void testBooleanExpressionSerialization() throws Exception { 
        BooleanExpression booleanExpression = new BooleanExpression();
       
        // Serialize, deserialize
        byte [] serializedData = serialize(booleanExpression);
        booleanExpression = (BooleanExpression) deserialize(serializedData);
    }
}