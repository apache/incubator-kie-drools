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

import org.drools.core.base.AccessorKey.AccessorType;
import org.drools.core.base.extractors.MVELDateClassFieldReader;
import org.drools.core.base.extractors.MVELNumberClassFieldReader;
import org.drools.core.base.extractors.MVELObjectClassFieldReader;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.api.definition.type.FactField;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClassFieldAccessorStore
    implements
    Externalizable {

    private static final long serialVersionUID = 510l;

    private Map<AccessorKey, BaseLookupEntry> lookup;

    private ClassFieldAccessorCache           cache;

    /**
     * This field is just there to assist in testing
     */
    private boolean                           eagerWire = true;

    public ClassFieldAccessorStore() {
        lookup = new HashMap<AccessorKey, BaseLookupEntry>();
    }

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

    public ClassFieldReader getReader(Class cls,
                                      String fieldName) {
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


    public InternalReadAccessor getMVELReader(final String pkgName,
                                                    final String className,
                                                    final String expr,
                                                    final boolean typesafe, 
                                                    Class returnType) {
        AccessorKey key = new AccessorKey( pkgName + className,
                                           expr,
                                           AccessorKey.AccessorType.FieldAccessor );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );
        if ( entry == null ) {
            InternalReadAccessor reader  = getReadAcessor( className, expr, typesafe, returnType );
            entry = new FieldLookupEntry( reader );
            this.lookup.put( key,
                             entry );
        }

        return entry.getClassFieldReader();
    }
    
    public static InternalReadAccessor getReadAcessor(String className, String expr, boolean typesafe, Class returnType) {
        if (Number.class.isAssignableFrom( returnType ) ||
            ( returnType == byte.class ||
              returnType == short.class ||
              returnType == int.class ||
              returnType == long.class ||
              returnType == float.class ||
              returnType == double.class ) ) {            
            return new MVELNumberClassFieldReader( className, expr, typesafe );            
        } else if (  Date.class.isAssignableFrom( returnType ) ) {
          return new MVELDateClassFieldReader( className, expr, typesafe );
        } else {
          return new MVELObjectClassFieldReader( className, expr, typesafe );
        }       
    }     

    public ClassFieldAccessor getAccessor(Class cls,
                                          String fieldName) {
        return getAccessor( cls.getName(),
                            fieldName );
    }

    public ClassFieldAccessor getAccessor(final String className,
                                          final String fieldName) {
        AccessorKey key = new AccessorKey( className,
                                           fieldName,
                                           AccessorKey.AccessorType.FieldAccessor );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );
        if ( entry == null ) {
            entry = new FieldLookupEntry( new ClassFieldReader( className,
                                                                fieldName ),
                                          new ClassFieldWriter( className,
                                                                fieldName ) );
            this.lookup.put( key,
                             entry );
        }

        ClassFieldAccessor accessor = new ClassFieldAccessor( (ClassFieldReader) entry.getClassFieldReader(),
                                                              entry.getClassFieldWriter() );

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
            wire( entry.getClassFieldWriter() );
        }

        return accessor;
    }

    public ClassObjectType getClassObjectType(final ClassObjectType objectType,
                                              final AcceptsClassObjectType target) {
        return getClassObjectType( objectType,
                                   objectType.isEvent(),
                                   target );
    }

    public ClassObjectType getClassObjectType(final ClassObjectType objectType,
                                              final boolean isEvent,
                                              final AcceptsClassObjectType target) {
        AccessorKey key = new AccessorKey( objectType.getClassName(),
                                           isEvent ? "$$DROOLS__isEvent__" : null,
                                           AccessorKey.AccessorType.ClassObjectType );

        ClassObjectTypeLookupEntry entry = (ClassObjectTypeLookupEntry) this.lookup.get( key );
        if ( entry == null ) {
            entry = new ClassObjectTypeLookupEntry( cache.getClassObjectType( objectType, false ) );
            this.lookup.put( key,
                             entry );
        }

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
                    FieldLookupEntry lookupEntry = (FieldLookupEntry) this.lookup.get( entry.getKey() );
                    if ( lookupEntry == null ) {
                        lookupEntry = (FieldLookupEntry) entry.getValue();
                        this.lookup.put( entry.getKey(), lookupEntry );
                    }
                    // wire up ClassFieldReaders
                    if (lookupEntry.getClassFieldReader() != null ) {
                        InternalReadAccessor reader = ((FieldLookupEntry)entry.getValue()).getClassFieldReader();
                        BaseClassFieldReader accessor = wire(reader);
                        if (other.cache != null && reader instanceof ClassFieldReader) {
                            other.cache.setReadAcessor( (ClassFieldReader) reader, accessor );
                        }
                    }
                    if (lookupEntry.getClassFieldWriter() != null) {
                        ClassFieldWriter writer = ((FieldLookupEntry)entry.getValue()).getClassFieldWriter();
                        BaseClassFieldWriter accessor = wire(writer);
                        if (other.cache != null) {
                            other.cache.setWriteAcessor( writer, accessor );
                        }
                    }
                    break;
                }

                case ClassObjectType : {
                    ClassObjectTypeLookupEntry lookupEntry = (ClassObjectTypeLookupEntry) this.lookup.get( entry.getKey() );
                    if ( lookupEntry == null ) {
                        // Create new entry with correct ClassObjectType and targets
                        ClassObjectType oldObjectType = ((ClassObjectTypeLookupEntry) entry.getValue()).getClassObjectType();
                        ClassObjectType newObjectType = cache.getClassObjectType( oldObjectType, true );
                        this.lookup.put( entry.getKey(), new ClassObjectTypeLookupEntry( newObjectType ) );
                        // also rewire the class of the old ClassObjectType in case it is still in use
                        oldObjectType.setClassType( newObjectType.getClassType() );
                    }
                }
            }
        }
    }

    public void wire() {
        for ( Entry<AccessorKey, BaseLookupEntry> entry : lookup.entrySet() ) {
            switch ( entry.getValue().getAccessorType() ) {
                case FieldAccessor : {
                    InternalReadAccessor reader = ((FieldLookupEntry) entry.getValue()).getClassFieldReader();
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

    public BaseClassFieldReader wire(InternalReadAccessor reader) {
        if ( reader  instanceof ClassFieldReader ) {
            BaseClassFieldReader accessor = cache.getReadAcessor( (ClassFieldReader) reader );
            ((ClassFieldReader)reader).setReadAccessor( accessor );
            return accessor;
        }
        return null;
    }

    public Class<?> getFieldType(Class<?> clazz, String fieldName) {
        return ClassFieldAccessorFactory.getFieldType( clazz, fieldName, cache.getCacheEntry(clazz) );
    }

    public BaseClassFieldWriter wire(ClassFieldWriter writer) {
        BaseClassFieldWriter accessor = cache.getWriteAcessor( writer );
        writer.setWriteAccessor( accessor );
        return accessor;
    }

    public void wire( ClassWireable wireable ) {
        try {
            if ( wireable.getClassType() == null || ! wireable.getClassType().isPrimitive() ) {
                Class cls = this.cache.getClassLoader().loadClass( wireable.getClassName() );
                wireable.wire( cls );
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Unable to load ClassObjectType class '" + wireable.getClassName() + "'" );
        }
    }

    public Collection<KnowledgeBuilderResult> getWiringResults( Class klass, String fieldName ) {
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
        private InternalReadAccessor reader;
        private ClassFieldWriter writer;

        public FieldLookupEntry() {

        }

        public FieldLookupEntry(InternalReadAccessor reader) {
            this.reader = reader;
        }

        public FieldLookupEntry(ClassFieldWriter writer) {
            this.writer = writer;
        }

        public FieldLookupEntry(ClassFieldReader reader,
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
            reader = (InternalReadAccessor) in.readObject();
            writer = (ClassFieldWriter) in.readObject();
        }

        public InternalReadAccessor getClassFieldReader() {
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
