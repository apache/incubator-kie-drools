package org.optaplanner.persistence.jackson.api.score.buildin.bendablebigdecimal;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class BendableBigDecimalScoreJacksonDeserializer
        extends AbstractScoreJacksonDeserializer<BendableBigDecimalScore> {

    @Override
    public BendableBigDecimalScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return BendableBigDecimalScore.parseScore(parser.getValueAsString());
    }

}
