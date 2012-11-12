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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTestNoUserGroupSetup;
import org.jbpm.task.Content;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.MvelFilePath;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingSetContentResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.runtime.process.WorkItemManager;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public abstract class TaskServiceDeadlinesBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {

    protected TaskServer server;
    protected AsyncTaskService client;
    private Properties conf;
    private Wiser wiser;

    private String emailAddressTony = "tony@domain.com"; 
    private String emailAddressDarth = "darth@domain.com"; 
    

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        getWiser().stop();
        super.tearDown();
    }
    
    public void testDelayedEmailNotificationOnDeadline() throws Exception {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() ); 
        
        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler( getConf() );
        WorkItemManager manager = new DefaultWorkItemManager( null );
        notificationHandler.setManager( manager );
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get("tony"), emailAddressTony );
        userInfo.getEmails().put( users.get("darth"), emailAddressDarth );
        
        userInfo.getLanguages().put(  users.get("tony"), "en-UK" );
        userInfo.getLanguages().put(  users.get("darth"), "en-UK" );
        notificationHandler.setUserInfo( userInfo );    
        
        taskService.setEscalatedDeadlineHandler( notificationHandler );
        
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( MvelFilePath.DeadlineWithNotification ) );
        Task task = ( Task )  eval( reader, vars );
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();    
                                        
        Content content = new Content();
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject", "My Subject");
        params.put("body", "My Body");
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        BlockingSetContentResponseHandler setContentResponseHandler  = new BlockingSetContentResponseHandler();
        client.setDocumentContent( taskId, content, setContentResponseHandler );
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler  getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent( contentId, getResponseHandler );
        content = getResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals( "{body=My Body, subject=My Subject}", unmarshalledObject.toString() );
        
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size() );             
        Thread.sleep( 100 );
        
        // nor yet
        assertEquals(0, getWiser().getMessages().size() );     
        
        long time = 0;
        while ( getWiser().getMessages().size() != 2 && time < 15000 ) {
            Thread.sleep( 500 );
            time += 500;
        }
        
        // 1 email with two recipients should now exist
        assertEquals(2, getWiser().getMessages().size() );        
        
        List<String> list = new ArrayList<String>(2);
        list.add( getWiser().getMessages().get( 0 ).getEnvelopeReceiver() );
        list.add( getWiser().getMessages().get( 1 ).getEnvelopeReceiver() );
        
        assertTrue( list.contains(emailAddressTony) );
        assertTrue( list.contains(emailAddressDarth) );
        
        
        MimeMessage msg = (( WiserMessage  ) getWiser().getMessages().get( 0 )).getMimeMessage();
        assertEquals( "My Body", msg.getContent() );
        assertEquals( "My Subject", msg.getSubject() );
        assertEquals( "from@domain.com", ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( "replyTo@domain.com", ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        boolean tonyMatched = false;
        boolean darthMatched = false;
        
        for( int i = 0; i < msg.getRecipients( RecipientType.TO ).length; ++i ) { 
            String emailAddress = ((InternetAddress)msg.getRecipients( RecipientType.TO )[i]).getAddress();
            if( emailAddressTony.equals(emailAddress) ) { 
                tonyMatched = true;
            }
            else if ( emailAddressDarth.equals(emailAddress) ) { 
                darthMatched = true;
            }
        }
        assertTrue(tonyMatched);
        assertTrue(darthMatched);
    }
    
    public void testDelayedReassignmentOnDeadline() throws Exception {
        Map vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() ); 
        
        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager( null );
        notificationHandler.setManager( manager );
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get("tony"), "tony@domain.com" );
        userInfo.getEmails().put( users.get("luke"), "luke@domain.com" );
        userInfo.getEmails().put( users.get("bobba"), "luke@domain.com" );
        userInfo.getEmails().put( users.get("jabba"), "luke@domain.com" );
        
        userInfo.getLanguages().put(  users.get("tony"), "en-UK" );
        userInfo.getLanguages().put(  users.get("luke"), "en-UK" );
        userInfo.getLanguages().put(  users.get("bobba"), "en-UK" );
        userInfo.getLanguages().put(  users.get("jabba"), "en-UK" );
        notificationHandler.setUserInfo( userInfo );    
        
        taskService.setEscalatedDeadlineHandler( notificationHandler );
        
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( MvelFilePath.DeadlineWithReassignment ) );
        Task task = ( Task )  eval( reader, vars );         
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();    
        
        // Shouldn't have re-assigned yet
        Thread.sleep( 1000 );
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskHandler );
        task = getTaskHandler.getTask();
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for ( OrganizationalEntity entity : potentialOwners ) {
            ids.add( entity.getId() );
        }
        assertTrue( ids.contains( users.get( "tony" ).getId() ));
        assertTrue( ids.contains( users.get( "luke" ).getId() ));        
        
        // should have re-assigned by now
        long time = 0;
        while ( getWiser().getMessages().size() != 2 && time < 15000 ) {
            Thread.sleep( 500 );
            time += 500;
        }
        
        getTaskHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskHandler );
        task = getTaskHandler.getTask();
        assertEquals( Status.Ready, task.getTaskData().getStatus()  );
        potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        System.out.println( potentialOwners );
        ids = new ArrayList<String>(potentialOwners.size());
        for ( OrganizationalEntity entity : potentialOwners ) {
            ids.add( entity.getId() );
        }
        assertTrue( ids.contains( users.get( "bobba" ).getId() ));
        assertTrue( ids.contains( users.get( "jabba" ).getId() ));                  
    }


    public void setConf(Properties conf) {
        this.conf = conf;
    }

    public Properties getConf() {
        return conf;
    }

    public void setWiser(Wiser wiser) {
        this.wiser = wiser;
    }

    public Wiser getWiser() {
        return wiser;
    }
}
