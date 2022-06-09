package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftbigdecimal;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardSoftBigDecimalScoreHibernateType extends AbstractScoreHibernateType {

    public HardSoftBigDecimalScoreHibernateType() {
        scoreDefinition = new HardSoftBigDecimalScoreDefinition();
        type = StandardBasicTypes.BIG_DECIMAL;
    }

}
