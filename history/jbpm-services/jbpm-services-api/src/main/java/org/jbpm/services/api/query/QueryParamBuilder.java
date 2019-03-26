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

/**
 * QueryParamBuilder is responsible for building up one or more filter parameters.
 * 
 * QueryService (that is primary consumer of this builder) will call the 
 * <code>build</code> method as long as it returns non null value. 
 * That way it allows to build multiple filters from single builder or build complex
 * filters e.g. nested etc with single builder
 *
 * @param <T> type of filter instances
 */
public interface QueryParamBuilder<T> {

    /**
     * Builds single condition per invocation and it is invoked as long as it returns non null value.
     * @return filter condition to be applied on query or null if done building.
     */
    T build();
}
