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
package org.jbpm.shared.services.api;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Query;

/**
 *
 * @author salaboy
 */
public interface JbpmServicesPersistenceManager {

    /**
     * It is strongly suggested that you only use this method within a transaction!!
     * </p>
     * PostgreSQL and DB2 are 2 databases which, depending on your situation, will probably require this.
     *
     * @param queryString The JPQL query string to execute.
     * @return The result of the query.
     */
    
    Object queryWithParametersInTransaction(String queryName, Map<String, Object> params);
    
    Object queryInTransaction(String queryName);
    
    Object queryStringInTransaction(String queryString );
   
    Object queryStringWithParametersInTransaction(String queryString,  Map<String, Object> params );
    
    HashMap<String, Object> addParametersToMap(Object ... parameterValues);

    void remove(Object entity);
    
    <T> T merge(T entity);

    void dispose();

    <T> T find(Class<T> entityClass, Object primaryKey);

    void persist(Object entity);
    
}
