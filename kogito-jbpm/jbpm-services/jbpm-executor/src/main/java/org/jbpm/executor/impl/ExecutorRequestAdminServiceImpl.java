/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmJTATransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.kie.internal.executor.api.ExecutorAdminService;

/**
 * Default implementation of <code>ExecutorAdminService</code> backed with JPA
 *
 */
@Transactional
public class ExecutorRequestAdminServiceImpl implements ExecutorAdminService {

    @Inject
    private JbpmServicesPersistenceManager pm;

    public ExecutorRequestAdminServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    @PostConstruct
    public void init() {
        // make sure it has tx manager as it runs as background thread - no request scope available
        if (!((JbpmServicesPersistenceManagerImpl) pm).hasTransactionManager()) {
            ((JbpmServicesPersistenceManagerImpl) pm).setTransactionManager(new JbpmJTATransactionManager());
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int clearAllRequests() {
        
        List<RequestInfo> requests = (List<RequestInfo>)pm.queryStringInTransaction("select r from RequestInfo r");
        for (RequestInfo r : requests) {
            pm.remove(r);

        }
        return requests.size();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int clearAllErrors() {
        List<ErrorInfo> errors = (List<ErrorInfo>)pm.queryStringInTransaction("select e from ErrorInfo e");

        for (ErrorInfo e : errors) {
            pm.remove(e);

        }
        return errors.size();
    }
}
