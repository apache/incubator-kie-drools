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

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

public class SendHtml {
    
    public static void sendHtml(Email email) {
        sendHtml(email, email.getConnection());
    } 
    
    public static void sendHtml(Email email, Connection connection) {
        int port = Integer.parseInt(connection.getPort());
        String mailhost = connection.getHost();
        String username = connection.getUserName();
        String password = connection.getPassword();
       
        Session session = getSession(connection);
        boolean debug = false;
        session.setDebug( debug );
        
        try {
            Message msg = fillMessage(email, session);
            
            // send the thing off
            Transport t = (Transport)session.getTransport("smtp");
            try {
                t.connect(mailhost, port, username, password);
                t.sendMessage(msg, msg.getAllRecipients());
            } catch (Exception e) {
                throw new RuntimeException( "Connection failure", e );
            } finally {
                t.close();
            }

        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to send email", e );
        }
    }

    private static Message fillMessage(Email email, Session session) { 
        org.jbpm.process.workitem.email.Message message = email.getMessage();

        String subject = message.getSubject();
        String from = message.getFrom();
        String replyTo = message.getReplyTo();

        String mailer = "sendhtml";
        
        if ( from == null ) {
            throw new RuntimeException("Email must have 'from' address" );
        }
        
        if ( replyTo == null ) {
            replyTo = from;
        }

        // Construct and fill the Message
        Message msg =  null;
        try {
            msg = new MimeMessage( session );
            msg.setFrom( new InternetAddress( from ) );
            msg.setReplyTo( new InternetAddress[] {  new InternetAddress( replyTo ) }  );
            
            for ( Recipient recipient : message.getRecipients().getRecipients() ) {
                RecipientType type = null;
                if ( "To".equals( recipient.getType() ) ) {
                    type = Message.RecipientType.TO;
                } else if ( "Cc".equals( recipient.getType() ) ) {
                    type = Message.RecipientType.CC;
                } else if ( "Bcc".equals( recipient.getType() ) ) {
                    type = Message.RecipientType.BCC;
                } else {
                    throw new RuntimeException( "Unable to determine recipient type" );
                }

                msg.addRecipients( type, InternetAddress.parse( recipient.getEmail(), false ) );
            }
            msg.setSubject( subject );
            collect( message.getBody(), msg );
            msg.setHeader( "X-Mailer", mailer );
            msg.setSentDate( new Date() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to send email", e );
        }
        
        return msg;
    }
    
    public static void collect(String body, Message msg) throws MessagingException, IOException {
//        String subject = msg.getSubject();
        StringBuffer sb = new StringBuffer();
//        sb.append( "<HTML>\n" );
//        sb.append( "<HEAD>\n" );
//        sb.append( "<TITLE>\n" );
//        sb.append( subject + "\n" );
//        sb.append( "</TITLE>\n" );
//        sb.append( "</HEAD>\n" );
//        sb.append( "<BODY>\n" );
//        sb.append( "<H1>" + subject + "</H1>" + "\n" );
        sb.append( body );
//        sb.append( "</BODY>\n" );
//        sb.append( "</HTML>\n" );
        msg.setDataHandler( new DataHandler( new ByteArrayDataSource( sb.toString(), "text/html" ) ) );
    }
    
    
    private static Session getSession(Connection connection) {
        String username = connection.getUserName();
        String password = connection.getPassword();

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", connection.getHost());
        properties.setProperty("mail.smtp.port", connection.getPort());

        Session session = null;
        if( username != null ) { 
            properties.setProperty("mail.smtp.submitter", username);
            if( password != null) {
                Authenticator authenticator = new Authenticator(username, password);
                properties.setProperty("mail.smtp.auth", "true");
                session = Session.getInstance(properties, authenticator);
            }
            else { 
                session = Session.getInstance(properties);
            }
        }
        else { 
            session = Session.getInstance(properties);
        }
        
        if( connection.getStartTls() != null && connection.getStartTls() ) { 
            properties.put("mail.smtp.starttls.enable","true");
        }
       
        return session;
    }

    private static class Authenticator extends javax.mail.Authenticator {
        private PasswordAuthentication authentication;

        public Authenticator(String username, String password) {
            authentication = new PasswordAuthentication(username, password);
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}
