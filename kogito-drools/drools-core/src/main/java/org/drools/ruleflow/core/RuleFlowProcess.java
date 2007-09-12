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

import java.util.List;
import java.util.Map;

import org.drools.ruleflow.common.core.Process;

/**
 * Represents a RuleFlow process. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface RuleFlowProcess
    extends
    Process {

    /**
     * Returns the start node of this RuleFlow process.
     * 
     * @return	the start node
     */
    StartNode getStart();

    /**
     * Returns the nodes of this RuleFlow process.
     * 
     * @return	the nodes of this RuleFlow process
     */
    Node[] getNodes();

    /**
     * Returns the node with the given id
     * 
     * @param id	the node id
     * @return	the node with the given id
     * @throws IllegalArgumentException if an unknown id is passed
     */
    Node getNode(long id);

    /**
     * Method for adding a node to this RuleFlow process. 
     * Note that the node will get an id unique for this process.
     * 
     * @param node	the node to be added
     * @throws IllegalArgumentException if <code>node</code> is null 
     */
    void addNode(Node node);

    /**
     * Method for removing a node from this RuleFlow process
     * 
     * @param node	the node to be removed
     * @throws IllegalArgumentException if <code>node</code> is null or unknown
     */
    void removeNode(Node node);

    /**
     * Returns the global variables used in this RuleFlow process
     * 
     * @return	a list of variables of this RuleFlow process
     */
    List getVariables();

    /**
     * Sets the global variables used in this RuleFlow process
     * 
     * @param variables	the variables
     * @throws IllegalArugmentException if <code>variables</code> is null
     */
    void setVariables(List variables);

    /**
     * Returns the names of the global variables used in this RuleFlow process
     * 
     * @return	the variable names of this RuleFlow process
     */
    String[] getVariableNames();
    
    /**
     * Returns the imports of this RuleFlow process.
     * They are defined as a List of fully qualified class names.
     * 
     * @return	the imports of this RuleFlow process
     */
    List getImports();
    
    /**
     * Sets the imports of this RuleFlow process
     * 
     * @param imports	the imports as a List of fully qualified class names
     */
    void setImports(List imports);

    /**
     * Returns the globals of this RuleFlow process.
     * They are defined as a Map with the name as key and the type as value.
     * 
     * @return	the imports of this RuleFlow process
     */
    Map getGlobals();
    
    /**
     * Sets the imports of this RuleFlow process
     * 
     * @param imports	the globals as a Map with the name as key and the type as value
     */
    void setGlobals(Map globals);

    /**
     * Returns the names of the globals used in this RuleFlow process
     * 
     * @return	the names of the globals of this RuleFlow process
     */
    String[] getGlobalNames();
    
}
