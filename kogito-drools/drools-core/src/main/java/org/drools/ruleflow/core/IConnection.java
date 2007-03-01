package org.drools.ruleflow.core;
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

/**
 * Represents a connection between two nodes in a RuleFlow. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface IConnection {
    
	/**
	 * The connection type
	 */
	int TYPE_NORMAL = 1;
	int TYPE_ABORT = 2;
	
	/**
	 * Returns the from node of the connection.
	 * @return the from node of the connection.
	 */
    INode getFrom();    

    /**
     * Returns the to node of the connection
     * @return the to node of the connection
     */
    INode getTo();    

    /**
     * Returns the connection type
     * @return the connection type
     */
    int getType();
    
    /**
     * Destroys the connection. This method also removes the
     * connection on the <code>to</code> and <code>from</code> nodes.
     * Onces a connection is destroyed, all methods throw
     * an <code>IllegalStateException</code>.
     */
    void terminate();
        
}
