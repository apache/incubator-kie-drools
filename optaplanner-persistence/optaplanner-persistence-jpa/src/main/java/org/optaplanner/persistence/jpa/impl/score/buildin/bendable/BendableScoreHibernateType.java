package org.optaplanner.persistence.jpa.impl.score.buildin.bendable;

import java.util.Properties;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.ParameterizedType;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class BendableScoreHibernateType extends AbstractScoreHibernateType implements ParameterizedType {

    @Override
    public void setParameterValues(Properties parameterMap) {
        int hardLevelsSize = extractIntParameter(parameterMap, "hardLevelsSize");
        int softLevelsSize = extractIntParameter(parameterMap, "softLevelsSize");
        scoreDefinition = new BendableScoreDefinition(hardLevelsSize, softLevelsSize);
        type = StandardBasicTypes.INTEGER;
    }

}
