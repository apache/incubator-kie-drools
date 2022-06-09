package org.optaplanner.persistence.jackson.api.score.buildin.hardmediumsoftbigdecimal;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftBigDecimalScoreJacksonDeserializer
        extends AbstractScoreJacksonDeserializer<HardMediumSoftBigDecimalScore> {

    @Override
    public HardMediumSoftBigDecimalScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardMediumSoftBigDecimalScore.parseScore(parser.getValueAsString());
    }

}
