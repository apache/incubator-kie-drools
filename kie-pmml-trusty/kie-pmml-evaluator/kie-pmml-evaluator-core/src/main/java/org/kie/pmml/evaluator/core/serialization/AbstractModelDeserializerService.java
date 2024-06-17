package org.kie.pmml.evaluator.core.serialization;


import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.efesto.common.core.serialization.DeserializerService;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;

public class AbstractModelDeserializerService implements DeserializerService<AbstractModelLocalUriIdPmml> {

    @Override
    public Class<AbstractModelLocalUriIdPmml> type() {
        return AbstractModelLocalUriIdPmml.class;
    }

    @Override
    public JsonDeserializer<? extends AbstractModelLocalUriIdPmml> deser() {
        return new AbstractModelLocalUriIdPmmlDeserializer();
    }
}
