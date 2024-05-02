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

import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

/*
Example YAML:
-----------------------------------------
apiVersion: keda.sh/v1alpha1
kind: TriggerAuthentication
metadata:
  name: school-timetabling-scaledobject-auth
  namespace: artemis
spec:
  secretTargetRef:
    - parameter: username
      name: activemq-secret
      key: activemq-username
    - parameter: password
      name: activemq-secret
      key: activemq-password
*/

@KubernetesDependent
public final class TriggerAuthenticationDependentResource
        extends CRUDKubernetesDependentResource<TriggerAuthentication, OptaPlannerSolver> {

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";

    public TriggerAuthenticationDependentResource() {
        super(TriggerAuthentication.class);
    }

    @Override
    protected TriggerAuthentication desired(OptaPlannerSolver optaPlannerSolver, Context<OptaPlannerSolver> context) {
        final SecretKeySelector amqUsernameSecretKeySelector =
                optaPlannerSolver.getSpec().getAmqBroker().getUsernameSecretRef();
        final SecretKeySelector amqPasswordSecretKeySelector =
                optaPlannerSolver.getSpec().getAmqBroker().getPasswordSecretRef();
        TriggerAuthenticationSpec spec = new TriggerAuthenticationSpec()
                .withSecretTargetRef(SecretTargetRef.fromSecretKeySelector(PARAM_USERNAME, amqUsernameSecretKeySelector))
                .withSecretTargetRef(SecretTargetRef.fromSecretKeySelector(PARAM_PASSWORD, amqPasswordSecretKeySelector));

        ObjectMeta metadata = new ObjectMetaBuilder()
                .withName(optaPlannerSolver.getTriggerAuthenticationName())
                .withNamespace(optaPlannerSolver.getNamespace())
                .build();
        TriggerAuthentication triggerAuthentication = new TriggerAuthentication();
        triggerAuthentication.setMetadata(metadata);
        triggerAuthentication.setSpec(spec);

        return triggerAuthentication;
    }
}
