/*
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

package org.optaplanner.operator.impl.solver.model.messaging;

import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public final class ArtemisQueueDependentResource extends CRUDKubernetesDependentResource<ArtemisQueue, OptaPlannerSolver> {

    public ArtemisQueueDependentResource(MessageAddress messageAddress, KubernetesClient kubernetesClient) {
        super(ArtemisQueue.class);
        this.messageAddress = messageAddress;
        setKubernetesClient(kubernetesClient);
    }

    private final MessageAddress messageAddress;

    @Override
    protected ArtemisQueue desired(OptaPlannerSolver solver, Context<OptaPlannerSolver> context) {
        final String queueName = solver.getMessageAddressName(messageAddress);

        ObjectMeta objectMeta = new ObjectMetaBuilder()
                .withName(queueName)
                .withNamespace(solver.getNamespace())
                .build();

        ArtemisQueueSpec spec = new ArtemisQueueSpec();
        spec.setAddressName(queueName);
        spec.setQueueName(queueName);

        ArtemisQueue artemisQueue = new ArtemisQueue();
        artemisQueue.setMetadata(objectMeta);
        artemisQueue.setSpec(spec);

        return artemisQueue;
    }
}
