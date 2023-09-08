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
package org.drools.mvel.accessors;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.base.base.AccessorKey;
import org.drools.base.base.AccessorKey.AccessorType;
import org.drools.base.base.BaseClassFieldReader;
import org.drools.core.base.BaseClassFieldWriter;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.base.base.ClassFieldInspector;
import org.drools.base.base.ClassObjectType;
import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.base.ReadAccessorSupplier;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.rule.accessor.AcceptsReadAccessor;
import org.drools.base.base.ClassWireable;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.base.ObjectType;
import org.drools.base.rule.accessor.WriteAccessor;
import org.kie.api.definition.type.FactField;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class ClassFieldAccessorStore implements ReadAccessorSupplier, Externalizable {

    private static final long serialVersionUID = 510l;

    private Map<AccessorKey, BaseLookupEntry> lookup = new ConcurrentHashMap<>();

    private ClassFieldAccessorCache cache;

    /**
     * This field is just there to assist in testing
     */
    private boolean                           eagerWire = true;

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( lookup );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        lookup = (Map<AccessorKey, BaseLookupEntry>) in.readObject();
    }

    public void setEagerWire(boolean eagerWire) {
        this.eagerWire = eagerWire;
    }

    public void setClassFieldAccessorCache(ClassFieldAccessorCache cache) {
        this.cache = cache;
    }

    public ClassFieldReader getReader(Class<?> cls, String fieldName) {
        return getReader( cls.getName(),
                          fieldName,
                          null,
                          AccessorKey.AccessorType.FieldAccessor );
    }

    public ClassFieldReader getReader(final String className,
                                      final String fieldName,
                                      final AcceptsReadAccessor target) {
        return getReader( className,
                          fieldName,
                          target,
                          AccessorKey.AccessorType.FieldAccessor );
    }

    public synchronized ClassFieldReader getReader(final String className,
                                                   String fieldName,
                                                   final AcceptsReadAccessor target,
                                                   final AccessorKey.AccessorType accessorType) {
        AccessorKey key = new AccessorKey( className,
                                           fieldName,
                                           accessorType );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );

        boolean exists = entry != null;
        if ( !exists ) {
            entry = new FieldLookupEntry( new ClassFieldReader( className,
                                                                fieldName ) );
        }

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
            ClassFieldReader reader = (ClassFieldReader) entry.getClassFieldReader();
            if ( ! reader.hasReadAccessor() ) {
                return null;
            }
        }

        if ( target != null ) {
            target.setReadAccessor( entry.getClassFieldReader() );
        }

        if ( !exists ) {
            // we delay the key writing as we only want to do it if the wiring was successful
            this.lookup.put( key,
                             entry );
        }

        return ( ClassFieldReader ) entry.getClassFieldReader();
    }

    @Override
    public ClassFieldReader getReader(AccessorKey key) {
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );
        return entry != null ? ( ClassFieldReader ) entry.getClassFieldReader() : null;
    }

    public ReadAccessor getMVELReader(final String pkgName,
                                                    final String className,
                                                    final String expr,
                                                    final boolean typesafe, 
                                                    Class<?> returnType) {
        AccessorKey key = new AccessorKey( pkgName + className, expr, AccessorKey.AccessorType.FieldAccessor );

        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.computeIfAbsent( key, k ->
            new FieldLookupEntry( getReadAcessor( className, expr, typesafe, returnType ) ) );

        return entry.getClassFieldReader();
    }
    
    public static ReadAccessor getReadAcessor(String className, String expr, boolean typesafe, Class<?> returnType) {
        return CoreComponentsBuilder.get().getReadAcessor(className, expr, typesafe, returnType);
    }

    public ClassFieldAccessor getAccessor(Class<?> cls, String fieldName) {
        return getAccessor( cls.getName(), fieldName );
    }

    public ClassFieldAccessor getAccessor(final String className,
                                          final String fieldName) {
        AccessorKey key = new AccessorKey( className,
                                           fieldName,
                                           AccessorKey.AccessorType.FieldAccessor );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.computeIfAbsent( key, k ->
                new FieldLookupEntry( new ClassFieldReader( className,
                                                            fieldName ),
                                      new ClassFieldWriter( className,
                                                            fieldName ) ) );

        ClassFieldAccessor accessor = new ClassFieldAccessor( (ClassFieldReader) entry.getClassFieldReader(),
                                                              entry.getClassFieldWriter() );

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
            wire( entry.getClassFieldWriter() );
        }

        return accessor;
    }

    public ObjectType wireObjectType(ObjectType objectType, AcceptsClassObjectType target) {
        if (!(objectType instanceof ClassObjectType)) {
            return objectType;
        }

        AccessorKey key = new AccessorKey( objectType.getClassName(),
                                           objectType.isEvent() ? "$$DROOLS__isEvent__" : null,
                                           AccessorKey.AccessorType.ClassObjectType );

        ClassObjectTypeLookupEntry entry = (ClassObjectTypeLookupEntry) this.lookup.computeIfAbsent( key, k ->
                new ClassObjectTypeLookupEntry( cache.getClassObjectType( (ClassObjectType) objectType, false ) ) );

        if ( target != null ) {
            target.setClassObjectType( entry.getClassObjectType() );
        }

        return entry.getClassObjectType();
    }

    public void removeType(TypeDeclaration type) {
        lookup.remove(new AccessorKey( type.getTypeClassName(), null, AccessorKey.AccessorType.ClassObjectType ));
        for (FactField field : type.getTypeClassDef().getFields()) {
            lookup.remove(new AccessorKey( type.getTypeClassName(), field.getName(), AccessorKey.AccessorType.FieldAccessor ));
        }
    }

    public void removeClass(Class<?> clazz) {
        lookup.remove(new AccessorKey( clazz.getName(), null, AccessorKey.AccessorType.ClassObjectType ));
        for (Field field : clazz.getDeclaredFields()) {
            lookup.remove(new AccessorKey( clazz.getName(), field.getName(), AccessorKey.AccessorType.FieldAccessor ));
        }
    }

    public void merge(ClassFieldAccessorStore other) {
        for ( Entry<AccessorKey, BaseLookupEntry> entry : other.lookup.entrySet() ) {
            switch ( entry.getValue().getAccessorType() ) {
                case FieldAccessor : {
                    FieldLookupEntry lookupEntry = (FieldLookupEntry) this.lookup.computeIfAbsent( entry.getKey(), e -> entry.getValue() );
                    // wire up ClassFieldReaders
                    if (lookupEntry.getClassFieldReader() != null ) {
                        ReadAccessor reader = ((FieldLookupEntry)entry.getValue()).getClassFieldReader();
                        BaseClassFieldReader accessor = wire(reader);
                        if (other.cache != null && reader instanceof ClassFieldReader) {
                            other.cache.setReadAcessor( ((ClassFieldReader) reader).getClassName(), ((ClassFieldReader) reader).getFieldName(), accessor );
                        }
                    }
                    if (lookupEntry.getClassFieldWriter() != null) {
                        ClassFieldWriter writer = ((FieldLookupEntry)entry.getValue()).getClassFieldWriter();
                        BaseClassFieldWriter accessor = wire(writer);
                        if (other.cache != null) {
                            other.cache.setWriteAcessor( writer.getClassName(), writer.getFieldName(), accessor );
                        }
                    }
                    break;
                }

                case ClassObjectType : {
                    if (!this.lookup.containsKey( entry.getKey() )) {
                        this.lookup.put( entry.getKey(), getBaseLookupEntry( entry ) );
                    }
                }
            }
        }
    }

    private BaseLookupEntry getBaseLookupEntry( Entry<AccessorKey, BaseLookupEntry> entry ) {
        ClassObjectType oldObjectType = (( ClassObjectTypeLookupEntry ) entry.getValue()).getClassObjectType();
        ClassObjectType newObjectType = cache.getClassObjectType( oldObjectType, true );
        oldObjectType.setClassType( newObjectType.getClassType() );
        return new ClassObjectTypeLookupEntry( newObjectType );
    }

    public void wire() {
        for ( Entry<AccessorKey, BaseLookupEntry> entry : lookup.entrySet() ) {
            switch ( entry.getValue().getAccessorType() ) {
                case FieldAccessor : {
                    ReadAccessor reader = ((FieldLookupEntry) entry.getValue()).getClassFieldReader();
                    if ( reader != null ) {
                        wire( reader );
                    }

                    ClassFieldWriter writer = ((FieldLookupEntry) entry.getValue()).getClassFieldWriter();
                    if ( writer != null ) {
                        wire( writer );
                    }
                    break;
                }
                case ClassObjectType : {
                    ClassObjectType classObjectType = ((ClassObjectTypeLookupEntry) entry.getValue()).getClassObjectType();
                    wire( classObjectType );
                    break;
                }
            }
        }
    }

    public BaseClassFieldReader wire(ReadAccessor reader) {
        if ( reader  instanceof ClassFieldReader ) {
            ReadAccessor accessor = cache.getReadAccessor( ((ClassFieldReader) reader).getClassName(), ((ClassFieldReader) reader).getFieldName() );
            ((ClassFieldReader)reader).setReadAccessor( accessor );
            return (BaseClassFieldReader) accessor;
        }
        return null;
    }

    public Class<?> getFieldType(Class<?> clazz, String fieldName) {
        ClassFieldAccessorCache.CacheEntry cache = this.cache.getCacheEntry(clazz);
        ClassFieldInspector inspector;
        try {
            inspector = getClassFieldInspector(clazz, cache);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Class<?> fieldType = inspector.getFieldType(fieldName);
        if (fieldType == null && fieldName.length() > 1 && Character.isLowerCase(fieldName.charAt(0)) && Character.isUpperCase(fieldName.charAt(1))) {
            String altFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            fieldType = inspector.getFieldType(altFieldName);
        }
        return fieldType;
    }

    public static ClassFieldInspector getClassFieldInspector( final Class<?> clazz, ClassFieldAccessorCache.CacheEntry cache ) throws IOException {
        Map<Class< ? >, ClassFieldInspector> inspectors = cache.getInspectors();
        ClassFieldInspector inspector = inspectors.get( clazz );
        if ( inspector == null ) {
            inspector = CoreComponentsBuilder.get().createClassFieldInspector( clazz );
            inspectors.put( clazz, inspector );
        }
        return inspector;
    }

    public BaseClassFieldWriter wire(ClassFieldWriter writer) {
        WriteAccessor accessor = cache.getWriteAccessor( writer.getClassName(), writer.getFieldName() );
        writer.setWriteAccessor( accessor );
        return (BaseClassFieldWriter) accessor;
    }

    public void wire( ClassWireable wireable ) {
        try {
            if ( wireable.getClassType() == null || ! wireable.getClassType().isPrimitive() ) {
                Class<?> cls = this.cache.getClassLoader().loadClass( wireable.getClassName() );
                wireable.wire( cls );
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Unable to load ClassObjectType class '" + wireable.getClassName() + "'" );
        }
    }

    public Collection<KnowledgeBuilderResult> getWiringResults( Class<?> klass, String fieldName ) {
        if ( cache == null ) {
            return Collections.EMPTY_LIST;
        }
        Map<Class<?>, ClassFieldInspector> inspectors = cache.getCacheEntry( klass ).getInspectors();
        return inspectors.containsKey( klass ) ? inspectors.get( klass ).getInspectionResults( fieldName ) : Collections.EMPTY_LIST;
    }

    public interface BaseLookupEntry extends Externalizable {
        AccessorKey.AccessorType getAccessorType();
    }

    public static class ClassObjectTypeLookupEntry implements BaseLookupEntry {
        ClassObjectType classObjectType;

        public ClassObjectTypeLookupEntry() {
            super();
        }

        public ClassObjectTypeLookupEntry(ClassObjectType classObjectType) {
            super();
            this.classObjectType = classObjectType;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( classObjectType );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            classObjectType = (ClassObjectType) in.readObject();
        }

        public ClassObjectType getClassObjectType() {
            return classObjectType;
        }

        public void setClassObjectType(ClassObjectType classObjectType) {
            this.classObjectType = classObjectType;
        }

        public AccessorType getAccessorType() {
            return AccessorKey.AccessorType.ClassObjectType;
        }

    }

    public static class FieldLookupEntry implements BaseLookupEntry {
        private ReadAccessor reader;
        private ClassFieldWriter writer;

        public FieldLookupEntry() { }

        public FieldLookupEntry(ReadAccessor reader) {
            this( reader, null );
        }

        public FieldLookupEntry(ReadAccessor reader,
                                ClassFieldWriter writer) {
            this.writer = writer;
            this.reader = reader;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( reader );
            out.writeObject( writer );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            reader = (ReadAccessor) in.readObject();
            writer = (ClassFieldWriter) in.readObject();
        }

        public ReadAccessor getClassFieldReader() {
            return reader;
        }

        public ClassFieldWriter getClassFieldWriter() {
            return this.writer;
        }

        public AccessorType getAccessorType() {
            return AccessorKey.AccessorType.FieldAccessor;
        }
    }
}
