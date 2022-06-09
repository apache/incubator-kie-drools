package org.optaplanner.persistence.jaxb.api.score.buildin.hardsoftlong;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardSoftLongScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardSoftLongScore> {

    @Override
    public HardSoftLongScore unmarshal(String scoreString) {
        return HardSoftLongScore.parseScore(scoreString);
    }

}
