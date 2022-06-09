package org.optaplanner.persistence.jpa.api.score.buildin.hardsoftlong;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@Converter
public class HardSoftLongScoreConverter implements AttributeConverter<HardSoftLongScore, String> {

    @Override
    public String convertToDatabaseColumn(HardSoftLongScore score) {
        if (score == null) {
            return null;
        }

        return score.toString();
    }

    @Override
    public HardSoftLongScore convertToEntityAttribute(String scoreString) {
        if (scoreString == null) {
            return null;
        }

        return HardSoftLongScore.parseScore(scoreString);
    }
}
