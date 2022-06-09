package org.optaplanner.persistence.jaxb.api.score.buildin.simplelong;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class SimpleLongScoreJaxbAdapter extends AbstractScoreJaxbAdapter<SimpleLongScore> {

    @Override
    public SimpleLongScore unmarshal(String scoreString) {
        return SimpleLongScore.parseScore(scoreString);
    }

}
