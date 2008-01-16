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

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.rule.Rule;
import org.drools.spi.Activation;

public interface InternalWorkingMemoryActions
    extends
    InternalWorkingMemory {
    public void update(FactHandle handle,
                       Object object,
                       Rule rule,
                       Activation activation) throws FactException;

    public void retract(FactHandle handle,
                        boolean removeLogical,
                        boolean updateEqualsMap,
                        Rule rule,
                        Activation activation) throws FactException;

    /*FactHandle insert(Object object,
                      boolean dynamic,
                      boolean logical,
                      Rule rule,
                      Activation activation) throws FactException;*/
    
    FactHandle insert(Object object,
    		long duration,
            boolean dynamic,
            boolean logical,
            Rule rule,
            Activation activation) throws FactException;

    /*public FactHandle insertLogical(Object object,
                                    boolean dynamic) throws FactException;*/
    
    public FactHandle insertLogical(Object object,
    								long duration,
    								boolean dynamic) throws FactException;

    public void modifyRetract(final FactHandle factHandle,
                              final Rule rule,
                              final Activation activation);

    public void modifyInsert(final FactHandle factHandle,
                             final Object object,
                             final Rule rule,
                             final Activation activation);
    
}