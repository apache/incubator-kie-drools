/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.internal.utils;

/**
 * Provides support for lazy load of content of given data object
 * e.g. process variable or case file data
 *
 * @param <T> type of service that is responsible for loading content
 */
public interface LazyLoaded<T> {

    /**
     * Should be set after object construction (usually in marshaling strategies) so whenever is needed 
     * content can be loaded via this service
     * @param service service implementation capable of loading the content
     */
    void setLoadService(T service);
    
    /**
     * Loads the actual content based on other attribute of the instance
     * using load service if given.
     */
    void load();
}
