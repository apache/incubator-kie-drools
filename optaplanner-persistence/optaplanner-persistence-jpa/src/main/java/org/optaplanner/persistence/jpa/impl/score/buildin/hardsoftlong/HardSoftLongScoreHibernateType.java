package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftlong;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardSoftLongScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardSoftLongScoreHibernateType extends AbstractScoreHibernateType {

    public HardSoftLongScoreHibernateType() {
        scoreDefinition = new HardSoftLongScoreDefinition();
        type = StandardBasicTypes.LONG;
    }

}
