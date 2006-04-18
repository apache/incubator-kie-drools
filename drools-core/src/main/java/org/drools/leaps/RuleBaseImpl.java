package org.drools.leaps;
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





import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.drools.FactException;
import org.drools.PackageIntegrationException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Package;
import org.drools.rule.PackageCompilationData;
import org.drools.rule.Rule;
import org.drools.spi.FactHandleFactory;

/**
 * This base class for the engine and analogous to Drool's RuleBase class. It
 * has a similar interface adapted to the Leaps algorithm
 * 
 * @author Alexander Bagerman
 * 
 */
public class RuleBaseImpl
    implements
    RuleBase {
    private static final long       serialVersionUID = 1487738104393155409L;

    private transient Map           leapsRules;

    /**
     * The fact handle factory.
     */
    private final FactHandleFactory factHandleFactory;

    private transient Map           globalDeclarations;

    private final Map               rulesPackages;

    /**
     * WeakHashMap to keep references of WorkingMemories but allow them to be
     * garbage collected
     */
    private transient Map           workingMemories;

    /** Special value when adding to the underlying map. */
    private static final Object     PRESENT          = new Object();

    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     * @throws PackageIntegrationException
     */
    public RuleBaseImpl() throws PackageIntegrationException {
        this( new HandleFactory() );
    }

    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     * @param conflictResolver
     *            The conflict resolver.
     * @param factHandleFactory
     *            The fact handle factory.
     * @param pkgs
     * @param applicationData
     * @throws PackageIntegrationException
     * @throws Exception
     */
    public RuleBaseImpl(FactHandleFactory factHandleFactory) {
        // casting to make sure that it's leaps handle factory
        this.factHandleFactory = (HandleFactory) factHandleFactory;
        this.globalDeclarations = new HashMap();
        this.workingMemories = new WeakHashMap();

        this.rulesPackages = new HashMap();
        this.leapsRules = new HashMap();
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory() {
        return newWorkingMemory( true );
    }

    /**
     * @see RuleBase
     */
    public WorkingMemory newWorkingMemory(boolean keepReference) {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( this );
        // add all rules added so far
        for ( Iterator it = this.leapsRules.values().iterator(); it.hasNext(); ) {
            workingMemory.addLeapsRules( (List) it.next() );
        }
        //
        if ( keepReference ) {
            this.workingMemories.put( workingMemory,
                                      RuleBaseImpl.PRESENT );
        }
        return workingMemory;
    }

    void disposeWorkingMemory(WorkingMemory workingMemory) {
        this.workingMemories.remove( workingMemory );
    }

    /**
     * @see RuleBase
     */
    public FactHandleFactory getFactHandleFactory() {
        return this.factHandleFactory;
    }

    /**
     * returns NEW fact handle factory because each working memory needs the new
     * one
     * 
     * @see RuleBase
     */
    public FactHandleFactory newFactHandleFactory() {
        return this.factHandleFactory.newInstance();
    }

    /**
     * @see RuleBase
     */

    public Package[] getPackages() {
        return (Package[]) this.rulesPackages.values().toArray( new Package[this.rulesPackages.size()] );
    }

    public Map getGlobalDeclarations() {
        return this.globalDeclarations;
    }

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network.
     * 
     * @param rulesPackage
     *            The rule-set to add.
     * @throws PackageIntegrationException
     * 
     * @throws FactException
     * @throws InvalidPatternException
     */
    public void addPackage(Package newPackage) throws PackageIntegrationException {
        newPackage.checkValidity();
        Package pkg = (Package) this.rulesPackages.get( newPackage.getName() );
        if ( pkg != null ) {
            mergePackage( pkg,
                          newPackage );
        } else {
            this.rulesPackages.put( newPackage.getName(),
                                    newPackage );
        }

        Map newGlobals = newPackage.getGlobals();

        // Check that the global data is valid, we cannot change the type
        // of an already declared global variable
        for ( Iterator it = newGlobals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            Class type = (Class) newGlobals.get( identifier );
            if ( this.globalDeclarations.containsKey( identifier ) && !this.globalDeclarations.get( identifier ).equals( type ) ) {
                throw new PackageIntegrationException( pkg );
            }
        }
        this.globalDeclarations.putAll( newGlobals );

        Rule[] rules = newPackage.getRules();

        for ( int i = 0; i < rules.length; ++i ) {
            addRule( rules[i] );
        }
    }

    public void mergePackage(Package existingPackage,
                             Package newPackage) throws PackageIntegrationException {
        Map globals = existingPackage.getGlobals();
        List imports = existingPackage.getImports();

        // First update the binary files
        // @todo: this probably has issues if you add classes in the incorrect
        // order - functions, rules, invokers.
        PackageCompilationData compilationData = existingPackage.getPackageCompilationData();
        PackageCompilationData newCompilationData = newPackage.getPackageCompilationData();
        String[] files = newCompilationData.list();
        for ( int i = 0, length = files.length; i < length; i++ ) {
            compilationData.write( files[i],
                                   newCompilationData.read( files[i] ) );
        }

        // Merge imports
        imports.addAll( newPackage.getImports() );

        // Add invokers
        compilationData.putAllInvokers( newCompilationData.getInvokers() );

        // Add globals
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            Class type = (Class) globals.get( identifier );
            if ( globals.containsKey( identifier ) && !globals.get( identifier ).equals( type ) ) {
                throw new PackageIntegrationException( "Unable to merge new Package",
                                                       newPackage );
            }
        }
    }

    /**
     * Creates leaps rule wrappers and propagate rule to the working memories
     * 
     * @param rule
     * @throws FactException
     * @throws InvalidPatternException
     */
    public void addRule(Rule rule) throws FactException,
                                  InvalidPatternException {
        if ( !rule.isValid() ) {
            throw new IllegalArgumentException( "The rule called " + rule.getName() + " is not valid. Check for compile errors reported." );
        }
        List rules = Builder.processRule( rule );

        this.leapsRules.put( rule,
                             rules );

        for ( Iterator it = this.workingMemories.keySet().iterator(); it.hasNext(); ) {
            ((WorkingMemoryImpl) it.next()).addLeapsRules( rules );
        }

        // Iterate each workingMemory and attempt to fire any rules, that were
        // activated as a result of the new rule addition
        for ( Iterator it = this.workingMemories.keySet().iterator(); it.hasNext(); ) {
            WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) it.next();
            workingMemory.fireAllRules();
        }
    }

    public void removeRule(Rule rule) {
        for ( Iterator it = this.workingMemories.keySet().iterator(); it.hasNext(); ) {
            ((WorkingMemoryImpl) it.next()).removeRule( (List) this.leapsRules.remove( rule ) );
        }
    }

    public Set getWorkingMemories() {
        return this.workingMemories.keySet();
    }

    /**
     * This is to allow the RuleBase to be serializable.
     */
    private void readObject(ObjectInputStream is) throws ClassNotFoundException,
                                                 IOException,
                                                 Exception {
        //always perform the default de-serialization first
        is.defaultReadObject();

        this.leapsRules = new HashMap();
        this.globalDeclarations = new HashMap();
        this.workingMemories = new WeakHashMap();

        Package[] packages = this.getPackages();
        this.rulesPackages.clear();
        for ( int i = 0; i < packages.length; i++ ) {
            this.addPackage( packages[i] );
            Rule[] rules = packages[i].getRules();

            for ( int k = 0; k < rules.length; k++ ) {
                addRule( rules[k] );
            }
        }
    }
}