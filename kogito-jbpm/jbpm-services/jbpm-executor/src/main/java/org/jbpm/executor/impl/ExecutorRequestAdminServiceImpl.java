/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.api.ExecutorRequestAdminService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;

/**
 *
 * @author salaboy
 */
@Transactional
public class ExecutorRequestAdminServiceImpl implements ExecutorRequestAdminService {

    @Inject
    private JbpmServicesPersistenceManager pm;

    public ExecutorRequestAdminServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }

    public int clearAllRequests() {
        List<RequestInfo> requests = (List<RequestInfo>)pm.queryStringInTransaction("select r from RequestInfo r");
        for (RequestInfo r : requests) {
            pm.remove(r);

        }
        return requests.size();
    }

    public int clearAllErrors() {
        List<ErrorInfo> errors = (List<ErrorInfo>)pm.queryStringInTransaction("select e from ErrorInfo e");

        for (ErrorInfo e : errors) {
            pm.remove(e);

        }
        return errors.size();
    }
}
