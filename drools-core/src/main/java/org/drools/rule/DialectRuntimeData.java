/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import org.drools.util.CompositeClassLoader;


public interface DialectRuntimeData extends Cloneable {
    public void removeRule( Package pkg, Rule rule );

    public void removeFunction( Package pkg, Function function );

    public void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData );

    public void merge( DialectRuntimeRegistry registry, DialectRuntimeData newData, boolean excludeDeclaredClasses );

    public boolean isDirty();

    public void setDirty( boolean dirty );

    public void reload();

    public DialectRuntimeData clone( DialectRuntimeRegistry registry, CompositeClassLoader rootClassLoader);

    public DialectRuntimeData clone( DialectRuntimeRegistry registry, CompositeClassLoader rootClassLoader, boolean excludeDeclaredClasses );

    public void onAdd( DialectRuntimeRegistry dialectRuntimeRegistry,
                       CompositeClassLoader rootClassLoader );

    public void onRemove();

    public void onBeforeExecute();
}
