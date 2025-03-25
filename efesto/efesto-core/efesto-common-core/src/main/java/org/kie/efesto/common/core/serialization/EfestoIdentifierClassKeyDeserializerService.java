package org.kie.efesto.common.core.serialization;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.api.cache.EfestoIdentifierClassKey;

public class EfestoIdentifierClassKeyDeserializerService implements DeserializerService<EfestoIdentifierClassKey> {

    @Override
    public Class<EfestoIdentifierClassKey> type() {
        return EfestoIdentifierClassKey.class;
    }

    @Override
    public JsonDeserializer<? extends EfestoIdentifierClassKey> deser() {
        return new EfestoIdentifierClassKeyDeserializer();
    }
}
