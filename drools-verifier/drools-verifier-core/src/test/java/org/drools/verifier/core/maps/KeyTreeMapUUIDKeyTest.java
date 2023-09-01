package org.drools.verifier.core.maps;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.maps.util.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KeyTreeMapUUIDKeyTest {

    AnalyzerConfiguration configuration;

    @BeforeEach
    public void setUp() throws Exception {
        configuration = new AnalyzerConfigurationMock();
    }

    @Test
    void testNoKey() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            final KeyTreeMap<NoKey> map = new KeyTreeMap<>(UUIDKey.UNIQUE_UUID);

            map.put(new NoKey());
        });
    }

    @Test
    void testTwoKeys() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            final KeyTreeMap<TwoKeys> map = new KeyTreeMap<>(UUIDKey.UNIQUE_UUID);

            map.put(new TwoKeys());
        });
    }

    private class NoKey
            implements HasKeys {

        @Override
        public Key[] keys() {
            return new Key[0];
        }

        @Override
        public UUIDKey getUuidKey() {
            return null;
        }
    }

    private class TwoKeys
            implements HasKeys {

        @Override
        public Key[] keys() {
            return new Key[]{
                    configuration.getUUID(this),
                    configuration.getUUID(this)
            };
        }

        @Override
        public UUIDKey getUuidKey() {
            return null;
        }
    }
}