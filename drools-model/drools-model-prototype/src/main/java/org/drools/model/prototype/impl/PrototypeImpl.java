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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import org.drools.core.reteoo.CoreComponentFactory;
import org.kie.api.prototype.Prototype;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public abstract class PrototypeImpl implements Prototype, Serializable {

    private final String pkg;
    private final String name;
    private final SortedMap<String, Field> fields;

    public PrototypeImpl( String fulllName, List<Field> fields ) {
        int lastDot = fulllName.lastIndexOf('.');
        this.pkg = lastDot > 0 ? fulllName.substring(0, lastDot) : DEFAULT_PACKAGE;
        this.name = lastDot > 0 ? fulllName.substring(lastDot+1) : fulllName;
        this.fields = initFields(fields);
        registerPrototype();
    }

    public PrototypeImpl( String pkg, String name, List<Field> fields ) {
        this.pkg = pkg != null && !pkg.isEmpty() ? pkg : DEFAULT_PACKAGE;
        this.name = name;
        this.fields = initFields(fields);
        registerPrototype();
    }

    private void registerPrototype() {
        CoreComponentFactory.get().createKnowledgePackage(pkg).addPrototype(this);
    }

    private SortedMap<String, Field> initFields(List<Field> fields) {
        final SortedMap<String, Field> sortedFields;
        if (fields != null && !fields.isEmpty()) {
            sortedFields = new TreeMap<>();
            for (Field field : fields) {
                sortedFields.put(field.getName(), field);
            }
        } else {
            sortedFields = Collections.emptySortedMap();
        }
        return sortedFields;
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
    public Prototype.Field getField(String name) {
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
        if (!(o instanceof PrototypeImpl)) return false;
        PrototypeImpl prototype = (PrototypeImpl) o;
        return pkg.equals(prototype.pkg) && name.equals(prototype.name) && fields.equals(prototype.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name, fields);
    }

    public static class FieldImpl implements Prototype.Field, Serializable {
        private final String name;
        private final Function<PrototypeFactInstance, Object> extractor;
        private final boolean typed;
        private final Class<?> type;

        public FieldImpl( String name ) {
            this(name, p -> p.get(name));
        }

        public FieldImpl( String name, Function<PrototypeFactInstance, Object> extractor ) {
            this.name = name;
            this.extractor = extractor;
            this.type = Object.class;
            this.typed = false;
        }

        public FieldImpl( String name, Class<?> type ) {
            this(name, type, p -> p.get(name));
        }

        public FieldImpl( String name, Class<?> type, Function<PrototypeFactInstance, Object> extractor ) {
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
        public Function<PrototypeFactInstance, Object> getExtractor() {
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
