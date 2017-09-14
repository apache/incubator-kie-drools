/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.impl;

import org.drools.model.Type;
import org.drools.model.Value;

import static org.drools.model.DSL.type;

public class ValueImpl<T> implements Value<T> {

    private final T value;
    private final Type<T> type;

    public ValueImpl( T value ) {
        this.value = value;
        this.type = type( (Class<T>) value.getClass()) ;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public Type<T> getType() {
        return type;
    }
}
