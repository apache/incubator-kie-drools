package org.optaplanner.persistence.jpa.impl.score.buildin.simplebigdecimal;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.SimpleBigDecimalScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class SimpleBigDecimalScoreHibernateType extends AbstractScoreHibernateType {

    public SimpleBigDecimalScoreHibernateType() {
        scoreDefinition = new SimpleBigDecimalScoreDefinition();
        type = StandardBasicTypes.BIG_DECIMAL;
    }

}
