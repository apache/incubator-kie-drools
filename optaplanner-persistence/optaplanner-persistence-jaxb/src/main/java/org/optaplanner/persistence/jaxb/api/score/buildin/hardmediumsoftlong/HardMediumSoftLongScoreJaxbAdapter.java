package org.optaplanner.persistence.jaxb.api.score.buildin.hardmediumsoftlong;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftLongScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardMediumSoftLongScore> {

    @Override
    public HardMediumSoftLongScore unmarshal(String scoreString) {
        return HardMediumSoftLongScore.parseScore(scoreString);
    }

}
