package org.optaplanner.persistence.jsonb.api.score.buildin.hardsoftlong;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class HardSoftLongScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardSoftLongScore> {

    @Override
    public HardSoftLongScore adaptFromJson(String scoreString) {
        return HardSoftLongScore.parseScore(scoreString);
    }
}
