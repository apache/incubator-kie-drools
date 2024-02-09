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
package org.drools.core.base;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.drools.base.base.AccessorKey;
import org.drools.base.base.ClassFieldInspector;
import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.WriteAccessor;
import org.drools.wiring.api.ComponentsFactory;
import org.drools.wiring.api.util.ByteArrayClassLoader;

import static org.drools.util.ClassUtils.convertPrimitiveNameToType;

public class ClassFieldAccessorCache {

    private Map<ClassLoader, CacheEntry> cacheByClassLoader;

    private ClassLoader                  classLoader;

    public ClassFieldAccessorCache(ClassLoader classLoader) {
        this.cacheByClassLoader = new WeakHashMap<>();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ClassObjectType getClassObjectType(ClassObjectType objectType, boolean lookupClass) {
        // lookup the class when the ClassObjectType might refer to the class from another ClassLoader
        Class cls = lookupClass ? getClass( objectType.getClassName() ) : objectType.getClassType();
        CacheEntry cache = getCacheEntry( cls );
        return cache.getClassObjectType( cls, objectType );
    }

    public static class ClassObjectTypeKey {
        private Class   cls;
        private boolean event;

        public ClassObjectTypeKey(Class cls,
                                  boolean event) {
            this.cls = cls;
            this.event = event;
        }

        public Class getCls() {
            return cls;
        }

        public boolean isEvent() {
            return event;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((cls == null) ? 0 : cls.hashCode());
            result = prime * result + (event ? 1231 : 1237);
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( !(obj instanceof ClassObjectTypeKey) ) {
                return false;
            }
            ClassObjectTypeKey other = (ClassObjectTypeKey) obj;
            if ( cls == null ) {
                if ( other.cls != null ) {
                    return false;
                }
            } else if ( !cls.equals( other.cls ) ) {
                return false;
            }
            return event == other.event;
        }

    }
    
    public ReadAccessor getReadAccessor(String className, String fieldName) {
        // get the ReaderAccessor for this key
        Class cls = getClass( className );
        return getCacheEntry( cls ).getReadAccessor( getAccessorKey( className, fieldName ), cls );
    }

    public void setReadAcessor(String className, String fieldName, ReadAccessor readAccessor) {
        // get the ReaderAccessor for this key
        Class cls = getClass( className );
        getCacheEntry( cls ).setReadAccessor( getAccessorKey( className, fieldName ), readAccessor );
    }

    private AccessorKey getAccessorKey(String className, String fieldName ) {
        return new AccessorKey( className, fieldName, AccessorKey.AccessorType.FieldAccessor );
    }

    public WriteAccessor getWriteAccessor(String className, String fieldName) {
        // get the ReaderAccessor for this key
        Class cls = getClass( className );
        return getCacheEntry( cls ).getWriteAccessor( getAccessorKey( className, fieldName ), cls );
    }

    public void setWriteAcessor(String className, String fieldName, BaseClassFieldWriter writeAccessor ) {
        // get the ReaderAccessor for this key
        Class cls = getClass( className );
        getCacheEntry( cls ).setWriteAccessor( getAccessorKey( className, fieldName ), writeAccessor );
    }

    private Class getClass(String className) {
        return getClass(this.classLoader, className);
    }

    private static Class getClass(ClassLoader cl, String className) {
        try {
            Class<?> primitiveType = convertPrimitiveNameToType( className );
            return primitiveType != null ? primitiveType : cl.loadClass( className );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Unable to resolve class '" + className + "'" );
        }
    }

    public CacheEntry getCacheEntry(Class cls) {
        // System classloader classes return null on some JVMs
        ClassLoader cl = cls.getClassLoader() != null ? cls.getClassLoader() : this.classLoader;

        CacheEntry cache = this.cacheByClassLoader.get( cl );
        if ( cache == null ) {
            // setup a cache for this ClassLoader
            cache = new CacheEntry( this.classLoader );
            this.cacheByClassLoader.put( cl,
                                         cache );
        }

        return cache;
    }

    public static class CacheEntry {
        private final ByteArrayClassLoader byteArrayClassLoader;
        private final ConcurrentMap<AccessorKey, ReadAccessor> readCache  = new ConcurrentHashMap<>();
        private final ConcurrentMap<AccessorKey, WriteAccessor> writeCache = new ConcurrentHashMap<>();

        private final ConcurrentMap<Class< ? >, ClassFieldInspector>     inspectors  = new ConcurrentHashMap<>();

        private final ConcurrentMap<ClassObjectTypeKey, ClassObjectType> objectTypes = new ConcurrentHashMap<>();

        public CacheEntry(ClassLoader parentClassLoader) {
            if ( parentClassLoader == null ) {
                throw new RuntimeException( "ClassFieldAccessorFactory cannot have a null parent ClassLoader" );
            }

            this.byteArrayClassLoader = AccessController.doPrivileged( (PrivilegedAction<ByteArrayClassLoader>)
                    () -> ComponentsFactory.createByteArrayClassLoader(parentClassLoader) );
        }

        public ByteArrayClassLoader getByteArrayClassLoader() {
            return byteArrayClassLoader;
        }

        public ReadAccessor getReadAccessor(AccessorKey key, Class cls) {
            ReadAccessor reader = this.readCache.get( key );
            if ( reader == null ) {
                reader = FieldAccessorFactory.get().getClassFieldReader( cls, key.getFieldName(), this );
                if ( reader != null ) {
                    ReadAccessor existingReader = this.readCache.putIfAbsent( key, reader );
                    if ( existingReader != null ) {
                        // Raced, use the (now) existing entry
                        reader = existingReader;
                    }
                }
            }

            return reader;
        }

        public void setReadAccessor(AccessorKey key, ReadAccessor reader) {
            this.readCache.put( key, reader );
        }

        public WriteAccessor getWriteAccessor(AccessorKey key, Class cls) {
            WriteAccessor writer = this.writeCache.get( key );
            if ( writer == null ) {
                writer = FieldAccessorFactory.get().getClassFieldWriter( cls, key.getFieldName(), this );
                if ( writer != null ) {
                    WriteAccessor existingWriter = this.writeCache.putIfAbsent( key, writer );
                    if ( existingWriter != null ) {
                        // Raced, use the (now) existing entry
                        writer = existingWriter;
                    }
                }
            }

            return writer;
        }

        public void setWriteAccessor(AccessorKey key, BaseClassFieldWriter writer) {
            this.writeCache.put( key, writer );
        }

        public Map<Class< ? >, ClassFieldInspector> getInspectors() {
            return inspectors;
        }

        public ClassObjectType getClassObjectType(Class<?> cls,
                                                  ClassObjectType objectType) {
            ClassObjectTypeKey key = new ClassObjectTypeKey( cls,
                                                             objectType.isEvent() );
            ClassObjectType existing = objectTypes.get( key );

            if ( existing == null ) {
                objectType.setClassType( cls ); // most likely set, but set anyway.
                existing = objectTypes.putIfAbsent( key, objectType );
                if ( existing == null ) {
                    // Not raced, use the one we created.
                    existing = objectType;
                }
            }

            return existing;
        }

    }
}
