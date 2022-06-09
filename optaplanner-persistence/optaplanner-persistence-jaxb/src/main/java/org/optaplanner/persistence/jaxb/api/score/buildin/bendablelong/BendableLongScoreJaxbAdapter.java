package org.optaplanner.persistence.jaxb.api.score.buildin.bendablelong;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class BendableLongScoreJaxbAdapter extends AbstractScoreJaxbAdapter<BendableLongScore> {

    @Override
    public BendableLongScore unmarshal(String scoreString) {
        return BendableLongScore.parseScore(scoreString);
    }

}
