/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jpa.impl.score;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * This class is Hibernate specific, because JPA 2.1's @Converter currently
 * cannot handle 1 class mapping to multiple SQL columns.
 */
public abstract class AbstractScoreHibernateType implements CompositeUserType {

    protected ScoreDefinition scoreDefinition;
    protected AbstractSingleColumnStandardBasicType type;

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
        return scoreDefinition.getScoreClass();
    }

    @Override
    public String[] getPropertyNames() {
        int levelsSize = scoreDefinition.getLevelsSize();
        String[] propertyNames = new String[levelsSize + 1];
        propertyNames[0] = scoreDefinition.getInitLabel();
        String[] levelLabels = scoreDefinition.getLevelLabels();
        for (int i = 0; i < levelsSize; i++) {
            String propertyName = levelLabels[i].replaceAll("\\s+s", "S").replaceAll("\\s+(\\d)", "$1");
            if (!propertyName.matches("[\\w\\d]+")) {
                throw new IllegalStateException("The levelLabel (" + levelLabels[i] + ") is not yet supported.");
            }
            propertyNames[i + 1] = propertyName;
        }
        return propertyNames;
    }

    @Override
    public Type[] getPropertyTypes() {
        int levelsSize = scoreDefinition.getLevelsSize();
        Type[] propertyTypes = new Type[levelsSize + 1];
        propertyTypes[0] = StandardBasicTypes.INTEGER;
        for (int i = 0; i < levelsSize; i++) {
            propertyTypes[i + 1] = type;
        }
        return propertyTypes;
    }

    @Override
    public Object getPropertyValue(Object o, int propertyIndex) {
        if (o == null) {
            return null;
        }
        Score score = (Score) o;
        if (propertyIndex == 0) {
            return score.getInitScore();
        }
        if (propertyIndex >= scoreDefinition.getLevelsSize() + 1) {
            throw new IllegalArgumentException("The propertyIndex (" + propertyIndex
                    + ") must be lower than the levelsSize for score (" + score + ").");
        }
        Number[] levelNumbers = score.toLevelNumbers();
        return levelNumbers[propertyIndex - 1];
    }

    @Override
    public Score nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        if (resultSet == null) {
            return null;
        }
        int nullCount = 0;
        Integer initScore = (Integer) StandardBasicTypes.INTEGER.nullSafeGet(resultSet, names[0], session, owner);
        if (initScore == null) {
            nullCount++;
        }
        int levelsSize = scoreDefinition.getLevelsSize();
        Number[] levelNumbers = new Number[levelsSize];
        for (int i = 0; i < levelsSize; i++) {
            Number levelNumber = (Number) type.nullSafeGet(resultSet, names[i + 1], session, owner);
            if (levelNumber == null) {
                nullCount++;
            } else {
                levelNumbers[i] = levelNumber;
            }
        }
        if (nullCount == levelsSize + 1) {
            return null;
        } else if (nullCount != 0) {
            throw new IllegalStateException("The nullCount (" + nullCount
                    + ") must be 0 or levelsSize (" + levelsSize + ") for " + getClass().getSimpleName() + ".");
        }
        Score score = scoreDefinition.fromLevelNumbers(initScore, levelNumbers);
        if (score == null) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ") must not build a score (" + null + ") that is null.");
        }
        return score;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int parameterIndex, SharedSessionContractImplementor session)
            throws SQLException {
        int levelsSize = scoreDefinition.getLevelsSize();
        if (value == null) {
            statement.setNull(parameterIndex, StandardBasicTypes.INTEGER.sqlType()); // initScore
            for (int i = 0; i < levelsSize; i++) {
                statement.setNull(parameterIndex + i + 1, type.sqlType());
            }
            return;
        }
        Score score = (Score) value;
        statement.setInt(parameterIndex, score.getInitScore());
        Number[] levelNumbers = score.toLevelNumbers();
        if (levelNumbers.length != levelsSize) {
            throw new IllegalStateException("The levelNumbers length (" + levelNumbers.length + ") for score (" + score
                    + ") must be equal to the levelsSize (" + levelsSize + ") for " + getClass().getSimpleName() + ".");
        }
        for (int i = 0; i < levelsSize; i++) {
            if (type == IntegerType.INSTANCE) {
                statement.setInt(parameterIndex + i + 1, (Integer) levelNumbers[i]);
            } else if (type == LongType.INSTANCE) {
                statement.setLong(parameterIndex + i + 1, (Long) levelNumbers[i]);
            } else if (type == DoubleType.INSTANCE) {
                statement.setDouble(parameterIndex + i + 1, (Double) levelNumbers[i]);
            } else if (type == BigDecimalType.INSTANCE) {
                statement.setBigDecimal(parameterIndex + i + 1, (BigDecimal) levelNumbers[i]);
            } else {
                throw new IllegalStateException("The type (" + type + ") is not yet supported.");
            }
        }
    }

    // ************************************************************************
    // Mutable related methods
    // ************************************************************************

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object deepCopy(Object value) {
        return value; // Score is immutable
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) {
        return original; // Score is immutable
    }

    @Override
    public void setPropertyValue(Object component, int property, Object value) {
        throw new UnsupportedOperationException("A Score is immutable.");
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    @Override
    public boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    @Override
    public int hashCode(Object o) {
        if (o == null) {
            return 0;
        }
        return o.hashCode();
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) {
        return cached;
    }

}
