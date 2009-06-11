package org.drools.workflow.core;

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

import java.io.Serializable;

import org.drools.definition.process.Connection;
import org.drools.definition.process.NodeContainer;
import org.drools.process.core.Contextable;

/**
 * Represents a node in a RuleFlow. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Node extends org.drools.definition.process.Node, Contextable, Serializable {

    static final String CONNECTION_DEFAULT_TYPE = "DROOLS_DEFAULT";
    
    /**
     * Method for setting the id of the node
     * 
     * @param id	the id of the node
     */
    void setId(long id);

    /**
     * Method for setting the name of the node
     * 
     * @param name 	the name of the node
     */
    void setName(String name);
    
    String getUniqueId();

    void addIncomingConnection(String type, Connection connection);
    
    void addOutgoingConnection(String type, Connection connection);
    
    void removeIncomingConnection(String type, Connection connection);
    
    void removeOutgoingConnection(String type, Connection connection);
    
    void setNodeContainer(NodeContainer nodeContainer);
    
    void setMetaData(String name, Object value);
    
}
