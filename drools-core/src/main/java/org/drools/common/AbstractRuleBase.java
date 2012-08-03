/*
 * Copyright 2005 JBoss Inc
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

package org.drools.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.TripleStore;
import org.drools.definition.process.Process;
import org.drools.definition.type.FactType;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleBaseEventSupport;
import org.drools.factmodel.traits.TripleStoreRegistry;
import org.drools.impl.EnvironmentFactory;
import org.drools.management.DroolsManagementAgent;
import org.drools.rule.DialectRuntimeRegistry;
import org.drools.rule.Function;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.WindowDeclaration;
import org.drools.spi.FactHandleFactory;
import org.drools.util.ClassLoaderUtil;
import org.drools.util.CompositeClassLoader;

import static org.drools.core.util.BitMaskUtil.isSet;

/**
 * Implementation of <code>RuleBase</code>.
 *
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
abstract public class AbstractRuleBase
                                      implements
                                      InternalRuleBase,
                                      Externalizable {

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    private String                                        id;

    private AtomicInteger                                 workingMemoryCounter         = new AtomicInteger( 0 );

    private RuleBaseConfiguration                         config;

    protected Map<String, Package>                        pkgs;

    private Map<String, Process>                          processes;

    private Map                                           agendaGroupRuleTotals;

    private transient CompositeClassLoader                rootClassLoader;

    /**
     * The fact handle factory.
     */
    private FactHandleFactory                             factHandleFactory;

    private transient Map<String, Class<?>>               globals;

    private final transient Queue<DialectRuntimeRegistry> reloadPackageCompilationData = new ConcurrentLinkedQueue<DialectRuntimeRegistry>();

    private RuleBaseEventSupport                          eventSupport                 = new RuleBaseEventSupport( this );

    private transient ObjectHashSet                       statefulSessions;

    // lock for entire rulebase, used for dynamic updates
    private final UpgradableReentrantReadWriteLock        lock                         = new UpgradableReentrantReadWriteLock();

    /**
     * This lock is used when adding to, or reading the <field>statefulSessions</field>
     */
    private final ReentrantLock                           statefulSessionLock          = new ReentrantLock();

    private int                                           additionsSinceLock;
    private int                                           removalsSinceLock;

    private transient Map<String, TypeDeclaration>        classTypeDeclaration;

    private transient JavaDialectRuntimeData.TypeDeclarationClassLoader
                                                          declarationClassLoader;

    private List<RuleBasePartitionId>                     partitionIDs;

    private ClassFieldAccessorCache                       classFieldAccessorCache;

    /**
     * Default constructor - for Externalizable. This should never be used by a user, as it
     * will result in an invalid state for the instance.
     */
    public AbstractRuleBase() {

    }

    public int nextWorkingMemoryCounter() {
        return this.workingMemoryCounter.getAndIncrement();
    }

    public int getWorkingMemoryCounter() {
        return this.workingMemoryCounter.get();
    }

    /**
     * Construct.
     *
     * @param id The rete network.
     */
    public AbstractRuleBase(final String id,
            final RuleBaseConfiguration config,
            final FactHandleFactory factHandleFactory) {

        this.config = ( config != null ) ? config : new RuleBaseConfiguration();
        this.config.makeImmutable();
        createRulebaseId( id );
        this.factHandleFactory = factHandleFactory;

        if (this.config.isSequential()) {
            this.agendaGroupRuleTotals = new HashMap();
        }

        this.rootClassLoader = this.config.getClassLoader();
        this.rootClassLoader.addClassLoader( getClass().getClassLoader() );

        this.declarationClassLoader = new JavaDialectRuntimeData.TypeDeclarationClassLoader(
                new JavaDialectRuntimeData(),
                this.rootClassLoader
        );
        this.rootClassLoader.addClassLoader( this.declarationClassLoader );



        this.pkgs = new HashMap<String, Package>();
        this.processes = new HashMap();
        this.globals = new HashMap<String, Class<?>>();
        this.statefulSessions = new ObjectHashSet();

        this.classTypeDeclaration = new HashMap<String, TypeDeclaration>();
        this.partitionIDs = new CopyOnWriteArrayList<RuleBasePartitionId>();

        this.classFieldAccessorCache = new ClassFieldAccessorCache( this.rootClassLoader );

        this.getConfiguration().getComponentFactory().getTraitFactory().setRuleBase( this );
    }

    private void createRulebaseId( final String id ) {
        if (id != null) {
            this.id = id;
        } else {
            String key = "";
            if (config.isMBeansEnabled()) {
                DroolsManagementAgent agent = DroolsManagementAgent.getInstance();
                key = String.valueOf( agent.getNextKnowledgeBaseId() );
            }
            this.id = "default" + key;
        }
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     */
    public void writeExternal( final ObjectOutput out ) throws IOException {
        ObjectOutput droolsStream;
        boolean isDrools = out instanceof DroolsObjectOutputStream;
        ByteArrayOutputStream bytes;

        if (isDrools) {
            droolsStream = out;
            bytes = null;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream( bytes );
        }

        // must write this option first in order to properly deserialize later
        droolsStream.writeBoolean( this.config.isClassLoaderCacheEnabled() );

        droolsStream.writeObject( this.declarationClassLoader.getStore() );


        droolsStream.writeObject( this.config );
        droolsStream.writeObject( this.pkgs );

        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        droolsStream.writeObject( this.id );
        droolsStream.writeInt( this.workingMemoryCounter.get() );
        droolsStream.writeObject( this.processes );
        droolsStream.writeObject( this.agendaGroupRuleTotals );
        droolsStream.writeUTF( this.factHandleFactory.getClass().getName() );
        droolsStream.writeObject( buildGlobalMapForSerialization() );
        droolsStream.writeObject( this.partitionIDs );

        this.eventSupport.removeEventListener( RuleBaseEventListener.class );
        droolsStream.writeObject( this.eventSupport );
        if (!isDrools) {
            droolsStream.flush();
            droolsStream.close();
            bytes.close();
            out.writeObject( bytes.toByteArray() );
        }
    }

    private Map<String, String> buildGlobalMapForSerialization() {
        Map<String, String> gl = new HashMap<String, String>();
        for (Map.Entry<String, Class<?>> entry : this.globals.entrySet()) {
            gl.put( entry.getKey(),
                    entry.getValue().getName() );
        }
        return gl;
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     */
    public void readExternal( final ObjectInput in ) throws IOException,
            ClassNotFoundException {
        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        DroolsObjectInput droolsStream;
        boolean isDrools = in instanceof DroolsObjectInputStream;
        ByteArrayInputStream bytes = null;

        if (isDrools) {
            droolsStream = (DroolsObjectInput) in;
        } else {
            bytes = new ByteArrayInputStream( (byte[]) in.readObject() );
            droolsStream = new DroolsObjectInputStream( bytes );
        }

        boolean classLoaderCacheEnabled = droolsStream.readBoolean();

        this.rootClassLoader = ClassLoaderUtil.getClassLoader( new ClassLoader[]{droolsStream.getParentClassLoader()},
                                                               getClass(),
                                                               classLoaderCacheEnabled );

        droolsStream.setClassLoader( this.rootClassLoader );
        droolsStream.setRuleBase( this );

        JavaDialectRuntimeData typeStore = (JavaDialectRuntimeData) droolsStream.readObject();
        this.declarationClassLoader = new JavaDialectRuntimeData.TypeDeclarationClassLoader(
                        typeStore,
                        this.rootClassLoader
        );
        this.rootClassLoader.addClassLoader( this.declarationClassLoader );

        this.classFieldAccessorCache = new ClassFieldAccessorCache( this.rootClassLoader );

        this.config = (RuleBaseConfiguration) droolsStream.readObject();
        this.config.setClassLoader( droolsStream.getParentClassLoader() );

        this.pkgs = (Map<String, Package>) droolsStream.readObject();

        for (final Object object : this.pkgs.values()) {
            ( (Package) object ).getDialectRuntimeRegistry().onAdd( this.rootClassLoader );
        }

        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        this.id = (String) droolsStream.readObject();
        this.workingMemoryCounter.set( droolsStream.readInt() );

        this.processes = (Map) droolsStream.readObject();
        this.agendaGroupRuleTotals = (Map) droolsStream.readObject();
        Class cls = null;
        try {
            cls = droolsStream.getParentClassLoader().loadClass( droolsStream.readUTF() );
            this.factHandleFactory = (FactHandleFactory) cls.newInstance();
        } catch (InstantiationException e) {
            DroolsObjectInputStream.newInvalidClassException( cls,
                                                              e );
        } catch (IllegalAccessException e) {
            DroolsObjectInputStream.newInvalidClassException( cls,
                                                              e );
        }

        for (final Object object : this.pkgs.values()) {
            ( (Package) object ).getDialectRuntimeRegistry().onBeforeExecute();
            ( (Package) object ).getClassFieldAccessorStore().setClassFieldAccessorCache( this.classFieldAccessorCache );
            ( (Package) object ).getClassFieldAccessorStore().wire();
        }

        this.populateTypeDeclarationMaps();

        // read globals
        Map<String, String> globs = (Map<String, String>) droolsStream.readObject();
        populateGlobalsMap( globs );

        this.partitionIDs = (List<RuleBasePartitionId>) droolsStream.readObject();

        this.eventSupport = (RuleBaseEventSupport) droolsStream.readObject();
        this.eventSupport.setRuleBase( this );
        this.statefulSessions = new ObjectHashSet();

        if (!isDrools) {
            droolsStream.close();
        }

        this.getConfiguration().getComponentFactory().getTraitFactory().setRuleBase( this );
    }

    /**
     * globals class types must be re-wired after serialization
     *
     * @param globs
     * @throws ClassNotFoundException
     */
    private void populateGlobalsMap( Map<String, String> globs ) throws ClassNotFoundException {
        this.globals = new HashMap<String, Class<?>>();
        for (Map.Entry<String, String> entry : globs.entrySet()) {
            this.globals.put( entry.getKey(),
                              this.rootClassLoader.loadClass( entry.getValue() ) );
        }
    }

    /**
     * type classes must be re-wired after serialization
     *
     * @throws ClassNotFoundException
     */
    private void populateTypeDeclarationMaps() throws ClassNotFoundException {
        // FIXME: readLock
        this.classTypeDeclaration = new HashMap<String, TypeDeclaration>();
        for (Package pkg : this.pkgs.values()) {
            for (TypeDeclaration type : pkg.getTypeDeclarations().values()) {
                type.setTypeClass( this.rootClassLoader.loadClass( type.getTypeClassName() ) );
                this.classTypeDeclaration.put( type.getTypeClassName(),
                                               type );
            }
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    public RuleBaseConfiguration getConfig() {
        return config;
    }

    /**
     * @see RuleBase
     */
    public StatefulSession newStatefulSession() {
        return newStatefulSession( SessionConfiguration.getDefaultInstance(),
                                   EnvironmentFactory.newEnvironment() );
    }

    public void disposeStatefulSession( final StatefulSession statefulSession ) {
        statefulSessionLock.lock();

        try {
            this.statefulSessions.remove( statefulSession );
            for (Object listener : statefulSession.getRuleBaseUpdateListeners()) {
                this.removeEventListener( (RuleBaseEventListener) listener );
            }
        } finally {
            statefulSessionLock.unlock();
        }
    }

    /**
     * @see RuleBase
     */
    public FactHandleFactory getFactHandleFactory() {
        return this.factHandleFactory;
    }

    public FactHandleFactory newFactHandleFactory() {
        return this.factHandleFactory.newInstance();
    }

    public FactHandleFactory newFactHandleFactory( int id,
            long counter ) {
        return this.factHandleFactory.newInstance( id,
                                                   counter );
    }

    public Process[] getProcesses() {
        readLock();
        try {
            return (Process[]) this.processes.values().toArray( new Process[this.processes.size()] );
        } finally {
            readUnlock();
        }
    }

    public Package[] getPackages() {
        readLock();
        try {
            return this.pkgs.values().toArray( new Package[this.pkgs.size()] );
        } finally {
            readUnlock();
        }
    }

    // FIXME: this returns the live map!
    public Map<String, Package> getPackagesMap() {
        return this.pkgs;
    }

    public Map getGlobals() {
        return this.globals;
    }

    public Map getAgendaGroupRuleTotals() {
        return this.agendaGroupRuleTotals;
    }

    public int getAdditionsSinceLock() {
        return additionsSinceLock;
    }

    public int getRemovalsSinceLock() {
        return removalsSinceLock;
    }

    public void lock() {
        // The lock is reentrant, so we need additional magic here to skip
        // notifications for locked if this thread already has locked it.
        boolean firstLock = !this.lock.isWriteLockedByCurrentThread();
        if (firstLock) {
            this.eventSupport.fireBeforeRuleBaseLocked();
        }
        // Always lock to increase the counter
        this.lock.writeLock();
        if ( firstLock ) {
            this.additionsSinceLock = 0;
            this.removalsSinceLock = 0;
            this.eventSupport.fireAfterRuleBaseLocked();
        }
    }

    public void unlock() {
        boolean lastUnlock = this.lock.getWriteHoldCount() == 1;
        if (lastUnlock) {
            this.eventSupport.fireBeforeRuleBaseUnlocked();
        }
        this.lock.writeUnlock();
        if ( lastUnlock ) {
            this.eventSupport.fireAfterRuleBaseUnlocked();
        }
    }

    public void readLock() {
        this.lock.readLock();
    }

    public void readUnlock() {
        this.lock.readUnlock();
    }

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network. Before update network each referenced <code>WorkingMemory</code>
     * is locked.
     *
     * @param newPkgs The package to add.
     */
    public void addPackages( final Collection<Package> newPkgs ) {
        lock();
        try {
            // we need to merge all byte[] first, so that the root classloader can resolve classes
            for (Package newPkg : newPkgs) {
                newPkg.checkValidity();
                this.additionsSinceLock++;
                this.eventSupport.fireBeforePackageAdded( newPkg );

                Package pkg = this.pkgs.get( newPkg.getName() );
                if ( pkg == null ) {
                    pkg = new Package( newPkg.getName() );

                    // @TODO we really should have a single root cache
                    pkg.setClassFieldAccessorCache( this.classFieldAccessorCache );
                    pkgs.put( pkg.getName(),
                              pkg );
                }

                // first merge anything related to classloader re-wiring
                pkg.getDialectRuntimeRegistry().merge( newPkg.getDialectRuntimeRegistry(),
                                                       this.rootClassLoader,
                                                       true );
            }


            // Add all Type Declarations, this has to be done first incase packages cross reference each other during build process.
            for ( Package newPkg : newPkgs ) {
                // we have to do this before the merging, as it does some classloader resolving
                String lastType = null;
                try {
                    // Add the type declarations to the RuleBase
                    if ( newPkg.getTypeDeclarations() != null ) {
                        // add type declarations
                        for ( TypeDeclaration newDecl : newPkg.getTypeDeclarations().values() ) {
                            lastType = newDecl.getTypeClassName();


                            TypeDeclaration typeDeclaration = this.classTypeDeclaration.get( newDecl.getTypeClassName() );
                            if ( typeDeclaration == null ) {
                                String className = newDecl.getTypeClassName();

                                byte [] def = ((JavaDialectRuntimeData) newPkg.getDialectRuntimeRegistry().getDialectData( "java" )).getClassDefinition(
                                        JavaDialectRuntimeData.convertClassToResourcePath( className )
                                );

                                Class<?> definedKlass = registerAndLoadTypeDefinition( className, def );

                                if ( definedKlass == null && typeDeclaration.isNovel() ) {
                                    throw new RuntimeException( "Registering null bytes for class " + className );
                                }


                                newDecl.getTypeClassDef().setDefinedClass( definedKlass );
                                newDecl.setTypeClass( definedKlass );

                                this.classTypeDeclaration.put( className, newDecl );
                                typeDeclaration = newDecl;
                            } else {
                                Class<?> definedKlass = typeDeclaration.getTypeClass();

                                newDecl.getTypeClassDef().setDefinedClass( definedKlass );
                                newDecl.setTypeClass( definedKlass );

                                mergeTypeDeclarations( typeDeclaration,
                                                       newDecl );
                            }

                            // update existing OTNs
                            updateDependentTypes( newPkg,
                                                  typeDeclaration );
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeDroolsException(
                                                      "unable to resolve Type Declaration class '" + lastType +
                                                              "'" );
                }
            }

             // now iterate again, this time onBeforeExecute will handle any wiring or cloader re-creating that needs to be done as part of the merge
            for (Package newPkg : newPkgs) {
                Package pkg = this.pkgs.get( newPkg.getName() );

                // this needs to go here, as functions will set a java dialect to dirty
                if (newPkg.getFunctions() != null) {
                    for (Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet()) {
                        pkg.addFunction( entry.getValue() );
                    }
                }

                pkg.getDialectRuntimeRegistry().onBeforeExecute();

                // with the classloader recreated for all byte[] classes, we should now merge and wire any new accessors
                pkg.getClassFieldAccessorStore().merge( newPkg.getClassFieldAccessorStore() );
            }


            for (Package newPkg : newPkgs) {
                Package pkg = this.pkgs.get( newPkg.getName() );

                // now merge the new package into the existing one
                mergePackage( pkg,
                              newPkg );

                // add the window declarations to the kbase
                for( WindowDeclaration window : newPkg.getWindowDeclarations().values() ) {
                    addWindowDeclaration( newPkg,
                                          window );
                }

                // add entry points to the kbase
                for (String id : newPkg.getEntryPointIds()) {
                    addEntryPoint( id );
                }

                // add the rules to the RuleBase
                final Rule[] rules = newPkg.getRules();
                for (int i = 0; i < rules.length; ++i) {
                    addRule( newPkg,
                             rules[i] );
                }

                // add the flows to the RuleBase
                if ( newPkg.getRuleFlows() != null ) {
                    final Map<String, org.drools.definition.process.Process> flows = newPkg.getRuleFlows();
                    for ( org.drools.definition.process.Process process : flows.values() ) {
                        // XXX: we could take the lock inside addProcess() out, but OTOH: this is what the VM is supposed to do ...
                        addProcess( process );
                    }
                }

                this.eventSupport.fireAfterPackageAdded( newPkg );
            }
        } finally {
            unlock();
        }
    }

    public Class<?> registerAndLoadTypeDefinition( String className, byte[] def ) throws ClassNotFoundException {
        this.declarationClassLoader.addClassDefinition( className, def );
        return this.rootClassLoader.loadClass( className, true );
    }


    protected abstract void updateDependentTypes( Package newPkg,
            TypeDeclaration typeDeclaration );

    private void mergeTypeDeclarations( TypeDeclaration existingDecl,
            TypeDeclaration newDecl ) {

        existingDecl.addRedeclaration( newDecl );

        if ( ! nullSafeEquals( existingDecl.getFormat(),
                               newDecl.getFormat() ) ||
            ! nullSafeEquals( existingDecl.getObjectType(),
                              newDecl.getObjectType() ) ||
            ! nullSafeEquals( existingDecl.getTypeClassName(),
                              newDecl.getTypeClassName() ) ||
            ! nullSafeEquals( existingDecl.getTypeName(),
                              newDecl.getTypeName() ) ) {

            throw new RuntimeDroolsException( "Unable to merge Type Declaration for class '" + existingDecl.getTypeName() +
                                              "'" );

        }

        existingDecl.setDurationAttribute( mergeLeft( existingDecl.getTypeName(),
                                                      "Unable to merge @duration attribute for type declaration of class:",
                                                      existingDecl.getDurationAttribute(),
                                                      newDecl.getDurationAttribute(),
                                                      true,
                                                      false ) );

        existingDecl.setDynamic( mergeLeft( existingDecl.getTypeName(),
                                            "Unable to merge @propertyChangeSupport  (a.k.a. dynamic) attribute for type declaration of class:",
                                            existingDecl.isDynamic(),
                                            newDecl.isDynamic(),
                                            true,
                                            false ) );

        existingDecl.setPropertyReactive( mergeLeft(existingDecl.getTypeName(),
                                          "Unable to merge @propertyReactive attribute for type declaration of class:",
                                          existingDecl.isPropertyReactive(),
                                          newDecl.isPropertyReactive(),
                                          true,
                                          false) );

        existingDecl.setExpirationOffset( Math.max( existingDecl.getExpirationOffset(),
                                                    newDecl.getExpirationOffset() ) );

        if ( newDecl.getNature().equals( TypeDeclaration.Nature.DEFINITION ) ) {
            existingDecl.setNovel( mergeLeft( existingDecl.getTypeName(),
                                              "Unable to merge @novel attribute for type declaration of class:",
                                              existingDecl.isNovel(),
                                              newDecl.isNovel(),
                                              true,
                                              false ) );
        }

        if ( newDecl.getNature().equals( TypeDeclaration.Nature.DEFINITION ) || existingDecl.getResource() == null ) {
            existingDecl.setResource( mergeLeft( existingDecl.getTypeName(),
                                                 "Unable to merge resource attribute for type declaration of class:",
                                                 existingDecl.getResource(),
                                                 newDecl.getResource(),
                                                 true,
                                                 true ) );
        }

        existingDecl.setRole( mergeLeft( existingDecl.getTypeName(),
                                         "Unable to merge @role attribute for type declaration of class:",
                                         isSet(existingDecl.getSetMask(), TypeDeclaration.ROLE_BIT)
                                           && newDecl.getRole() != TypeDeclaration.Role.FACT
                                           ? existingDecl.getRole() : null,
                                         newDecl.getRole(),
                                         true,
                                         false ) );

        existingDecl.setTimestampAttribute( mergeLeft( existingDecl.getTypeName(),
                                                       "Unable to merge @timestamp attribute for type declaration of class:",
                                                       existingDecl.getTimestampAttribute(),
                                                       newDecl.getTimestampAttribute(),
                                                       true,
                                                       false ) );

        existingDecl.setTypesafe( mergeLeft(existingDecl.getTypeName(),
                                  "Unable to merge @typesafe attribute for type declaration of class:",
                                  existingDecl.isTypesafe(),
                                  newDecl.isTypesafe(),
                                  true,
                                  false ) );
    }

    private <T> T mergeLeft( String typeClass,
            String errorMsg,
            T leftVal,
            T rightVal,
            boolean errorOnDiff,
            boolean override ) {
        T newValue = leftVal;
        if ( ! nullSafeEquals( leftVal,
                               rightVal ) ) {
            if ( leftVal == null && rightVal != null ) {
                newValue = rightVal;
            } else if ( leftVal != null && rightVal != null ) {
                if ( override ) {
                    newValue = rightVal;
                } else {
                    if ( errorOnDiff ) {
                        throw new RuntimeDroolsException( errorMsg + " '" + typeClass + "'" );
                    } else {
                        // do nothing, just use the left value
                    }
                }
            }
        }
        return newValue;
    }

    private boolean nullSafeEquals( Object o1,
            Object o2 ) {
        return ( o1 == null ) ? o2 == null : o1.equals( o2 );
    }

    /**
     * Merge a new package with an existing package.
     * Most of the work is done by the concrete implementations,
     * but this class does some work (including combining imports, compilation data, globals,
     * and the actual Rule objects into the package).
     */
    private void mergePackage( final Package pkg,
            final Package newPkg ) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll( newPkg.getImports() );

        String lastIdent = null;
        String lastType = null;
        try {
            // merge globals
            if (newPkg.getGlobals() != null && newPkg.getGlobals() != Collections.EMPTY_MAP) {
                Map<String, String> globals = pkg.getGlobals();
                // Add globals
                for (final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet()) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastIdent = identifier;
                    lastType = type;
                    if (globals.containsKey( identifier ) && !globals.get( identifier ).equals( type )) {
                        throw new PackageIntegrationException( pkg );
                    } else {
                        pkg.addGlobal( identifier,
                                       this.rootClassLoader.loadClass( type ) );
                        // this isn't a package merge, it's adding to the rulebase, but I've put it here for convienience
                        this.globals.put( identifier,
                                          this.rootClassLoader.loadClass( type ) );
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeDroolsException( "Unable to resolve class '" + lastType +
                                              "' for global '" + lastIdent + "'" );
        }

        // merge entry point declarations
        if (newPkg.getEntryPointIds() != null) {
            for (String ep : newPkg.getEntryPointIds()) {
                pkg.addEntryPointId( ep );

            }
        }

        // merge the type declarations
        if (newPkg.getTypeDeclarations() != null) {
            // add type declarations
            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                // @TODO should we allow overrides? only if the class is not in use.
                if (!pkg.getTypeDeclarations().containsKey( type.getTypeName() )) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration( type );
                }
            }
        }

        // merge window declarations
        if ( newPkg.getWindowDeclarations() != null ) {
            // add window declarations
            for ( WindowDeclaration window : newPkg.getWindowDeclarations().values() ) {
                if ( !pkg.getWindowDeclarations().containsKey( window.getName() ) ||
                      pkg.getWindowDeclarations().get( window.getName() ).equals( window ) ) {
                    pkg.addWindowDeclaration( window );
                } else {
                    throw new RuntimeDroolsException( "Unable to merge two conflicting window declarations for window named: "+window.getName() );
                }
            }
        }

        //Merge rules into the RuleBase package
        //as this is needed for individual rule removal later on
        final Rule[] newRules = newPkg.getRules();
        for (int i = 0; i < newRules.length; i++) {
            final Rule newRule = newRules[i];

            // remove the rule if it already exists
            if (pkg.getRule( newRule.getName() ) != null) {
                removeRule( pkg,
                            pkg.getRule( newRule.getName() ) );
            }

            pkg.addRule( newRule );
        }

        //Merge The Rule Flows
        if (newPkg.getRuleFlows() != null) {
            final Map flows = newPkg.getRuleFlows();
            for (final Iterator iter = flows.values().iterator(); iter.hasNext();) {
                final Process flow = (Process) iter.next();
                pkg.addProcess( flow );
            }
        }
    }

    public ClassLoader getTypeDeclarationClassLoader() {
        return this.declarationClassLoader;
    }


    private static class TypeDeclarationCandidate {

        public TypeDeclaration candidate = null;
        public int             score     = Integer.MAX_VALUE;
    }

    public TypeDeclaration getTypeDeclaration( Class<?> clazz ) {
        TypeDeclaration typeDeclaration = this.classTypeDeclaration.get( clazz.getName() );
        if (typeDeclaration == null) {
            // check super classes and keep a score of how up in the hierarchy is there a declaration
            TypeDeclarationCandidate candidate = checkSuperClasses( clazz );
            // now check interfaces
            candidate = checkInterfaces( clazz,
                                         candidate,
                                         1 );
            if (candidate != null) {
                typeDeclaration = candidate.candidate;
            }
        }
        return typeDeclaration;
    }

    private TypeDeclarationCandidate checkSuperClasses( Class<?> clazz ) {

        TypeDeclarationCandidate candidate = null;
        TypeDeclaration typeDeclaration = null;
        Class<?> current = clazz.getSuperclass();
        int score = 0;
        while ( typeDeclaration == null && current != null ) {
            score++;
            typeDeclaration = this.classTypeDeclaration.get( current.getName() );
            current = current.getSuperclass();
        }
        if ( typeDeclaration == null ) {
            // no type declaration found for superclasses
            score = Integer.MAX_VALUE;
        } else {
            candidate = new TypeDeclarationCandidate();
            candidate.candidate = typeDeclaration;
            candidate.score = score;
        }
        return candidate;
    }

    private TypeDeclarationCandidate checkInterfaces( Class<?> clazz,
            TypeDeclarationCandidate baseline,
            int level ) {
        TypeDeclarationCandidate candidate = null;
        TypeDeclaration typeDeclaration = null;
        if (baseline == null || level < baseline.score) {
            // search
            for (Class<?> ifc : clazz.getInterfaces()) {
                typeDeclaration = this.classTypeDeclaration.get( ifc.getName() );
                if (typeDeclaration != null) {
                    candidate = new TypeDeclarationCandidate();
                    candidate.candidate = typeDeclaration;
                    candidate.score = level;
                    break;
                } else {
                    candidate = checkInterfaces( ifc,
                                                 baseline,
                                                 level + 1 );
                }
            }
        } else {
            candidate = baseline;
        }
        return candidate;
    }

    public Collection<TypeDeclaration> getTypeDeclarations() {
        return this.classTypeDeclaration.values();
    }

    public void addRule( final Package pkg,
            final Rule rule ) throws InvalidPatternException {
        lock();
        try {
            this.eventSupport.fireBeforeRuleAdded( pkg,
                                                   rule );
            //        if ( !rule.isValid() ) {
            //            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
            //        }
            addRule( rule );
            this.eventSupport.fireAfterRuleAdded( pkg,
                                                  rule );
        } finally {
            unlock();
        }
    }

    protected void addEntryPoint( final Package pkg,
                                  final String id ) throws InvalidPatternException {
        lock();
        try {
            addEntryPoint( id );
        } finally {
            unlock();
        }
    }

    /**
     * This method is called with the rulebase lock held.
     *
     * @param rule
     * @throws InvalidPatternException
     */
    protected abstract void addRule( final Rule rule ) throws InvalidPatternException;

    /**
     * This method is called with the rulebase lock held.
     *
     * @param id the entry point ID
     * @throws InvalidPatternException
     */
    protected abstract void addEntryPoint( final String id );

    public void addWindowDeclaration( final Package pkg,
                                      final WindowDeclaration window ) throws InvalidPatternException {
        lock();
        try {
            addWindowDeclaration( window );
        } finally {
            unlock();
        }
    }

    /**
    * This method is called with the rulebase lock held.
    *
    * @param window
    * @throws InvalidPatternException
    */
    protected abstract void addWindowDeclaration( final WindowDeclaration window ) throws InvalidPatternException;

    public void removePackage( final String packageName ) {
        lock();
        try {
            final Package pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }
            this.removalsSinceLock++;

            this.eventSupport.fireBeforePackageRemoved( pkg );

            final Rule[] rules = pkg.getRules();

            for (int i = 0; i < rules.length; ++i) {
                removeRule( pkg,
                            rules[i] );
            }

            // getting the list of referenced globals
            final Set<String> referencedGlobals = new HashSet<String>();
            for (Package pkgref : this.pkgs.values()) {
                if (pkgref != pkg) {
                    referencedGlobals.addAll( pkgref.getGlobals().keySet() );
                }
            }
            // removing globals declared inside the package that are not shared
            for (String globalName : pkg.getGlobals().keySet()) {
                if (!referencedGlobals.contains( globalName )) {
                    this.globals.remove( globalName );
                }
            }
            //and now the rule flows
            final Map flows = pkg.getRuleFlows();
            for (final Iterator iter = flows.keySet().iterator(); iter.hasNext();) {
                removeProcess( (String) iter.next() );
            }
            // removing the package itself from the list
            this.pkgs.remove( pkg.getName() );

            pkg.getDialectRuntimeRegistry().onRemove();

            //clear all members of the pkg
            pkg.clear();

            this.eventSupport.fireAfterPackageRemoved( pkg );
        } finally {
            unlock();
        }
    }

    public void removeQuery( final String packageName,
            final String ruleName ) {
        removeRule( packageName,
                    ruleName );
    }

    public void removeRule( final String packageName,
            final String ruleName ) {
        lock();
        try {
            final Package pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }

            final Rule rule = pkg.getRule( ruleName );
            if (rule == null) {
                throw new IllegalArgumentException( "Rule name '" + ruleName +
                                                    "' does not exist in the Package '" +
                                                    packageName +
                                                    "'." );
            }

            this.removalsSinceLock++;

            removeRule( pkg,
                        rule );
            pkg.removeRule( rule );
            addReloadDialectDatas( pkg.getDialectRuntimeRegistry() );
        } finally {
            unlock();
        }
    }

    /**
     * Notify listeners and sub-classes about imminent removal of a rule from a package.
     *
     * @param pkg
     * @param rule
     */
    // FIXME: removeRule(String, String) and removeRule(Package, Rule) do totally different things!
    public void removeRule( final Package pkg,
            final Rule rule ) {
        lock();
        try {
            this.eventSupport.fireBeforeRuleRemoved( pkg,
                                                     rule );
            removeRule( rule );
            this.eventSupport.fireAfterRuleRemoved( pkg,
                                                    rule );
        } finally {
            unlock();
        }
    }

    /**
     * Handle rule removal.
     *
     * This method is intended for sub-classes, and called after the
     * {@link RuleBaseEventListener#beforeRuleRemoved(org.drools.event.BeforeRuleRemovedEvent) before-rule-removed}
     * event is fired, and before the rule is physically removed from the package.
     *
     * This method is called with the rulebase lock held.
     * @param rule
     */
    protected abstract void removeRule( Rule rule );

    public void removeFunction( final String packageName,
            final String functionName ) {
        lock();
        try {
            final Package pkg = this.pkgs.get( packageName );
            if (pkg == null) {
                throw new IllegalArgumentException( "Package name '" + packageName +
                                                    "' does not exist for this Rule Base." );
            }

            if (!pkg.getFunctions().containsKey( functionName )) {
                throw new IllegalArgumentException( "function name '" + packageName +
                                                    "' does not exist in the Package '" +
                                                    packageName +
                                                    "'." );
            }

            removeFunction( pkg,
                            functionName );
            pkg.removeFunction( functionName );

            addReloadDialectDatas( pkg.getDialectRuntimeRegistry() );
        } finally {
            unlock();
        }
    }

    /**
     * Handle function removal.
     *
     * This method is intended for sub-classes, and called after the
     * {@link RuleBaseEventListener#beforeFunctionRemoved(org.drools.event.BeforeFunctionRemovedEvent) before-function-removed}
     * event is fired, and before the function is physically removed from the package.
     *
     * This method is called with the rulebase lock held.
     * @param functionName
     */
    protected/* abstract */void removeFunction( String functionName ) {
        // Nothing in default.
    }

    /**
     * Notify listeners and sub-classes about imminent removal of a function from a package.
     *
     * This method is called with the rulebase lock held.
     * @param pkg
     * @param functionName
     */
    private void removeFunction( final Package pkg,
            final String functionName ) {
        this.eventSupport.fireBeforeFunctionRemoved( pkg,
                                                     functionName );
        removeFunction( functionName );
        this.eventSupport.fireAfterFunctionRemoved( pkg,
                                                    functionName );
    }

    public void addProcess( final Process process ) {
        // XXX: could use a synchronized(processes) here.
        this.eventSupport.fireBeforeProcessAdded( process );
        lock();
        try {
            this.processes.put( process.getId(),
                                process );
        } finally {
            unlock();
        }
        this.eventSupport.fireAfterProcessAdded( process );
    }

    public void removeProcess( final String id ) {
        Process process = this.processes.get( id );
        if (process == null) {
            throw new IllegalArgumentException( "Process '" + id + "' does not exist for this Rule Base." );
        }
        this.eventSupport.fireBeforeProcessRemoved( process );
        lock();
        try {
            this.processes.remove( id );
            this.pkgs.get(process.getPackageName()).removeRuleFlow(id);
        } finally {
            unlock();
        }
        this.eventSupport.fireAfterProcessRemoved( process );
    }

    public Process getProcess( final String id ) {
        readLock();
        try {
            return (Process) this.processes.get( id );
        } finally {
            readUnlock();
        }
    }

    public void addStatefulSession( final StatefulSession statefulSession ) {
        statefulSessionLock.lock();
        try {
            this.statefulSessions.add( statefulSession );
        } finally {
            statefulSessionLock.unlock();
        }

    }

    public Package getPackage( final String name ) {
        return this.pkgs.get( name );
    }

    public StatefulSession[] getStatefulSessions() {
        statefulSessionLock.lock();
        try {
            final StatefulSession[] copyOfSessions = new StatefulSession[this.statefulSessions.size()];
            this.statefulSessions.toArray( copyOfSessions );
            return copyOfSessions;
        } finally {
            statefulSessionLock.unlock();
        }
    }

    public InternalWorkingMemory[] getWorkingMemories() {
        statefulSessionLock.lock();
        try {
            final InternalWorkingMemory[] copyOfMemories = new InternalWorkingMemory[this.statefulSessions.size()];
            this.statefulSessions.toArray( copyOfMemories );
            return copyOfMemories;
        } finally {
            statefulSessionLock.unlock();
        }
    }

    public RuleBaseConfiguration getConfiguration() {
        if ( this.config == null ) {
            this.config = new RuleBaseConfiguration();
        }
        return this.config;
    }

    public CompositeClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    public void executeQueuedActions() {
        DialectRuntimeRegistry registry;
        while (( registry = reloadPackageCompilationData.poll() ) != null) {
            registry.onBeforeExecute();
        }
    }

    public RuleBasePartitionId createNewPartitionId() {
        RuleBasePartitionId p;
        synchronized (this.partitionIDs) {
            p = new RuleBasePartitionId( "P-" + this.partitionIDs.size() );
            this.partitionIDs.add( p );
        }
        return p;
    }

    public List<RuleBasePartitionId> getPartitionIds() {
        // this returns an unmodifiable CopyOnWriteArrayList, so should be safe for concurrency
        return Collections.unmodifiableList( this.partitionIDs );
    }

    public void addEventListener( final RuleBaseEventListener listener ) {
        // no need for synchonization or locking because eventSupport is thread-safe
        this.eventSupport.addEventListener( listener );
    }

    public void removeEventListener( final RuleBaseEventListener listener ) {
        // no need for synchonization or locking because eventSupport is thread-safe
        this.eventSupport.removeEventListener( listener );
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        // no need for synchonization or locking because eventSupport is thread-safe
        return this.eventSupport.getEventListeners();
    }

    public boolean isEvent( Class<?> clazz ) {
        readLock();
        try {
            for (Package pkg : this.pkgs.values()) {
                if (pkg.isEvent( clazz )) {
                    return true;
                }
            }
            return false;
        } finally {
            readUnlock();
        }
    }

    public FactType getFactType( final String name ) {
        readLock();
        try {
            for (Package pkg : this.pkgs.values()) {
                FactType type = pkg.getFactType( name );
                if (type != null) {
                    return type;
                }
            }
            return null;
        } finally {
            readUnlock();
        }
    }

    private void addReloadDialectDatas( DialectRuntimeRegistry registry ) {
        this.reloadPackageCompilationData.offer( registry );
    }

    public static interface RuleBaseAction
                                          extends
                                          Externalizable {

        public void execute( InternalRuleBase ruleBase );
    }

    public ClassFieldAccessorCache getClassFieldAccessorCache() {
        return this.classFieldAccessorCache;
    }

    public Set<String> getEntryPointIds() {
        Set<String> entryPointIds = new HashSet<String>();
        for (Package pkg : this.pkgs.values()) {
            entryPointIds.addAll( pkg.getEntryPointIds() );
        }
        return entryPointIds;
    }

    public TripleStore getTripleStore() {
        return TripleStoreRegistry.getRegistry( id );
    }

}