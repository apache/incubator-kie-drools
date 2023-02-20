package org.optaplanner.persistence.jsonb.api.score.buildin.hardsoft;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

public class HardSoftScoreJsonbAdapter extends AbstractScoreJsonbAdapter<HardSoftScore> {

    @Override
    public HardSoftScore adaptFromJson(String scoreString) {
        return HardSoftScore.parseScore(scoreString);
    }
}
