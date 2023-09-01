package org.kie.dmn.openapi.impl;

import java.util.UUID;

import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.openapi.impl.DefaultNamingPolicy;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultNamingPolicyTest {

    @Test
    public void test() {
        DefaultNamingPolicy ut = new DefaultNamingPolicy("#/definitions/");

        assertThat(ut.getName(unregisteredType("tPerson"))).isEqualTo("tPerson");
        assertThat(ut.getName(unregisteredType("my person type"))).isEqualTo("my_32person_32type");
        assertThat(ut.getName(unregisteredType("my_person_type"))).isEqualTo("my__person__type");
        assertThat(ut.getName(unregisteredType("my-person-type"))).isEqualTo("my_45person_45type");
    }

    private static DMNType unregisteredType(String name) {
        return new SimpleTypeImpl("ns1", name, UUID.randomUUID().toString());
    }
}
