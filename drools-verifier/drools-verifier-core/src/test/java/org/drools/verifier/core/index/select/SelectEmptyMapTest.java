package org.drools.verifier.core.index.select;

import java.util.Collection;

import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectEmptyMapTest {

    private Select<Address> select;

    @BeforeEach
    public void setUp() throws Exception {
        select = new Select<>(MultiMapFactory.make(),
                              new ExactMatcher(KeyDefinition.newKeyDefinition().withId("name").build(),
                                               "Toni"));
    }

    @Test
    void testAll() throws Exception {
        final Collection<Address> all = select.all();

        assertThat(all).isEmpty();
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isNull();
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isNull();
    }

    private class Address {
    }
}