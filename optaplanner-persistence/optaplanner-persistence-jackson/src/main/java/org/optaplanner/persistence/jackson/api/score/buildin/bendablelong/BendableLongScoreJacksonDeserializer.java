package org.optaplanner.persistence.jackson.api.score.buildin.bendablelong;

import java.io.IOException;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jackson.api.score.AbstractScoreJacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * {@inheritDoc}
 */
public class BendableLongScoreJacksonDeserializer extends AbstractScoreJacksonDeserializer<BendableLongScore> {

    @Override
    public BendableLongScore deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return BendableLongScore.parseScore(parser.getValueAsString());
    }

}
