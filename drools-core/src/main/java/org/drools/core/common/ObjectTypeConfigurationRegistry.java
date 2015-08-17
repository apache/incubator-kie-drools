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

package org.drools.core.common;

import org.drools.core.base.ClassObjectType;
import org.drools.core.facttemplates.Fact;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.FactTemplateTypeConf;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ObjectTypeConfigurationRegistry implements Serializable {
    private static final long serialVersionUID = 510l;

    private final ConcurrentMap<Object, ObjectTypeConf> typeConfMap = new ConcurrentHashMap<Object, ObjectTypeConf>();

    private final InternalKnowledgeBase kBase;

    public ObjectTypeConfigurationRegistry(InternalKnowledgeBase kBase ) {
        this.kBase = kBase;
    }

    /**
     * Returns the ObjectTypeConfiguration object for the given object or
     * creates a new one if none is found in the cache
     */
    public ObjectTypeConf getObjectTypeConf(EntryPointId entrypoint,
                                            Object object) {
        
        // first see if it's a ClassObjectTypeConf        
        Object key;
        if (object instanceof Activation) {
            key = ClassObjectType.Match_ObjectType.getClassType();
        } else if (object instanceof Fact) {
            key = ((Fact) object).getFactTemplate().getName();
        } else {
            key = object.getClass();
        }
        ObjectTypeConf objectTypeConf = this.typeConfMap.get( key );
        
        // it doesn't exist, so create it.
        if ( objectTypeConf == null ) {
            if ( object instanceof Fact ) {
                objectTypeConf = new FactTemplateTypeConf( entrypoint,
                                                           ((Fact) object).getFactTemplate(),
                                                           this.kBase );
            } else {
                objectTypeConf = new ClassObjectTypeConf( entrypoint,
                                                          (Class<?>) key,
                                                          this.kBase );
            }
            ObjectTypeConf existing = this.typeConfMap.putIfAbsent( key, objectTypeConf );
            if ( existing != null ) {
                // Raced, take the (now) existing.
                objectTypeConf = existing;
            }
        }
        return objectTypeConf;
    }

    public ObjectTypeConf getObjectTypeConfByClass(Class<?> cls) {
        return typeConfMap.get(cls);
    }

    public Collection<ObjectTypeConf> values() {
        return this.typeConfMap.values();
    }
}
