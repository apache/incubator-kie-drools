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

package org.jbpm.process.workitem.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import junit.framework.TestCase;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItemManager;
import org.drools.util.ChainedProperties;
import org.drools.util.ClassLoaderUtil;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class EmailWorkItemHandlerTest extends TestCase {
    Wiser wiser;    
    
    String emailHost;
    String emailPort;
    
    @Override
    protected void setUp() throws Exception {
        ChainedProperties props = new ChainedProperties( "email.conf", ClassLoaderUtil.getClassLoader( null, getClass(), false ));
        emailHost = props.getProperty( "host", "localhost" );
        emailPort = props.getProperty( "port", "2345" );
        
        wiser = new Wiser();
        wiser.setHostname( emailHost );
        wiser.setPort( Integer.parseInt( emailPort ) );
        wiser.start();
    }
    
    @Override
    protected void tearDown() throws Exception {
        wiser.stop();
    }
    
    public void test1() {
        
    }
    
    public void FIXME_testSingleTo() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection( emailHost, emailPort, null, null );   
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "To", "person1@domain.com" );
        workItem.setParameter( "From", "person2@domain.com" );
        workItem.setParameter( "Reply-To", "person3@domain.com" );
        workItem.setParameter( "Subject", "Subject 1" );
        workItem.setParameter( "Body", "Body 1" );
        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );
        
        assertEquals( 1, wiser.getMessages().size() );
        
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        assertEquals( workItem.getParameter( "Body" ), msg.getContent() );
        assertEquals( workItem.getParameter( "Subject" ), msg.getSubject() );
        assertEquals( workItem.getParameter( "From" ), ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( workItem.getParameter( "Reply-To" ), ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( workItem.getParameter( "To" ), ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertNull( msg.getRecipients( RecipientType.CC ) );
        assertNull( msg.getRecipients( RecipientType.BCC ) );
    }
    
    public void FIXME_testSingleToWithSingleCCAndBCC() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection( emailHost, emailPort, null, null ); 
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "To", "person1@domain.com" );
        workItem.setParameter( "Cc", "person2@domain.com" );
        workItem.setParameter( "Bcc", "person3@domain.com" );
        workItem.setParameter( "From", "person4@domain.com" );
        workItem.setParameter( "Reply-To", "person5@domain.com" );
        workItem.setParameter( "Subject", "Subject 1" );
        workItem.setParameter( "Body", "Body 1" );
        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );
        
        assertEquals( 3, wiser.getMessages().size() );
        
        List<String> list = new ArrayList<String>(3);
        list.add( wiser.getMessages().get( 0 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 1 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 2 ).getEnvelopeReceiver() );
        
        assertTrue( list.contains("person1@domain.com"));
        assertTrue( list.contains("person2@domain.com"));
        assertTrue( list.contains("person3@domain.com"));
        
        
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        assertEquals( workItem.getParameter( "From" ), wiser.getMessages().get( 0 ).getEnvelopeSender() );
        assertEquals( workItem.getParameter( "Body" ), msg.getContent() );
        assertEquals( workItem.getParameter( "Subject" ), msg.getSubject() );
        assertEquals( workItem.getParameter( "From" ), ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( workItem.getParameter( "Reply-To" ), ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( workItem.getParameter( "To" ), ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( workItem.getParameter( "Cc" ),((InternetAddress)msg.getRecipients( RecipientType.CC )[0]).getAddress()  );
        
        msg = (( WiserMessage  ) wiser.getMessages().get( 1 )).getMimeMessage();
        assertEquals( workItem.getParameter( "From" ), wiser.getMessages().get( 1 ).getEnvelopeSender() );
        assertEquals( workItem.getParameter( "Body" ), msg.getContent() );
        assertEquals( workItem.getParameter( "Subject" ), msg.getSubject() );
        assertEquals( workItem.getParameter( "From" ), ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( workItem.getParameter( "Reply-To" ), ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( workItem.getParameter( "To" ), ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( workItem.getParameter( "Cc" ),((InternetAddress)msg.getRecipients( RecipientType.CC )[0]).getAddress()  );
        
        msg = (( WiserMessage  ) wiser.getMessages().get( 2 )).getMimeMessage();
        assertEquals( workItem.getParameter( "From" ), wiser.getMessages().get( 2 ).getEnvelopeSender() );
        assertEquals( workItem.getParameter( "Body" ), msg.getContent() );
        assertEquals( workItem.getParameter( "Subject" ), msg.getSubject() );
        assertEquals( workItem.getParameter( "From" ), ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( workItem.getParameter( "Reply-To" ), ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( workItem.getParameter( "To" ), ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertEquals( workItem.getParameter( "Cc" ),((InternetAddress)msg.getRecipients( RecipientType.CC )[0]).getAddress()  );        
    }    
    
    public void FIXME_testMultipleToWithSingleCCAndBCC() throws Exception {
        EmailWorkItemHandler handler = new EmailWorkItemHandler();
        handler.setConnection( emailHost, emailPort, null, null );    
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "To", "person1@domain.com; person2@domain.com" );
        workItem.setParameter( "Cc", "person3@domain.com; person4@domain.com"  );
        workItem.setParameter( "Bcc","person5@domain.com; person6@domain.com"  );
        workItem.setParameter( "From", "person4@domain.com" );
        workItem.setParameter( "Reply-To", "person5@domain.com" );
        workItem.setParameter( "Subject", "Subject 1" );
        workItem.setParameter( "Body", "Body 1" );
        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );
        
        assertEquals( 6, wiser.getMessages().size() );
        
        List<String> list = new ArrayList<String>(6);
        list.add( wiser.getMessages().get( 0 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 1 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 2 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 3 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 4 ).getEnvelopeReceiver() );
        list.add( wiser.getMessages().get( 5 ).getEnvelopeReceiver() );
        
        assertTrue( list.contains("person1@domain.com"));
        assertTrue( list.contains("person2@domain.com"));
        assertTrue( list.contains("person3@domain.com"));
        assertTrue( list.contains("person4@domain.com"));
        assertTrue( list.contains("person5@domain.com"));
        assertTrue( list.contains("person6@domain.com"));
                
        // We know from previous test that all MimeMessages will be identical
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        assertEquals( workItem.getParameter( "From" ), wiser.getMessages().get( 0 ).getEnvelopeSender() );
        assertEquals( workItem.getParameter( "Body" ), msg.getContent() );
        assertEquals( workItem.getParameter( "Subject" ), msg.getSubject() );
        assertEquals( workItem.getParameter( "From" ), ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( workItem.getParameter( "Reply-To" ), ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        assertEquals( workItem.getParameter( "To" ), ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() + "; " + ((InternetAddress)msg.getRecipients( RecipientType.TO )[1]).getAddress() );
        assertEquals( workItem.getParameter( "Cc" ),((InternetAddress)msg.getRecipients( RecipientType.CC )[0]).getAddress()  + "; " +  ((InternetAddress)msg.getRecipients( RecipientType.CC )[1]).getAddress() );       
    }    
}
