package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.kie.efesto.common.core.serialization.SerializerService;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

public class EfestoOutputSerializerService implements SerializerService<EfestoOutput> {

    @Override
    public Class<EfestoOutput> type() {
        return EfestoOutput.class;
    }

    @Override
    public JsonSerializer<? extends EfestoOutput> ser() {
        return new EfestoOutputSerializer();
    }
}
