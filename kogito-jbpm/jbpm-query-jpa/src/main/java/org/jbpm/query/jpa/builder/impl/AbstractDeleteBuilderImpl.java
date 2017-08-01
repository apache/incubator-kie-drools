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

package org.jbpm.query.jpa.builder.impl;


/**
 * This is the abstract parent class for all delete (query) builder implementations.
 *
 * @param <T> The type of the interface that the delete builder implements. This is used for the fluent API.
 */
public abstract class AbstractDeleteBuilderImpl<T> extends AbstractQueryBuilderImpl<T> {

    /**
     * This operation is *NOT* supported on delete queries, 
     * because String based query building would be way too much work,
     * especially in comparison to the JPA Criteria infrastructure we have for normal queries.
     */
    @Override
    public T newGroup() {
        return unsupported();
    }

    /**
     * This operation is *NOT* supported on delete queries, 
     * because String based query building would be way too much work,
     * especially in comparison to the JPA Criteria infrastructure we have for normal queries.
     */
    @Override
    public T endGroup() {
        return unsupported();
    } 

    static <T> T unsupported() { 
        String methodName = (new Throwable()).getStackTrace()[1].getMethodName();
        // in jBPM 7.x, this will be available, once we move to JPA 2.1
        throw new UnsupportedOperationException(methodName + " is not supported on for delete queries!");
    }
}
