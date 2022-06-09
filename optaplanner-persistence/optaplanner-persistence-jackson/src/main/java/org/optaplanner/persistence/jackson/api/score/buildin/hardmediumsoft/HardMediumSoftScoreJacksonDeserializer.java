package org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoft;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<HardMediumSoftScore> {

    @Override
    public HardMediumSoftScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardMediumSoftScore.parseScore(parser.getValueAsString());
    }

}
