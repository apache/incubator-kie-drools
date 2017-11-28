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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SendHtml {

    private static final String MAIL_JNDI_KEY = System.getProperty("org.kie.mail.session",
                                                                   "mail/jbpmMailSession");

    private static boolean debug = Boolean.parseBoolean(System.getProperty("org.kie.mail.debug",
                                                                           "false"));

    public static void sendHtml(Email email) {
        sendHtml(email,
                 email.getConnection());
    }

    public static void sendHtml(Email email,
                                boolean debug) {
        sendHtml(email,
                 email.getConnection(),
                 debug);
    }

    public static void sendHtml(Email email,
                                Connection connection) {
        sendHtml(email,
                 connection,
                 debug);
    }

    public static void sendHtml(Email email,
                                Connection connection,
                                boolean debug) {

        Session session = getSession(connection);
        session.setDebug(debug);

        try {
            Message msg = fillMessage(email,
                                      session);

            // send the thing off
            Transport t = null;
            try {
                
                if (connection != null) {
                    t = (Transport) session.getTransport("smtp");
                    
                    int port = Integer.parseInt(connection.getPort());
                    String mailhost = connection.getHost();
                    String username = connection.getUserName();
                    String password = connection.getPassword();
                
                    t.connect(mailhost,
                              port,
                              username,
                              password);
                    t.sendMessage(msg,
                                  msg.getAllRecipients());
                } else {
                    Transport.send(msg, msg.getAllRecipients());
                }
                
            } catch (Exception e) {
                throw new RuntimeException("Connection failure",
                                           e);
            } finally {
                if (t != null) {
                    t.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email",
                                       e);
        }
    }

    private static Message fillMessage(Email email,
                                       Session session) {
        org.jbpm.process.workitem.email.Message message = email.getMessage();

        String subject = message.getSubject();
        String from = message.getFrom();
        String replyTo = message.getReplyTo();

        String mailer = "sendhtml";

        if (from == null) {
            throw new RuntimeException("Email must have 'from' address");
        }

        if (replyTo == null) {
            replyTo = from;
        }

        // Construct and fill the Message
        Message msg = null;
        try {
            msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(replyTo)});

            for (Recipient recipient : message.getRecipients().getRecipients()) {
                RecipientType type = null;
                if ("To".equals(recipient.getType())) {
                    type = Message.RecipientType.TO;
                } else if ("Cc".equals(recipient.getType())) {
                    type = Message.RecipientType.CC;
                } else if ("Bcc".equals(recipient.getType())) {
                    type = Message.RecipientType.BCC;
                } else {
                    throw new RuntimeException("Unable to determine recipient type");
                }

                msg.addRecipients(type,
                                  InternetAddress.parse(recipient.getEmail(),
                                                        false));
            }

            if (message.hasAttachment()) {
                Multipart multipart = new MimeMultipart();
                // prepare body as first mime body part
                MimeBodyPart messageBodyPart = new MimeBodyPart();

                messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(message.getBody(),
                                                                                       "text/html")));
                multipart.addBodyPart(messageBodyPart);

                List<String> attachments = message.getAttachments();
                for (String attachment : attachments) {
                    MimeBodyPart attachementBodyPart = new MimeBodyPart();
                    URL attachmentUrl = getAttachemntURL(attachment);
                    String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(attachmentUrl.getFile());
                    attachementBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(attachmentUrl.openStream(),
                                                                                               contentType)));
                    String fileName = new File(attachmentUrl.getFile()).getName();
                    attachementBodyPart.setFileName(fileName);
                    attachementBodyPart.setContentID("<" + fileName + ">");

                    multipart.addBodyPart(attachementBodyPart);
                }
                // Put parts in message
                msg.setContent(multipart);
            } else {
                msg.setDataHandler(new DataHandler(new ByteArrayDataSource(message.getBody(),
                                                                           "text/html")));
            }

            msg.setSubject(subject);

            msg.setHeader("X-Mailer",
                          mailer);
            msg.setSentDate(new Date());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email",
                                       e);
        }

        return msg;
    }

    public static void collect(String body,
                               Message msg) throws MessagingException, IOException {
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
        sb.append(body);
//        sb.append( "</BODY>\n" );
//        sb.append( "</HTML>\n" );

    }

    private static Session getSession(Connection connection) {

        Session session = null;
        try {
            session = InitialContext.doLookup(MAIL_JNDI_KEY);
        } catch (NamingException e1) {
            if (connection == null) {
                throw new RuntimeException("Connection details are not given and mail session was not found in JNDI - " + MAIL_JNDI_KEY, e1);
            }
            String username = connection.getUserName();
            String password = connection.getPassword();

            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host",
                                   connection.getHost());
            properties.setProperty("mail.smtp.port",
                                   connection.getPort());

            if (connection.getStartTls() != null && connection.getStartTls()) {
                properties.put("mail.smtp.starttls.enable",
                               "true");
            }
            if (username != null) {
                properties.setProperty("mail.smtp.submitter",
                                       username);
                if (password != null) {
                    Authenticator authenticator = new Authenticator(username,
                                                                    password);
                    properties.setProperty("mail.smtp.auth",
                                           "true");
                    session = Session.getInstance(properties,
                                                  authenticator);
                } else {
                    session = Session.getInstance(properties);
                }
            } else {
                session = Session.getInstance(properties);
            }
        }

        return session;
    }

    protected static URL getAttachemntURL(String attachment) throws MalformedURLException {
        if (attachment.startsWith("classpath:")) {
            String location = attachment.replaceFirst("classpath:",
                                                      "");
            return SendHtml.class.getResource(location);
        } else {
            URL attachmentUrl = new URL(attachment);

            return attachmentUrl;
        }
    }

    private static class Authenticator extends javax.mail.Authenticator {

        private PasswordAuthentication authentication;

        public Authenticator(String username,
                             String password) {
            authentication = new PasswordAuthentication(username,
                                                        password);
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }
}