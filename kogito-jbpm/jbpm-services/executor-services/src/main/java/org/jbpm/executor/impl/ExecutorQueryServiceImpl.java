/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor.impl;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.entities.ErrorInfo;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.entities.STATUS;

/**
 *
 * @author salaboy
 */
@Transactional
public class ExecutorQueryServiceImpl implements ExecutorQueryService {

    @Inject
    private EntityManager em;
    @Inject
    private EntityManagerFactory emf;

    public ExecutorQueryServiceImpl() {
    }

    public List<RequestInfo> getPendingRequests() {
        return getEntityManager().createNamedQuery("PendingRequests", RequestInfo.class).setParameter("now", new Date()).getResultList();
    }
    public List<RequestInfo> getPendingRequestById(Long id) {
        return getEntityManager().createNamedQuery("PendingRequestById", RequestInfo.class).setParameter("id", id).getResultList();
    }
    public List<RequestInfo> getRunningRequests() {
        return getEntityManager().createNamedQuery("RunningRequests", RequestInfo.class).getResultList();
    }

    public List<RequestInfo> getQueuedRequests() {
        return getEntityManager().createNamedQuery("QueuedRequests", RequestInfo.class).getResultList();
    }
    
    public List<RequestInfo> getFutureQueuedRequests() {
        return getEntityManager().createNamedQuery("FutureQueuedRequests", RequestInfo.class).setParameter("now", new Date()).getResultList();
    }

    public List<RequestInfo> getCompletedRequests() {
        return getEntityManager().createNamedQuery("CompletedRequests", RequestInfo.class).getResultList();
    }

    public List<RequestInfo> getInErrorRequests() {
        return getEntityManager().createNamedQuery("InErrorRequests", RequestInfo.class).getResultList();
    }

    public List<RequestInfo> getCancelledRequests() {
        return getEntityManager().createNamedQuery("CancelledRequests", RequestInfo.class).getResultList();
    }

    public List<ErrorInfo> getAllErrors() {
    	return getEntityManager().createNamedQuery("GetAllErrors", ErrorInfo.class).getResultList();
    }

    public List<RequestInfo> getAllRequests() {
    	return getEntityManager().createNamedQuery("GetAllRequests", RequestInfo.class).getResultList();
    }
    
	public List<RequestInfo> getRequestsByStatus(List<STATUS> statuses) {
    	return getEntityManager().createNamedQuery("GetRequestsByStatus", RequestInfo.class).
    		setParameter("statuses", statuses).getResultList();
	}

	/*
     * following are supporting methods to allow execution on application startup
     * as at that time RequestScoped entity manager cannot be used so instead
     * use EntityManagerFactory and manage transaction manually
     */
    protected EntityManager getEntityManager() {
        try {
            this.em.toString();          
            return this.em;
        } catch (ContextNotActiveException e) {
            EntityManager em = this.emf.createEntityManager();
            return em;
        }
    }

}
