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
package org.jbpm.services.cdi.impl.audit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.process.audit.jms.AsyncAuditLogReceiver;

/**
 * Extension of default <code>AsyncAuditLogReceiver</code> that is MessageListener
 * to allow entity manager factory to be injected.
 * This class shall be declared as the actual Audit log processor for JMS environments - like MDB.
 */
public class AuditJMSProcessor extends AsyncAuditLogReceiver {

    @Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory entityManagerFactory;
    
    public AuditJMSProcessor() {
        super(null);
    }
    
    @PostConstruct
    public void configure() {
        setEntityManagerFactory(entityManagerFactory);
    }
}
