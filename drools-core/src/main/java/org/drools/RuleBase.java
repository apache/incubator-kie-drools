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
import java.util.Set;

import org.drools.rule.Package;
import org.drools.ruleflow.common.core.Process;

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
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleBase.java,v 1.2 2005/08/04 23:33:30 mproctor Exp $
 */
public interface RuleBase
    extends
    Serializable {

    public static final int RETEOO = 1;
    public static final int LEAPS  = 2;
    
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

    void addPackage(Package pkg) throws Exception;

    void removePackage(String packageName);

    void addProcess(Process process);

    void removeProcess(String id);

    Process getProcess(String id);

    void removeRule(String packageName,
                    String ruleName);

    public StatefulSession[] getStatefulSessions();
}
