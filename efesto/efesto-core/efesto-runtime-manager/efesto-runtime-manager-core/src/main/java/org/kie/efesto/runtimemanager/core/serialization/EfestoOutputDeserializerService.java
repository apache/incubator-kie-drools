package org.kie.efesto.runtimemanager.core.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.core.serialization.DeserializerService;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

@SuppressWarnings("rawtypes")
public class EfestoOutputDeserializerService implements DeserializerService<EfestoOutput> {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<EfestoOutput> type() {
        return EfestoOutput.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public JsonDeserializer<? extends EfestoOutput> deser() {
        return new EfestoOutputDeserializer();
    }
}
