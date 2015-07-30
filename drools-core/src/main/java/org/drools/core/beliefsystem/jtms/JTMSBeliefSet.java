/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.beliefsystem.jtms;


import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.LinkedListNode;
import org.kie.internal.runtime.beliefs.Mode;

public interface JTMSBeliefSet<M extends ModedAssertion<M>> extends BeliefSet<M> {

//    void setNegativeFactHandle(InternalFactHandle insert);
//
//    InternalFactHandle getNegativeFactHandle();
//
//    void setPositiveFactHandle(InternalFactHandle fh);
//
//    InternalFactHandle getPositiveFactHandle();

    Object getLast();
}
