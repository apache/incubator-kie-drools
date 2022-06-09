package org.optaplanner.persistence.jackson.api.score.buildin.hardsoftbigdecimal;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class HardSoftBigDecimalScoreJacksonDeserializer
        extends AbstractScoreJacksonDeserializer<HardSoftBigDecimalScore> {

    @Override
    public HardSoftBigDecimalScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return HardSoftBigDecimalScore.parseScore(parser.getValueAsString());
    }

}
