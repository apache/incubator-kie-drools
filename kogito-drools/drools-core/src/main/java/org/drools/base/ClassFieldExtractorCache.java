package org.drools.base;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

/**
 * As class field Extractors have some cost to generate
 * (inspecting the class, and generating classes via ASM)
 * it makes sense to cache them.
 * This is that cache.
 * 
 * @author Michael Neale
 *
 */
public class ClassFieldExtractorCache {
    private static final ClassFieldExtractorCache INSTANCE = new ClassFieldExtractorCache();

    private Map                                   cache;
    private ClassFieldExtractorFactory            factory;
    
    private ClassFieldExtractorCache() {
        this.factory = new ClassFieldExtractorFactory();
    }

    public static ClassFieldExtractorCache getInstance() {
        //return INSTANCE;
        return new ClassFieldExtractorCache();
    }

    public synchronized ClassFieldExtractor getExtractor(final Class clazz,
                                                         final String fieldName,
                                                         ClassLoader classLoader) {
        if ( cache == null ) {
            cache = new HashMap();
        }

        final ExtractorKey key = new ExtractorKey( clazz,
                                                   fieldName );

        if ( cache.containsKey( key ) ) {
            return (ClassFieldExtractor) cache.get( key );
        } else {
            final ClassFieldExtractor ex = new ClassFieldExtractor( clazz,
                                                                    fieldName,
                                                                    classLoader,
                                                                    factory );
            cache.put( key,
                       ex );
            return ex;
        }
    }

    private static class ExtractorKey
        implements
        Externalizable {
        private static final long serialVersionUID = 400;

        private Class       clazz;
        private String      fieldName;
        private int         hashCode;

        public ExtractorKey() {
        }

        public ExtractorKey(Class clazz,
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

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            clazz   = (Class)in.readObject();
            fieldName   = (String)in.readObject();
            hashCode    = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(clazz);
            out.writeObject(fieldName);
            out.writeInt(hashCode);
        }

        public Class getClazz() {
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
            final ExtractorKey other = (ExtractorKey) obj;
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
