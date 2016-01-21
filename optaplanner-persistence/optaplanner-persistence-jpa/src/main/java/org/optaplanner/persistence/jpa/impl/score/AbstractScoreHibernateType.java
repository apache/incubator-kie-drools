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

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
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
        return scoreDefinition.getClass();
    }

    @Override
    public String[] getPropertyNames() {
        int levelsSize = scoreDefinition.getLevelsSize();
        String[] levelLabels = scoreDefinition.getLevelLabels();
        String[] propertyNames = new String[levelsSize];
        for (int i = 0; i < levelsSize; i++) {
            String propertyName = levelLabels[i].replaceAll("\\s+s", "S").replaceAll("\\s+(\\d)", "$1");
            if (!propertyName.matches("[\\w\\d]+")) {
                throw new IllegalStateException("The levelLabel (" + levelLabels[i] + ") is not yet supported.");
            }
            propertyNames[i] = propertyName;
        }
        return propertyNames;
    }

    @Override
    public Type[] getPropertyTypes() {
        int levelsSize = scoreDefinition.getLevelsSize();
        Type[] propertyTypes = new Type[levelsSize];
        for (int i = 0; i < levelsSize; i++) {
            propertyTypes[i] = type;
        }
        return propertyTypes;
    }

    @Override
    public Object getPropertyValue(Object o, int propertyIndex) {
        if (o == null) {
            return null;
        }
        Score score = (Score) o;
        Number[] levelNumbers = score.toLevelNumbers();
        if (propertyIndex >= scoreDefinition.getLevelsSize()) {
            throw new IllegalArgumentException("The propertyIndex (" + propertyIndex
                    + ") must be lower than the levelsSize for score (" + score + ").");
        }
        return levelNumbers[propertyIndex];
    }

    @Override
    public Score nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        if (resultSet == null) {
            return null;
        }
        int levelsSize = scoreDefinition.getLevelsSize();
        Number[] levelNumbers = new Number[levelsSize];
        int nullCount = 0;
        for (int i = 0; i < levelsSize; i++) {
            Number levelNumber = (Number) type.nullSafeGet(resultSet, names[i], session, owner);
            if (levelNumber == null) {
                nullCount++;
            } else {
                levelNumbers[i] = levelNumber;
            }
        }
        if (nullCount == levelsSize) {
            return null;
        } else if (nullCount != 0) {
            throw new IllegalStateException("The nullCount (" + nullCount
                    + ") must be 0 or levelsSize (" + levelsSize + ") for " + getClass().getSimpleName() + ".");
        }
        Score score = scoreDefinition.fromLevelNumbers(levelNumbers);
        if (score == null) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ") must not build a score (" + null + ") that is null.");
        }
        return score;
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int parameterIndex, SessionImplementor session)
            throws SQLException {
        int levelsSize = scoreDefinition.getLevelsSize();
        if (value == null) {
            for (int i = 0; i < levelsSize; i++) {
                statement.setNull(parameterIndex + i, type.sqlType());
            }
            return;
        }
        Score score = (Score) value;
        Number[] levelNumbers = score.toLevelNumbers();
        if (levelNumbers.length != levelsSize) {
            throw new IllegalStateException("The levelNumbers length (" + levelNumbers.length + ") for score (" + score
                    + ") must be equal to the levelsSize (" + levelsSize + ") for " + getClass().getSimpleName() + ".");
        }
        for (int i = 0; i < levelsSize; i++) {
            if (type == IntegerType.INSTANCE) {
                statement.setInt(parameterIndex + i, (Integer) levelNumbers[i]);
            } else if (type == LongType.INSTANCE) {
                statement.setLong(parameterIndex + i, (Long) levelNumbers[i]);
            } else if (type == DoubleType.INSTANCE) {
                statement.setDouble(parameterIndex + i, (Double) levelNumbers[i]);
            } else if (type == BigDecimalType.INSTANCE) {
                statement.setBigDecimal(parameterIndex + i, (BigDecimal) levelNumbers[i]);
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
    public Object replace(Object original, Object target, SessionImplementor session, Object owner) {
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
    public Serializable disassemble(Object value, SessionImplementor session) {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, SessionImplementor session, Object owner) {
        return cached;
    }

}
