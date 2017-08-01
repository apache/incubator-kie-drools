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
package org.jbpm.services.task.deadlines.notifications.impl.email;

import java.util.Properties;

import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSessionProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailSessionProducer.class);
    private static final String MAIL_JNDI_KEY = System.getProperty("org.kie.mail.session", "mail/jbpmMailSession");

    private static Session mailSession;
    
    public static Session produceSession() {
        if (mailSession == null) {
            
            try {
                mailSession = InitialContext.doLookup(MAIL_JNDI_KEY);
            } catch (NamingException e1) {
                logger.debug("Mail session was not found in JNDI under {} trying to look up email.properties on classspath", MAIL_JNDI_KEY);
                Properties conf = new Properties();
                try {
                    conf.load(EmailSessionProducer.class.getResourceAsStream("/email.properties"));
                    
                    mailSession = Session.getInstance(conf);
                } catch (Exception e) {
                    logger.debug("email.properties was not found on classpath, nor mail session available in JNDI, unable to configure deadlines");
                    return null;
                }
            }
        }
        
        return mailSession;
    }
}
