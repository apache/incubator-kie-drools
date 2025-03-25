package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

class EfestoOutputDeserializerTest {

    @Test
    void deserializeTest() throws JsonProcessingException {
        String toDeserialize = "{\"modelLocalUriId\":{\"model\":\"mock\",\"basePath\":\"/org/kie/efesto/runtimemanager/core/mocks/MockEfestoOutput\",\"fullPath\":\"/mock/org/kie/efesto/runtimemanager/core/mocks/MockEfestoOutput\"},\"outputData\":\"MockEfestoOutput\",\"kind\":\"org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput\"}";
        EfestoOutput retrieved = getObjectMapper().readValue(toDeserialize, EfestoOutput.class);
        assertThat(retrieved).isNotNull().isExactlyInstanceOf(MockEfestoOutput.class);
    }
}