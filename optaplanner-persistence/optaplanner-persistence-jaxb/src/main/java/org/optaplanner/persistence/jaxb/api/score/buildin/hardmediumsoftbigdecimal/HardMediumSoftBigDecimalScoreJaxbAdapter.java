package org.optaplanner.persistence.jaxb.api.score.buildin.hardmediumsoftbigdecimal;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftBigDecimalScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardMediumSoftBigDecimalScore> {

    @Override
    public HardMediumSoftBigDecimalScore unmarshal(String scoreString) {
        return HardMediumSoftBigDecimalScore.parseScore(scoreString);
    }

}
