package org.optaplanner.persistence.jaxb.api.score.buildin.hardsoft;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardSoftScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardSoftScore> {

    @Override
    public HardSoftScore unmarshal(String scoreString) {
        return HardSoftScore.parseScore(scoreString);
    }

}
