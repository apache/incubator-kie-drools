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

/**
 * Represents a connection between two nodes in a workflow. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Connection {

    /**
     * Returns the from node of the connection.
     * @return the from node of the connection.
     */
    Node getFrom();

    /**
     * Returns the to node of the connection
     * @return the to node of the connection
     */
    Node getTo();

    /**
     * Returns the type of the connection at the from node
     * 
     * @return the type of the connection at the from node
     */
    String getFromType();

    /**
     * Returns the type of the connection at the to node
     * 
     * @return the type of the connection at the to node
     */
    String getToType();

}
