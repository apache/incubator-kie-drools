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
package org.drools.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.drools.model.Prototype;
import org.drools.model.PrototypeFact;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class PrototypeImpl implements Prototype {

    private final String pkg;
    private final String name;
    private final SortedMap<String, Field> fields;

    private boolean event;

    public PrototypeImpl( String name ) {
        this(name, new Field[0]);
    }

    public PrototypeImpl( String name, String... fields ) {
        this(name, Stream.of(fields).map(FieldImpl::new).toArray(Field[]::new));
    }

    public PrototypeImpl( String name, Field... fields ) {
        int lastDot = name.lastIndexOf('.');
        this.pkg = lastDot > 0 ? name.substring(0, lastDot) : DEFAULT_PACKAGE;
        this.name = lastDot > 0 ? name.substring(lastDot+1) : name;
        if (fields != null && fields.length > 0) {
            this.fields = new TreeMap<>();
            for (Field field : fields) {
                this.fields.put(field.getName(), field);
            }
        } else {
            this.fields = Collections.emptySortedMap();
        }
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<String> getFieldNames() {
        return fields.keySet();
    }

    @Override
    public Field getField(String name) {
        return fields.get(name);
    }

    @Override
    public int getFieldIndex(final String name) {
        int i = 0;
        for ( String field : fields.keySet() ) {
            if ( field.equals( name ) ) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrototypeImpl prototype = (PrototypeImpl) o;
        return pkg.equals(prototype.pkg) && name.equals(prototype.name) && fields.equals(prototype.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name, fields);
    }

    public boolean isEvent() {
        return event;
    }

    public PrototypeImpl setAsEvent(boolean event) {
        this.event = event;
        return this;
    }

    public static class FieldImpl implements Prototype.Field {
        private final String name;
        private final Function<PrototypeFact, Object> extractor;
        private final boolean typed;
        private final Class<?> type;

        public FieldImpl( String name ) {
            this(name, p -> p.get(name));
        }

        public FieldImpl( String name, Function<PrototypeFact, Object> extractor ) {
            this.name = name;
            this.extractor = extractor;
            this.type = Object.class;
            this.typed = false;
        }

        public FieldImpl( String name, Class<?> type ) {
            this(name, type, p -> p.get(name));
        }

        public FieldImpl( String name, Class<?> type, Function<PrototypeFact, Object> extractor ) {
            this.name = name;
            this.extractor = extractor;
            this.type = type;
            this.typed = true;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Function<PrototypeFact, Object> getExtractor() {
            return extractor;
        }

        @Override
        public boolean isTyped() {
            return typed;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }
}
