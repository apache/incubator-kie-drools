/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.util.Map;

/**
 * Operations that return a list of objects. Normally a <code>kind: List</code> with <code>items</code> attribute.
 */
public interface ListOperations extends Operations {

    /**
     * Query for a list of services within a namespace.
     * 
     * @param namespace
     * @param labels
     * @return A JSON Document reference of the Service API response
     */
    OperationsResponseParser listNamespaced(final String namespace, final Map<String, String> labels);

    /**
     * Queries for a list of services in the entire cluster. A service account with permissions to query the cluster might be needed. 
     * 
     * @param labels
     * @return A JSON Document reference of the Service API response
     */
    OperationsResponseParser list(final Map<String, String> labels);

}
