package org.drools.verifier.core.index.select;


import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.KeyTreeMap;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectNoMatchesTest {

    private Select<Address> select;

    @BeforeEach
    public void setUp() throws Exception {
        final KeyDefinition keyDefinition = KeyDefinition.newKeyDefinition().withId("name").build();
        final KeyTreeMap<Address> map = new KeyTreeMap<>(keyDefinition);
        final Address object = new Address();
        map.put(object);

        select = new Select<>(map.get(UUIDKey.UNIQUE_UUID),
                              new ExactMatcher(keyDefinition,
                                               "Toni"));
    }

    @Test
    void testAll() throws Exception {
        assertThat(select.all()).isEmpty();
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isNull();
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isNull();
    }

    private class Address implements HasKeys {

        private UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}