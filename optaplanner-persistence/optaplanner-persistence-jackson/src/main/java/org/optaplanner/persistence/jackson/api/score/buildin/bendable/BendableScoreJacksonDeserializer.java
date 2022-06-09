package org.optaplanner.persistence.jackson.api.score.buildin.bendable;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class BendableScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<BendableScore> {

    @Override
    public BendableScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return BendableScore.parseScore(parser.getValueAsString());
    }

}
