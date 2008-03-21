/*
 * Copyright 2008 JBoss Inc
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
 *
 * Created on Feb 6, 2008
 */

package org.drools.reteoo;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.BaseNode;

/**
 * This context class is used during rule removal to ensure
 * network consistency.
 *
 * @author etirelli
 *
 */
public class RuleRemovalContext
    implements
    Externalizable {

    private Map visitedNodes;

    public RuleRemovalContext() {
        this.visitedNodes = new HashMap();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        visitedNodes = (Map) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( visitedNodes );
    }

    /**
     * We need to track tuple source nodes that we visit
     * to avoid multiple removal in case of subnetworks
     *
     * @param node
     */
    public void visitTupleSource(LeftTupleSource node) {
        this.visitedNodes.put( new Integer( node.getId() ),
                               node );
    }

    /**
     * We need to track tuple source nodes that we visit
     * to avoid multiple removal in case of subnetworks
     *
     * @param node
     * @return
     */
    public boolean alreadyVisited(LeftTupleSource node) {
        return this.visitedNodes.containsKey( new Integer( node.getId() ) );
    }

    public void clear() {
        this.visitedNodes.clear();
    }
}
