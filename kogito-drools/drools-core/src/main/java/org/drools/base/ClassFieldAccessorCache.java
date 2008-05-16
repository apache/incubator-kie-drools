package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * As class field accessors have some cost to generate
 * (inspecting the class, and generating classes via ASM)
 * it makes sense to cache them.
 * This is that cache.
 * 
 * @author Michael Neale
 *
 */
public class ClassFieldAccessorCache {

    private Map<AccessorKey, ClassFieldReader>   readerCache;
    private Map<AccessorKey, ClassFieldWriter>   writerCache;
    private Map<AccessorKey, ClassFieldAccessor> accessorCache;
    private ClassFieldAccessorFactory            factory;

    private ClassFieldAccessorCache() {
        this.factory = new ClassFieldAccessorFactory();
    }

    public static ClassFieldAccessorCache getInstance() {
        return new ClassFieldAccessorCache();
    }

    public synchronized ClassFieldReader getReader(final Class< ? > clazz,
                                                   final String fieldName,
                                                   ClassLoader classLoader) {
        if ( readerCache == null ) {
            readerCache = new HashMap<AccessorKey, ClassFieldReader>();
        }

        final AccessorKey key = new AccessorKey( clazz,
                                                 fieldName );

        if ( readerCache.containsKey( key ) ) {
            return readerCache.get( key );
        } else {
            final ClassFieldReader ex = new ClassFieldReader( clazz,
                                                              fieldName,
                                                              classLoader,
                                                              factory );
            readerCache.put( key,
                             ex );
            return ex;
        }
    }

    public synchronized ClassFieldWriter getWriter(final Class< ? > clazz,
                                                   final String fieldName,
                                                   ClassLoader classLoader) {
        if ( writerCache == null ) {
            writerCache = new HashMap<AccessorKey, ClassFieldWriter>();
        }

        final AccessorKey key = new AccessorKey( clazz,
                                                 fieldName );

        if ( writerCache.containsKey( key ) ) {
            return writerCache.get( key );
        } else {
            final ClassFieldWriter writer = new ClassFieldWriter( clazz,
                                                                  fieldName,
                                                                  classLoader,
                                                                  factory );
            writerCache.put( key,
                             writer );
            return writer;
        }
    }

    public synchronized ClassFieldAccessor getAccessor(final Class< ? > clazz,
                                                       final String fieldName,
                                                       ClassLoader classLoader) {
        if ( accessorCache == null ) {
            accessorCache = new HashMap<AccessorKey, ClassFieldAccessor>();
        }

        final AccessorKey key = new AccessorKey( clazz,
                                                 fieldName );

        if ( accessorCache.containsKey( key ) ) {
            return accessorCache.get( key );
        } else {
            final ClassFieldReader reader = getReader( clazz,
                                                       fieldName,
                                                       classLoader );
            final ClassFieldWriter writer = getWriter( clazz,
                                                       fieldName,
                                                       classLoader );
            final ClassFieldAccessor accessor = new ClassFieldAccessor( reader,
                                                                        writer );
            accessorCache.put( key,
                               accessor );
            return accessor;
        }
    }

    private static class AccessorKey
        implements
        Externalizable {
        private static final long serialVersionUID = 400;

        private Class< ? >        clazz;
        private String            fieldName;
        private int               hashCode;

        public AccessorKey() {
        }

        public AccessorKey(Class< ? > clazz,
                           String fieldName) {
            super();
            this.clazz = clazz;
            this.fieldName = fieldName;

            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((clazz == null) ? 0 : clazz.hashCode());
            result = PRIME * result + ((fieldName == null) ? 0 : fieldName.hashCode());
            this.hashCode = result;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            clazz = (Class< ? >) in.readObject();
            fieldName = (String) in.readObject();
            hashCode = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( clazz );
            out.writeObject( fieldName );
            out.writeInt( hashCode );
        }

        public Class< ? > getClazz() {
            return clazz;
        }

        public String getFieldName() {
            return fieldName;
        }

        public int hashCode() {
            return hashCode;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final AccessorKey other = (AccessorKey) obj;
            if ( clazz == null ) {
                if ( other.clazz != null ) return false;
            } else if ( !clazz.equals( other.clazz ) ) return false;
            if ( fieldName == null ) {
                if ( other.fieldName != null ) return false;
            } else if ( !fieldName.equals( other.fieldName ) ) return false;
            return true;
        }

        public String toString() {
            return this.clazz + "@" + Math.abs( System.identityHashCode( this.clazz ) ) + "(" + this.fieldName + ")";
        }
    }

}
