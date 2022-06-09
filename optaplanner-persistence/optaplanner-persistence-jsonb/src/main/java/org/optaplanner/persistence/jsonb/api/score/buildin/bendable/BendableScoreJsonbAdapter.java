package org.optaplanner.persistence.jsonb.api.score.buildin.bendable;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class BendableScoreJsonbAdapter extends AbstractScoreJsonbAdapter<BendableScore> {

    @Override
    public BendableScore adaptFromJson(String scoreString) {
        return BendableScore.parseScore(scoreString);
    }
}
