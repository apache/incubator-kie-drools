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

package org.jbpm.services.api.query.model;

import java.util.Map;

/**
 * Definition of a query that can be registered in the query service
 *
 */
public interface QueryDefinition {
    
    public enum Target {
        PROCESS,
        TASK,
        BA_TASK,
        PO_TASK,
        JOBS,
        FILTERED_PROCESS,
        FILTERED_BA_TASK,
        FILTERED_PO_TASK,
        CUSTOM;
    }

    /**
     * Return unique name of this query
     * @return
     */
    String getName();
    
    /**
     * Sets unique name for this query definition
     * @param name
     */
    void setName(String name);
    
    /**
     * Returns source location of this query 
     * Depends on exact type of the query definition (e.g. data base location - data source name) 
     * @return
     */
    String getSource();
    
    /**
     * Sets source location of this query
     * Depends on exact type of the query definition (e.g. data base location - data source name) 
     * @param source
     */
    void setSource(String source);
    
    /**
     * Returns expression used to collect/fetch data as part of the query
     * @return
     */
    String getExpression();
    
    /**
     * Sets expression used to collect/fetch data as part of the query
     * @param expression
     */
    void setExpression(String expression);
    
    /**
     * Returns target of this query definition
     * @return
     */
    Target getTarget();
    
    /**
     * Returns resolved columns types once the query was successfully registered.
     */
    Map<String, String> getColumns();
}
