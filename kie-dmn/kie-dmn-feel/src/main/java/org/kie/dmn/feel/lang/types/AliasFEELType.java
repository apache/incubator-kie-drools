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

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;

/**
 * Useful for ItemDefinition at DMN layer redefining as an alias a basic FEEL type.
 */
public class AliasFEELType implements SimpleType {

    private String name;
    private BuiltInType wrapped;

    public AliasFEELType(String name, BuiltInType wrapped) {
        this.name = name;
        this.wrapped = wrapped;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return wrapped.isInstanceOf(o);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        return wrapped.isAssignableValue(value);
    }


    public BuiltInType getBuiltInType() {
        return wrapped;
    }

    @Override
    public boolean conformsTo(Type t) {
        return this.wrapped.conformsTo(t);
    }
}
