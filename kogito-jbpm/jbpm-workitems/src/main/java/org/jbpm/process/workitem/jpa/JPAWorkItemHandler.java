/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.jpa;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A WorkItemHandler to perform JPA operations. <br />
 * An <b>Action</b> must be provided as an input parameter. The supported value
 * for the Action parameters:
 * <ul>
 * <li>Create: Persist the object and return the attached entity. TO persist an
 * object you must provide the object using the WIH parameter <b>Entity</b></li>
 * <li>Update: Update an attached entity and returns the updated entity. You
 * must provide the attached entity using the <b>Entity</b> WIH parameter.</li>
 * <li>Delete: Delete an attached entity. You must provide the attached entity
 * using the <b>Entity</b> WIH parameter.</li>
 * <li>Get: Get an entity by ID. You must provide the return type using the
 * <b>Type</b> parameter with the FQN of the target class and the ID using the
 * <b>Id</b> parameter.</li>
 * <li>Query: Executes a named query and return the list of results (if any).
 * The query must be provided using the <b>Query</b> input parameter and you may
 * provide query parameters in the form of a Map with key String and value
 * Object using the <b>QueryParameters</b> WIH input parameter. The result of
 * the query is put on the output parameter <b>QueryResults</b>.</li>
 * </ul>
 * When registering the WIH in the deployment descriptor, you must provide the
 * classloader where your mapped entities are and the name of the persistence
 * unit you configured in <em>persistence.xml</em>.
 * 
 */
public class JPAWorkItemHandler extends AbstractLogOrThrowWorkItemHandler
        implements Cacheable {

    private static final Logger logger = LoggerFactory
            .getLogger(JPAWorkItemHandler.class);

    public static final String P_RESULT = "Result";
    public static final String P_TYPE = "Type";
    public static final String P_ID = "Id";
    public static final String P_ENTITY = "Entity";
    public static final String P_ACTION = "Action";
    public static final String P_QUERY = "Query";
    public static final String P_QUERY_PARAMS = "QueryParameters";
    public static final String P_QUERY_RESULTS = "QueryResults";

    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";
    public static final String GET_ACTION = "GET";
    public static final String DELETE_ACTION = "DELETE";
    public static final String QUERY_ACTION = "QUERY";

    private EntityManagerFactory emf;

    private ClassLoader classloader;

    public JPAWorkItemHandler(String persistenceUnit, ClassLoader cl) {
        setLogThrownException(true);
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            this.emf = Persistence.createEntityManagerFactory(persistenceUnit);

        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
        this.classloader = cl;
    }

    public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
        Object actionParam = wi.getParameter(P_ACTION);
        Object entity = wi.getParameter(P_ENTITY);
        Object id = wi.getParameter(P_ID);
        Object type = wi.getParameter(P_TYPE);
        Object queryName = wi.getParameter(P_QUERY);
        Object queryParams = wi.getParameter(P_QUERY_PARAMS);
        Map<String, Object> params = new HashMap<String, Object>();
        List<Object> queryResults = Collections.emptyList();
        String action;
        if (actionParam == null) {
            throw new IllegalArgumentException(
                    "An action is required. Use 'delete', 'create', 'update', query or 'get'");
        }
        // Only QUERY does no require an entity parameter
        if (entity == null && P_ACTION.equals(QUERY_ACTION)) {
            throw new IllegalArgumentException(
                    "An entity is required. Use the 'entity' parameter");
        }
        action = String.valueOf(actionParam).trim().toUpperCase();
        logger.debug("Action {} on {}", action, entity);
        EntityManager em = emf.createEntityManager();
        try {
            // join the process transaction
            em.joinTransaction();
            switch (action) {
            case DELETE_ACTION:
                doDelete(em, entity, id);
                break;
            case GET_ACTION:
                if (id == null || type == null) {
                    throw new IllegalArgumentException(
                            "Id or type can't be null when getting an entity");
                }
                // only works with long for now
                entity = doGet(em, type.toString(), Long.parseLong(id.toString()));
                break;
            case UPDATE_ACTION:
                doUpdate(em, entity);
                break;
            case CREATE_ACTION:
                em.persist(entity);
                break;
            case QUERY_ACTION:
                if (queryName == null) {
                    throw new IllegalArgumentException("You must provide a '"
                            + P_QUERY + "' parameter to run named queries.");
                }
                queryResults = doQuery(em, String.valueOf(queryName), queryParams);
                break;
            default:
                throw new IllegalArgumentException(
                        "Action " + action+  " not recognized. Use 'delete', 'create', 'update', query, or 'get'");
            }
        } catch (Exception e) {
            logger.debug("Error performing JPA action ", e);
            throw e;
        } finally {
            em.close();
        }
        params.put(P_RESULT, entity);
        params.put(P_QUERY_RESULTS, queryResults);
        wim.completeWorkItem(wi.getId(), params);
    }

    @SuppressWarnings("unchecked")
    private List<Object> doQuery(EntityManager em, String queryName, Object queryParams) {
        logger.debug("About to run query {}", queryName);
        Map<String, Object> params;
        Query namedQuery = em.createQuery(queryName);
        if (queryParams == null) {
            logger.debug("No parameters were provided");
        } else {
            params = ((Map<String, Object>) queryParams);
            logger.debug("Parameters {}", params);
            params.forEach(namedQuery::setParameter);
        }
        return namedQuery.getResultList();
    }

    private Object doUpdate(EntityManager em, Object entity) {
        return em.merge(entity);
    }

    private Object doGet(EntityManager em, String clazz, Object id) {
        Class<?> type = loadClass(clazz);
        return em.find(type, id);
    }

    private void doDelete(EntityManager em, Object entity, Object id) {
        // detached entity
        if(!em.contains(entity)) {
            entity = doGet(em, entity.getClass().getName(), id);
        }
        if(entity == null) {
            throw new IllegalArgumentException("Can't load the entity to remove. Provide an attached entity or the id to load it.");
        }
        em.remove(entity);
    }

    public void close() {
        emf.close();
    }

    public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
        wim.abortWorkItem(wi.getId());
    }

    private Class<?> loadClass(String clazz) {
        try {
            return Class.forName(clazz, false, classloader);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't load type " + clazz);
        }
    }
}
