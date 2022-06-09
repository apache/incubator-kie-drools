package org.optaplanner.persistence.jackson.api.score.buildin.hardsoftlong;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardSoftLongScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<HardSoftLongScore> {

    @Override
    public HardSoftLongScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardSoftLongScore.parseScore(parser.getValueAsString());
    }

}
