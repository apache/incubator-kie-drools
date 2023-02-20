package org.optaplanner.persistence.jsonb.api.score.buildin.bendablebigdecimal;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

public class BendableBigDecimalScoreJsonbAdapter extends AbstractScoreJsonbAdapter<BendableBigDecimalScore> {

    @Override
    public BendableBigDecimalScore adaptFromJson(String scoreString) {
        return BendableBigDecimalScore.parseScore(scoreString);
    }
}
