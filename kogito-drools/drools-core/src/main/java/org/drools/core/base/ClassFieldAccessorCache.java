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

package org.drools.core.base;

import org.drools.core.util.ByteArrayClassLoader;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.asm.ClassFieldInspector;

import java.security.ProtectionDomain;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassFieldAccessorCache {

    private Map<ClassLoader, CacheEntry> cacheByClassLoader;

    private ClassLoader                  classLoader;

    public ClassFieldAccessorCache(ClassLoader classLoader) {
        //        lookup = new HashMap<AccessorKey, LookupEntry>();
        cacheByClassLoader = new WeakHashMap<ClassLoader, CacheEntry>();
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public ClassObjectType getClassObjectType(ClassObjectType objectType, boolean lookupClass) {
        // lookup the class when the ClassObjectType might refer to the class from another ClassLoader
        Class cls = lookupClass ? getClass( objectType.getClassName() ) : objectType.getClassType();
        CacheEntry cache = getCacheEntry( cls );
        return cache.getClassObjectType( cls,
                                         objectType );
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
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( !(obj instanceof ClassObjectTypeKey) ) return false;
            ClassObjectTypeKey other = (ClassObjectTypeKey) obj;
            if ( cls == null ) {
                if ( other.cls != null ) return false;
            } else if ( !cls.equals( other.cls ) ) return false;
            return event == other.event;
        }

    }
    
    public BaseClassFieldReader getReadAcessor(ClassFieldReader reader) {
        String className = reader.getClassName();
        String fieldName = reader.getFieldName();

        Class cls = getClass( className );
        CacheEntry cache = getCacheEntry( cls );

        // get the ReaderAccessor for this key
        return cache.getReadAccessor( new AccessorKey( className,
                                                       fieldName,
                                                       AccessorKey.AccessorType.FieldAccessor ),
                                      cls );
    }

    public BaseClassFieldWriter getWriteAcessor(ClassFieldWriter writer) {
        String className = writer.getClassName();
        String fieldName = writer.getFieldName();

        Class cls = getClass( className );
        CacheEntry cache = getCacheEntry( cls );

        // get the ReaderAccessor for this key
        return cache.getWriteAccessor( new AccessorKey( className,
                                                        fieldName,
                                                        AccessorKey.AccessorType.FieldAccessor ),
                                       cls );
    }

    public Class getClass(String className) {
        try {
            return this.classLoader.loadClass( className );
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
        private ByteArrayClassLoader                                     byteArrayClassLoader;
        private final ConcurrentMap<AccessorKey, BaseClassFieldReader>   readCache   = new ConcurrentHashMap<AccessorKey, BaseClassFieldReader>();
        private final ConcurrentMap<AccessorKey, BaseClassFieldWriter>   writeCache  = new ConcurrentHashMap<AccessorKey, BaseClassFieldWriter>();

        private final ConcurrentMap<Class< ? >, ClassFieldInspector>     inspectors  = new ConcurrentHashMap<Class< ? >, ClassFieldInspector>();

        private final ConcurrentMap<ClassObjectTypeKey, ClassObjectType> objectTypes = new ConcurrentHashMap<ClassObjectTypeKey, ClassObjectType>();

        public CacheEntry(ClassLoader parentClassLoader) {
            if ( parentClassLoader == null ) {
                throw new RuntimeException( "ClassFieldAccessorFactory cannot have a null parent ClassLoader" );
            }
            this.byteArrayClassLoader = ClassUtils.isAndroid() ?
                    (ByteArrayClassLoader) ClassUtils.instantiateObject(
                            "org.drools.android.MultiDexClassLoader", null, parentClassLoader) :
                    new DefaultByteArrayClassLoader( parentClassLoader );
        }

        public ByteArrayClassLoader getByteArrayClassLoader() {
            return byteArrayClassLoader;
        }

        public BaseClassFieldReader getReadAccessor(AccessorKey key,
                                                    Class cls) {
            BaseClassFieldReader reader = this.readCache.get( key );
            if ( reader == null ) {
                reader = ClassFieldAccessorFactory.getClassFieldReader( cls,
                                                                        key.getFieldName(),
                                                                        this );
                if ( reader != null ) {
                    BaseClassFieldReader existingReader = this.readCache.putIfAbsent( key,
                                                                                      reader );
                    if ( existingReader != null ) {
                        // Raced, use the (now) existing entry
                        reader = existingReader;
                    }
                }
            }

            return reader;
        }

        public BaseClassFieldWriter getWriteAccessor(AccessorKey key,
                                                     Class cls) {
            BaseClassFieldWriter writer = this.writeCache.get( key );
            if ( writer == null ) {
                writer = ClassFieldAccessorFactory.getClassFieldWriter( cls,
                                                                        key.getFieldName(),
                                                                        this );
                if ( writer != null ) {
                    BaseClassFieldWriter existingWriter = this.writeCache.putIfAbsent( key,
                                                                                       writer );
                    if ( existingWriter != null ) {
                        // Raced, use the (now) existing entry
                        writer = existingWriter;
                    }
                }
            }

            return writer;
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

    public static class DefaultByteArrayClassLoader extends ClassLoader implements ByteArrayClassLoader {
        public DefaultByteArrayClassLoader(final ClassLoader parent) {
            super( parent );
        }

        public Class< ? > defineClass(final String name,
                                      final byte[] bytes,
                                      final ProtectionDomain domain) {
            return defineClass( name,
                                bytes,
                                0,
                                bytes.length,
                                domain );
        }
    }

}
