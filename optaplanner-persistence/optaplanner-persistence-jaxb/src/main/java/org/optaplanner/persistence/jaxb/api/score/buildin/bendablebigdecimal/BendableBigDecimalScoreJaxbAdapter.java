package org.optaplanner.persistence.jaxb.api.score.buildin.bendablebigdecimal;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class BendableBigDecimalScoreJaxbAdapter extends AbstractScoreJaxbAdapter<BendableBigDecimalScore> {

    @Override
    public BendableBigDecimalScore unmarshal(String scoreString) {
        return BendableBigDecimalScore.parseScore(scoreString);
    }

}
