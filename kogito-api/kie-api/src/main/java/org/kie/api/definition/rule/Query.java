/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.definition.rule;

import java.util.Map;

import org.kie.api.definition.KieDefinition;

/**
 * Public Query interface for runtime query inspection.
 */
public interface Query
    extends
    KieDefinition {

    /**
     * Returns the package name (namespace) this query is tied to.
     *  
     * @return the package name.
     */
    String getPackageName();
    
    /**
     * Returns this query's name.
     * 
     * @return the query name
     */
    String getName();

    /**
     * Returns an immutable Map&lt;String key, Object value&gt; of all meta data attributes associated with 
     * this query object.
     * 
     * @return an immutable Map&lt;String key, Object value&gt; of meta data attributes.
     */
    Map<String, Object> getMetaData();

}
