package org.optaplanner.persistence.jsonb.api.score.buildin.hardsoftbigdecimal;

import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class HardSoftBigDecimalScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardSoftBigDecimalScore> {

    @Override
    public HardSoftBigDecimalScore adaptFromJson(String scoreString) {
        return HardSoftBigDecimalScore.parseScore(scoreString);
    }
}
