/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.base.ClassObjectType;
import org.drools.core.facttemplates.Fact;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.FactTemplateTypeConf;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;

public class ObjectTypeConfigurationRegistry implements Serializable {
    private static final long serialVersionUID = 510l;

    private final Map<Object, ObjectTypeConf> typeConfMap = new ConcurrentHashMap<>();

    private final InternalKnowledgeBase kBase;

    public ObjectTypeConfigurationRegistry(InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    public ObjectTypeConf getObjectTypeConf(Object object) {
        return this.typeConfMap.get( getKey( object ) );
    }

    /**
     * Returns the ObjectTypeConfiguration object for the given object or
     * creates a new one if none is found in the cache
     */
    public ObjectTypeConf getOrCreateObjectTypeConf(EntryPointId entrypoint, Object object) {
        Object key = getKey( object );
        ObjectTypeConf conf = this.typeConfMap.get( key );
        if (conf == null) {
            conf = createObjectTypeConf( entrypoint, key, object );
            ObjectTypeConf existingConf = this.typeConfMap.putIfAbsent( key, conf );
            if (existingConf != null) {
                conf = existingConf;
            }
        }
        return conf;
    }

    private Object getKey( Object object ) {
        if ( object instanceof Activation) {
            return ClassObjectType.Match_ObjectType.getClassType();
        }
        if ( object instanceof Fact) {
            return ((Fact) object).getFactTemplate().getName();
        }
        return object.getClass();
    }

    private ObjectTypeConf createObjectTypeConf(EntryPointId entrypoint, Object key, Object object) {
        return object instanceof Fact ?
                new FactTemplateTypeConf( entrypoint, ((Fact) object).getFactTemplate(), this.kBase ) :
                new ClassObjectTypeConf( entrypoint, (Class<?>) key, this.kBase );
    }

    public ObjectTypeConf getObjectTypeConfByClass(Class<?> cls) {
        return typeConfMap.get(cls);
    }

    public Collection<ObjectTypeConf> values() {
        return this.typeConfMap.values();
    }
}
