package org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftlong;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftLongScoreJacksonDeserializer
        extends AbstractScoreJacksonDeserializer<HardMediumSoftLongScore> {

    @Override
    public HardMediumSoftLongScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardMediumSoftLongScore.parseScore(parser.getValueAsString());
    }

}
