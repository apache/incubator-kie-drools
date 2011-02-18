/*
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

package org.drools.spi;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.WorkingMemory;

/**
 * Accumulator
 *
 * Created: 04/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */
public interface Accumulator
    extends
    Invoker {
    
    /**
     * Creates and return a context object for each working memory instance
     * 
     * @return
     */
    public Object createWorkingMemoryContext();
    
    /**
     * Creates the context object for an accumulator session.
     * The context is passed as a parameter to every subsequent accumulator
     * method call in the same session.
     * 
     * @return
     */
    public Serializable createContext();

    /**
     * Executes the initialization block of code
     * 
     * @param leftTuple tuple causing the rule fire
     * @param declarations previous declarations
     * @param workingMemory
     * @throws Exception
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception;

    /**
     * Executes the accumulate (action) code for the given fact handle
     * 
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param workingMemory
     * @throws Exception
     */
    public void accumulate(Object workingMemoryContext,
                           Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception;
    
    /**
     * Returns true if this accumulator supports operation reversal
     * 
     * @return
     */
    public boolean supportsReverse();
    
    /**
     * Reverses the accumulate action for the given fact handle
     * 
     * @param context
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param workingMemory
     * @throws Exception
     */
    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception;

    /**
     * Gets the result of the accummulation
     * 
     * @param leftTuple
     * @param declarations
     * @param workingMemory
     * @return
     * @throws Exception
     */
    public Object getResult(Object workingMemoryContext,
                            Object context, 
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception;

}
