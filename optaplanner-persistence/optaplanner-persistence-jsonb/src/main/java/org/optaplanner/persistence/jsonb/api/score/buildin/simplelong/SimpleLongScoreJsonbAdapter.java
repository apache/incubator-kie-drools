package org.optaplanner.persistence.jsonb.api.score.buildin.simplelong;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleLongScoreJsonbAdapter extends AbstractScoreJsonbAdapter<SimpleLongScore> {

    @Override
    public SimpleLongScore adaptFromJson(String scoreString) {
        return SimpleLongScore.parseScore(scoreString);
    }
}
