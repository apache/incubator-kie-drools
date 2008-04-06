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
import java.util.List;
import java.util.Map;

import org.drools.process.core.Contextable;

/**
 * Represents a node in a RuleFlow. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Node extends Contextable, Serializable {

    static final String CONNECTION_DEFAULT_TYPE = "DROOLS_DEFAULT";
    
    /**
     * Returns the id of the node
     * 
     * @return the id of the node
     */
    long getId();

    /**
     * Method for setting the id of the node
     * 
     * @param id	the id of the node
     */
    void setId(long id);

    /**
     * Returns the name of the node
     * 
     * @return the name of the node
     */
    String getName();

    /**
     * Method for setting the name of the node
     * 
     * @param name 	the name of the node
     */
    void setName(String name);

    /**
     * Returns the incoming connections
     * 
     * @return the incoming connections 
     */
    Map<String, List<Connection>> getIncomingConnections();

    /**
     * Returns the outgoing connections
     * 
     * @return the outgoing connections 
     */
    Map<String, List<Connection>> getOutgoingConnections();

    void addIncomingConnection(String type, Connection connection);
    
    void addOutgoingConnection(String type, Connection connection);
    
    void removeIncomingConnection(String type, Connection connection);
    
    void removeOutgoingConnection(String type, Connection connection);
    
    List<Connection> getIncomingConnections(String type);
    
    List<Connection> getOutgoingConnections(String type);
    
    NodeContainer getNodeContainer();
    
    void setNodeContainer(NodeContainer nodeContainer);
    
    void setMetaData(String name, Object value);
    
    Object getMetaData(String name);
    
}
