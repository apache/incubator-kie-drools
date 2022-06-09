package org.optaplanner.persistence.jaxb.api.score.buildin.hardsoftbigdecimal;

import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardSoftBigDecimalScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardSoftBigDecimalScore> {

    @Override
    public HardSoftBigDecimalScore unmarshal(String scoreString) {
        return HardSoftBigDecimalScore.parseScore(scoreString);
    }

}
