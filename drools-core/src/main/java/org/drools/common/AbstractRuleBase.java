package org.drools.common;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.StatefulSession;
import org.drools.concurrent.CommandExecutor;
import org.drools.concurrent.ExecutorService;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleBaseEventSupport;
import org.drools.objenesis.Objenesis;
import org.drools.objenesis.ObjenesisStd;
import org.drools.process.core.Process;
import org.drools.rule.CompositePackageClassLoader;
import org.drools.rule.DialectDatas;
import org.drools.rule.EntryPoint;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ExecutorServiceFactory;
import org.drools.spi.FactHandleFactory;
import org.drools.util.ObjectHashSet;

/**
 * Implementation of <code>RuleBase</code>.
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
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
    protected String                                   id;

    protected int                                      workingMemoryCounter;

    protected RuleBaseConfiguration                    config;

    protected Map<String, Package>                     pkgs;

    protected Map                                      processes;

    protected Map                                      agendaGroupRuleTotals;

    protected transient CompositePackageClassLoader    packageClassLoader;

    protected transient MapBackedClassLoader           classLoader;

    private transient Objenesis                     objenesis;

    /** The fact handle factory. */
    protected FactHandleFactory                     factHandleFactory;

    protected Map                                      globals;

    private ReloadPackageCompilationData               reloadPackageCompilationData = null;

    private RuleBaseEventSupport                       eventSupport                 = new RuleBaseEventSupport( this );

    /**
     * WeakHashMap to keep references of WorkingMemories but allow them to be
     * garbage collected
     */
    protected transient ObjectHashSet                  statefulSessions;

    // wms used for lock list during dynamic updates
    InternalWorkingMemory[]                            wms;

    // indexed used to track invariant lock
    int                                                lastAquiredLock;

    // lock for entire rulebase, used for dynamic updates
    protected final ReentrantLock                      lock                         = new ReentrantLock();

    private int                                     additionsSinceLock;
    private int                                     removalsSinceLock;

    private transient Map<Class< ? >, TypeDeclaration> classTypeDeclaration;

    /**
     * Default constructor - for Externalizable. This should never be used by a user, as it
     * will result in an invalid state for the instance.
     */
    public AbstractRuleBase() {

    }

    public synchronized int nextWorkingMemoryCounter() {
        return this.workingMemoryCounter++;
    }

    /**
     * Construct.
     *
     * @param rete
     *            The rete network.
     */
    public AbstractRuleBase(final String id,
                            final RuleBaseConfiguration config,
                            final FactHandleFactory factHandleFactory) {
        if ( id != null ) {
            this.id = id;
        } else {
            this.id = "default";
        }
        this.config = (config != null) ? config : new RuleBaseConfiguration();
        this.config.makeImmutable();
        this.factHandleFactory = factHandleFactory;

        if ( this.config.isSequential() ) {
            this.agendaGroupRuleTotals = new HashMap();
        }

        this.packageClassLoader = new CompositePackageClassLoader( this.config.getClassLoader() );
        this.classLoader = new MapBackedClassLoader( this.config.getClassLoader() );
        this.packageClassLoader.addClassLoader( this.classLoader );
        this.pkgs = new HashMap<String, Package>();
        this.processes = new HashMap();
        this.globals = new HashMap();
        this.statefulSessions = new ObjectHashSet();
        this.objenesis = createObjenesis();

        this.classTypeDeclaration = new HashMap<Class< ? >, TypeDeclaration>();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     *
     */
    public void doWriteExternal(final ObjectOutput stream,
                                final Object[] objects) throws IOException {        
        stream.writeObject( this.pkgs );
        
        synchronized ( this.classLoader.getStore() ) {
            stream.writeObject( this.classLoader.getStore() );
        }

        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( this.id );
        out.writeObject( this.processes );
        out.writeObject( this.agendaGroupRuleTotals );
        out.writeObject( this.factHandleFactory );
        out.writeObject( this.globals );
        out.writeObject( this.config );
        
        this.eventSupport.removeEventListener( RuleBaseEventListener.class );
        out.writeObject( this.eventSupport );
        
        for ( int i = 0, length = objects.length; i < length; i++ ) {
            out.writeObject( objects[i] );
        }        
        
        out.close();
        stream.writeObject( bos.toByteArray() );
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     *
     */
    public void doReadExternal(final ObjectInput stream,
                               final Object[] objects) throws IOException,
                                                        ClassNotFoundException {
        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules        
        this.pkgs = (Map) stream.readObject();
        Map store = (Map) stream.readObject();
        
        if ( stream instanceof DroolsObjectInputStream ) {
            final DroolsObjectInputStream parentStream = (DroolsObjectInputStream) stream;
            parentStream.setRuleBase( this );
            this.packageClassLoader = new CompositePackageClassLoader( parentStream.getClassLoader() );
            this.classLoader = new MapBackedClassLoader( parentStream.getClassLoader(), store );
        } else {
            this.packageClassLoader = new CompositePackageClassLoader( Thread.currentThread().getContextClassLoader() );
            this.classLoader = new MapBackedClassLoader( Thread.currentThread().getContextClassLoader(), store );
        }

        this.packageClassLoader.addClassLoader( this.classLoader );
        this.objenesis = createObjenesis();

        for ( final Iterator it = this.pkgs.values().iterator(); it.hasNext(); ) {
            this.packageClassLoader.addClassLoader( ((Package) it.next()).getDialectDatas().getClassLoader() );
        }

        // Return the rules stored as a byte[]
        final byte[] bytes = (byte[]) stream.readObject();

        //  Use a custom ObjectInputStream that can resolve against a given classLoader
        final DroolsObjectInputStream childStream = new DroolsObjectInputStream( new ByteArrayInputStream( bytes ),
                                                                                 this.packageClassLoader );
        childStream.setRuleBase( this );

        this.id = (String) childStream.readObject();
        this.processes = (Map) childStream.readObject();
        this.agendaGroupRuleTotals = (Map) childStream.readObject();
        this.factHandleFactory = (FactHandleFactory) childStream.readObject();
        this.globals = (Map) childStream.readObject();

        this.config = (RuleBaseConfiguration) childStream.readObject();
        this.config.setClassLoader( childStream.getClassLoader() );

        this.eventSupport = (RuleBaseEventSupport) childStream.readObject();
        this.eventSupport.setRuleBase( this );

        this.statefulSessions = new ObjectHashSet();

        for ( int i = 0, length = objects.length; i < length; i++ ) {
            objects[i] = childStream.readObject();
        }
        childStream.close();
        
        this.populateTypeDeclarationMaps();
    }

    /**
     * Creates Objenesis instance for the RuleBase. 
     * @return a standart Objenesis instanse with caching turned on.
     */
    protected Objenesis createObjenesis() {
        return new ObjenesisStd( true );
    }

    private void populateTypeDeclarationMaps() {
        this.classTypeDeclaration = new HashMap<Class<?>, TypeDeclaration>();
        for( Package pkg : this.pkgs.values() ) {
            for( TypeDeclaration type : pkg.getTypeDeclarations().values() ) {
                this.classTypeDeclaration.put( type.getTypeClass(), type );
            }
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see RuleBase
     */
    public StatefulSession newStatefulSession() {
        return newStatefulSession( true );
    }

    /**
     * @see RuleBase
     */
    abstract public StatefulSession newStatefulSession(boolean keepReference);

    public synchronized void disposeStatefulSession(final StatefulSession statefulSession) {
        this.statefulSessions.remove( statefulSession );
        for ( Iterator it = statefulSession.getRuleBaseUpdateListeners().iterator(); it.hasNext(); ) {
            this.removeEventListener( (RuleBaseEventListener) it.next() );
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

    public Process[] getProcesses() {
        return (Process[]) this.processes.values().toArray( new Process[this.processes.size()] );
    }

    public Package[] getPackages() {
        return (Package[]) this.pkgs.values().toArray( new Package[this.pkgs.size()] );
    }

    public Map getPackagesMap() {
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
        this.additionsSinceLock = 0;
        this.removalsSinceLock = 0;

        this.eventSupport.fireBeforeRuleBaseLocked();
        this.lock.lock();

        // INVARIANT: lastAquiredLock always contains the index of the last aquired lock +1
        // in the working memory array
        this.lastAquiredLock = 0;

        this.wms = getWorkingMemories();

        // Iterate each workingMemory and lock it
        // This is so we don't update the Rete network during propagation
        for ( this.lastAquiredLock = 0; this.lastAquiredLock < this.wms.length; this.lastAquiredLock++ ) {
            this.wms[this.lastAquiredLock].getLock().lock();
        }

        this.eventSupport.fireAfterRuleBaseLocked();
    }

    public void unlock() {
        this.eventSupport.fireBeforeRuleBaseUnlocked();

        // Iterate each workingMemory and attempt to fire any rules, that were activated as a result

        // as per the INVARIANT defined above, we need to iterate from lastAquiredLock-1 to 0.
        for ( this.lastAquiredLock--; this.lastAquiredLock > -1; this.lastAquiredLock-- ) {
            this.wms[this.lastAquiredLock].getLock().unlock();
        }

        this.lock.unlock();

        this.eventSupport.fireAfterRuleBaseUnlocked();

        this.wms = null;
    }

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network. Before update network each referenced <code>WorkingMemory</code>
     * is locked.
     *
     * @param pkg
     *            The package to add.
     */
    public synchronized void addPackage(final Package newPkg) {
        newPkg.checkValidity();
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( newPkg.getName() );

            // only acquire the lock if it hasn't been done explicitely
            boolean doUnlock = false;
            if ( !this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0) ) {
                lock();
                doUnlock = true;
            }
            this.additionsSinceLock++;

            this.eventSupport.fireBeforePackageAdded( newPkg );

            if ( pkg != null ) {
                mergePackage( pkg,
                              newPkg );
            } else {
                this.pkgs.put( newPkg.getName(),
                               newPkg );
            }

            final Map newGlobals = newPkg.getGlobals();

            // Check that the global data is valid, we cannot change the type
            // of an already declared global variable
            for ( final Iterator it = newGlobals.keySet().iterator(); it.hasNext(); ) {
                final String identifier = (String) it.next();
                final Class type = (Class) newGlobals.get( identifier );
                final boolean f = this.globals.containsKey( identifier );
                if ( f ) {
                    final boolean y = !this.globals.get( identifier ).equals( type );
                    if ( f && y ) {
                        throw new PackageIntegrationException( pkg );
                    }
                }
            }
            this.globals.putAll( newGlobals );

            // Add type declarations
            for ( TypeDeclaration type : newPkg.getTypeDeclarations().values() ) {
                // should we allow overrides?
                if ( !this.classTypeDeclaration.containsKey( type.getTypeClass() ) ) {
                    this.classTypeDeclaration.put( type.getTypeClass(),
                                                   type );
                }
            }
            
            final Rule[] rules = newPkg.getRules();

            for ( int i = 0; i < rules.length; ++i ) {
                if ( pkg != null && pkg.getRule( rules[i].getName() ) != null ) {
                    removeRule( pkg, pkg.getRule( rules[i].getName() ) );
                }
                addRule( newPkg,
                         rules[i] );
            }

            //and now the rule flows
            if ( newPkg.getRuleFlows() != Collections.EMPTY_MAP ) {
                final Map flows = newPkg.getRuleFlows();
                for ( final Iterator iter = flows.entrySet().iterator(); iter.hasNext(); ) {
                    final Entry flow = (Entry) iter.next();
                    this.processes.put( flow.getKey(),
                                        flow.getValue() );
                }
            }

            this.packageClassLoader.addClassLoader( newPkg.getDialectDatas().getClassLoader() );

            this.eventSupport.fireAfterPackageAdded( newPkg );

            // only unlock if it had been acquired implicitely
            if ( doUnlock ) {
                unlock();
            }
        }

    }

    /**
     * Merge a new package with an existing package.
     * Most of the work is done by the concrete implementations,
     * but this class does some work (including combining imports, compilation data, globals,
     * and the actual Rule objects into the package).
     */
    private void mergePackage(final Package pkg,
                              final Package newPkg) {
        final Map globals = pkg.getGlobals();
        final Map<String, ImportDeclaration> imports = pkg.getImports();

        // Merge imports
        imports.putAll( newPkg.getImports() );

        // Add globals
        for ( final Iterator it = newPkg.getGlobals().keySet().iterator(); it.hasNext(); ) {
            final String identifier = (String) it.next();
            final Class type = (Class) globals.get( identifier );
            if ( globals.containsKey( identifier ) && !globals.get( identifier ).equals( type ) ) {
                throw new PackageIntegrationException( "Unable to merge new Package",
                                                       newPkg );
            }
        }
        globals.putAll( newPkg.getGlobals() );

        // add type declarations
        for ( TypeDeclaration type : newPkg.getTypeDeclarations().values() ) {
            // should we allow overrides?
            if ( !this.classTypeDeclaration.containsKey( type.getTypeClass() ) ) {
                this.classTypeDeclaration.put( type.getTypeClass(),
                                               type );
            }
            if ( !pkg.getTypeDeclarations().containsKey( type.getTypeName() ) ) {
                pkg.addTypeDeclaration( type );
            }
        }
        
        //Add rules into the RuleBase package
        //as this is needed for individual rule removal later on
        final Rule[] newRules = newPkg.getRules();
        for ( int i = 0; i < newRules.length; i++ ) {
            final Rule newRule = newRules[i];
            if ( pkg.getRule( newRule.getName() ) == null ) {
                pkg.addRule( newRule );
            }
        }

        //and now the rule flows
        if ( newPkg.getRuleFlows() != Collections.EMPTY_MAP ) {
            final Map flows = newPkg.getRuleFlows();
            for ( final Iterator iter = flows.values().iterator(); iter.hasNext(); ) {
                final Process flow = (Process) iter.next();
                pkg.addRuleFlow( flow );
            }
        }

        pkg.getDialectDatas().merge( newPkg.getDialectDatas() );

        if ( this.reloadPackageCompilationData == null ) {
            this.reloadPackageCompilationData = new ReloadPackageCompilationData();
        }
        this.reloadPackageCompilationData.addDialectDatas( pkg.getDialectDatas() );
    }
    
    public TypeDeclaration getTypeDeclaration(Class< ? > clazz) {
        return this.classTypeDeclaration.get( clazz );
    }

    private synchronized void addRule(final Package pkg,
                                      final Rule rule) throws InvalidPatternException {
        this.eventSupport.fireBeforeRuleAdded( pkg,
                                               rule );
        if ( !rule.isValid() ) {
            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
        }
        addRule( rule );
        this.eventSupport.fireAfterRuleAdded( pkg,
                                              rule );
    }

    protected abstract void addRule(final Rule rule) throws InvalidPatternException;

    public void removePackage(final String packageName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );
            if ( pkg == null ) {
                throw new IllegalArgumentException( "Package name '" + packageName + "' does not exist for this Rule Base." );
            }

            // only acquire the lock if it hasn't been done explicitely
            boolean doUnlock = false;
            if ( !this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0) ) {
                lock();
                doUnlock = true;
            }
            try {
                this.removalsSinceLock++;

                this.eventSupport.fireBeforePackageRemoved( pkg );

                final Rule[] rules = pkg.getRules();

                for ( int i = 0; i < rules.length; ++i ) {
                    removeRule( pkg,
                                rules[i] );
                }

                this.packageClassLoader.removeClassLoader( pkg.getDialectDatas().getClassLoader() );

                // getting the list of referenced globals
                final Set referencedGlobals = new HashSet();
                for ( final Iterator it = this.pkgs.values().iterator(); it.hasNext(); ) {
                    final org.drools.rule.Package pkgref = (org.drools.rule.Package) it.next();
                    if ( pkgref != pkg ) {
                        referencedGlobals.addAll( pkgref.getGlobals().keySet() );
                    }
                }
                // removing globals declared inside the package that are not shared
                for ( final Iterator it = pkg.getGlobals().keySet().iterator(); it.hasNext(); ) {
                    final String globalName = (String) it.next();
                    if ( !referencedGlobals.contains( globalName ) ) {
                        this.globals.remove( globalName );
                    }
                }
                //and now the rule flows
                final Map flows = pkg.getRuleFlows();
                for ( final Iterator iter = flows.keySet().iterator(); iter.hasNext(); ) {
                    removeProcess( (String) iter.next() );
                }
                // removing the package itself from the list
                this.pkgs.remove( pkg.getName() );

                //clear all members of the pkg
                pkg.clear();

                this.eventSupport.fireAfterPackageRemoved( pkg );

                // only unlock if it had been acquired implicitely
            } finally {
                if ( doUnlock ) {
                    unlock();
                }
            }
        }
    }

    public void removeRule(final String packageName,
                           final String ruleName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );
            if ( pkg == null ) {
                throw new IllegalArgumentException( "Package name '" + packageName + "' does not exist for this Rule Base." );
            }

            final Rule rule = pkg.getRule( ruleName );
            if ( rule == null ) {
                throw new IllegalArgumentException( "Rule name '" + ruleName + "' does not exist in the Package '" + packageName + "'." );
            }

            // only acquire the lock if it hasn't been done explicitely
            boolean doUnlock = false;
            if ( !this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0) ) {
                lock();
                doUnlock = true;
            }
            this.removalsSinceLock++;

            removeRule( pkg,
                        rule );
            pkg.removeRule( rule );
            if ( this.reloadPackageCompilationData == null ) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addDialectDatas( pkg.getDialectDatas() );

            // only unlock if it had been acquired implicitely
            if ( doUnlock ) {
                unlock();
            }
        }
    }

    private void removeRule(final Package pkg,
                            final Rule rule) {
        this.eventSupport.fireBeforeRuleRemoved( pkg,
                                                 rule );
        removeRule( rule );
        this.eventSupport.fireAfterRuleRemoved( pkg,
                                                rule );
    }

    protected abstract void removeRule(Rule rule);

    public void removeFunction(final String packageName,
                               final String functionName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );
            if ( pkg == null ) {
                throw new IllegalArgumentException( "Package name '" + packageName + "' does not exist for this Rule Base." );
            }

            this.eventSupport.fireBeforeFunctionRemoved( pkg,
                                                         functionName );

            if ( !pkg.getFunctions().containsKey( functionName ) ) {
                throw new IllegalArgumentException( "function name '" + packageName + "' does not exist in the Package '" + packageName + "'." );
            }

            pkg.removeFunction( functionName );

            if ( this.reloadPackageCompilationData == null ) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addDialectDatas( pkg.getDialectDatas() );

            this.eventSupport.fireAfterFunctionRemoved( pkg,
                                                        functionName );
        }
    }

    public synchronized void addProcess(final Process process) {
        synchronized ( this.pkgs ) {
            this.processes.put( process.getId(),
                                process );
        }

    }

    public synchronized void removeProcess(final String id) {
        synchronized ( this.pkgs ) {
            this.processes.remove( id );
        }
    }

    public Process getProcess(final String id) {
        Process process = null;
        synchronized ( this.pkgs ) {
            process = (Process) this.processes.get( id );
        }
        return process;
    }

    public Objenesis getObjenesis() {
        return objenesis;
    }

    protected synchronized void addStatefulSession(final StatefulSession statefulSession) {
        this.statefulSessions.add( statefulSession );
    }

    public Package getPackage(final String name) {
        return (Package) this.pkgs.get( name );
    }

    public StatefulSession[] getStatefulSessions() {
        return (StatefulSession[]) this.statefulSessions.toArray( new StatefulSession[this.statefulSessions.size()] );
    }

    public InternalWorkingMemory[] getWorkingMemories() {
        return (InternalWorkingMemory[]) this.statefulSessions.toArray( new InternalWorkingMemory[this.statefulSessions.size()] );
    }

    public RuleBaseConfiguration getConfiguration() {
        return this.config;
    }

    public StatefulSession newStatefulSession(final InputStream stream) throws IOException,
                                                                       ClassNotFoundException {
        return newStatefulSession( stream,
                                   true );
    }

    public StatefulSession newStatefulSession(final InputStream stream,
                                              final boolean keepReference) throws IOException,
                                                                          ClassNotFoundException {

        if ( this.config.isSequential() ) {
            throw new RuntimeException( "Cannot have a stateful rule session, with sequential configuration set to true" );
        }

        final DroolsObjectInputStream streamWithLoader = new DroolsObjectInputStream( stream,
                                                                                      this.packageClassLoader );
        streamWithLoader.setRuleBase( this );

        final StatefulSession session = (StatefulSession) streamWithLoader.readObject();                

        synchronized ( this.pkgs ) {
            ((InternalWorkingMemory) session).setRuleBase( this );
            ((InternalWorkingMemory) session).setId( (nextWorkingMemoryCounter()) );
                        

            ExecutorService executor = ExecutorServiceFactory.createExecutorService( this.config.getExecutorService() );
            executor.setCommandExecutor( new CommandExecutor( session ) );            
            ((InternalWorkingMemory) session).setExecutorService( executor );            

            if ( keepReference ) {
                addStatefulSession( session );
                for ( Iterator it = session.getRuleBaseUpdateListeners().iterator(); it.hasNext(); ) {
                    addEventListener( (RuleBaseEventListener) it.next() );
                }
            }

            return (StatefulSession) session;
        }
    }

    public void addClass(final String className,
                         final byte[] bytes) {
        this.classLoader.addClass( className,
                                   bytes );
    }

    public CompositePackageClassLoader getCompositePackageClassLoader() {
        return this.packageClassLoader;
    }

    public MapBackedClassLoader getMapBackedClassLoader() {
        return this.classLoader;
    }

    public void executeQueuedActions() {
        synchronized ( this.pkgs ) {
            if ( this.reloadPackageCompilationData != null ) {
                this.reloadPackageCompilationData.execute( this );
                this.reloadPackageCompilationData = null;
            }

        }
    }

    public void addEventListener(final RuleBaseEventListener listener) {
        // since the event support is thread-safe, no need for locks... right?
        this.eventSupport.addEventListener( listener );
    }

    public void removeEventListener(final RuleBaseEventListener listener) {
        // since the event support is thread-safe, no need for locks... right?
        this.eventSupport.removeEventListener( listener );
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        // since the event support is thread-safe, no need for locks... right?
        return this.eventSupport.getEventListeners();
    }

    public boolean isEvent(Class clazz) {
        for ( Package pkg : this.pkgs.values() ) {
            if ( pkg.isEvent( clazz ) ) {
                return true;
            }
        }
        return false;
    }

    public static class ReloadPackageCompilationData
        implements
        RuleBaseAction {
        private static final long serialVersionUID = 1L;
        private Set               set;

        public void addDialectDatas(final DialectDatas dialectDatas) {
            if ( this.set == null ) {
                this.set = new HashSet();
            }

            this.set.add( dialectDatas );
        }

        public void execute(final InternalRuleBase ruleBase) {
            for ( final Iterator it = this.set.iterator(); it.hasNext(); ) {
                final DialectDatas dialectDatas = (DialectDatas) it.next();
                dialectDatas.reloadDirty();
            }
        }
    }

    public static interface RuleBaseAction
        extends
        Serializable {
        public void execute(InternalRuleBase ruleBase);
    }
}
