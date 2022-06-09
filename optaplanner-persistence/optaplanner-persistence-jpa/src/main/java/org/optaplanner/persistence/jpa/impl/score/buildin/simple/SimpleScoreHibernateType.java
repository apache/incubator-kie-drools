package org.optaplanner.persistence.jpa.impl.score.buildin.simple;

import org.hibernate.type.StandardBasicTypes;
import org.optaplanner.core.impl.score.buildin.SimpleScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class SimpleScoreHibernateType extends AbstractScoreHibernateType {

    public SimpleScoreHibernateType() {
        scoreDefinition = new SimpleScoreDefinition();
        type = StandardBasicTypes.INTEGER;
    }

}
