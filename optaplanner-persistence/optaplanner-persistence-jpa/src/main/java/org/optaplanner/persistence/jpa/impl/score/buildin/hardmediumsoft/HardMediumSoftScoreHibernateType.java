package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoft;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftScoreHibernateType extends AbstractScoreHibernateType {

    public HardMediumSoftScoreHibernateType() {
        scoreDefinition = new HardMediumSoftScoreDefinition();
        type = StandardBasicTypes.INTEGER;
    }

}
