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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.StatefulSession;
import org.drools.definition.process.Process;
import org.drools.reteoo.Rete;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.Package;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.util.CompositeClassLoader;

public interface InternalRuleBase
    extends
    RuleBase {

    /**
     * @return the id
     */
    public String getId();
    
    public int nextWorkingMemoryCounter();

    public FactHandleFactory newFactHandleFactory();
    
    public FactHandleFactory newFactHandleFactory(int id, long counter) throws IOException ;

    public Map getGlobals();
    
    public Map getAgendaGroupRuleTotals();
    
    public RuleBaseConfiguration getConfiguration();
    
    public Package getPackage(String name);
    
    public Map getPackagesMap();

    void disposeStatefulSession(StatefulSession statefulSession);
    
    void executeQueuedActions();
    
    ReteooBuilder getReteooBuilder();

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
    public void assertObject(FactHandle handle,
                             Object object,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) throws FactException;

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
    public void retractObject(FactHandle handle,
                              PropagationContext context,
                              ReteooWorkingMemory workingMemory) throws FactException;
 
    public CompositeClassLoader getRootClassLoader();
    
    public Rete getRete();
    
    public InternalWorkingMemory[] getWorkingMemories();
    
    public Process getProcess(String id);
    
    public Process[] getProcesses();
    
    /**
     * Returns true if clazz represents an Event class. False otherwise.
     *  
     * @param clazz
     * @return
     */
    public boolean isEvent( Class<?> clazz );

    public int getNodeCount();

    /**
     * Returns the type declaration associated to the given class
     *
     * @param clazz
     * @return
     */
    public TypeDeclaration getTypeDeclaration(Class<?> clazz);

    /**
     * Returns a collection with all TypeDeclarations in this rulebase
     * 
     * @return
     */
    public Collection<TypeDeclaration> getTypeDeclarations();

    /**
     * Creates and allocates a new partition ID for this rulebase
     * 
     * @return
     */
    public RuleBasePartitionId createNewPartitionId();

    /**
     * Return the list of Partition IDs for this rulebase
     * @return
     */
    List<RuleBasePartitionId> getPartitionIds();
    
    /**
     * Acquires a read lock on the rulebase
     */
    public void readLock();
    
    /**
     * Releases a read lock on the rulebase
     */
    public void readUnlock();
    

}
