package org.kie.pmml.evaluator.core.serialization;


import com.fasterxml.jackson.databind.JsonSerializer;
import org.kie.efesto.common.core.serialization.SerializerService;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;

public class AbstractSerializerService implements SerializerService<AbstractModelLocalUriIdPmml> {

    @Override
    public Class<AbstractModelLocalUriIdPmml> type() {
        return AbstractModelLocalUriIdPmml.class;
    }

    @Override
    public JsonSerializer<? extends AbstractModelLocalUriIdPmml> ser() {
        return new AbstractModelLocalUriIdPmmlSerializer();
    }
}
