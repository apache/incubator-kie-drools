/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;

import java.util.Collection;
import java.util.List;

public class SimpleTypeImpl
        extends BaseDMNTypeImpl {

    public SimpleTypeImpl() {
        this( null, null, null, false, null, null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id) {
        this( namespace, name, id, false, null, null, null );
    }

    public SimpleTypeImpl(String namespace, String name, String id, boolean isCollection, List<UnaryTest> allowedValues, DMNType baseType, Type feelType) {
        super( namespace, name, id, isCollection, null, feelType );
        setAllowedValues( allowedValues );
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    public BaseDMNTypeImpl clone() {
        return new SimpleTypeImpl( getNamespace(), getName(), getId(), isCollection(), getAllowedValues(), getBaseType(), getFeelType() );
    }

    @Override
    protected boolean internalIsInstanceOf(Object o) {
        return getFeelType().isInstanceOf(o);
    }

    @Override
    protected boolean internalIsAssignableValue(Object o) {
        return getFeelType().isAssignableValue(o);
    }
}
