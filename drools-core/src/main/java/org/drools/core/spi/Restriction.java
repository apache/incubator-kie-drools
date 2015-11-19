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

package org.drools.core.spi;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;

import java.io.Externalizable;

public interface Restriction
    extends
    Externalizable,
    Cloneable {
    Declaration[] getRequiredDeclarations();

    public boolean isAllowed(InternalReadAccessor extractor,
                             InternalFactHandle handle,
                             InternalWorkingMemory workingMemory,
                             ContextEntry context );

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(Tuple tuple,
                                        ContextEntry context);

    public ContextEntry createContextEntry();

    /**
     * A restriction may be required to replace an old
     * declaration object by a new updated one
     *
     * @param oldDecl
     * @param newDecl
     */
    void replaceDeclaration(Declaration oldDecl,
                            Declaration newDecl);
    
    public Evaluator getEvaluator();

    /**
     * Returns true if this is a temporal restriction
     * 
     * @return
     */
    public boolean isTemporal();

    public Restriction clone();

}
