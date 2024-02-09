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
package org.drools.core.rule.consequence;

import java.io.Serializable;

import org.drools.base.rule.consequence.ConsequenceContext;
import org.drools.core.WorkingMemory;

/**
 * KnowledgeHelper implementation types are injected into consequenses
 * instrumented at compile time and instances passed at runtime. It provides
 * convenience methods for users to interact with the WorkingMemory.
 * <p>
 * Of particular interest is the update method as it allows an object to
 * be modified without having to specify the facthandle, because they are not
 * passed to the consequence at runtime. To achieve this the implementation will
 * need to lookup the fact handle of the object form the WorkingMemory.
 */
public interface KnowledgeHelper extends ConsequenceContext, Serializable {
    
    void setActivation(InternalMatch internalMatch);

    InternalMatch getActivation();

    default void restoreActivationOnConsequenceFailure(InternalMatch internalMatch) { }


    WorkingMemory getWorkingMemory();

    InternalMatch getMatch();


    ClassLoader getProjectClassLoader();

}
