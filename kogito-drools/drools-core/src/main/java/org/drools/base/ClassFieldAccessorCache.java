package org.drools.base;

import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.RuntimeDroolsException;
import org.drools.core.util.asm.ClassFieldInspector;

public class ClassFieldAccessorCache {

    private Map<ClassLoader, CacheEntry>   cacheByClassLoader;

    private ClassLoader                    classLoader;


    public ClassFieldAccessorCache(ClassLoader classLoader) {
        //        lookup = new HashMap<AccessorKey, LookupEntry>();
        cacheByClassLoader = new WeakHashMap<ClassLoader, CacheEntry>();
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    public ClassObjectType getClassObjectType(ClassObjectType objectType) {
        // always lookup the class, as the ClassObjectType might refer to the class from another ClassLoader
        Class cls = getClass( objectType.getClassName() );
        CacheEntry cache = getCacheEntry( cls );
        return cache.getClassObjectType( cls, objectType );                
    }
    
    public static class ClassObjectTypeKey {
        private Class cls;
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
            if ( event != other.event ) return false;
            return true;
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
            throw new RuntimeDroolsException( "Unable to resolve class '" + className + "'" );
        }
    }    
        
    public CacheEntry getCacheEntry(Class cls) {
        // System classloader classes return null on some JVMs
        ClassLoader cl = cls.getClassLoader() != null ? 
        		         cls.getClassLoader() : this.classLoader;

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
        private ByteArrayClassLoader                         byteArrayClassLoader;
        private final Map<AccessorKey, BaseClassFieldReader> readCache   = new HashMap<AccessorKey, BaseClassFieldReader>();
        private final Map<AccessorKey, BaseClassFieldWriter> writeCache  = new HashMap<AccessorKey, BaseClassFieldWriter>();

        private final Map<Class< ? >, ClassFieldInspector>   inspectors  = new HashMap<Class< ? >, ClassFieldInspector>();

        private final Map<ClassObjectTypeKey, ClassObjectType>       objectTypes = new HashMap<ClassObjectTypeKey, ClassObjectType>();

        public CacheEntry(ClassLoader parentClassLoader) {
            if ( parentClassLoader == null ) {
                throw new RuntimeDroolsException( "ClassFieldAccessorFactory cannot have a null parent ClassLoader" );
            }
            this.byteArrayClassLoader = new ByteArrayClassLoader( parentClassLoader );
        }
        
        public ByteArrayClassLoader getByteArrayClassLoader() {
            return byteArrayClassLoader;
        }

        public BaseClassFieldReader getReadAccessor(AccessorKey key,
                                                    Class cls) {
            BaseClassFieldReader reader = this.readCache.get( key );
            if ( reader == null ) {
                reader = ClassFieldAccessorFactory.getInstance().getClassFieldReader( cls,
                                                                                      key.getFieldName(),
                                                                                      this );
                this.readCache.put( key,
                                    reader );
            }

            return reader;
        }

        public BaseClassFieldWriter getWriteAccessor(AccessorKey key,
                                                     Class cls) {
            BaseClassFieldWriter reader = this.writeCache.get( key );
            if ( reader == null ) {
                reader = ClassFieldAccessorFactory.getInstance().getClassFieldWriter( cls,
                                                                                      key.getFieldName(),
                                                                                      this );
                this.writeCache.put( key,
                                     reader );
            }

            return reader;
        }

        public Map<Class< ? >, ClassFieldInspector> getInspectors() {
            return inspectors;
        }
        
        public ClassObjectType getClassObjectType(Class cls, ClassObjectType objectType) {
            ClassObjectTypeKey key = new ClassObjectTypeKey(cls, objectType.isEvent() );            
            ClassObjectType existing = objectTypes.get( key );
            
            if ( existing != null ) {
                objectType = existing;
            } else {
                objectType.setClassType( cls ); // most likely set, but set anyway.
                objectTypes.put(  key, objectType );
            }
            
            return objectType;            
        }

    }

    public static class ByteArrayClassLoader extends ClassLoader {
        public ByteArrayClassLoader(final ClassLoader parent) {
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
