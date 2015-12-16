/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.metadata;


import java.io.Serializable;
import java.net.URI;
import java.util.List;

public abstract class ClassLiteral<T> implements MetaClass<T>, Serializable {

    protected MetaProperty<T,?,?>[] properties;
    protected List<String> propertyNames;
    protected URI key;

    public ClassLiteral( MetaProperty<T,?,?>[] propertyLiterals ) {
        this.properties = propertyLiterals;
        cachePropertyNames();
    }

    protected abstract void cachePropertyNames();

    @Override
    public MetaProperty<T,?,?>[] getProperties() {
        return properties;
    }

    public int getPropertyIndex( MetaProperty prop ) {
        return propertyNames.indexOf( prop.getName() );
    }

    public Object getId() {
        return getUri();
    }

    public abstract Class<T> getTargetClass();
}
