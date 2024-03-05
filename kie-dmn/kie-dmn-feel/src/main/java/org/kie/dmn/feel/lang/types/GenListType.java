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
package org.kie.dmn.feel.lang.types;

import java.util.Collection;

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;

public class GenListType implements SimpleType {

    /**
     * Represents the "generic" type of the current list
     */
    private final Type gen;


    public GenListType(Type gen) {
        this.gen = gen;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return o instanceof Collection && ((Collection<?>) o).stream().allMatch(gen::isInstanceOf);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true; // a null-value can be assigned to any type.
        }
        if (!(value instanceof Collection)) {
            return gen.isAssignableValue(value);
        }
        return isInstanceOf(value);
    }

    @Override
    public String getName() {
        return "[anonymous]";
    }

    public Type getGen() {
        return gen;
    }

    @Override
    public boolean conformsTo(Type t) {
        return (t instanceof GenListType && this.gen.conformsTo(((GenListType) t).gen)) || t == BuiltInType.LIST;
    }

}
