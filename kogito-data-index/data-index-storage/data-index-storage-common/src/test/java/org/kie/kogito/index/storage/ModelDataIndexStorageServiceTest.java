package org.kie.kogito.index.storage;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessDefinitionKey;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelDataIndexStorageServiceTest {

    @Test
    void testIdAndVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", "1_0"));
    }

    @Test
    void testIdEmptyVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", ""));
    }

    @Test
    void testIdNullVersion() {
        assertKeyConversion(new ProcessDefinitionKey("Javierito", null));
    }

    private void assertKeyConversion(ProcessDefinitionKey key) {
        Set<ProcessDefinitionKey> set = new HashSet<>();
        set.add(key);
        ProcessDefinitionKey deserializedKey = ModelProcessDefinitionStorage.fromString(ModelProcessDefinitionStorage.toString(key));
        set.add(deserializedKey);
        assertThat(deserializedKey).isEqualTo(key);
        assertThat(set).hasSize(1);
    }
}
