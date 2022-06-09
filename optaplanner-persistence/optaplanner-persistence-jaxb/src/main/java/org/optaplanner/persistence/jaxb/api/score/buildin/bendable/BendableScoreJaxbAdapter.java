package org.optaplanner.persistence.jaxb.api.score.buildin.bendable;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class BendableScoreJaxbAdapter extends AbstractScoreJaxbAdapter<BendableScore> {

    @Override
    public BendableScore unmarshal(String scoreString) {
        return BendableScore.parseScore(scoreString);
    }

}
