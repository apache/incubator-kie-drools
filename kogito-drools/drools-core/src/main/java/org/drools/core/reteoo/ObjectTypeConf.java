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

package org.drools.core.reteoo;

import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;

public interface ObjectTypeConf {
	String getTypeName();

    ObjectTypeNode[] getObjectTypeNodes();

    ObjectTypeNode getConcreteObjectTypeNode();

    void resetCache();

    boolean isAssignableFrom(Object object);

    boolean isActive();

    boolean isEvent();

    boolean isTrait();

    boolean isDynamic();
   
    TypeDeclaration getTypeDeclaration();
    
    /** Whether or not, TMS is active for this object type. */
    boolean isTMSEnabled();

    boolean isTraitTMSEnabled();

    /**
     * Enable TMS for this object type. 
     * */
    void enableTMS();
    
    EntryPointId getEntryPoint();
    
    boolean isSupportsPropertyChangeListeners();
}
