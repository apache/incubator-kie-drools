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
package org.drools.base.rule;

import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.accessor.ReadAccessor;

public interface DialectRuntimeData extends Cloneable {
    void removeRule( KnowledgePackageImpl pkg, RuleImpl rule );

    void removeFunction( KnowledgePackageImpl pkg, Function function );

    void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData );

    void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData, boolean excludeDeclaredClasses );

    boolean isDirty();

    void setDirty( boolean dirty );

    void reload();

    DialectRuntimeData clone( DialectRuntimeRegistry registry, ClassLoader rootClassLoader);

    DialectRuntimeData clone( DialectRuntimeRegistry registry, ClassLoader rootClassLoader, boolean excludeDeclaredClasses );

    void onAdd( DialectRuntimeRegistry dialectRuntimeRegistry, ClassLoader rootClassLoader );

    void onRemove();

    void onBeforeExecute();

    default void resetParserConfiguration() { }

    default void compile(ReadAccessor reader) {
        throw new UnsupportedOperationException();
    }

    default public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException();
    }

    default boolean remove(String typeClassName) {
        throw new UnsupportedOperationException();
    }

    default ClassLoader getRootClassLoader() {
        throw new UnsupportedOperationException();
    }
}
