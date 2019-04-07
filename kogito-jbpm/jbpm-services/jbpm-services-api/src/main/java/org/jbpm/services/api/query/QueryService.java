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

package org.jbpm.services.api.query;

import java.util.List;

import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryParam;
import org.kie.api.runtime.query.QueryContext;

/**
 * Advanced queries service that allows to register custom queries 
 * that will be equipped with tailored capabilities of querying data.
 *
 */
public interface QueryService {

    /**
     * Registers new query definition in the system so it can be used for executing queries on top of it.
     * @param queryDefinition definition of the query to be registered
     * @throws QueryAlreadyRegisteredException in case there is already query registered with given name (queryDefinition.getName())
     */
    void registerQuery(QueryDefinition queryDefinition) throws QueryAlreadyRegisteredException;

    /**
     * Registers or replaces existing query. Similar to what <code>registerQuery</code> does, though it won't throw 
     * exception in case there is already query registered but simply replace it.
     * @param queryDefinition definition of the query to be registered/replaced
     */
    void replaceQuery(QueryDefinition queryDefinition);

    /**
     * Removes the query definition from the system
     * @param uniqueQueryName unique name that query was registered under
     * @throws QueryNotFoundException in case there is no such query registered
     */
    void unregisterQuery(String uniqueQueryName) throws QueryNotFoundException;

    /**
     * Returns query definition details that is registered under given uniqueQueryName
     * @param uniqueQueryName unique name that query was registered under 
     * @return query definition details if found
     * @throws QueryNotFoundException in case there is no such query registered
     */
    QueryDefinition getQuery(String uniqueQueryName) throws QueryNotFoundException;

    /**
     * Returns list of query definitions registered in the system
     * @param queryContext provides pagnition information for the query
     * @return returns list of found queries
     */
    List<QueryDefinition> getQueries(QueryContext queryContext);

    /**
     * Performs query on given query definition that had to be previously registered. Results will be mapped 
     * by given <code>mapper</code> and:
     * <ul>
     *  <li>sorting and paging will be applied based on <code>queryContext</code></li>
     *  <li>filtering of results will be done based on <code>filterParams</code> if given</li>
     *</ul>
     * @param queryName unique name that query was registered under
     * @param mapper type of the <code>QueryResultMapper</code> to map raw data set into list of objects
     * @param queryContext query context carrying paging and sorting details
     * @param filterParams additional filter parameters to narrow down the result
     * @return results mapped to objects from raw data set
     * @throws QueryNotFoundException in case there is no such query registered
     */
    <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParam... filterParams) throws QueryNotFoundException;

    /**
     * Performs query on given query definition that had to be previously registered. Results will be mapped 
     * by given <code>mapper</code> and:
     * <ul>
     *  <li>sorting and paging will be applied based on <code>queryContext</code></li>
     *  <li>filtering of results will be done based on <code>paramBuilder</code> which 
     *  is an implementation of <code>QueryParamBuilder</code> for building advanced filters</li>
     *</ul>
     * @param queryName unique name that query was registered under
     * @param mapper type of the <code>QueryResultMapper</code> to map raw data set into list of objects
     * @param queryContext query context carrying paging and sorting details
     * @param paramBuilder implementation of <code>QueryParamBuilder</code> that allows to build custom filters in advanced way
     * @return results mapped to objects from raw data set
     * @throws QueryNotFoundException in case there is no such query registered
     */
    <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParamBuilder<?> paramBuilder) throws QueryNotFoundException;

}
