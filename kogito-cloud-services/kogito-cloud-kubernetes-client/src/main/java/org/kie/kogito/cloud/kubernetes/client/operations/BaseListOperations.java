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

import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;

public abstract class BaseListOperations extends BaseOperations implements ListOperations {

    public BaseListOperations(KogitoKubeConfig clientConfig) {
        super(clientConfig);
    }

    @Override
    public OperationsResponseParser listNamespaced(String namespace, Map<String, String> labels) {
        return this.execute(namespace, labels);
    }

    @Override
    public OperationsResponseParser list(Map<String, String> labels) {
        return this.listNamespaced(null, labels);
    }

}
