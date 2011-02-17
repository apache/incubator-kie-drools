/**
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

package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.RuntimeDroolsException;
import org.drools.base.AccessorKey.AccessorType;
import org.drools.spi.Acceptor;
import org.drools.spi.AcceptsClassObjectType;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.AcceptsWriteAccessor;

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

    public ClassFieldWriter getWriter(Class cls,
                                      String fieldName,
                                      ClassLoader classLoader) {
        return getWriter( cls.getName(),
                          fieldName,
                          null );
    }

    public ClassFieldReader getReader(Class cls,
                                      String fieldName,
                                      ClassLoader classLoader) {
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
                                                   final String fieldName,
                                                   final AcceptsReadAccessor target,
                                                   final AccessorKey.AccessorType accessorType) {
        AccessorKey key = new AccessorKey( className,
                                           fieldName,
                                           accessorType );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );
        
        boolean exists = true;
        if ( entry == null ) {
            exists = false;
            entry = new FieldLookupEntry( new ClassFieldReader( className,
                                                                fieldName ) );
        }

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
        }        
        

        if ( target != null ) {
            target.setReadAccessor( entry.getClassFieldReader() );
            entry.addAccessorTarget( target );
        }
        
        if( exists == false ) {
            // we delay the key writing as we only want to do it if the wiring was successful
            this.lookup.put( key,
                             entry );   
        }

        return entry.getClassFieldReader();
    }

    public synchronized ClassFieldWriter getWriter(final String className,
                                                   final String fieldName,
                                                   final AcceptsWriteAccessor target) {
        AccessorKey key = new AccessorKey( className,
                                           fieldName,
                                           AccessorKey.AccessorType.FieldAccessor );
        FieldLookupEntry entry = (FieldLookupEntry) this.lookup.get( key );
        boolean exists = true;
        if ( entry == null ) {
            exists = false;
            entry = new FieldLookupEntry( new ClassFieldWriter( className,
                                                                fieldName ) );
        }

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
        }

        if ( target != null ) {
            target.setWriteAccessor( entry.getClassFieldWriter() );
            entry.addAccessorTarget( target );
        }

        if( exists == false ) {
            // we delay the key writing as we only want to do it if the wiring was successful
            this.lookup.put( key,
                             entry );   
        }        
        
        return entry.getClassFieldWriter();
    }

    public ClassFieldAccessor getAccessor(Class cls,
                                          String fieldName,
                                          ClassLoader classLoader) {
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

        ClassFieldAccessor accessor = new ClassFieldAccessor( entry.getClassFieldReader(),
                                                              entry.getClassFieldWriter() );

        entry.addAccessorTarget( accessor );

        if ( this.eagerWire ) {
            wire( entry.getClassFieldReader() );
            wire( entry.getClassFieldWriter() );
        }

        return accessor;
    }

    //    public PatternExtractor getObjectAccessor(final Class cls,
    //                                              final String identifier,
    //                                              final Declaration declaration) {
    //        AccessorKey key = new AccessorKey( cls.getName(),
    //                                           identifier, // we are re-using the fieldName as a global identifier
    //                                           AccessorKey.AccessorType.GlobalAccessor );
    //
    //        ObjectExtractorLookupEntry entry = (ObjectExtractorLookupEntry) this.lookup.get( key );
    //        if ( entry == null ) {
    //            PatternExtractor extractor = (PatternExtractor) declaration.getExtractor();
    //            entry = new ObjectExtractorLookupEntry( extractor );
    //            this.lookup.put( key,
    //                             entry );
    //        }
    //
    //        entry.addAccessorTarget( declaration );
    //
    //        // there is no wiring here as the GlobalExtractor already references the class, 
    //        // although we will need to re-wire on serialisation 
    //
    //        return entry.getObjectExtractor();
    //    }
    //
    //    public GlobalExtractor getGlobalAccessor(final ClassObjectType classObjectType,
    //                                             final String identifier,
    //                                             final Declaration declaration) {
    //        AccessorKey key = new AccessorKey( classObjectType.getClassType().getName(),
    //                                           identifier, // we are re-using the fieldName as a global identifier
    //                                           AccessorKey.AccessorType.GlobalAccessor );
    //
    //        GlobalExtractorLookupEntry entry = (GlobalExtractorLookupEntry) this.lookup.get( key );
    //        if ( entry == null ) {
    //            entry = new GlobalExtractorLookupEntry( new GlobalExtractor( identifier,
    //                                                                         classObjectType ) );
    //            this.lookup.put( key,
    //                             entry );
    //        }
    //
    //        entry.addAccessorTarget( declaration );
    //        declaration.setReadAccessor( entry.getGlobalExtractor() );
    //
    //        // there is no wiring here as the GlobalExtractor already references the class, 
    //        // although we will need to re-wire on serialisation 
    //
    //        return entry.getGlobalExtractor();
    //    }

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
            entry = new ClassObjectTypeLookupEntry(  cache.getClassObjectType( objectType ) );
            this.lookup.put( key,
                             entry );
        }

        if ( target != null ) {
            entry.addAccessorTarget( target );
            target.setClassObjectType( entry.getClassObjectType() );
        }

        return entry.getClassObjectType();
    }

    public void merge(ClassFieldAccessorStore other) {
        for ( Entry<AccessorKey, BaseLookupEntry> entry : other.lookup.entrySet() ) {

            switch ( entry.getValue().getAccessorType() ) {
                case FieldAccessor : {
                    FieldLookupEntry lookupEntry = (FieldLookupEntry) this.lookup.get( entry.getKey() );
                    if ( lookupEntry == null ) {
                        lookupEntry = (FieldLookupEntry) entry.getValue();
                        this.lookup.put( entry.getKey(),
                                         lookupEntry );
                        // wire up ClassFieldReaders
                        if ( lookupEntry.getClassFieldReader() != null ) {
                            wire( lookupEntry.getClassFieldReader() );
                        }

                        if ( lookupEntry.getClassFieldWriter() != null ) {
                            wire( lookupEntry.getClassFieldWriter() );
                        }
                    } else {
                        // iterate through new targets adding them and wiring them up
                        // to the existing ClassFieldReader, no need to wire generated accessor
                        // as we know it already exists
                        for ( Acceptor target : entry.getValue().getAccessorTargets() ) {
                            if ( target instanceof AcceptsReadAccessor ) {
                                ((AcceptsReadAccessor) target).setReadAccessor( lookupEntry.getClassFieldReader() );
                            } else if ( target instanceof AcceptsWriteAccessor ) {
                                ((AcceptsWriteAccessor) target).setWriteAccessor( lookupEntry.getClassFieldWriter() );
                            }
                            lookupEntry.addAccessorTarget( target );
                        }
                        if (lookupEntry.getClassFieldReader() != null) {
                            wire(((FieldLookupEntry)entry.getValue()).getClassFieldReader());
                        }
                        if (lookupEntry.getClassFieldWriter() != null) {
                            wire(((FieldLookupEntry)entry.getValue()).getClassFieldWriter());
                        }                        
                    }
                    break;
                }

                case ClassObjectType : {
                    ClassObjectTypeLookupEntry lookupEntry = (ClassObjectTypeLookupEntry) this.lookup.get( entry.getKey() );
                    if ( lookupEntry == null ) {
                                                // Create new entry with correct ClassObjectType and targets
                        lookupEntry = new ClassObjectTypeLookupEntry(  cache.getClassObjectType( ((ClassObjectTypeLookupEntry) entry.getValue()).getClassObjectType() ) );                        
                        
                        this.lookup.put( entry.getKey(),
                                         lookupEntry );
                                               
                    } 
                    
                    for ( Acceptor target : entry.getValue().getAccessorTargets() ) {
                        ((AcceptsClassObjectType) target).setClassObjectType( lookupEntry.getClassObjectType() );                            
                        lookupEntry.addAccessorTarget( target );
                    }
                }

                    //                case ObjectAccessor : {
                    //                    ObjectExtractorLookupEntry lookupEntry = ( ObjectExtractorLookupEntry ) this.lookup.get( entry.getKey() );
                    //                    if ( lookupEntry == null ) {
                    //                        lookupEntry = ( ObjectExtractorLookupEntry )  entry.getValue();
                    //                        this.lookup.put( entry.getKey(),
                    //                                         lookupEntry );
                    //                        wire( lookupEntry.getObjectExtractor() );                       
                    //                    } else {
                    //                        for ( Acceptor target : entry.getValue().getAccessorTargets() ) {
                    //                            ((Declaration)target).setReadAccessor( lookupEntry.getObjectExtractor() );
                    //                            lookupEntry.addAccessorTarget( target );
                    //                        }
                    //                    }
                    //                    break;
                    //                }
                    //                case GlobalAccessor : {
                    //                    GlobalExtractorLookupEntry lookupEntry = ( GlobalExtractorLookupEntry ) this.lookup.get( entry.getKey() );
                    //                    if ( lookupEntry == null ) {
                    //                        lookupEntry = ( GlobalExtractorLookupEntry )  entry.getValue();
                    //                        this.lookup.put( entry.getKey(),
                    //                                         lookupEntry );
                    //                        wire( lookupEntry.getGlobalExtractor() );  
                    //                    } else {
                    //                        for ( Acceptor target : entry.getValue().getAccessorTargets() ) {
                    //                            ((Declaration)target).setReadAccessor( lookupEntry.getGlobalExtractor() );
                    //                            lookupEntry.addAccessorTarget( target );
                    //                        }                        
                    //                    }                    
                    //                    break;
                    //                }
            }
        }
    }

    public void wire() {
        for ( Entry<AccessorKey, BaseLookupEntry> entry : lookup.entrySet() ) {
            switch ( entry.getValue().getAccessorType() ) {
                case FieldAccessor : {
                    ClassFieldReader reader = ((FieldLookupEntry) entry.getValue()).getClassFieldReader();
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

                    //                case ObjectAccessor : {
                    //                    PatternExtractor reader = ((ObjectExtractorLookupEntry) entry.getValue()).getObjectExtractor();
                    //                    wire( reader );
                    //                    break;
                    //                }
                    //                case GlobalAccessor : {
                    //                    GlobalExtractor reader = ((GlobalExtractorLookupEntry) entry.getValue()).getGlobalExtractor();
                    //                    wire( reader );
                    //                    break;
                    //                }
            }
        }
    }

    public void wire(ClassFieldReader reader) {
        reader.setReadAccessor( cache.getReadAcessor( reader ) );
    }

    public void wire(ClassFieldWriter writer) {
        writer.setWriteAccessor( cache.getWriteAcessor( writer ) );
    }

    //    public void wire(PatternExtractor reader) {
    //        ObjectType objectType = reader.getObjectType();
    //
    //        if ( objectType instanceof ClassObjectType ) {
    //            ClassObjectType cot = (ClassObjectType) objectType;
    //            try {
    //                Class cls = this.cache.getClassLoader().loadClass( cot.getClassName() );
    //                cot.setClassType( cls );
    //            } catch ( ClassNotFoundException e ) {
    //                throw new RuntimeDroolsException( "Unable to load ClassObjectType class '" + cot.getClassName() + "'" );
    //            }
    //        }
    //    }    

    public void wire(ClassObjectType objectType) {
        try {
            Class cls = this.cache.getClassLoader().loadClass( objectType.getClassName() );
            objectType.setClassType( cls );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "Unable to load ClassObjectType class '" + objectType.getClassName() + "'" );
        }
    }

    public static abstract class BaseLookupEntry
        implements
        Externalizable {
        // we use an identity hashmap to avoid hashcode/equals being called on stored targets
        private Map<Acceptor, Object> accessorTargets = Collections.<Acceptor, Object> emptyMap();

        public BaseLookupEntry() {

        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( accessorTargets );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            accessorTargets = (Map<Acceptor, Object>) in.readObject();

        }

        public Set<Acceptor> getAccessorTargets() {
            return accessorTargets.keySet();
        }

        public void addAccessorTarget(Acceptor target) {
            if ( this.accessorTargets == Collections.EMPTY_MAP ) {
                this.accessorTargets = new IdentityHashMap<Acceptor, Object>();
            }

            this.accessorTargets.put( target, null );
        }
        
        public void addAccessorTargets(Set<Acceptor> targets) {
            if ( this.accessorTargets == Collections.EMPTY_MAP ) {
                this.accessorTargets = new IdentityHashMap<Acceptor, Object>( );
            }
            
            for ( Acceptor target : targets ) {
                this.accessorTargets.put( target, null );
            }
        }

        public void removeTarget(Acceptor target) {
            this.accessorTargets.remove( target );
        }

        public abstract AccessorKey.AccessorType getAccessorType();

    }

    public static class ClassObjectTypeLookupEntry extends BaseLookupEntry {
        ClassObjectType classObjectType;

        public ClassObjectTypeLookupEntry() {
            super();
        }

        public ClassObjectTypeLookupEntry(ClassObjectType classObjectType) {
            super();
            this.classObjectType = classObjectType;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeObject( classObjectType );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
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

    //
    //    public static class GlobalExtractorLookupEntry extends BaseLookupEntry {
    //        GlobalExtractor globalExtractor;
    //
    //        public GlobalExtractorLookupEntry() {
    //            super();
    //        }
    //
    //        public GlobalExtractorLookupEntry(GlobalExtractor globalExtractor) {
    //            super();
    //            this.globalExtractor = globalExtractor;
    //        }
    //
    //        public void writeExternal(ObjectOutput out) throws IOException {
    //            super.writeExternal( out );
    //            out.writeObject( globalExtractor );
    //        }
    //
    //        public void readExternal(ObjectInput in) throws IOException,
    //                                                ClassNotFoundException {
    //            super.readExternal( in );
    //            globalExtractor = (GlobalExtractor) in.readObject();
    //        }
    //
    //        public GlobalExtractor getGlobalExtractor() {
    //            return globalExtractor;
    //        }
    //
    //        public AccessorType getAccessorType() {
    //            return AccessorKey.AccessorType.GlobalAccessor;
    //        }
    //
    //    }
    //
    //    public static class ObjectExtractorLookupEntry extends BaseLookupEntry {
    //        PatternExtractor patternExtractor;
    //
    //        public ObjectExtractorLookupEntry() {
    //            super();
    //        }
    //
    //        public ObjectExtractorLookupEntry(PatternExtractor patternExtractor) {
    //            super();
    //            this.patternExtractor = patternExtractor;
    //        }
    //
    //        public void writeExternal(ObjectOutput out) throws IOException {
    //            super.writeExternal( out );
    //            out.writeObject( patternExtractor );
    //        }
    //
    //        public void readExternal(ObjectInput in) throws IOException,
    //                                                ClassNotFoundException {
    //            super.readExternal( in );
    //            patternExtractor = (PatternExtractor) in.readObject();
    //        }
    //
    //        public PatternExtractor getObjectExtractor() {
    //            return patternExtractor;
    //        }
    //
    //        public AccessorType getAccessorType() {
    //            return AccessorKey.AccessorType.ObjectAccessor;
    //        }
    //    }
    //
    public static class FieldLookupEntry extends BaseLookupEntry {
        private ClassFieldReader reader;
        private ClassFieldWriter writer;

        public FieldLookupEntry() {

        }

        public FieldLookupEntry(ClassFieldReader reader) {
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
            super.writeExternal( out );
            out.writeObject( reader );
            out.writeObject( writer );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            reader = (ClassFieldReader) in.readObject();
            writer = (ClassFieldWriter) in.readObject();
        }

        public ClassFieldReader getClassFieldReader() {
            return reader;
        }

        public ClassFieldWriter getClassFieldWriter() {
            return this.writer;
        }

        public AccessorType getAccessorType() {
            return AccessorKey.AccessorType.FieldAccessor;
        }
    }

    //    public static class LookupEntry
    //        implements
    //        Externalizable {
    //        private ClassFieldReader     reader;
    //        private ClassFieldWriter     writer;
    //        private Set<AcceptsAccessor> accessorTargets = Collections.<AcceptsAccessor> emptySet();
    //
    //        public LookupEntry() {
    //
    //        }
    //
    //        public void writeExternal(ObjectOutput out) throws IOException {
    //            out.writeObject( reader );
    //            out.writeObject( writer );
    //            out.writeObject( accessorTargets );
    //        }
    //
    //        public void readExternal(ObjectInput in) throws IOException,
    //                                                ClassNotFoundException {
    //            reader = (ClassFieldReader) in.readObject();
    //            writer = (ClassFieldWriter) in.readObject();
    //            accessorTargets = (Set<AcceptsAccessor>) in.readObject();
    //
    //        }
    //
    //        public LookupEntry(ClassFieldReader reader) {
    //            this.reader = reader;
    //        }
    //
    //        public LookupEntry(ClassFieldWriter writer) {
    //            this.writer = writer;
    //        }
    //
    //        public LookupEntry(ClassFieldReader reader,
    //                           ClassFieldWriter writer) {
    //            this.writer = writer;
    //            this.reader = reader;
    //        }
    //
    //        public ClassFieldReader getClassFieldReader() {
    //            return reader;
    //        }
    //
    //        public ClassFieldWriter getClassFieldWriter() {
    //            return this.writer;
    //        }
    //
    //        public Set<AcceptsAccessor> getAccessorTargets() {
    //            return accessorTargets;
    //        }
    //
    //        public void addAccessorTarget(AcceptsAccessor target) {
    //            if ( this.accessorTargets == Collections.EMPTY_SET ) {
    //                this.accessorTargets = new HashSet<AcceptsAccessor>();
    //            }
    //
    //            this.accessorTargets.add( target );
    //        }
    //
    //        public void removeConstraint(Constraint constraint) {
    //            this.accessorTargets.remove( constraint );
    //        }
    //    }
}
