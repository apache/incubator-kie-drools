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

package org.jbpm.process.workitem.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.jbpm.services.task.identity.DefaultUserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.utils.ChainedProperties;
import org.kie.internal.utils.ClassLoaderUtil;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class EmailNotificationPublisherTest {

    private Wiser wiser;

    private String emailHost;
    private String emailPort;
    
    private Connection connection;
    private UserInfo userInfo;
    
    private TaskModelFactory factory = TaskModelProvider.getFactory();

    @Before
    public void setUp() throws Exception {
        System.setProperty("org.jbpm.email.templates.dir", new File("src/test/resources/templates").getAbsolutePath());
        TemplateManager.reset();
        
        ChainedProperties props = ChainedProperties.getChainedProperties( "email.conf", ClassLoaderUtil.getClassLoader( null, getClass(), false ));
        emailHost = props.getProperty( "mail.smtp.host", "localhost" );
        emailPort = props.getProperty( "mail.smtp.port", "2345" );

        wiser = new Wiser();
        wiser.setHostname( emailHost );
        wiser.setPort( Integer.parseInt( emailPort ) );
        wiser.start();
        Thread.sleep(200);
        
        connection = new Connection(emailHost, emailPort);
        userInfo = new DefaultUserInfo(true);
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("org.jbpm.email.templates.dir");
        if( wiser != null ) {
            wiser.getMessages().clear();
            wiser.stop();
            wiser = null;
            Thread.sleep(1000);
        }
    }
    
    @Test
    public void testGetEntityForEmail() {
        
        String entityId = userInfo.getEntityForEmail("mary@domain.com");
        assertEquals( "mary", entityId );
    }
    
    @Test
    public void testEmailNotificationWithoutTemplate() throws Exception {
                        
        EmailNotificationPublisher publisher = new EmailNotificationPublisher(connection, userInfo);
        
        Set<OrganizationalEntity> recipients = new HashSet<>(Arrays.asList(factory.newUser("john")));
        publisher.publish("admin@jbpm.org", "Test notification", recipients, "Test body");
        
        assertEquals( 1, wiser.getMessages().size() );

        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        // Side effect of MIME encoding (I think.. ): \r\n..
        String content = ((String) msg.getContent()).replace("\r\n", "");
        assertEquals( "Test body", content );
        assertEquals( "Test notification", msg.getSubject() );
        assertEquals( "admin@jbpm.org", ((InternetAddress)msg.getFrom()[0]).getAddress() );        
        assertEquals( "john@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertNull( msg.getRecipients( RecipientType.CC ) );
        assertNull( msg.getRecipients( RecipientType.BCC ) );
    }
    
    @Test
    public void testEmailNotificationWithTemplate() throws Exception {
                        
        EmailNotificationPublisher publisher = new EmailNotificationPublisher(connection, userInfo);
        
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Name", "John Doe");
        Set<OrganizationalEntity> recipients = new HashSet<>(Arrays.asList(factory.newUser("john")));
        publisher.publish("admin@jbpm.org", "Test notification", recipients, "basic-email", parameters);
        
        assertEquals( 1, wiser.getMessages().size() );

        String expectedBody = "<html><body>Hello John Doe</body></html>";
        
        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        // Side effect of MIME encoding (I think.. ): \r\n..
        String content = ((String) msg.getContent()).replace("\r\n", "");
        assertEquals( expectedBody, content );
        assertEquals( "Test notification", msg.getSubject() );
        assertEquals( "admin@jbpm.org", ((InternetAddress)msg.getFrom()[0]).getAddress() );        
        assertEquals( "john@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertNull( msg.getRecipients( RecipientType.CC ) );
        assertNull( msg.getRecipients( RecipientType.BCC ) );
    }
    
    @Test
    public void testEmailNotificationWithoutTemplateToGroup() throws Exception {
                        
        EmailNotificationPublisher publisher = new EmailNotificationPublisher(connection, userInfo);
        
        Set<OrganizationalEntity> recipients = new HashSet<>(Arrays.asList(factory.newGroup("managers")));
        publisher.publish("admin@jbpm.org", "Test notification", recipients, "Test body");
        
        assertEquals( 1, wiser.getMessages().size() );

        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        // Side effect of MIME encoding (I think.. ): \r\n..
        String content = ((String) msg.getContent()).replace("\r\n", "");
        assertEquals( "Test body", content );
        assertEquals( "Test notification", msg.getSubject() );
        assertEquals( "admin@jbpm.org", ((InternetAddress)msg.getFrom()[0]).getAddress() );        
        assertEquals( "john@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );
        assertNull( msg.getRecipients( RecipientType.CC ) );
        assertNull( msg.getRecipients( RecipientType.BCC ) );
    }
    
    @Test
    public void testEmailNotificationWithoutTemplateToMultipleRecipients() throws Exception {
                        
        EmailNotificationPublisher publisher = new EmailNotificationPublisher(connection, userInfo);
        
        Set<OrganizationalEntity> recipients = new LinkedHashSet<>(Arrays.asList(factory.newGroup("managers"), factory.newUser("mary")));
        publisher.publish("admin@jbpm.org", "Test notification", recipients, "Test body");
        
        assertEquals( 2, wiser.getMessages().size() );

        MimeMessage msg = (( WiserMessage  ) wiser.getMessages().get( 0 )).getMimeMessage();
        // Side effect of MIME encoding (I think.. ): \r\n..
        String content = ((String) msg.getContent()).replace("\r\n", "");
        assertEquals( "Test body", content );
        assertEquals( "Test notification", msg.getSubject() );
        assertEquals( "admin@jbpm.org", ((InternetAddress)msg.getFrom()[0]).getAddress() );        
        assertEquals( "john@domain.com", ((InternetAddress)msg.getRecipients( RecipientType.TO )[0]).getAddress() );       
        assertNull( msg.getRecipients( RecipientType.CC ) );
        assertNull( msg.getRecipients( RecipientType.BCC ) );
    }
}
