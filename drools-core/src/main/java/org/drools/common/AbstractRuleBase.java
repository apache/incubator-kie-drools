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

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
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
import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.base.ClassFieldAccessorCache;
import org.drools.definition.type.FactType;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleBaseEventSupport;
import org.drools.impl.EnvironmentFactory;
import org.drools.process.core.Process;
import org.drools.reteoo.ReteooBuilder;
import org.drools.rule.CompositeClassLoader;
import org.drools.rule.DialectRuntimeRegistry;
import org.drools.rule.Function;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.FactHandleFactory;
import org.drools.util.ObjectHashSet;

/**
 * Implementation of <code>RuleBase</code>.
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
abstract public class AbstractRuleBase implements InternalRuleBase, Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    private String id;

    private int workingMemoryCounter;

    private RuleBaseConfiguration config;

    protected Map<String, Package> pkgs;

    private Map processes;

    private Map agendaGroupRuleTotals;

    private transient CompositeClassLoader rootClassLoader;

    /**
     * The fact handle factory.
     */
    private FactHandleFactory factHandleFactory;

    private transient Map<String, Class<?>> globals;

    private ReloadPackageCompilationData reloadPackageCompilationData = null;

    private RuleBaseEventSupport eventSupport = new RuleBaseEventSupport(this);

    private transient ObjectHashSet statefulSessions;

    // wms used for lock list during dynamic updates
    private InternalWorkingMemory[] wms;

    // indexed used to track invariant lock
    private int lastAquiredLock;

    // lock for entire rulebase, used for dynamic updates
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * This lock is used when adding to, or reading the <field>statefulSessions</field>
     */
    private final ReentrantLock statefuleSessionLock = new ReentrantLock();

    private int additionsSinceLock;
    private int removalsSinceLock;

    private transient Map<Class<?>, TypeDeclaration> classTypeDeclaration;

    private List<RuleBasePartitionId> partitionIDs;

    private ClassFieldAccessorCache classFieldAccessorCache;

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
     * @param id The rete network.
     */
    public AbstractRuleBase(final String id,
                            final RuleBaseConfiguration config,
                            final FactHandleFactory factHandleFactory) {
        if (id != null) {
            this.id = id;
        } else {
            this.id = "default";
        }
        this.config = (config != null) ? config : new RuleBaseConfiguration();
        this.config.makeImmutable();
        this.factHandleFactory = factHandleFactory;

        if (this.config.isSequential()) {
            this.agendaGroupRuleTotals = new HashMap();
        }

        this.rootClassLoader = new CompositeClassLoader(this.config.getClassLoader());
        this.pkgs = new HashMap<String, Package>();
        this.processes = new HashMap();
        this.globals = new HashMap<String, Class<?>>();
        this.statefulSessions = new ObjectHashSet();

        this.classTypeDeclaration = new HashMap<Class<?>, TypeDeclaration>();
        this.partitionIDs = new ArrayList<RuleBasePartitionId>();

        this.classFieldAccessorCache = new ClassFieldAccessorCache(this.rootClassLoader);
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     */
    public void writeExternal(final ObjectOutput out) throws IOException {
        ObjectOutput droolsStream;
        boolean isDrools = out instanceof DroolsObjectOutputStream;
        ByteArrayOutputStream bytes;

        if (isDrools) {
            droolsStream = out;
            bytes = null;
        } else {
            bytes = new ByteArrayOutputStream();
            droolsStream = new DroolsObjectOutputStream(bytes);
        }

        droolsStream.writeObject(this.config);
        droolsStream.writeObject(this.pkgs);

        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        droolsStream.writeObject(this.id);
        droolsStream.writeInt(this.workingMemoryCounter);
        droolsStream.writeObject(this.processes);
        droolsStream.writeObject(this.agendaGroupRuleTotals);
        droolsStream.writeUTF(this.factHandleFactory.getClass().getName());
        droolsStream.writeObject(buildGlobalMapForSerialization());
        droolsStream.writeObject(this.partitionIDs);

        this.eventSupport.removeEventListener(RuleBaseEventListener.class);
        droolsStream.writeObject(this.eventSupport);
        if (!isDrools) {
            bytes.close();
            out.writeObject(bytes.toByteArray());
        }
    }

    private Map<String, String> buildGlobalMapForSerialization() {
        Map<String, String> gl = new HashMap<String, String>();
        for (Map.Entry<String, Class<?>> entry : this.globals.entrySet()) {
            gl.put(entry.getKey(), entry.getValue().getName());
        }
        return gl;
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     */
    public void readExternal(final ObjectInput in) throws IOException,
            ClassNotFoundException {
        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        DroolsObjectInput droolsStream;
        boolean isDrools = in instanceof DroolsObjectInput;

        if (isDrools) {
            droolsStream = (DroolsObjectInput) in;
        } else {
            droolsStream = new DroolsObjectInputStream((ObjectInputStream) in);

        }

        this.rootClassLoader = new CompositeClassLoader(droolsStream.getParentClassLoader());
        droolsStream.setClassLoader(this.rootClassLoader);
        droolsStream.setRuleBase(this);

        this.classFieldAccessorCache = new ClassFieldAccessorCache(this.rootClassLoader);

        this.config = (RuleBaseConfiguration) droolsStream.readObject();
        this.config.setClassLoader(droolsStream.getParentClassLoader());

        this.pkgs = (Map<String, Package>) droolsStream.readObject();


        for (final Object object : this.pkgs.values()) {
            ((Package) object).getDialectRuntimeRegistry().onAdd(this.rootClassLoader);
        }

        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        this.id = (String) droolsStream.readObject();
        this.workingMemoryCounter = droolsStream.readInt();

        this.processes = (Map) droolsStream.readObject();
        this.agendaGroupRuleTotals = (Map) droolsStream.readObject();
        Class cls = null;
        try {
            cls = droolsStream.getParentClassLoader().loadClass(droolsStream.readUTF());
            this.factHandleFactory = (FactHandleFactory) cls.newInstance();
        } catch (InstantiationException e) {
            DroolsObjectInputStream.newInvalidClassException(cls,
                    e);
        } catch (IllegalAccessException e) {
            DroolsObjectInputStream.newInvalidClassException(cls,
                    e);
        }

        for (final Object object : this.pkgs.values()) {
            ((Package) object).getDialectRuntimeRegistry().onBeforeExecute();
            ((Package) object).getClassFieldAccessorStore().setClassFieldAccessorCache(this.classFieldAccessorCache);
            ((Package) object).getClassFieldAccessorStore().wire();
        }

        this.populateTypeDeclarationMaps();

        // read globals
        Map<String, String> globs = (Map<String, String>) droolsStream.readObject();
        populateGlobalsMap(globs);

        this.partitionIDs = (List<RuleBasePartitionId>) droolsStream.readObject();

        this.eventSupport = (RuleBaseEventSupport) droolsStream.readObject();
        this.eventSupport.setRuleBase(this);
        this.statefulSessions = new ObjectHashSet();

        if (!isDrools) {
            droolsStream.close();
        }
    }

    /**
     * globals class types must be re-wired after serialization
     *
     * @param globs
     * @throws ClassNotFoundException
     */
    private void populateGlobalsMap(Map<String, String> globs) throws ClassNotFoundException {
        this.globals = new HashMap<String, Class<?>>();
        for (Map.Entry<String, String> entry : globs.entrySet()) {
            this.globals.put(entry.getKey(), this.rootClassLoader.loadClass(entry.getValue()));
        }
    }

    /**
     * type classes must be re-wired after serialization
     *
     * @throws ClassNotFoundException
     */
    private void populateTypeDeclarationMaps() throws ClassNotFoundException {
        this.classTypeDeclaration = new HashMap<Class<?>, TypeDeclaration>();
        for (Package pkg : this.pkgs.values()) {
            for (TypeDeclaration type : pkg.getTypeDeclarations().values()) {
                type.setTypeClass(this.rootClassLoader.loadClass(type.getTypeClassName()));
                this.classTypeDeclaration.put(type.getTypeClass(),
                        type);
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
        return newStatefulSession(new SessionConfiguration(), EnvironmentFactory.newEnvironment());
    }

    public void disposeStatefulSession(final StatefulSession statefulSession) {
        try {
            statefuleSessionLock.lock();

            this.statefulSessions.remove(statefulSession);
            for (Object listener : statefulSession.getRuleBaseUpdateListeners()) {
                this.removeEventListener((RuleBaseEventListener) listener);
            }
        } finally {
            statefuleSessionLock.unlock();
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

    public FactHandleFactory newFactHandleFactory(int id,
                                                  long counter) {
        return this.factHandleFactory.newInstance(id,
                counter);
    }

    public Process[] getProcesses() {
        return (Process[]) this.processes.values().toArray(new Process[this.processes.size()]);
    }

    public Package[] getPackages() {
        return this.pkgs.values().toArray(new Package[this.pkgs.size()]);
    }

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
        for (this.lastAquiredLock = 0; this.lastAquiredLock < this.wms.length; this.lastAquiredLock++) {            
            this.wms[this.lastAquiredLock].getLock().lock();
        }

        this.eventSupport.fireAfterRuleBaseLocked();
    }

    public void unlock() {
        this.eventSupport.fireBeforeRuleBaseUnlocked();

        // Iterate each workingMemory and attempt to fire any rules, that were activated as a result

        // as per the INVARIANT defined above, we need to iterate from lastAquiredLock-1 to 0.
        for (this.lastAquiredLock--; this.lastAquiredLock > -1; this.lastAquiredLock--) {
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
     * @param newPkg The package to add.
     */
    public void addPackages(final Collection<Package> newPkgs) {
        synchronized (this.pkgs) {
            boolean doUnlock = false;
            // only acquire the lock if it hasn't been done explicitely
            if (!this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0)) {
                lock();
                doUnlock = true;
            }
            try {
                // we need to merge all byte[] first, so that the root classloader can resolve classes
                for (Package newPkg : newPkgs) {
                    newPkg.checkValidity();
                    this.additionsSinceLock++;
                    this.eventSupport.fireBeforePackageAdded(newPkg);

                    Package pkg = this.pkgs.get(newPkg.getName());
                    if (pkg == null) {
                        pkg = new Package(newPkg.getName());

                        // @TODO we really should have a single root cache
                        pkg.setClassFieldAccessorCache(this.classFieldAccessorCache);
                        pkgs.put(pkg.getName(),
                                pkg);
                    }

                    // first merge anything related to classloader re-wiring
                    pkg.getDialectRuntimeRegistry().merge(newPkg.getDialectRuntimeRegistry(), this.rootClassLoader);
                }

                // now iterate again, this time onBeforeExecute will handle any wiring or cloader re-creating that needs to be done as part of the merge
                for (Package newPkg : newPkgs) {
                    Package pkg = this.pkgs.get(newPkg.getName());
                    
                    // this needs to go here, as functions will set a java dialect to dirty
                    if (newPkg.getFunctions() != null) {
                        for (Map.Entry<String, Function> entry : newPkg.getFunctions().entrySet()) {
                            pkg.addFunction(entry.getValue());
                        }
                    }
                    
                    pkg.getDialectRuntimeRegistry().onBeforeExecute();
                    // with the classloader recreated for all byte[] classes, we should now merge and wire any new accessors
                    pkg.getClassFieldAccessorStore().merge(newPkg.getClassFieldAccessorStore());
                }

                for (Package newPkg : newPkgs) {
                    Package pkg = this.pkgs.get(newPkg.getName());

                    // we have to do this before the merging, as it does some classloader resolving
                    TypeDeclaration lastType = null;
                    try {
                        // Add the type declarations to the RuleBase
                        if (newPkg.getTypeDeclarations() != null) {
                            // add type declarations
                            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                                lastType = type;
                                type.setTypeClass(this.rootClassLoader.loadClass(type.getTypeClassName()));
                                // @TODO should we allow overrides? only if the class is not in use.
                                if (!this.classTypeDeclaration.containsKey(type.getTypeClass())) {
                                    // add to rulebase list of type declarations                        
                                    this.classTypeDeclaration.put(type.getTypeClass(),
                                            type);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeDroolsException("unable to resolve Type Declaration class '" + lastType.getTypeName() + "'");
                    }

                    // now merge the new package into the existing one
                    mergePackage(pkg,
                            newPkg);

                    // add the rules to the RuleBase
                    final Rule[] rules = newPkg.getRules();
                    for (int i = 0; i < rules.length; ++i) {
                        addRule(newPkg,
                                rules[i]);
                    }

                    // add the flows to the RuleBase
                    if (newPkg.getRuleFlows() != null) {
                        final Map flows = newPkg.getRuleFlows();
                        for (final Object object : flows.entrySet()) {
                            final Entry flow = (Entry) object;
                            this.processes.put(flow.getKey(),
                                    flow.getValue());
                        }
                    }

                    this.eventSupport.fireAfterPackageAdded(newPkg);
                }
            } finally {
                // only unlock if it had been acquired implicitely
                if (doUnlock) {
                    unlock();
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
                              final Package newPkg) {
        // Merge imports
        final Map<String, ImportDeclaration> imports = pkg.getImports();
        imports.putAll(newPkg.getImports());

        String lastType = null;
        try {
            // merge globals
            if (newPkg.getGlobals() != null && newPkg.getGlobals() != Collections.EMPTY_MAP) {
                Map<String, String> globals = pkg.getGlobals();
                // Add globals
                for (final Map.Entry<String, String> entry : newPkg.getGlobals().entrySet()) {
                    final String identifier = entry.getKey();
                    final String type = entry.getValue();
                    lastType = type;
                    if (globals.containsKey(identifier) && !globals.get(identifier).equals(type)) {
                        throw new PackageIntegrationException(pkg);
                    } else {
                        pkg.addGlobal(identifier,
                                this.rootClassLoader.loadClass(type));
                        // this isn't a package merge, it's adding to the rulebase, but I've put it here for convienience
                        this.globals.put(identifier,
                                this.rootClassLoader.loadClass(type));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeDroolsException("Unable to resolve class '" + lastType + "'");
        }

        // merge the type declarations
        if (newPkg.getTypeDeclarations() != null) {
            // add type declarations
            for (TypeDeclaration type : newPkg.getTypeDeclarations().values()) {
                // @TODO should we allow overrides? only if the class is not in use.
                if (!pkg.getTypeDeclarations().containsKey(type.getTypeName())) {
                    // add to package list of type declarations
                    pkg.addTypeDeclaration(type);
                }
            }
        }

        //Merge rules into the RuleBase package
        //as this is needed for individual rule removal later on
        final Rule[] newRules = newPkg.getRules();
        for (int i = 0; i < newRules.length; i++) {
            final Rule newRule = newRules[i];

            // remove the rule if it already exists
            if (pkg.getRule(newRule.getName()) != null) {
                removeRule(pkg,
                        pkg.getRule(newRule.getName()));
            }

            pkg.addRule(newRule);
        }

        //Merge The Rule Flows
        if (newPkg.getRuleFlows() != null) {
            final Map flows = newPkg.getRuleFlows();
            for (final Iterator iter = flows.values().iterator(); iter.hasNext();) {
                final Process flow = (Process) iter.next();
                pkg.addProcess(flow);
            }
        }

//        // this handles re-wiring any dirty Packages, it's done lazily to allow incremental 
//        // additions without incurring the repeated cost.
//        if ( this.reloadPackageCompilationData == null ) {
//            this.reloadPackageCompilationData = new ReloadPackageCompilationData();
//        }
//        this.reloadPackageCompilationData.addDialectDatas( pkg.getDialectRuntimeRegistry() );
    }

    public TypeDeclaration getTypeDeclaration(Class<?> clazz) {
        return this.classTypeDeclaration.get(clazz);
    }

    public Collection<TypeDeclaration> getTypeDeclarations() {
        return this.classTypeDeclaration.values();
    }

    public void addRule(final Package pkg,
                        final Rule rule) throws InvalidPatternException {
        synchronized (this.pkgs) {
            this.eventSupport.fireBeforeRuleAdded(pkg,
                    rule);
            //        if ( !rule.isValid() ) {
            //            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
            //        }
            addRule(rule);
            this.eventSupport.fireAfterRuleAdded(pkg,
                    rule);
        }
    }

    protected abstract void addRule(final Rule rule) throws InvalidPatternException;

    public void removePackage(final String packageName) {
        synchronized (this.pkgs) {
            final Package pkg = this.pkgs.get(packageName);
            if (pkg == null) {
                throw new IllegalArgumentException("Package name '" + packageName + "' does not exist for this Rule Base.");
            }

            // only acquire the lock if it hasn't been done explicitely
            boolean doUnlock = false;
            if (!this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0)) {
                lock();
                doUnlock = true;
            }
            try {
                this.removalsSinceLock++;

                this.eventSupport.fireBeforePackageRemoved(pkg);

                final Rule[] rules = pkg.getRules();

                for (int i = 0; i < rules.length; ++i) {
                    removeRule(pkg,
                            rules[i]);
                }

                // getting the list of referenced globals
                final Set referencedGlobals = new HashSet();
                for (final Iterator it = this.pkgs.values().iterator(); it.hasNext();) {
                    final org.drools.rule.Package pkgref = (org.drools.rule.Package) it.next();
                    if (pkgref != pkg) {
                        referencedGlobals.addAll(pkgref.getGlobals().keySet());
                    }
                }
                // removing globals declared inside the package that are not shared
                for (final Iterator it = pkg.getGlobals().keySet().iterator(); it.hasNext();) {
                    final String globalName = (String) it.next();
                    if (!referencedGlobals.contains(globalName)) {
                        this.globals.remove(globalName);
                    }
                }
                //and now the rule flows
                final Map flows = pkg.getRuleFlows();
                for (final Iterator iter = flows.keySet().iterator(); iter.hasNext();) {
                    removeProcess((String) iter.next());
                }
                // removing the package itself from the list
                this.pkgs.remove(pkg.getName());

                pkg.getDialectRuntimeRegistry().onRemove();

                //clear all members of the pkg
                pkg.clear();

                this.eventSupport.fireAfterPackageRemoved(pkg);

                // only unlock if it had been acquired implicitely
            } finally {
                if (doUnlock) {
                    unlock();
                }
            }
        }
    }

    public void removeRule(final String packageName,
                           final String ruleName) {
        synchronized (this.pkgs) {
            final Package pkg = this.pkgs.get(packageName);
            if (pkg == null) {
                throw new IllegalArgumentException("Package name '" + packageName + "' does not exist for this Rule Base.");
            }

            final Rule rule = pkg.getRule(ruleName);
            if (rule == null) {
                throw new IllegalArgumentException("Rule name '" + ruleName + "' does not exist in the Package '" + packageName + "'.");
            }

            // only acquire the lock if it hasn't been done explicitely
            boolean doUnlock = false;
            if (!this.lock.isHeldByCurrentThread() && (this.wms == null || this.wms.length == 0)) {
                lock();
                doUnlock = true;
            }
            this.removalsSinceLock++;

            removeRule(pkg,
                    rule);
            pkg.removeRule(rule);
            if (this.reloadPackageCompilationData == null) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addDialectDatas(pkg.getDialectRuntimeRegistry());

            // only unlock if it had been acquired implicitely
            if (doUnlock) {
                unlock();
            }
        }
    }

    public void removeRule(final Package pkg,
                           final Rule rule) {
        synchronized (this.pkgs) {
            this.eventSupport.fireBeforeRuleRemoved(pkg,
                    rule);

            removeRule(rule);
            this.eventSupport.fireAfterRuleRemoved(pkg,
                    rule);
        }
    }

    protected abstract void removeRule(Rule rule);

    public void removeFunction(final String packageName,
                               final String functionName) {
        synchronized (this.pkgs) {
            final Package pkg = this.pkgs.get(packageName);
            if (pkg == null) {
                throw new IllegalArgumentException("Package name '" + packageName + "' does not exist for this Rule Base.");
            }

            this.eventSupport.fireBeforeFunctionRemoved(pkg,
                    functionName);

            if (!pkg.getFunctions().containsKey(functionName)) {
                throw new IllegalArgumentException("function name '" + packageName + "' does not exist in the Package '" + packageName + "'.");
            }

            pkg.removeFunction(functionName);

            if (this.reloadPackageCompilationData == null) {
                this.reloadPackageCompilationData = new ReloadPackageCompilationData();
            }
            this.reloadPackageCompilationData.addDialectDatas(pkg.getDialectRuntimeRegistry());

            this.eventSupport.fireAfterFunctionRemoved(pkg,
                    functionName);
        }
    }

    public void addProcess(final Process process) {
        synchronized (this.pkgs) {
            this.processes.put(process.getId(),
                    process);
        }

    }

    public void removeProcess(final String id) {
        synchronized (this.pkgs) {
            this.processes.remove(id);
        }
    }

    public Process getProcess(final String id) {
        Process process;
        synchronized (this.pkgs) {
            process = (Process) this.processes.get(id);
        }
        return process;
    }

    public void addStatefulSession(final StatefulSession statefulSession) {
        try {
            statefuleSessionLock.lock();

            this.statefulSessions.add(statefulSession);
        } finally {
            statefuleSessionLock.unlock();
        }

    }

    public Package getPackage(final String name) {
        return this.pkgs.get(name);
    }

    public StatefulSession[] getStatefulSessions() {
        final StatefulSession[] copyOfSessions;
        try {
            statefuleSessionLock.lock();
            copyOfSessions = new StatefulSession[this.statefulSessions.size()];

            this.statefulSessions.toArray(copyOfSessions);
        } finally {
            statefuleSessionLock.unlock();
        }

        return copyOfSessions;
    }

    public InternalWorkingMemory[] getWorkingMemories() {
        final InternalWorkingMemory[] copyOfMemories;
        try {
            statefuleSessionLock.lock();
            copyOfMemories = new InternalWorkingMemory[this.statefulSessions.size()];

            this.statefulSessions.toArray(copyOfMemories);
        } finally {
            statefuleSessionLock.unlock();
        }

        return copyOfMemories;
    }

    public RuleBaseConfiguration getConfiguration() {
        return this.config;
    }

    public CompositeClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    public void executeQueuedActions() {
        synchronized (this.pkgs) {
            if (this.reloadPackageCompilationData != null) {
                this.reloadPackageCompilationData.execute(this);
                this.reloadPackageCompilationData = null;
            }
        }
    }

    public RuleBasePartitionId createNewPartitionId() {
        RuleBasePartitionId p;
        synchronized (this.partitionIDs) {
            p = new RuleBasePartitionId("P-" + this.partitionIDs.size());
            this.partitionIDs.add(p);
        }
        return p;
    }

    public List<RuleBasePartitionId> getPartitionIds() {
        return this.partitionIDs;
    }

    public void addEventListener(final RuleBaseEventListener listener) {
        // no need for synchonization or locking because eventSupport is thread-safe
        this.eventSupport.addEventListener(listener);
    }

    public void removeEventListener(final RuleBaseEventListener listener) {
        // no need for synchonization or locking because eventSupport is thread-safe
        this.eventSupport.removeEventListener(listener);
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        // no need for synchonization or locking because eventSupport is thread-safe
        return this.eventSupport.getEventListeners();
    }

    public boolean isEvent(Class clazz) {
        for (Package pkg : this.pkgs.values()) {
            if (pkg.isEvent(clazz)) {
                return true;
            }
        }
        return false;
    }

    public FactType getFactType(final String name) {
        for (Package pkg : this.pkgs.values()) {
            FactType type = pkg.getFactType(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public static class ReloadPackageCompilationData
            implements
            RuleBaseAction {
        private static final long serialVersionUID = 1L;
        private Set<DialectRuntimeRegistry> set;

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            set = (Set<DialectRuntimeRegistry>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(set);
        }

        public void addDialectDatas(final DialectRuntimeRegistry registry) {
            if (this.set == null) {
                this.set = new HashSet<DialectRuntimeRegistry>();
            }
            if (!this.set.contains(registry)) this.set.add(registry);
        }

        public void execute(final InternalRuleBase ruleBase) {
            for (final DialectRuntimeRegistry registry : this.set) {
                registry.onBeforeExecute();
            }
        }
    }

    public static interface RuleBaseAction
            extends
            Externalizable {
        public void execute(InternalRuleBase ruleBase);
    }

    public ClassFieldAccessorCache getClassFieldAccessorCache() {
        return this.classFieldAccessorCache;
    }
}
