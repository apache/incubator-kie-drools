package org.optaplanner.persistence.jsonb.api.score.buildin.simple;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jsonb.api.score.AbstractScoreJsonbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleScoreJsonbAdapter extends AbstractScoreJsonbAdapter<SimpleScore> {

    @Override
    public SimpleScore adaptFromJson(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }
}
