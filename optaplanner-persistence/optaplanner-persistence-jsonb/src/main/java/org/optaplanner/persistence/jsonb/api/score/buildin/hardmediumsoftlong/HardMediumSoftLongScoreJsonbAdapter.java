package org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoftlong;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftLongScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardMediumSoftLongScore> {

    @Override
    public HardMediumSoftLongScore adaptFromJson(String scoreString) {
        return HardMediumSoftLongScore.parseScore(scoreString);
    }
}
