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

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TriggerAuthenticationTest extends AbstractKubernetesCustomResourceTest<TriggerAuthentication> {

    public TriggerAuthenticationTest() {
        super(TriggerAuthentication.class);
    }

    @Override
    protected TriggerAuthentication createCustomResource() {
        TriggerAuthenticationSpec triggerAuthenticationSpec = new TriggerAuthenticationSpec();
        triggerAuthenticationSpec
                .withSecretTargetRef(new SecretTargetRef("username", "activemq-secret", "activemq-username"))
                .withSecretTargetRef(new SecretTargetRef("password", "activemq-secret", "activemq-password"));

        TriggerAuthentication triggerAuthentication = new TriggerAuthentication();
        ObjectMeta triggerAuthenticationMetadata = new ObjectMeta();
        triggerAuthenticationMetadata.setName("test-trigger-auth");
        triggerAuthentication.setMetadata(triggerAuthenticationMetadata);
        triggerAuthentication.setSpec(triggerAuthenticationSpec);

        return triggerAuthentication;
    }
}
