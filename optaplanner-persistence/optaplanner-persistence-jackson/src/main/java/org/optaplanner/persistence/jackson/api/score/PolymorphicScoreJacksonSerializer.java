package org.optaplanner.persistence.jackson.api.score;

import java.io.IOException;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jackson.api.score.buildin.hardsoft.HardSoftScoreJacksonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Jackson binding support for a {@link Score} type (but not a subtype).
 * For a {@link Score} subtype field, use {@link HardSoftScoreJacksonSerializer} or similar instead.
 * <p>
 * For example: use
 * {@code @JsonSerialize(using = PolymorphicScoreJacksonSerializer.class) @JsonDeserialize(using = PolymorphicScoreJacksonDeserializer.class)}
 * on a {@code Score score} field which contains a {@link HardSoftScore} instance
 * and it will marshalled to JSON as {@code "score":{"type":"HARD_SOFT",score:"-999hard/-999soft"}}.
 *
 * @see Score
 * @see PolymorphicScoreJacksonDeserializer
 */
public class PolymorphicScoreJacksonSerializer extends JsonSerializer<Score> {

    @Override
    public void serialize(Score score, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        generator.writeStringField(score.getClass().getSimpleName(), score.toString());
        generator.writeEndObject();
    }

}
