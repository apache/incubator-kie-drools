package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoftlong;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftLongScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftLongScoreHibernateType extends AbstractScoreHibernateType {

    public HardMediumSoftLongScoreHibernateType() {
        scoreDefinition = new HardMediumSoftLongScoreDefinition();
        type = StandardBasicTypes.LONG;
    }

}
