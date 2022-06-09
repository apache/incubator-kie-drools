package org.optaplanner.persistence.jackson.api.score.buildin.hardsoft;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardSoftScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<HardSoftScore> {

    @Override
    public HardSoftScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardSoftScore.parseScore(parser.getValueAsString());
    }

}
