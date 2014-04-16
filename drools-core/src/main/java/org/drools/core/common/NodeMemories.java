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

package org.drools.core.common;


import org.drools.core.impl.InternalKnowledgeBase;

/**
 * An interface for node memories implementation
 */
public interface NodeMemories {

    Memory getNodeMemory(MemoryFactory node, InternalWorkingMemory wm);

    void clearNodeMemory( MemoryFactory node );

    void setKnowledgeBaseReference( InternalKnowledgeBase kBase );

    void clear();

    /**
     * Peeks at the content of the node memory for the given
     * node ID. This method has no side effects, so if the
     * given memory slot for the given node ID is null, it
     * will return null.
     *
     * @param nodeId
     * @return
     */
    Memory peekNodeMemory( int nodeId );

    /**
     * Returns the number of positions in this memory
     *
     * @return
     */
    int length();

}
