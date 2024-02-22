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
package org.kie.dmn.core.impl;

import java.util.Collection;
import java.util.List;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;

/**
 * @see DMNType
 */
public class SimpleTypeImpl
        extends BaseDMNTypeImpl {


    public static SimpleTypeImpl UNKNOWN_DMNTYPE(String uriFEEL) {
        return new SimpleTypeImpl(uriFEEL,
                                  BuiltInType.UNKNOWN.getName(),
                                  null, true, null, null, null,
                                  BuiltInType.UNKNOWN );
    }

    public SimpleTypeImpl() {
        this( null, null, null, false, null, null,null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id) {
        this( namespace, name, id, false, null, null, null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id, boolean isCollection, List<UnaryTest> allowedValues, List<UnaryTest> typeConstraint, DMNType baseType, Type feelType) {
        super(namespace, name, id, isCollection, baseType, feelType);
        setAllowedValues( allowedValues );
        setTypeConstraint(typeConstraint);
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    public BaseDMNTypeImpl clone() {
        return new SimpleTypeImpl( getNamespace(), getName(), getId(), isCollection(), getAllowedValuesFEEL(), getTypeConstraintFEEL(), getBaseType(), getFeelType() );
    }

    @Override
    protected boolean internalIsInstanceOf(Object o) {
        return getBaseType() != null ? getBaseType().isInstanceOf(o) : getFeelType().isInstanceOf(o);
    }

    @Override
    protected boolean internalAllowedValueIsInstanceOf(Object o) {
        return getBaseType() != null ? getBaseType().isInstanceOf(o) : getFeelType().isInstanceOf(o);
    }

    @Override
    protected boolean internalTypeConstraintIsInstanceOf(Object o) {
        return getFeelType().isInstanceOf(o);
    }

    @Override
    protected boolean internalAllowedValueIsAssignableValue(Object o) {
        return getBaseType() != null ? getBaseType().isAssignableValue(o) : getFeelType().isAssignableValue(o);
    }

    @Override
    protected boolean internalTypeConstraintIsAssignableValue(Object o) {
        return isCollection() ? o instanceof Collection : getFeelType().isAssignableValue(o);
    }
}
