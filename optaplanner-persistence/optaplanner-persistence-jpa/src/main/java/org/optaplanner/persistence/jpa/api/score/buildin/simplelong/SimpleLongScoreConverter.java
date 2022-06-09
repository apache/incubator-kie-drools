package org.optaplanner.persistence.jpa.api.score.buildin.simplelong;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;

@Converter
public class SimpleLongScoreConverter implements AttributeConverter<SimpleLongScore, String> {

    @Override
    public String convertToDatabaseColumn(SimpleLongScore score) {
        if (score == null) {
            return null;
        }

        return score.toString();
    }

    @Override
    public SimpleLongScore convertToEntityAttribute(String scoreString) {
        if (scoreString == null) {
            return null;
        }

        return SimpleLongScore.parseScore(scoreString);
    }
}
