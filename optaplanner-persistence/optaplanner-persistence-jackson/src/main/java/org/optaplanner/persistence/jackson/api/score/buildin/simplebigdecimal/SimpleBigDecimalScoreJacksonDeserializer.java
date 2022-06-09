package org.optaplanner.persistence.jackson.api.score.buildin.simplebigdecimal;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class SimpleBigDecimalScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<SimpleBigDecimalScore> {

    @Override
    public SimpleBigDecimalScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return SimpleBigDecimalScore.parseScore(parser.getValueAsString());
    }

}
