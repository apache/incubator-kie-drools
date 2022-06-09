package org.optaplanner.persistence.jsonb.api.score.buildin.simplebigdecimal;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleBigDecimalScoreJsonbAdapter extends AbstractScoreJsonbAdapter<SimpleBigDecimalScore> {

    @Override
    public SimpleBigDecimalScore adaptFromJson(String scoreString) {
        return SimpleBigDecimalScore.parseScore(scoreString);
    }
}
