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

package org.jbpm.task.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.kie.util.ChainedProperties;
import org.kie.util.ClassLoaderUtil;
import org.jbpm.task.Deadline;
import org.jbpm.task.I18NText;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.UserInfo;

//import net.fortuna.ical4j.model.Calendar;
//import net.fortuna.ical4j.model.DateTime;
//import net.fortuna.ical4j.model.TimeZone;
//import net.fortuna.ical4j.model.TimeZoneRegistry;
//import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
//import net.fortuna.ical4j.model.component.VEvent;
//import net.fortuna.ical4j.model.component.VTimeZone;
//import net.fortuna.ical4j.model.component.VToDo;
//import net.fortuna.ical4j.model.property.CalScale;
//import net.fortuna.ical4j.model.property.Description;
//import net.fortuna.ical4j.model.property.Method;
//import net.fortuna.ical4j.model.property.Priority;
//import net.fortuna.ical4j.model.property.ProdId;
//import net.fortuna.ical4j.model.property.Uid;
//import net.fortuna.ical4j.model.property.Version;

public class SendIcal {
    private static SimpleDateFormat df = new SimpleDateFormat( "yyyyMMdd'T'HHmmss'Z'" );
    static {
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
    }

    private Properties              connection;
    private String                  defaultLanguage;

    private static SendIcal         instance;

    public static void initInstance(Properties properties) {
        if ( instance == null ) {
            instance = new SendIcal( properties );
        }
    }

    public static SendIcal getInstance() {
        if ( instance == null ) {
            instance = new SendIcal();
        }
        return instance;
    }

    SendIcal() {
        ChainedProperties conf = new ChainedProperties( "drools.email.conf",ClassLoaderUtil.getClassLoader( null, getClass(), false ) );
        String host = conf.getProperty( "mail.smtp.host",
                                        "localhost" );
        String port = conf.getProperty( "mail.smtp.port",
                                        "25" );

        connection = new Properties();
        connection.setProperty( "mail.transport.protocol",
                                "smtp" );

        if ( host != null && host.trim().length() > 0 ) {
            connection.setProperty( "mail.smtp.host",
                                    host );
        }
        if ( port != null && port.trim().length() > 0 ) {
            connection.setProperty( "mail.smtp.port",
                                    port );
        }

        defaultLanguage = conf.getProperty( "defaultLanguage",
                                            "en-UK" );
    }

    SendIcal(Properties conf) {
        String host = conf.getProperty( "mail.smtp.host",
                                        "localhost" );
        String port = conf.getProperty( "mail.smtp.port",
                                        "25" );

        connection = new Properties();
        connection.setProperty( "mail.transport.protocol",
                                "smtp" );

        if ( host != null && host.trim().length() > 0 ) {
            connection.setProperty( "mail.smtp.host",
                                    host );
        }
        if ( port != null && port.trim().length() > 0 ) {
            connection.setProperty( "mail.smtp.port",
                                    port );
        }

        defaultLanguage = conf.getProperty( "defaultLanguage",
                                            "en-UK" );
    }

    public void sendIcalForTask(Task task,
                                UserInfo userInfo) {
    	if (userInfo == null) {
    		return;
    	}
    	
        TaskData data = task.getTaskData();
        User owner = data.getActualOwner();
        User creator = data.getCreatedBy();
        Date createdOn = data.getCreatedOn();

        if ( task.getDeadlines() == null ) {
            return;
        }

        // get earliest start deadline
        List<Deadline> startDeadlines = task.getDeadlines().getStartDeadlines();
        Deadline start = null;
        for ( Deadline deadline : startDeadlines ) {
            if ( start == null || start.getDate().getTime() > deadline.getDate().getTime() ) {
                start = deadline;
            }
        }

        // get latest end deadline
        List<Deadline> endDeadlines = task.getDeadlines().getEndDeadlines();
        Deadline end = null;
        for ( Deadline deadline : endDeadlines ) {
            if ( end == null || end.getDate().getTime() < deadline.getDate().getTime() ) {
                end = deadline;
            }
        }

        String language = userInfo.getLanguageForEntity( owner );
        String name = I18NText.getLocalText( task.getNames(),
                                             language,
                                             defaultLanguage );
        String summary = I18NText.getLocalText( task.getSubjects(),
                                                language,
                                                defaultLanguage );
        String description = I18NText.getLocalText( task.getDescriptions(),
                                                    language,
                                                    defaultLanguage );
        // send ical for start
        if ( start != null ) {
            try {
                sendIcal( task.getId(),
                          name,
                          summary,
                          description,
                          task.getPriority(),
                          start.getDate(),
                          owner,
                          creator,
                          createdOn,
                          userInfo,
                          "Start" );
            } catch ( Exception e ) {

            }
        }

        // send ical for end
        if ( end != null ) {
            try {
                sendIcal( task.getId(),
                          name,
                          summary,
                          description,
                          task.getPriority(),
                          end.getDate(),
                          owner,
                          creator,
                          createdOn,
                          userInfo,
                          "End" );
            } catch ( Exception e ) {

            }
        }
    }

    public void sendIcal(long taskId,
                         String name,
                         String summary,
                         String description,
                         int priority,
                         Date startDate,
                         User owner,
                         User creator,
                         Date createdOn,
                         UserInfo userInfo,
                         String type) throws Exception {
        MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap) MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes( "text/calendar ics ICS" );

        MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap( "text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain" );

        System.out.println( connection );
        Session session = Session.getInstance( connection,
                                               null );

        // Define message
        MimeMessage message = new MimeMessage( session );
        message.setHeader( "Content-Class",
                           "urn:content-classes:calendarmessage" );
        message.setHeader( "Content-ID",
                           "calendar_message" );

        String creatorEmail = userInfo.getEmailForEntity( creator );
        message.setFrom( new InternetAddress( creatorEmail ) );
        message.setReplyTo( new InternetAddress[]{new InternetAddress( creatorEmail )} );
        message.addRecipient( Message.RecipientType.TO,
                              new InternetAddress( userInfo.getEmailForEntity( owner ) ) );
        message.setSubject( "Task Assignment " + type + " Event: " + name );
        message.setSentDate( new Date() );

        // Create a Multipart
        Multipart multipart = new MimeMultipart( "alternative" );

        // Add text message
        BodyPart messageBodyPart = new MimeBodyPart();
        String text = "Summary\n-------\n\n" + summary + "\n\nDescription\n-----------\n\n" + description;
        messageBodyPart.setText( text );
        messageBodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( text,
                                                                                  "text/plain; charset=UTF8;" ) ) );
        multipart.addBodyPart( messageBodyPart );

        // Add ical
        messageBodyPart = new MimeBodyPart();
        String filename = "ical-" + type + "-" + taskId + ".ics";
        messageBodyPart.setFileName( filename );
        messageBodyPart.setHeader( "Content-Class",
                                   "urn:content-classes:calendarmessage" );
        messageBodyPart.setHeader( "Content-ID",
                                   "calendar_message" );
        String icalStr = getIcal( summary,
                                  description,
                                  startDate,
                                  priority,
                                  userInfo.getDisplayName( creator ),
                                  creatorEmail,
                                  type );

        messageBodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( icalStr,
                                                                                  "text/calendar; charset=UTF8; " ) ) );
        multipart.addBodyPart( messageBodyPart );

        message.setContent( multipart );
        message.saveChanges();

        Transport.send( message );
    }

    private String getIcal(String summary,
                           String description,
                           Date date,
                           int priority,
                           String organizerDisplayName,
                           String organizerEmail,
                           String type) {
        StringBuilder builder = new StringBuilder();
        builder.append( "BEGIN:VCALENDAR\n" );
        builder.append( "PRODID:-//iCal4j 1.0//EN\n" );
        builder.append( "CALSCALE:GREGORIAN\n" );
        builder.append( "VERSION:2.0\n" );
        builder.append( "METHOD:REQUEST\n" );
        builder.append( "BEGIN:VEVENT\n" );
        builder.append("DTSTART;TZID=UTC:").append(df.format(date)).append("\n");
        builder.append("UID:").append(UUID.randomUUID().toString()).append("\n");
        builder.append("ORGANIZER;CN=\"").append(organizerDisplayName).append("\":mailto:").append(organizerEmail).append("\n");
        builder.append("DTSTAMP;TZID=UTC:").append(df.format(new Date())).append("\n");
        builder.append("SUMMARY:\"Task ").append(type).append(" : ").append(summary).append("\"\n");
        builder.append("DESCRIPTION:\"").append(description).append("\"\n");
        builder.append("PRIORITY:").append(priority).append("\n");
        builder.append( "END:VEVENT\n" );
        builder.append( "END:VCALENDAR\n" );
        return builder.toString();
    }
}
