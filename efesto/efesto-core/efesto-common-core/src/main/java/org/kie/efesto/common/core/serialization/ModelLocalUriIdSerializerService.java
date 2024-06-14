package org.kie.efesto.common.core.serialization;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class ModelLocalUriIdSerializerService implements SerializerService<ModelLocalUriId> {

    @Override
    public Class<ModelLocalUriId> type() {
        return ModelLocalUriId.class;
    }

    @Override
    public JsonSerializer<? extends ModelLocalUriId> ser() {
        return new ModelLocalUriIdSerializer();
    }
}
