package org.optaplanner.persistence.jackson.api.score.buildin.simple;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class SimpleScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<SimpleScore> {

    @Override
    public SimpleScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return SimpleScore.parseScore(parser.getValueAsString());
    }

}
