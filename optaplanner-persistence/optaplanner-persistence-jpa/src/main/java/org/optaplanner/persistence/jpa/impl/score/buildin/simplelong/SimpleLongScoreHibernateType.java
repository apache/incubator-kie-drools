package org.optaplanner.persistence.jpa.impl.score.buildin.simplelong;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.SimpleLongScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class SimpleLongScoreHibernateType extends AbstractScoreHibernateType {

    public SimpleLongScoreHibernateType() {
        scoreDefinition = new SimpleLongScoreDefinition();
        type = StandardBasicTypes.LONG;
    }

}
