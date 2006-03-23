package org.drools.reteoo;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleIntegrationException;
import org.drools.PackageIntegrationException;
import org.drools.WorkingMemory;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.PackageCompilationData;
import org.drools.rule.Rule;
import org.drools.rule.Package;
import org.drools.spi.ClassObjectTypeResolver;
import org.drools.spi.ConflictResolver;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.ObjectTypeResolver;
import org.drools.spi.PropagationContext;

/**
 * Implementation of <code>RuleBase</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a> 
 * 
 * @version $Id: RuleBaseImpl.java,v 1.5 2005/08/14 22:44:12 mproctor Exp $
 */
public class RuleBaseImpl
    implements
    RuleBase {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The root Rete-OO for this <code>RuleBase</code>. */
    private final Rete              rete;

    private final ReteooBuilder       reteooBuilder;

    /** The fact handle factory. */
    private final FactHandleFactory factHandleFactory;

    private Map                     pkgs;

    private Map                     globalDeclarations;

    // @todo: replace this with a weak HashSet
    /**
     * WeakHashMap to keep references of WorkingMemories but allow them to be
     * garbage collected
     */
    private final transient Map     workingMemories;

    /** Special value when adding to the underlying map. */
    private static final Object     PRESENT = new Object();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     */
    public RuleBaseImpl() {
        this( new DefaultFactHandleFactory() );
    }
    
    /**
     * Construct.
     * 
     * @param rete
     *            The rete network.
     */
    public RuleBaseImpl(FactHandleFactory factHandleFactory) {
        ObjectTypeResolver resolver = new ClassObjectTypeResolver();
        this.rete = new Rete( resolver );
        this.reteooBuilder = new ReteooBuilder( this,
                                            resolver );
        this.factHandleFactory = factHandleFactory;
        this.pkgs = new HashMap();
        this.globalDeclarations = new HashMap();
        this.workingMemories = new WeakHashMap();
    }    

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

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
        if ( keepReference ) {
            this.workingMemories.put( workingMemory,
                                      RuleBaseImpl.PRESENT );
        }
        workingMemory.assertObject( InitialFactImpl.getInstance() );
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

    public FactHandleFactory newFactHandleFactory() {
        return this.factHandleFactory.newInstance();
    }

    /**
     * Retrieve the Rete-OO network for this <code>RuleBase</code>.
     * 
     * @return The RETE-OO network.
     */
    Rete getRete() {
        return this.rete;
    }

    /**
     * Assert a fact object.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The fact.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the assertion.
     */
    void assertObject(FactHandle handle,
                      Object object,
                      PropagationContext context,
                      WorkingMemoryImpl workingMemory) throws FactException {
        getRete().assertObject( (FactHandleImpl) handle,
                                context,
                                workingMemory );
    }

    void modifyObject(FactHandle handle,
                      PropagationContext context,
                      WorkingMemoryImpl workingMemory) throws FactException {
        getRete().modifyObject( (FactHandleImpl) handle,
                                context,
                                workingMemory );
    }

    /**
     * Retract a fact object.
     * 
     * @param handle
     *            The handle.
     * @param workingMemory
     *            The working-memory.
     * 
     * @throws FactException
     *             If an error occurs while performing the retraction.
     */
    void retractObject(FactHandle handle,
                       PropagationContext context,
                       WorkingMemoryImpl workingMemory) throws FactException {
        getRete().retractObject( (FactHandleImpl) handle,
                                 context,
                                 workingMemory );
    }

    public Package[] getPackages() {
        return (Package[]) this.pkgs.values().toArray( new Package[this.pkgs.size()] );
    }

    public Map getGlobalDeclarations() {
        return this.globalDeclarations;
    }

    /**
     * Add a <code>Package</code> to the network. Iterates through the
     * <code>Package</code> adding Each individual <code>Rule</code> to the
     * network.
     * 
     * @param pkg
     *            The rule-set to add.
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws FactException
     * @throws InvalidPatternException
     */
    public void addPackage(Package newPkg) throws RuleIntegrationException,
                                       PackageIntegrationException,
                                       FactException,
                                       InvalidPatternException {
        newPkg.checkValidity();
        Package pkg = ( Package ) this.pkgs.get(  newPkg.getName() );
        if ( pkg != null ) {
            mergePackage( pkg, newPkg );            
        } else {
            this.pkgs.put( newPkg.getName(),
                           newPkg );
        }
        
        Map newGlobals = newPkg.getGlobals();

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

        Rule[] rules = newPkg.getRules();

        for ( int i = 0; i < rules.length; ++i ) {
            addRule( rules[i] );
        }
    }
    
    public void mergePackage(Package pkg, Package newPkg) throws PackageIntegrationException {
        Map globals = pkg.getGlobals();
        List imports = pkg.getImports();
        
        // First update the binary files
        // @todo: this probably has issues if you add classes in the incorrect order - functions, rules, invokers.
        PackageCompilationData compilationData = pkg.getPackageCompilationData();
        PackageCompilationData newCompilationData = newPkg.getPackageCompilationData();
        String[] files = newCompilationData.list();
        for ( int i = 0, length = files.length; i < length; i++ ) {
            compilationData.write( files[i], newCompilationData.read( files[i] ) );
        }
        
        // Merge imports
        imports.addAll( newPkg.getImports() );
        
        // Add invokers
        compilationData.putAllInvokers( newCompilationData.getInvokers() );
        
        // Add globals
        for ( Iterator it = globals.keySet().iterator(); it.hasNext(); ) {
            String identifier = (String) it.next();
            Class type = (Class) globals.get( identifier );
            if ( globals.containsKey( identifier ) && !globals.get( identifier ).equals( type ) ) {
                throw new PackageIntegrationException( "Unable to merge new Package", newPkg );
            }
        }                       
    }

    public void addRule(Rule rule) throws InvalidPatternException {
        if (!rule.isValid())
            throw new IllegalArgumentException("The rule called " + rule.getName() + " is not valid. Check for compile errors reported.");
        // This adds the rule. ReteBuilder has a reference to the WorkingMemories and will propagate any existing facts.
        this.reteooBuilder.addRule( rule );
        
        // Iterate each workingMemory and attempt to fire any rules, that were activated as a result of the new rule addition
        for ( Iterator it = this.workingMemories.keySet().iterator(); it.hasNext(); ) {
            WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) it.next();
            workingMemory.fireAllRules();
        }
    }

    public void removeRule(Rule rule) {
        this.reteooBuilder.removeRule( rule );
    }

    public Set getWorkingMemories() {
        return this.workingMemories.keySet();
    }
}
