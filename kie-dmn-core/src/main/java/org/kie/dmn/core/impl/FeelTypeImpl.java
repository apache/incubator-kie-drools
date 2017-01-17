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

import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FeelTypeImpl
        extends BaseDMNTypeImpl
        implements DMNType {

    private Type    feelType;

    public FeelTypeImpl() {
        this( null, null, null, false, null );
    }

    public FeelTypeImpl(String name, String id) {
        this( name, id, null, false, null );
    }

    public FeelTypeImpl(String name, String id, Type feelType, boolean isCollection, List<?> allowedValues) {
        super( name, id, isCollection );
        this.feelType = feelType;
        setAllowedValues( allowedValues );
    }

    public Type getFeelType() {
        return feelType;
    }

    public void setFeelType(Type feelType) {
        this.feelType = feelType;
    }

    @Override
    public Object parseValue(String value) {
        // TODO we should add the EventListener here somehow?
        return feelType.fromString( value );
    }

    @Override
    public String toString(Object value) {
        // TODO we should add the EventListener here somehow?
        return feelType.toString( value );
    }

    @Override
    public boolean isComposite() {
        return feelType == BuiltInType.CONTEXT;
    }

    public BaseDMNTypeImpl clone() {
        return new FeelTypeImpl( getName(), getId(), feelType, isCollection(), getAllowedValues() );
    }

}
