/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.persistence.jpa.impl.score.buildin.bendable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.ParameterizedType;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class BendableScoreHibernateType extends AbstractScoreHibernateType implements ParameterizedType {

    protected BendableScoreDefinition scoreDefinition;

    @Override
    public void setParameterValues(Properties parameterMap) {
        int hardLevelsSize = extractIntParameter(parameterMap, "hardLevelsSize");
        int softLevelsSize = extractIntParameter(parameterMap, "softLevelsSize");
        scoreDefinition = new BendableScoreDefinition(hardLevelsSize, softLevelsSize);
    }

    protected int extractIntParameter(Properties parameterMap, String parameterName) {
        String valueString = (String) parameterMap.get(parameterName);
        if (valueString == null) {
            throw new IllegalArgumentException("The parameter " + parameterName + " (" + valueString
                    + ") is missing for " + getClass().getSimpleName() + ".");
        }
        try {
            return Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The parameter " + parameterName + " (" + valueString
                    + ") is not a valid integer for " + getClass().getSimpleName() + ".");
        }
    }

    @Override
    public Class returnedClass() {
        return BendableScore.class;
    }

    @Override
    public String[] getPropertyNames() {
        int hardLevelsSize = scoreDefinition.getHardLevelsSize();
        int softLevelsSize = scoreDefinition.getSoftLevelsSize();
        String[] propertyNames = new String[hardLevelsSize + softLevelsSize];
        for (int i = 0; i < propertyNames.length; i++) {
            String labelPrefix;
            if (i < hardLevelsSize) {
                labelPrefix = "hard" + i;
            } else {
                labelPrefix = "soft" + (i - hardLevelsSize);
            }
            propertyNames[i] = labelPrefix + "Score";
        }
        return propertyNames;
    }

    @Override
    public Type[] getPropertyTypes() {
        Type[] propertyTypes = new Type[scoreDefinition.getLevelsSize()];
        for (int i = 0; i < propertyTypes.length; i++) {
            propertyTypes[i] = IntegerType.INSTANCE;
        }
        return propertyTypes;
    }

    @Override
    public Object getPropertyValue(Object o, int propertyIndex) {
        if (o == null) {
            return null;
        }
        BendableScore score = (BendableScore) o;
        if (propertyIndex >= scoreDefinition.getLevelsSize()) {
            throw new IllegalArgumentException("The propertyIndex (" + propertyIndex
                    + ") must be lower than the levelsSize for score (" + score + ").");
        }
        return score.getHardOrSoftScore(propertyIndex);
    }

    @Override
    public BendableScore nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        if (resultSet == null) {
            return null;
        }
        int levelsSize = scoreDefinition.getLevelsSize();
        int[] scoreWeights = new int[levelsSize];
        int nullCount = 0;
        for (int i = 0; i < levelsSize; i++) {
            Integer scoreWeight = (Integer) StandardBasicTypes.INTEGER.nullSafeGet(resultSet, names[i], session, owner);
            if (scoreWeight == null) {
                nullCount++;
            } else {
                scoreWeights[i] = scoreWeight;
            }
        }
        if (nullCount == levelsSize) {
            return null;
        } else if (nullCount != 0) {
            throw new IllegalStateException("The nullCount (" + nullCount
                    + ") must be 0 or levelsSize (" + levelsSize + ").");
        }
        int hardLevelsSize = scoreDefinition.getHardLevelsSize();
        return BendableScore.valueOf(Arrays.copyOfRange(scoreWeights, 0, hardLevelsSize),
                Arrays.copyOfRange(scoreWeights, hardLevelsSize, levelsSize));
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int parameterIndex, SessionImplementor session)
            throws SQLException {
        int levelsSize = scoreDefinition.getLevelsSize();
        if (value == null) {
            for (int i = 0; i < levelsSize; i++) {
                statement.setNull(parameterIndex + i, StandardBasicTypes.INTEGER.sqlType());
            }
            return;
        }
        BendableScore score = (BendableScore) value;
        for (int i = 0; i < levelsSize; i++) {
            statement.setInt(parameterIndex + i, score.getHardOrSoftScore(i));
        }
    }

}
