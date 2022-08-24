package org.optaplanner.operator.impl.solver.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.operator.impl.solver.AbstractKubernetesTest;

import io.fabric8.kubernetes.client.CustomResource;

public abstract class AbstractKubernetesCustomResourceTest<CustomResourceType extends CustomResource<?, ?>>
        extends AbstractKubernetesTest {

    private final Class<CustomResourceType> customResourceClass;

    public AbstractKubernetesCustomResourceTest(Class<CustomResourceType> customResourceClass) {
        this.customResourceClass = customResourceClass;
    }

    @Test
    void createAndVerifyCustomResource() {
        CustomResourceType customResource = createCustomResource();
        CustomResourceType returned = getMockServer().getClient().resources(customResourceClass).create(customResource);
        assertThat(returned).isNotSameAs(customResource);
        assertThat(returned).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(customResource);
    }

    abstract protected CustomResourceType createCustomResource();
}
