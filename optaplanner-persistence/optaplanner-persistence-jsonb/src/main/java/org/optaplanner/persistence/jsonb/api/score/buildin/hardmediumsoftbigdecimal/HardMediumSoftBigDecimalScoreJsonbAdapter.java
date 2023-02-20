package org.optaplanner.persistence.jsonb.api.score.buildin.hardmediumsoftbigdecimal;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

public class HardMediumSoftBigDecimalScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardMediumSoftBigDecimalScore> {

    @Override
    public HardMediumSoftBigDecimalScore adaptFromJson(String scoreString) {
        return HardMediumSoftBigDecimalScore.parseScore(scoreString);
    }
}
