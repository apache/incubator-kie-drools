package org.optaplanner.persistence.jpa.api.score.buildin.hardmediumsoftlong;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

@Converter
public class HardMediumSoftLongScoreConverter implements AttributeConverter<HardMediumSoftLongScore, String> {

    @Override
    public String convertToDatabaseColumn(HardMediumSoftLongScore score) {
        if (score == null) {
            return null;
        }

        return score.toString();
    }

    @Override
    public HardMediumSoftLongScore convertToEntityAttribute(String scoreString) {
        if (scoreString == null) {
            return null;
        }

        return HardMediumSoftLongScore.parseScore(scoreString);
    }
}
