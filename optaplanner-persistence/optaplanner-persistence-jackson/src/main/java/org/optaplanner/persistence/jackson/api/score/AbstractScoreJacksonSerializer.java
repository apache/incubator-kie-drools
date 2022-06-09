package org.optaplanner.persistence.jackson.api.score;

import java.io.IOException;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Jackson binding support for a {@link Score} subtype.
 * For a {@link Score} field, use {@link PolymorphicScoreJacksonSerializer} instead,
 * so the score type is recorded too and it can be deserialized.
 * <p>
 * For example: use
 * {@code @JsonSerialize(using = HardSoftScoreJacksonSerializer.class) @JsonDeserialize(using = HardSoftScoreJacksonDeserializer.class)}
 * on a {@code HardSoftScore score} field and it will marshalled to JSON as {@code "score":"-999hard/-999soft"}.
 * Or better yet, use {@link OptaPlannerJacksonModule} instead.
 *
 * @see Score
 * @param <Score_> the actual score type
 */
public abstract class AbstractScoreJacksonSerializer<Score_ extends Score<Score_>> extends JsonSerializer<Score_>
        implements ContextualSerializer {

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property)
            throws JsonMappingException {
        JavaType propertyType = property.getType();
        if (Score.class.equals(propertyType.getRawClass())) {
            // If the property type is Score (not HardSoftScore for example),
            // delegate to PolymorphicScoreJacksonSerializer instead to write the score type too
            // This presumes that OptaPlannerJacksonModule is registered
            return provider.findValueSerializer(propertyType);
        }
        return this;
    }

    @Override
    public void serialize(Score_ score, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeString(score.toString());
    }

}
