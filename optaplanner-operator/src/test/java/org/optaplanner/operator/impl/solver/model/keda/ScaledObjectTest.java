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

package org.optaplanner.operator.impl.solver.model.keda;

import org.optaplanner.operator.impl.solver.model.AbstractKubernetesCustomResourceTest;
import org.optaplanner.operator.impl.solver.model.common.ResourceNameReference;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ScaledObjectTest extends AbstractKubernetesCustomResourceTest<ScaledObject> {

    public ScaledObjectTest() {
        super(ScaledObject.class);
    }

    @Override
    protected ScaledObject createCustomResource() {
        TriggerMetadata triggerMetadata = new TriggerMetadata();
        triggerMetadata.setBrokerAddress("test-messaging-address");
        triggerMetadata.setBrokerName("amq-broker");
        triggerMetadata.setQueueLength(String.valueOf(1));
        triggerMetadata.setQueueName("test-messaging-queue");
        triggerMetadata.setManagementEndpoint("test-host:8161");

        Trigger trigger = new Trigger();
        trigger.setAuthenticationRef(new ResourceNameReference("test-trigger-auth"));
        trigger.setMetadata(triggerMetadata);
        trigger.setName("test-keda-trigger");
        trigger.setType("artemis-queue");

        ScaledObjectSpec scaledObjectSpec = new ScaledObjectSpec();
        scaledObjectSpec.setCooldownPeriod(10);
        scaledObjectSpec.setMaxReplicaCount(5);
        scaledObjectSpec.setMinReplicaCount(0);
        scaledObjectSpec.setPollingInterval(10);
        scaledObjectSpec.setScaleTargetRef(new ResourceNameReference("test-deployment"));
        scaledObjectSpec.withTrigger(trigger);

        ObjectMeta scaledObjectMeta = new ObjectMeta();
        scaledObjectMeta.setName("test-scaled-object");
        ScaledObject scaledObject = new ScaledObject();
        scaledObject.setMetadata(scaledObjectMeta);
        scaledObject.setSpec(scaledObjectSpec);

        return scaledObject;
    }
}
