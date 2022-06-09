package org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardMediumSoftScore> {

    @Override
    public HardMediumSoftScore adaptFromJson(String scoreString) {
        return HardMediumSoftScore.parseScore(scoreString);
    }
}
