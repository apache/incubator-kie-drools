package org.kie.efesto.runtimemanager.core.mocks;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.core.serialization.DeserializerService;

public class MockEfestoOutputDeserializerService implements DeserializerService<MockEfestoOutput> {

    @Override
    public Class<MockEfestoOutput> type() {
        return MockEfestoOutput.class;
    }

    @Override
    public JsonDeserializer<? extends MockEfestoOutput> deser() {
        return new MockEfestoOutputDeserializer();
    }
}
