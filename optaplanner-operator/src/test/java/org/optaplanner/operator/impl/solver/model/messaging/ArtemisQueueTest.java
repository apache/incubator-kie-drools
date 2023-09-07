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

import org.optaplanner.operator.impl.solver.model.AbstractKubernetesCustomResourceTest;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ArtemisQueueTest extends AbstractKubernetesCustomResourceTest<ArtemisQueue> {

    public ArtemisQueueTest() {
        super(ArtemisQueue.class);
    }

    @Override
    protected ArtemisQueue createCustomResource() {
        ArtemisQueueSpec artemisQueueSpec = new ArtemisQueueSpec();
        artemisQueueSpec.setQueueName("test-queue");
        artemisQueueSpec.setAddressName("test-queue-address");

        ObjectMeta metadata = new ObjectMeta();
        metadata.setName("test-artemis-queue");

        ArtemisQueue artemisQueue = new ArtemisQueue();
        artemisQueue.setMetadata(metadata);
        artemisQueue.setSpec(artemisQueueSpec);

        return artemisQueue;
    }
}
