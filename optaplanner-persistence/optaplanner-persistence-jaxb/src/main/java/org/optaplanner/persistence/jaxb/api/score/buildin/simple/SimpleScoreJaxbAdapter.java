package org.optaplanner.persistence.jaxb.api.score.buildin.simple;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleScoreJaxbAdapter extends AbstractScoreJaxbAdapter<SimpleScore> {

    @Override
    public SimpleScore unmarshal(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }

}
