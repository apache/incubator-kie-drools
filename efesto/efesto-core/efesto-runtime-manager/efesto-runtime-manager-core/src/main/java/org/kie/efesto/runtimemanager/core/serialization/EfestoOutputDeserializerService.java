package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.core.serialization.DeserializerService;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

public class EfestoOutputDeserializerService implements DeserializerService<EfestoOutput> {

    @Override
    public Class<EfestoOutput> type() {
        return EfestoOutput.class;
    }

    @Override
    public JsonDeserializer<? extends EfestoOutput> deser() {
        return new EfestoOutputDeserializer();
    }
}
