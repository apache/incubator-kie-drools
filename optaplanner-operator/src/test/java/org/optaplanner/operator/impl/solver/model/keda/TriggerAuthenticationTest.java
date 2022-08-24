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
