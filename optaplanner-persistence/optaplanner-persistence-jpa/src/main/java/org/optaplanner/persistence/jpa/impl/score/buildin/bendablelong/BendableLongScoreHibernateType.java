package org.optaplanner.persistence.jpa.impl.score.buildin.bendablelong;

import java.util.Properties;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.ParameterizedType;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class BendableLongScoreHibernateType extends AbstractScoreHibernateType implements ParameterizedType {

    @Override
    public void setParameterValues(Properties parameterMap) {
        int hardLevelsSize = extractIntParameter(parameterMap, "hardLevelsSize");
        int softLevelsSize = extractIntParameter(parameterMap, "softLevelsSize");
        scoreDefinition = new BendableLongScoreDefinition(hardLevelsSize, softLevelsSize);
        type = StandardBasicTypes.LONG;
    }

}
