/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.metadata;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.metadata.Metadatable;
import org.drools.core.util.bitmask.BitMask;

public interface Modify<T> extends WorkingMemoryTask<T> {

    public T getTarget();

    public T call( T o );

    public T call( InternalKnowledgeBase knowledgeBase );

    public BitMask getModificationMask();

    public Class getModificationClass();

    public ModifyTask getSetterChain();

    public Object[] getAdditionalUpdates();

    public BitMask getAdditionalUpdatesModificationMask( int j );
}
