package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoft;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardSoftScoreHibernateType extends AbstractScoreHibernateType {

    public HardSoftScoreHibernateType() {
        scoreDefinition = new HardSoftScoreDefinition();
        type = StandardBasicTypes.INTEGER;
    }

}
