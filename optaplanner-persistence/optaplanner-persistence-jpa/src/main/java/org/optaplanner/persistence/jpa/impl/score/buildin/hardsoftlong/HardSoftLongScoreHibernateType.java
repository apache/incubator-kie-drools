package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoftlong;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.CompositeUserType;
import org.optaplanner.core.impl.score.buildin.HardSoftLongScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * @deprecated This class has been deprecated as the Hibernate 6 does not provide full backward compatibility
 *             for the {@link CompositeUserType}.
 *             The class will remain available in the OptaPlanner 8 releases to provide
 *             integration with Hibernate 5 but will be removed in OptaPlanner 9.
 *             To integrate the {@link org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore}
 *             with Hibernate 6, either use the score converter
 *             {@link org.optaplanner.persistence.jpa.api.score.buildin.hardsoftlong.HardSoftLongScoreConverter})
 *             or implement the {@link CompositeUserType} yourself.
 */
@Deprecated(forRemoval = true)
public class HardSoftLongScoreHibernateType extends AbstractScoreHibernateType {

    public HardSoftLongScoreHibernateType() {
        scoreDefinition = new HardSoftLongScoreDefinition();
        type = StandardBasicTypes.LONG;
    }

}
