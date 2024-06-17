package org.kie.pmml.evaluator.core.serialization;


import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kie.pmml.api.dto.PMMLRequestData;
import org.kie.efesto.common.core.serialization.DeserializerService;

public class PMMLRequestDataDeserializerService implements DeserializerService<PMMLRequestData> {

    @Override
    public Class<PMMLRequestData> type() {
        return PMMLRequestData.class;
    }

    @Override
    public JsonDeserializer<? extends PMMLRequestData> deser() {
        return new PMMLRequestDataDeserializer();
    }
}
