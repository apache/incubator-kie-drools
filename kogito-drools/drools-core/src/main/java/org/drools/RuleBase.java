package org.drools;

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
import java.io.InputStream;
import java.io.Serializable;

import org.drools.rule.Package;

/**
 * Active collection of <code>Rule</code>s.
 * 
 * <p>
 * From a <code>RuleBase</code> many <code>WorkingMemory</code> rule
 * sessions may be instantiated. Additionally, it may be inspected to determine
 * which <code>Package</code> s it contains.
 * </p>
 * 
 * @see WorkingMemory
 */
public interface RuleBase
    extends
    Serializable,
    RuleBaseEventManager {

    public static final int RETEOO = 1;

    StatelessSession newStatelessSession();

    /**
     * Create a new <code>WorkingMemory</code> session for this
     * <code>RuleBase</code>. By default the RuleBase retains a
     * weak reference to returned WorkingMemory.
     * 
     * <p>
     * The created <code>WorkingMemory</code> uses the default conflict
     * resolution strategy.
     * </p>
     * 
     * @see WorkingMemory
     * @see org.drools.conflict.DefaultConflictResolver
     * 
     * @return A newly initialized <code>WorkingMemory</code>.
     */
    StatefulSession newStatefulSession();

    /**
     * Create a new <code>WorkingMemory</code> session for this
     * <code>RuleBase</code>. Optionally the RuleBase retains a
     * weak reference to returned WorkingMemory.
     * 
     * <p>
     * The created <code>WorkingMemory</code> uses the default conflict
     * resolution strategy.
     * </p>
     * 
     * @see WorkingMemory
     * @see org.drools.conflict.DefaultConflictResolver
     * 
     * @return A newly initialized <code>WorkingMemory</code>.
     */
    StatefulSession newStatefulSession(boolean keepReference);

    /**
     * RuleBases handle the returning of a Serialized WorkingMemory
     * pass as an InputStream. If the reference is a byte[] then
     * wrap with new ByteArrayInputStream. By default the RuleBase retains a
     * weak reference to returned WorkingMemory.
     * 
     * <p>
     * The created <code>WorkingMemory</code> uses the default conflict
     * resolution strategy.
     * </p>
     * 
     * @see WorkingMemory
     * @see org.drools.conflict.DefaultConflictResolver
     * 
     * @return A serialised initialized <code>WorkingMemory</code>.
     */
    StatefulSession newStatefulSession(InputStream stream) throws IOException,
                                                          ClassNotFoundException;
    
    /**
     * Creates a new temporal session using the defined clock type.
     * 
     * @param clockType
     * @return
     */
    TemporalSession newTemporalSession(ClockType clockType);

    /**
     * Creates a new temporal session using the defined clock type.
     * 
     * @param keepReference maintains a reference in the rulebase to the created session
     * @param clockType
     * @return
     */
    TemporalSession newTemporalSession(boolean keepReference, ClockType clockType);

    /**
     * RuleBases handle the returning of a Serialized WorkingMemory
     * pass as an InputStream. If the reference is a byte[] then
     * wrap with new ByteArrayInputStream. Optionally the RuleBase retains a
     * weak reference to returned WorkingMemory.
     * 
     * <p>
     * The created <code>WorkingMemory</code> uses the default conflict
     * resolution strategy.
     * </p>
     * 
     * @see WorkingMemory
     * @see org.drools.conflict.DefaultConflictResolver
     * 
     * @return A serialised initialized <code>WorkingMemory</code>.
     */
    StatefulSession newStatefulSession(InputStream stream,
                                       boolean keepReference) throws IOException,
                                                             ClassNotFoundException;

    Package[] getPackages();

    Package getPackage(String name);

    void addPackage(Package pkg) throws Exception;
    
    /**
     * This locks the current RuleBase and all there referenced StatefulSessions. This should be 
     * used when there is a number of dynamic RuleBase changes you wish to make, but cannot have any normal
     * WorkingMemory operations occuring inbetween.
     *
     */
    void lock();
    
    /**
     * Unlocks the RuleBase and all of the referenced StatefulSessions.
     *
     */
    void unlock();

    /**
     * Returns the number of additive operations applied since the last lock() was obtained 
     * @return
     */
    int getAdditionsSinceLock();

    /**
     * Returns the number of removal operations applied since the last lock() was obtained 
     * @return
     */
    int getRemovalsSinceLock();

    /**
     * Remove the package and all it's rules, functions etc
     * @param packageName
     */
    void removePackage(String packageName);

    /**
     * Remove a specific Rule in a Package
     * @param packageName
     * @param ruleName
     */
    void removeRule(String packageName,
                    String ruleName);

    /**
     * Removes a specific function in a specific package.
     * @param packageName
     * @param functionName
     */
    void removeFunction(String packageName,
                        String functionName);

    /**
     * Removes a process by the process' id
     * @param id
     */
    void removeProcess(String id);

    /**
     * Returns an array of all the referenced StatefulSessions
     * @return
     */
    public StatefulSession[] getStatefulSessions();
}
