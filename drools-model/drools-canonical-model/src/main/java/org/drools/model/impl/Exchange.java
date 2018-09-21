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

import org.drools.model.functions.Function0;

import static org.drools.model.impl.NamesGenerator.generateName;

public class Exchange<T> extends VariableImpl<T> {

    private T object;

    private Function0<T> messageSupplier;

    public Exchange(Class<T> type) {
        this(type, generateName("exchange"));
    }

    public Exchange(Class<T> type, String name) {
        super(type, name);
    }

    public T getObject() {
        return object;
    }

    public void setObject( T object ) {
        this.object = object;
    }

    public Function0<T> getMessageSupplier() {
        return messageSupplier;
    }

    public void setMessageSupplier( Function0<T> messageSupplier ) {
        this.messageSupplier = messageSupplier;
    }
}
