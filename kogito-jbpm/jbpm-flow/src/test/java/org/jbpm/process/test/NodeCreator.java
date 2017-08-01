/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.test;

import java.lang.reflect.Constructor;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.WorkItemNode;

public class NodeCreator<T extends NodeImpl> {
    NodeContainer nodeContainer;
    Constructor<T> constructor;

    private static long idGen = 1;

    public NodeCreator(NodeContainer nodeContainer, Class<T> clazz) {
        this.nodeContainer = nodeContainer;
        this.constructor = (Constructor<T>) clazz.getConstructors()[0];
    }

    public T createNode(String name) throws Exception {
        T result = this.constructor.newInstance(new Object[0]);
        result.setId(idGen++);
        result.setName(name);
        this.nodeContainer.addNode(result);
        
        if( result instanceof WorkItemNode ) { 
            Work work = new WorkImpl();
            ((WorkItemNode) result).setWork(work);
        }
        return result;
    }

    public void setNodeContainer(NodeContainer newNodeContainer) {
        this.nodeContainer = newNodeContainer;
    }

    public static void connect(Node nodeOne, Node nodeTwo ) { 
        new ConnectionImpl(
                nodeOne, Node.CONNECTION_DEFAULT_TYPE, 
                nodeTwo, Node.CONNECTION_DEFAULT_TYPE
        );
    }
}