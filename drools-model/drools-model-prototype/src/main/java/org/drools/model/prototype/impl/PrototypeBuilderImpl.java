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
package org.drools.model.prototype.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeBuilder;
import org.kie.api.prototype.PrototypeEvent;
import org.kie.api.prototype.PrototypeFact;
import org.kie.api.prototype.PrototypeFactInstance;

public class PrototypeBuilderImpl implements PrototypeBuilder {

    private final String name;

    private final List<Prototype.Field> fields = new ArrayList<>();

    public PrototypeBuilderImpl(String name) {
        this.name = name;
    }

    @Override
    public PrototypeBuilder withField(String name) {
        fields.add(new PrototypeImpl.FieldImpl(name));
        return this;
    }

    @Override
    public PrototypeBuilder withField(String name, Function<PrototypeFactInstance, Object> extractor) {
        fields.add(new PrototypeImpl.FieldImpl(name, extractor));
        return this;
    }

    @Override
    public PrototypeBuilder withField(String name, Class<?> type) {
        fields.add(new PrototypeImpl.FieldImpl(name, type));
        return this;
    }

    @Override
    public PrototypeBuilder withField(String name, Class<?> type, Function<PrototypeFactInstance, Object> extractor) {
        fields.add(new PrototypeImpl.FieldImpl(name, type, extractor));
        return this;
    }

    @Override
    public PrototypeFact asFact() {
        return new PrototypeFactImpl( name, fields );
    }

    @Override
    public PrototypeEvent asEvent() {
        return new PrototypeEventImpl( name, fields );
    }

    public static class CreatorImpl implements PrototypeBuilder.Creator {

        @Override
        public PrototypeBuilder newPrototype(String name) {
            return new PrototypeBuilderImpl(name);
        }
    }
}
