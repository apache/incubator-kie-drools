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

import org.drools.model.Prototype;

public class PrototypeImpl implements Prototype {

    private final String pkg;
    private final String name;
    private final Field[] fields;

    public PrototypeImpl( String pkg, String name, Field[] fields ) {
        this.pkg = pkg;
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Field[] getFields() {
        return fields;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getName() {
        return name;
    }

    public static class FieldImpl implements Prototype.Field {
        private final String name;
        private final Class<?> type;

        public FieldImpl( String name, Class<?> type ) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }
}
