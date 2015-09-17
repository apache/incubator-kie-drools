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

package org.optaplanner.persistence.jpa.impl.score.buildin.hardsoft;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.persistence.jpa.impl.score.AbstractScoreHibernateType;

/**
 * {@inheritDoc}
 */
public class HardSoftScoreHibernateType extends AbstractScoreHibernateType {

    @Override
    public Class returnedClass() {
        return HardSoftScore.class;
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] {"hardScore", "softScore"};
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[] {IntegerType.INSTANCE, IntegerType.INSTANCE};
    }

    @Override
    public Object getPropertyValue(Object o, int propertyIndex) {
        if (o == null) {
            return null;
        }
        HardSoftScore score = (HardSoftScore) o;
        switch (propertyIndex) {
            case 0:
                return score.getHardScore();
            case 1:
                return score.getSoftScore();
            default:
                throw new IllegalArgumentException("The propertyIndex (" + propertyIndex
                        + ") must be lower than the levelsSize for score (" + score + ").");
        }
    }

    @Override
    public HardSoftScore nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
            throws SQLException {
        if (resultSet == null) {
            return null;
        }
        Integer hardScore = (Integer) StandardBasicTypes.INTEGER.nullSafeGet(resultSet, names[0], session, owner);
        Integer softScore = (Integer) StandardBasicTypes.INTEGER.nullSafeGet(resultSet, names[1], session, owner);
        if (hardScore == null && softScore == null) {
            return null;
        }
        return HardSoftScore.valueOf(hardScore, softScore);
    }

    @Override
    public void nullSafeSet(PreparedStatement statement, Object value, int parameterIndex, SessionImplementor session)
            throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, StandardBasicTypes.INTEGER.sqlType());
            statement.setNull(parameterIndex + 1, StandardBasicTypes.INTEGER.sqlType());
            return;
        }
        HardSoftScore score = (HardSoftScore) value;
        statement.setInt(parameterIndex, score.getHardScore());
        statement.setInt(parameterIndex + 1, score.getSoftScore());
    }

}
