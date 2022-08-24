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
