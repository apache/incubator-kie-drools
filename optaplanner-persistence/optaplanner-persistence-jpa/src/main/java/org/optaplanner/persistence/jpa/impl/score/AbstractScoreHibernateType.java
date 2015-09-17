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

package org.optaplanner.persistence.jpa.impl.score;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.CompositeUserType;

/**
 * This class is Hibernate specific, because JPA 2.1's @Converter currently
 * cannot handle 1 class mapping to multiple SQL columns.
 */
public abstract class AbstractScoreHibernateType implements CompositeUserType {

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
