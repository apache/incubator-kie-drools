package org.optaplanner.persistence.jsonb.api.score.buildin.bendablelong;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class BendableLongScoreJsonbAdapter extends AbstractScoreJsonbAdapter<BendableLongScore> {

    @Override
    public BendableLongScore adaptFromJson(String scoreString) {
        return BendableLongScore.parseScore(scoreString);
    }
}
