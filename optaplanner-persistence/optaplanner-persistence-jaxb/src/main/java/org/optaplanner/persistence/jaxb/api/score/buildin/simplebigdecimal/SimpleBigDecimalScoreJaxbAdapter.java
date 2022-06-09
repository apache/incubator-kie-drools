package org.optaplanner.persistence.jaxb.api.score.buildin.simplebigdecimal;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleBigDecimalScoreJaxbAdapter extends AbstractScoreJaxbAdapter<SimpleBigDecimalScore> {

    @Override
    public SimpleBigDecimalScore unmarshal(String scoreString) {
        return SimpleBigDecimalScore.parseScore(scoreString);
    }

}
