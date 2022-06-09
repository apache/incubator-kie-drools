package org.optaplanner.persistence.jaxb.api.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.persistence.jaxb.api.score.AbstractScoreJaxbAdapter;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftScoreJaxbAdapter extends AbstractScoreJaxbAdapter<HardMediumSoftScore> {

    @Override
    public HardMediumSoftScore unmarshal(String scoreString) {
        return HardMediumSoftScore.parseScore(scoreString);
    }

}
