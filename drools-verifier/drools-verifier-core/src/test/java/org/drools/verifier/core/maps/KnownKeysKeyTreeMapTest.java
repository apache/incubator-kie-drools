package org.drools.verifier.core.maps;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KnownKeysKeyTreeMapTest {

    private KeyTreeMap<Person> map;

    @BeforeEach
    public void setUp() throws Exception {
        map = new KeyTreeMap<>(KeyDefinition.newKeyDefinition().withId("age").build());
    }

    @Test
    void testExisting() throws Exception {
        assertThat(map.get(KeyDefinition.newKeyDefinition().withId("age").build())).isNotNull();
    }

    @Test
    void testUnknown() throws Exception {
        assertThat(map.get(KeyDefinition.newKeyDefinition().withId("unknown").build())).isNull();
    }

    class Person implements HasKeys {

        private UUIDKey uuidKey = new AnalyzerConfigurationMock().getUUID(this);

        @Override
        public Key[] keys() {
            return new Key[]{
                    uuidKey,
                    new Key(KeyDefinition.newKeyDefinition().withId("name").build(),
                            "hello")
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return uuidKey;
        }
    }
}