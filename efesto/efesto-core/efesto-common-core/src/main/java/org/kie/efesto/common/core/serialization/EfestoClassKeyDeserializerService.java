package org.kie.efesto.common.core.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.api.cache.EfestoClassKey;

public class EfestoClassKeyDeserializerService implements DeserializerService<EfestoClassKey> {

    @Override
    public Class<EfestoClassKey> type() {
        return EfestoClassKey.class;
    }

    @Override
    public JsonDeserializer<? extends EfestoClassKey> deser() {
        return new EfestoClassKeyDeserializer();
    }
}
