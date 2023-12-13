/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast.infixexecutors;

import java.util.Objects;

public class ClassIdentifierTuple {

    private final Class<?> LEFT_TYPE;
    private final Class<?> RIGHT_TYPE;


    public ClassIdentifierTuple(Object leftObject, Object rightObject) {
        this.LEFT_TYPE = leftObject != null ? leftObject.getClass() : null;
        this.RIGHT_TYPE = rightObject != null ? rightObject.getClass() : null;
    }

    public ClassIdentifierTuple(Class<?> leftType, Class<?> rightType) {
        this.LEFT_TYPE = leftType;
        this.RIGHT_TYPE = rightType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassIdentifierTuple that = (ClassIdentifierTuple) o;
        return isEquals(LEFT_TYPE, that.LEFT_TYPE) && isEquals(RIGHT_TYPE, that.RIGHT_TYPE);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    static boolean isEquals(Class<?> thisClass, Class<?> thatClass) {
        return (thisClass != null && thatClass != null) &&
                (Objects.equals(thisClass, thatClass) || thatClass.isAssignableFrom(thisClass));
    }
}
