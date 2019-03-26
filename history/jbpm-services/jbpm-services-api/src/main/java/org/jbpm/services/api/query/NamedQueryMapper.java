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

import java.util.Map;

/**
 * A delegate mapper that will delay look up of actual mapper to the time its 
 * <code>map</code> method is invoked. Especially useful for EJB remote usage when dependency
 * to api only is preferred and the actual implementations will be found on server side.
 *
 * @param <T> the result type expected to be returned
 */
public class NamedQueryMapper<T> implements QueryResultMapper<T> {

    private static final long serialVersionUID = -5975192042647431269L;
    private String mapperName;
    
    /**
     * Creates new instance with actual mapper name
     * @param mapperName unique name of the mapper that will be used to produce results.
     */
    public NamedQueryMapper(String mapperName) {
        this.mapperName = mapperName;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T map(Object result) {
        QueryResultMapper<T> actualMapper = (QueryResultMapper<T>) QueryMapperRegistry.get().mapperFor(mapperName, null);
        
        return actualMapper.map(result);
    }

    @Override
    public String getName() {
        return "NamedQueryMapper";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<?> getType() {
        QueryResultMapper<T> actualMapper = (QueryResultMapper<T>) QueryMapperRegistry.get().mapperFor(mapperName, null);
        return actualMapper.getType();
    }

    @Override
    public QueryResultMapper<T> forColumnMapping(Map<String, String> columnMapping) {
        return new NamedQueryMapper<T>(mapperName);
    }

}
