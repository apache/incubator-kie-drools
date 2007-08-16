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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleIntegrationException;
import org.drools.StatefulSession;
import org.drools.rule.CompositePackageClassLoader;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.MapBackedClassLoader;
import org.drools.rule.Package;
import org.drools.rule.PackageCompilationData;
import org.drools.rule.Rule;
import org.drools.ruleflow.common.core.Process;
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
    protected String                                id;

    protected int                                   workingMemoryCounter;

    protected RuleBaseConfiguration                 config;

    protected Map                                   pkgs;

    protected Map                                   processes;

    protected Map                                   agendaGroupRuleTotals;

    protected transient CompositePackageClassLoader packageClassLoader;

    protected transient MapBackedClassLoader        classLoader;

    /** The fact handle factory. */
    protected FactHandleFactory                     factHandleFactory;

    protected Map                                   globals;
    
    private ReloadPackageCompilationData reloadPackageCompilationData = null;

    /**
     * WeakHashMap to keep references of WorkingMemories but allow them to be
     * garbage collected
     */
    protected transient ObjectHashSet               statefulSessions;

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

        this.packageClassLoader = new CompositePackageClassLoader( Thread.currentThread().getContextClassLoader() );
        this.classLoader = new MapBackedClassLoader( Thread.currentThread().getContextClassLoader() );
        this.packageClassLoader.addClassLoader( this.classLoader );
        this.pkgs = new HashMap();
        this.processes = new HashMap();
        this.globals = new HashMap();
        this.statefulSessions = new ObjectHashSet();
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

        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( this.id );
        out.writeObject( this.agendaGroupRuleTotals );
        out.writeObject( this.factHandleFactory );
        out.writeObject( this.globals );
        out.writeObject( this.config );

        for ( int i = 0, length = objects.length; i < length; i++ ) {
            out.writeObject( objects[i] );
        }

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

        if ( stream instanceof DroolsObjectInputStream ) {
            DroolsObjectInputStream parentStream = (DroolsObjectInputStream) stream;
            parentStream.setRuleBase( this );
            this.packageClassLoader = new CompositePackageClassLoader( parentStream.getClassLoader() );
            this.classLoader = new MapBackedClassLoader( parentStream.getClassLoader() );
        } else {
            this.packageClassLoader = new CompositePackageClassLoader( Thread.currentThread().getContextClassLoader() );
            this.classLoader = new MapBackedClassLoader( Thread.currentThread().getContextClassLoader() );
        }

        this.packageClassLoader.addClassLoader( this.classLoader );

        for ( final Iterator it = this.pkgs.values().iterator(); it.hasNext(); ) {
            this.packageClassLoader.addClassLoader( ((Package) it.next()).getPackageCompilationData().getClassLoader() );
        }

        // Return the rules stored as a byte[]
        final byte[] bytes = (byte[]) stream.readObject();

        //  Use a custom ObjectInputStream that can resolve against a given classLoader
        final DroolsObjectInputStream childStream = new DroolsObjectInputStream( new ByteArrayInputStream( bytes ),
                                                                                 this.packageClassLoader );
        childStream.setRuleBase( this );

        this.id = (String) childStream.readObject();
        this.agendaGroupRuleTotals = (Map) childStream.readObject();
        this.factHandleFactory = (FactHandleFactory) childStream.readObject();
        this.globals = (Map) childStream.readObject();

        this.config = (RuleBaseConfiguration) childStream.readObject();

        this.statefulSessions = new ObjectHashSet();

        for ( int i = 0, length = objects.length; i < length; i++ ) {
            objects[i] = childStream.readObject();
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

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network. Before update network each referenced <code>WorkingMemory</code>
     * is locked.
     * 
     * @param pkg
     *            The package to add.
     * @throws PackageIntegrationException 
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws FactException
     * @throws InvalidPatternException
     */
    public synchronized void addPackage(final Package newPkg) throws PackageIntegrationException {
        newPkg.checkValidity();

        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( newPkg.getName() );
            // INVARIANT: lastAquiredLock always contains the index of the last aquired lock +1 
            // in the working memory array 
            int lastAquiredLock = 0;
            // get a snapshot of current working memories for locking
            final InternalWorkingMemory[] wms = getWorkingMemories();

            try {
                // Iterate each workingMemory and lock it
                // This is so we don't update the Rete network during propagation
                for ( lastAquiredLock = 0; lastAquiredLock < wms.length; lastAquiredLock++ ) {
                    wms[lastAquiredLock].getLock().lock();
                }

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

                final Rule[] rules = newPkg.getRules();

                for ( int i = 0; i < rules.length; ++i ) {
                    addRule( rules[i] );
                }

                //and now the rule flows
                if ( newPkg.getRuleFlows() != Collections.EMPTY_MAP ) {
                    Map flows = newPkg.getRuleFlows();
                    for ( Iterator iter = flows.entrySet().iterator(); iter.hasNext(); ) {
                        Entry flow = (Entry) iter.next();
                        this.processes.put( flow.getKey(),
                                            flow.getValue() );
                    }
                }

                this.packageClassLoader.addClassLoader( newPkg.getPackageCompilationData().getClassLoader() );

            } finally {
                // Iterate each workingMemory and attempt to fire any rules, that were activated as a result 
                // of the new rule addition. Unlock after fireAllRules();

                // as per the INVARIANT defined above, we need to iterate from lastAquiredLock-1 to 0. 
                for ( lastAquiredLock--; lastAquiredLock > -1; lastAquiredLock-- ) {
                    wms[lastAquiredLock].fireAllRules();
                    wms[lastAquiredLock].getLock().unlock();
                }
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
                              final Package newPkg) throws PackageIntegrationException {
        final Map globals = pkg.getGlobals();
        final Set imports = pkg.getImports();

        // First update the binary files
        // @todo: this probably has issues if you add classes in the incorrect order - functions, rules, invokers.
        final PackageCompilationData compilationData = pkg.getPackageCompilationData();
        final PackageCompilationData newCompilationData = newPkg.getPackageCompilationData();
        final String[] files = newCompilationData.list();
        for ( int i = 0, length = files.length; i < length; i++ ) {
            compilationData.write( files[i],
                                   newCompilationData.read( files[i] ) );
        }

        // Merge imports
        imports.addAll( newPkg.getImports() );

        // Add invokers
        compilationData.putAllInvokers( newCompilationData.getInvokers() );
        
        if ( compilationData.isDirty() ) {
            if ( this.reloadPackageCompilationData == null ) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addPackageCompilationData( compilationData );            
        }

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
            Map flows = newPkg.getRuleFlows();
            for ( Iterator iter = flows.values().iterator(); iter.hasNext(); ) {
                Process flow = (Process) iter.next();
                pkg.addRuleFlow( flow );
            }
        }
    }

    protected synchronized void addRule(final Rule rule) throws InvalidPatternException {
        if ( !rule.isValid() ) {
            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
        }
    }

    public synchronized void removePackage(final String packageName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );

            // INVARIANT: lastAquiredLock always contains the index of the last aquired lock +1 
            // in the working memory array 
            int lastAquiredLock = 0;
            // get a snapshot of current working memories for locking
            final InternalWorkingMemory[] wms = getWorkingMemories();

            try {
                // Iterate each workingMemory and lock it
                // This is so we don't update the Rete network during propagation
                for ( lastAquiredLock = 0; lastAquiredLock < wms.length; lastAquiredLock++ ) {
                    wms[lastAquiredLock].getLock().lock();
                }

                final Rule[] rules = pkg.getRules();

                for ( int i = 0; i < rules.length; ++i ) {
                    removeRule( rules[i] );
                }

                this.packageClassLoader.removeClassLoader( pkg.getPackageCompilationData().getClassLoader() );

                pkg.clear();

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
                Map flows = pkg.getRuleFlows();
                for ( Iterator iter = flows.keySet().iterator(); iter.hasNext(); ) {
                    removeProcess( (String) iter.next() );
                }
                // removing the package itself from the list
                this.pkgs.remove( pkg.getName() );
            } finally {
                // Iterate each workingMemory and attempt to fire any rules, that were activated as a result 
                // of the new rule addition. Unlock after fireAllRules();

                // as per the INVARIANT defined above, we need to iterate from lastAquiredLock-1 to 0. 
                for ( lastAquiredLock--; lastAquiredLock > -1; lastAquiredLock-- ) {
                    wms[lastAquiredLock].fireAllRules();
                    wms[lastAquiredLock].getLock().unlock();
                }
            }
        }
    }

    public void removeRule(final String packageName,
                           final String ruleName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );
            final Rule rule = pkg.getRule( ruleName );

            // INVARIANT: lastAquiredLock always contains the index of the last aquired lock +1 
            // in the working memory array 
            int lastAquiredLock = 0;
            // get a snapshot of current working memories for locking
            final InternalWorkingMemory[] wms = getWorkingMemories();
            
            PackageCompilationData compilationData = null;

            try {
                // Iterate each workingMemory and lock it
                // This is so we don't update the Rete network during propagation
                for ( lastAquiredLock = 0; lastAquiredLock < wms.length; lastAquiredLock++ ) {
                    wms[lastAquiredLock].getLock().lock();
                }

                removeRule( rule );
                compilationData = pkg.removeRule( rule );
                if ( this.reloadPackageCompilationData == null ) {
                    this.reloadPackageCompilationData = new ReloadPackageCompilationData();
                }
                this.reloadPackageCompilationData.addPackageCompilationData( compilationData );

            } finally {
                // Iterate each workingMemory and attempt to fire any rules, that were activated as a result 
                // of the new rule addition. Unlock after fireAllRules();

                // as per the INVARIANT defined above, we need to iterate from lastAquiredLock-1 to 0. 
                for ( lastAquiredLock--; lastAquiredLock > -1; lastAquiredLock-- ) {
                    wms[lastAquiredLock].getLock().unlock();
                }
            }                       
        }
    }

    protected abstract void removeRule(Rule rule);
    
    public void removeFunction(String packageName, String functionName) {
        synchronized ( this.pkgs ) {
            final Package pkg = (Package) this.pkgs.get( packageName );
            PackageCompilationData compilationData = pkg.removeFunction( functionName );
            
            if ( this.reloadPackageCompilationData == null ) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addPackageCompilationData( compilationData );            
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
            process = ( Process ) this.processes.get( id );
        }
        return process;
    }

    protected synchronized void addStatefulSession(final StatefulSession statefulSession) {
        this.statefulSessions.add( statefulSession );
    }

    public Package getPackage(String name) {
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

        final AbstractWorkingMemory workingMemory = (AbstractWorkingMemory) streamWithLoader.readObject();

        synchronized ( this.pkgs ) {
            workingMemory.setRuleBase( this );
            return (StatefulSession) workingMemory;
        }
    }

    public void addClass(String className,
                         byte[] bytes) {
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
           }
        }
    }    
    
    public static class ReloadPackageCompilationData implements RuleBaseAction {
        private Set set;
        
        public void addPackageCompilationData(PackageCompilationData packageCompilationData) {
            if ( set == null ) {
                this.set = new HashSet();
            }
            
            this.set.add( packageCompilationData );
        }
        
        public void execute(InternalRuleBase ruleBase) {
            for ( Iterator it = this.set.iterator(); it.hasNext(); ) {
                PackageCompilationData packageCompilationData = ( PackageCompilationData ) it.next();
                packageCompilationData.reload();
            }
        }
    }

    public static interface RuleBaseAction extends Serializable  {
        public void execute(InternalRuleBase ruleBase);
    }
}
