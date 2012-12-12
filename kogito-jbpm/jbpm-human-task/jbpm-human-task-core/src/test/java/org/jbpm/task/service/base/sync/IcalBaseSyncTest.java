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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.jbpm.task.BaseTest;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.TaskServer;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public abstract class IcalBaseSyncTest extends BaseTest {

	protected TaskServer server;
	protected TaskService client;
	private Wiser wiser;
	private String emailHost;
	private String emailPort;    

    protected void tearDown() throws Exception {
        getWiser().stop();
        super.tearDown();
    }
    
	public void testSendWithStartandEndDeadline() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, createdBy = users['tony'], activationTime = now}), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) {potentialOwners = [users['steve' ], users['tony' ]]}), ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')],";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my task subject')],";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my task description')],";
        str += "deadlines = (with (new Deadlines() ) {";
        str += "    startDeadlines = [ ";
        str += "       (with (new Deadline()) {";
        str += "           date = now";
        str += "       } ) ],";
        str += "    endDeadlines = [";
        str += "        (with (new Deadline()) {";
        str += "             date = new Date( now.time + ( 1000 * 60 * 60 * 24 ) )"; // set to tomorrow
        str += "        } ) ]";
        str += "} ) })";

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get( "tony" ),
                                  "tony@domain.com" );
        userInfo.getEmails().put( users.get( "steve" ),
                                  "steve@domain.com" );

        userInfo.getLanguages().put( users.get( "tony" ),
                                     "en-UK" );
        userInfo.getLanguages().put( users.get( "steve" ),
                                     "en-UK" );
        taskService.setUserinfo( userInfo );

        
        Task task = (Task) eval( new StringReader( str ),
                                 vars );
        client.addTask( task, null );
        long taskId = task.getId();
        
        client.claim( taskId,
                      users.get( "steve" ).getId());

        assertEquals( 2,
                      getWiser().getMessages().size() );
        
        assertEquals( "steve@domain.com", getWiser().getMessages().get( 0 ).getEnvelopeReceiver() );
        assertEquals( "steve@domain.com", getWiser().getMessages().get( 1 ).getEnvelopeReceiver() );

        String subject = "Summary\n-------\n\nThis is my task subject\n\n";
        String description = "Description\n-----------\n\nThis is my task description";

        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get( 0 )).getMimeMessage();
        assertEqualsIgnoreWhitespace( "multipart/alternative;boundary=\"----=_Part_",
                                      msg.getContentType(),
                                      0,
                                      47 );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getFrom()[0]).getAddress() );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getReplyTo()[0]).getAddress() );
        assertEquals( "steve@domain.com",
                      ((InternetAddress) msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( "Task Assignment Start Event: This is my task name",
                      msg.getSubject() );
        
        MimeMultipart multiPart = (MimeMultipart) msg.getContent();
                        
        BodyPart messageBodyPart = multiPart.getBodyPart( 0 );
        assertEquals( "text/plain; charset=UTF8;", messageBodyPart.getDataHandler().getContentType() );
        String content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );        
        assertEqualsIgnoreWhitespace( subject + description, content );
        
        messageBodyPart = multiPart.getBodyPart( 1 );
        List<String> expectedContentType = new ArrayList<String>();
        expectedContentType.add("text/calendar");
        expectedContentType.add("charset=UTF8");
        expectedContentType.add("name=ical-Start-1.ics");
        Collections.sort(expectedContentType);
        
        List<String> actualtContentType = Arrays.asList(messageBodyPart.getDataHandler().getContentType().split("; "));
        Collections.sort(actualtContentType);

        assertEquals(expectedContentType, actualtContentType);        content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );
        assertEqualsIgnoreWhitespace( "BEGIN:VCALENDARPRODID:-//iCal4j 1.0//ENCALSCALE:GREGORIANVERSION:2.0METHOD:REQUESTBEGIN:VEVENTDTSTART;TZID=UTC:", content.substring( 0, 123) );           
        assertEqualsIgnoreWhitespace( "SUMMARY:\"Task Start : This is my task subject\"DESCRIPTION:\"This is my task description\"PRIORITY:55END:VEVENTEND:VCALENDAR", content.substring( content.length()-131, content.length()) );
        
        
        msg = ((WiserMessage) getWiser().getMessages().get( 1 )).getMimeMessage();
        assertEqualsIgnoreWhitespace( "multipart/alternative;boundary=\"----=_Part_",
                                      msg.getContentType(),
                                      0,
                                      47 );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getFrom()[0]).getAddress() );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getReplyTo()[0]).getAddress() );
        assertEquals( "steve@domain.com",
                      ((InternetAddress) msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( "Task Assignment End Event: This is my task name",
                      msg.getSubject() );
        
        multiPart = (MimeMultipart) msg.getContent();
                        
        messageBodyPart = multiPart.getBodyPart( 0 );
        assertEquals( "text/plain; charset=UTF8;", messageBodyPart.getDataHandler().getContentType() );
        content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );        
        assertEqualsIgnoreWhitespace( subject + description, content );
        
        messageBodyPart = multiPart.getBodyPart( 1 );
        expectedContentType = new ArrayList<String>();
        expectedContentType.add("text/calendar");
        expectedContentType.add("charset=UTF8");
        expectedContentType.add("name=ical-End-1.ics");
        Collections.sort(expectedContentType);
        
        actualtContentType = Arrays.asList(messageBodyPart.getDataHandler().getContentType().split("; "));
        Collections.sort(actualtContentType);

        assertEquals(expectedContentType, actualtContentType);        content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );
        assertEqualsIgnoreWhitespace( "BEGIN:VCALENDARPRODID:-//iCal4j 1.0//ENCALSCALE:GREGORIANVERSION:2.0METHOD:REQUESTBEGIN:VEVENTDTSTART;TZID=UTC:", content.substring( 0, 123) );           
        assertEqualsIgnoreWhitespace( "SUMMARY:\"Task End : This is my task subject\"DESCRIPTION:\"This is my task description\"PRIORITY:55END:VEVENTEND:VCALENDAR", content.substring( content.length()-131, content.length()) );
    }
    
    public void testSendWithStartDeadline() throws Exception {
        Map vars = new HashMap();
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() );

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, createdBy = users['tony'], activationTime = now}), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) {potentialOwners = [users['steve' ], users['tony' ]]}), ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')],";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my task subject')],";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my task description')],";
        str += "deadlines = (with (new Deadlines() ) {";
        str += "    startDeadlines = [ ";
        str += "       (with (new Deadline()) {";
        str += "           date = now";
        str += "       } ) ]";
        str += "} ) })";

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get( "tony" ),
                                  "tony@domain.com" );
        userInfo.getEmails().put( users.get( "steve" ),
                                  "steve@domain.com" );

        userInfo.getLanguages().put( users.get( "tony" ),
                                     "en-UK" );
        userInfo.getLanguages().put( users.get( "steve" ),
                                     "en-UK" );
        taskService.setUserinfo( userInfo );

        
        Task task = (Task) eval( new StringReader( str ),
                                 vars );
        client.addTask( task, null );
        long taskId = task.getId();

        client.claim( taskId,
                      users.get( "steve" ).getId() );

        assertEquals( 1,
                      getWiser().getMessages().size() );

        assertEquals( "steve@domain.com", getWiser().getMessages().get( 0 ).getEnvelopeReceiver() );

        String subject = "Summary\n-------\n\nThis is my task subject\n\n";
        String description = "Description\n-----------\n\nThis is my task description";

        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get( 0 )).getMimeMessage();
        assertEqualsIgnoreWhitespace( "multipart/alternative;boundary=\"----=_Part_",
                                      msg.getContentType(),
                                      0,
                                      47 );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getFrom()[0]).getAddress() );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getReplyTo()[0]).getAddress() );
        assertEquals( "steve@domain.com",
                      ((InternetAddress) msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( "Task Assignment Start Event: This is my task name",
                      msg.getSubject() );
        
        MimeMultipart multiPart = (MimeMultipart) msg.getContent();
                        
        BodyPart messageBodyPart = multiPart.getBodyPart( 0 );
        assertEquals( "text/plain; charset=UTF8;", messageBodyPart.getDataHandler().getContentType() );
        String content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );        
        assertEqualsIgnoreWhitespace( subject + description, content );
        
        messageBodyPart = multiPart.getBodyPart( 1 );
        List<String> expectedContentType = new ArrayList<String>();
        expectedContentType.add("text/calendar");
        expectedContentType.add("charset=UTF8");
        expectedContentType.add("name=ical-Start-1.ics");
        Collections.sort(expectedContentType);
        
        List<String> actualtContentType = Arrays.asList(messageBodyPart.getDataHandler().getContentType().split("; "));
        Collections.sort(actualtContentType);

        assertEquals(expectedContentType, actualtContentType);
        content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );
        assertEqualsIgnoreWhitespace( "BEGIN:VCALENDARPRODID:-//iCal4j 1.0//ENCALSCALE:GREGORIANVERSION:2.0METHOD:REQUESTBEGIN:VEVENTDTSTART;TZID=UTC:", content.substring( 0, 123) );           
        assertEqualsIgnoreWhitespace( "SUMMARY:\"Task Start : This is my task subject\"DESCRIPTION:\"This is my task description\"PRIORITY:55END:VEVENTEND:VCALENDAR", content.substring( content.length()-131, content.length()) );
    }    
    
    public void testSendWithEndDeadline() throws Exception {
        Map vars = new HashMap();
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() );

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, createdBy = users['tony'], activationTime = now}), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) {potentialOwners = [users['steve' ], users['tony' ]]}), ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')],";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my task subject')],";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my task description')],";
        str += "deadlines = (with (new Deadlines() ) {";
        str += "    endDeadlines = [";
        str += "        (with (new Deadline()) {";
        str += "             date = new Date( now.time + ( 1000 * 60 * 60 * 24 ) )"; // set to tomorrow
        str += "        } ) ]";
        str += "} ) })";

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get( "tony" ),
                                  "tony@domain.com" );
        userInfo.getEmails().put( users.get( "steve" ),
                                  "steve@domain.com" );

        userInfo.getLanguages().put( users.get( "tony" ),
                                     "en-UK" );
        userInfo.getLanguages().put( users.get( "steve" ),
                                     "en-UK" );
        taskService.setUserinfo( userInfo );

        
        Task task = (Task) eval( new StringReader( str ),
                                 vars );
        client.addTask( task, null );
        long taskId = task.getId();

        
        client.claim( taskId,
                      users.get( "steve" ).getId() );


        assertEquals( 1,
                      getWiser().getMessages().size() );
        
        assertEquals( "steve@domain.com", getWiser().getMessages().get( 0 ).getEnvelopeReceiver() );

        String subject = "Summary\n-------\n\nThis is my task subject\n\n";
        String description = "Description\n-----------\n\nThis is my task description";
                
        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get( 0 )).getMimeMessage();
        assertEqualsIgnoreWhitespace( "multipart/alternative;boundary=\"----=_Part_",
                                      msg.getContentType(),
                                      0,
                                      47 );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getFrom()[0]).getAddress() );
        assertEquals( "tony@domain.com",
                      ((InternetAddress) msg.getReplyTo()[0]).getAddress() );
        assertEquals( "steve@domain.com",
                      ((InternetAddress) msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( "Task Assignment End Event: This is my task name",
                      msg.getSubject() );
        
        MimeMultipart multiPart = (MimeMultipart) msg.getContent();
                        
        BodyPart messageBodyPart = multiPart.getBodyPart( 0 );
        assertEquals( "text/plain; charset=UTF8;", messageBodyPart.getDataHandler().getContentType() );
        String content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );        
        assertEqualsIgnoreWhitespace( subject + description, content );
        
        messageBodyPart = multiPart.getBodyPart( 1 );
        List<String> expectedContentType = new ArrayList<String>();
        expectedContentType.add("text/calendar");
        expectedContentType.add("charset=UTF8");
        expectedContentType.add("name=ical-End-1.ics");
        Collections.sort(expectedContentType);
        
        List<String> actualtContentType = Arrays.asList(messageBodyPart.getDataHandler().getContentType().split("; "));
        Collections.sort(actualtContentType);

        assertEquals(expectedContentType, actualtContentType);        content = new String( getBytes( messageBodyPart.getDataHandler().getInputStream() ) );
        assertEqualsIgnoreWhitespace( "BEGIN:VCALENDARPRODID:-//iCal4j 1.0//ENCALSCALE:GREGORIANVERSION:2.0METHOD:REQUESTBEGIN:VEVENTDTSTART;TZID=UTC:", content.substring( 0, 123) );           
        assertEqualsIgnoreWhitespace( "SUMMARY:\"Task End : This is my task subject\"DESCRIPTION:\"This is my task description\"PRIORITY:55END:VEVENTEND:VCALENDAR", content.substring( content.length()-131, content.length()) );
    }
    
    public void testSendWithNoDeadline() throws Exception {
        Map vars = new HashMap();
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() );

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, createdBy = users['tony'], activationTime = now}), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) {potentialOwners = [users['steve' ], users['tony' ]]}), ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')],";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my task subject')],";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my task description')]";
        str += "})";

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get( "tony" ),
                                  "tony@domain.com" );
        userInfo.getEmails().put( users.get( "steve" ),
                                  "steve@domain.com" );

        userInfo.getLanguages().put( users.get( "tony" ),
                                     "en-UK" );
        userInfo.getLanguages().put( users.get( "steve" ),
                                     "en-UK" );
        taskService.setUserinfo( userInfo );

        
        Task task = (Task) eval( new StringReader( str ),
                                 vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

        client.claim( taskId,
                      users.get( "steve" ).getId() );

        assertEquals( 0,
                      getWiser().getMessages().size() );        
    }       
    
    private static void assertEqualsIgnoreWhitespace(final String expected,
                                                     final String actual) {
        assertEqualsIgnoreWhitespace(expected, actual, 0, actual.length());
    }

    private static void assertEqualsIgnoreWhitespace(final String expected,
                                                     final String actual,
                                                     int beginIndex,
                                                     int endIndex) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" ).replaceAll( "\\n", "" ).replaceAll( "\\r", "" );
        
        final String cleanActual = actual.substring( beginIndex,
                                                     endIndex ).replaceAll( "\\s+",
                                                      "" ).replaceAll( "\\n", "" ).replaceAll( "\\r", "" );
        assertEquals( cleanExpected,
                      cleanActual );
    } 
    
    private static byte[] getBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        byte[] bytes = new byte[512];
     
        // Read bytes from the input stream in bytes.length-sized chunks and write
        // them into the output stream
        int readBytes;
        while ((readBytes = inputStream.read(bytes)) > 0) {
            outputStream.write(bytes, 0, readBytes);
        }
     
        // Convert the contents of the output stream into a byte array
        byte[] byteData = outputStream.toByteArray();
        
        // Close the streams
        inputStream.close();
        outputStream.close();
     
        return byteData;
    }

	public void setWiser(Wiser wiser) {
		this.wiser = wiser;
	}

	public Wiser getWiser() {
		return wiser;
	}

	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}

	public String getEmailPort() {
		return emailPort;
	}

    
}
