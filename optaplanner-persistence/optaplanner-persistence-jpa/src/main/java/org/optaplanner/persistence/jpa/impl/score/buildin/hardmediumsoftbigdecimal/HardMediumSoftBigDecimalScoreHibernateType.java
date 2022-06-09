package org.optaplanner.persistence.jpa.impl.score.buildin.hardmediumsoftbigdecimal;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.HardMediumSoftBigDecimalScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardMediumSoftBigDecimalScoreHibernateType extends AbstractScoreHibernateType {

    public HardMediumSoftBigDecimalScoreHibernateType() {
        scoreDefinition = new HardMediumSoftBigDecimalScoreDefinition();
        type = StandardBasicTypes.BIG_DECIMAL;
    }

}
