package org.kie.efesto.common.core.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class ModelLocalUriIdDeserializerService implements DeserializerService<ModelLocalUriId> {

    @Override
    public Class<ModelLocalUriId> type() {
        return ModelLocalUriId.class;
    }

    @Override
    public JsonDeserializer<? extends ModelLocalUriId> deser() {
        return new ModelLocalUriIdDeserializer();
    }
}
