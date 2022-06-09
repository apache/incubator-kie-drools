package org.optaplanner.persistence.jackson.api.score.buildin.simplelong;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class SimpleLongScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<SimpleLongScore> {

    @Override
    public SimpleLongScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return SimpleLongScore.parseScore(parser.getValueAsString());
    }

}
