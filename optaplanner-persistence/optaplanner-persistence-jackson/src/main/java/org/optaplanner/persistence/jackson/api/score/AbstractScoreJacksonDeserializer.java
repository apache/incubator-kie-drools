package org.optaplanner.persistence.jackson.api.score;

import org.optaplanner.core.api.score.Score;

import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Jackson binding support for a {@link Score} type.
 * <p>
 * For example: use
 * {@code @JsonSerialize(using = HardSoftScoreScoreJacksonSerializer.class) @JsonDeserialize(using = HardSoftScoreJacksonDeserializer.class)}
 * on a {@code HardSoftScore score} field and it will marshalled to JSON as {@code "score":"-999hard/-999soft"}.
 *
 * @see Score
 * @param <Score_> the actual score type
 */
public abstract class AbstractScoreJacksonDeserializer<Score_ extends Score<Score_>>
        extends JsonDeserializer<Score_> {

}
