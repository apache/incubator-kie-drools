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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allow to find mappers by name instead of using their instances
 * Mappers are discovered by ServiceLoader mechanism - meaning must be defined
 * in services file:<br>
 * META-INF/services/org.jbpm.services.api.query.QueryResultMapper
 * <br>
 * as fully qualified class names that implement <code>org.jbpm.services.api.query.QueryResultMapper</code>
 */
public class QueryMapperRegistry {
    
    public static final Logger logger = LoggerFactory.getLogger(QueryMapperRegistry.class);
    private static QueryMapperRegistry INSTANCE = new QueryMapperRegistry();
    
    private ConcurrentMap<String, QueryResultMapper<?>> knownMappers = new ConcurrentHashMap<String, QueryResultMapper<?>>();
    
    protected QueryMapperRegistry() {
        discoverAndAddMappers(this.getClass().getClassLoader());
    }
    
    /**
     * Returns instance of the registry that is already populated with known mappers.
     * @return
     */
    public static QueryMapperRegistry get() {
        return INSTANCE;
    }
    
    /**
     * Returns mapper for given name if found
     * @param name unique name that mapper is bound to
     * @param columnMapping provides column mapping (name to type) that can be 
     * shipped to mapper for improved transformation - can be null (accepted types: string, long, integer, date, double)
     * @return instance of the <code>QueryResultMapper</code> if found
     * @throws IllegalArgumentException in case there is no mapper found with given name
     */
    public QueryResultMapper<?> mapperFor(String name, Map<String, String> columnMapping) {
        if (!knownMappers.containsKey(name)) {
            throw new IllegalArgumentException("No mapper found with name " + name);
        }
        if (columnMapping == null) {
            return knownMappers.get(name);
        } else {
            return knownMappers.get(name).forColumnMapping(columnMapping);
        }
    }
    
    /**
     * Discovers and adds all <code>QueryResultMappers</code> to the known set
     * @param cl class laoder used to discover mappers
     * @return returns list of added (not previously existing) mappers
     */
    @SuppressWarnings("rawtypes")
    public List<String> discoverAndAddMappers(ClassLoader cl) {
        List<String> added = new ArrayList<String>();
        ServiceLoader<QueryResultMapper> availableProviders = ServiceLoader.load(QueryResultMapper.class, cl);
        for (QueryResultMapper<?> mapper : availableProviders) {
            QueryResultMapper<?> existed = knownMappers.putIfAbsent(mapper.getName(), mapper);
            if (existed == null) {
                added.add(mapper.getName());
                logger.debug("Added mapper {} to the registry", mapper.getName());
            } else {
                logger.debug("Mapper {} already existing in the registry", mapper.getName());
            }
        }
        
        return added;
    }
    
    public void addMapper(QueryResultMapper<?> mapper) {
        QueryResultMapper<?> existed = knownMappers.putIfAbsent(mapper.getName(), mapper);
        if (existed == null) {
            logger.debug("Added mapper {} to the registry", mapper.getName());
        } else {
            logger.debug("Mapper {} already existing in the registry", mapper.getName());
        }
    }
    
    public void removeMapper(String mapperName) {
        QueryResultMapper<?> existed = knownMappers.remove(mapperName);
        if (existed != null) {
            logger.debug("Removed mapper {} from the registry", mapperName);
        } else {
            logger.debug("Mapper {} not found in the registry", mapperName);
        }
    }
}
