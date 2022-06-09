package org.optaplanner.persistence.jpa.api.score.buildin.bendablelong;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;

@Converter
public class BendableLongScoreConverter implements AttributeConverter<BendableLongScore, String> {

    @Override
    public String convertToDatabaseColumn(BendableLongScore score) {
        if (score == null) {
            return null;
        }

        return score.toString();
    }

    @Override
    public BendableLongScore convertToEntityAttribute(String scoreString) {
        if (scoreString == null) {
            return null;
        }

        return BendableLongScore.parseScore(scoreString);
    }
}
