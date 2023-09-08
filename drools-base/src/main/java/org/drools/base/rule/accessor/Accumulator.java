/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.rule.accessor;
import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Accumulator
 *
 * Created: 04/06/2006
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
    Object createWorkingMemoryContext();
    
    /**
     * Creates the context object for an accumulator session.
     * The context is passed as a parameter to every subsequent accumulator
     * method call in the same session.
     * 
     * @return
     */
    Object createContext();

    /**
     * Executes the initialization block of code
     * 
     * @param leftTuple tuple causing the rule fire
     * @param declarations previous declarations
     * @param valueResolver
     * @throws Exception
     */
    Object init(Object workingMemoryContext,
                Object context,
                BaseTuple leftTuple,
                Declaration[] declarations,
                ValueResolver valueResolver);

    /**
     * Executes the accumulate (action) code for the given fact handle
     * 
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param valueResolver
     * @throws Exception
     */
    Object accumulate(Object workingMemoryContext,
                      Object context,
                      BaseTuple leftTuple,
                      FactHandle handle,
                      Declaration[] declarations,
                      Declaration[] innerDeclarations,
                      ValueResolver valueResolver);
    
    /**
     * Returns true if this accumulator supports operation reversal
     * 
     * @return
     */
    boolean supportsReverse();
    
    /**
     * Reverses the accumulate action for the given fact handle
     * 
     * @param context
     * @param leftTuple
     * @param handle
     * @param declarations
     * @param innerDeclarations
     * @param valueResolver
     * @throws Exception
     */
    boolean tryReverse(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       FactHandle handle,
                       Object value,
                       Declaration[] declarations,
                       Declaration[] innerDeclarations,
                       ValueResolver valueResolver);

    /**
     * Gets the result of the accummulation
     * 
     * @param leftTuple
     * @param declarations
     * @param valueResolver
     * @return
     * @throws Exception
     */
    Object getResult(Object workingMemoryContext,
                     Object context,
                     BaseTuple leftTuple,
                     Declaration[] declarations,
                     ValueResolver valueResolver);

    default void replaceDeclaration(Declaration declaration, Declaration resolved) { }

    default Declaration[] getRequiredDeclarations() { return new Declaration[0]; }
}
