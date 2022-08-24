package org.optaplanner.operator.impl.solver.model.keda;

import org.optaplanner.operator.impl.solver.model.OptaPlannerSolver;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUKubernetesDependentResource;
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
        extends CRUKubernetesDependentResource<TriggerAuthentication, OptaPlannerSolver> {

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";

    public TriggerAuthenticationDependentResource(KubernetesClient kubernetesClient) {
        super(TriggerAuthentication.class);
        setKubernetesClient(kubernetesClient);
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
