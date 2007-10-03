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
 * Represents a RuleFlow validation error. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface RuleFlowProcessValidationError {

    String NO_PROCESS_NAME                              = "RuleFlow process has no name.";
    String NO_PROCESS_ID                                = "RuleFlow process has no id.";
    String NO_START_NODE                                = "RuleFlow process has no start node.";
    String NO_PACKAGE_NAME                              = "RuleFlow process has no package name.";
    String START_NODE_WITHOUT_OUTGOING_NODES            = "Start node has no outgoing connection.";
    String END_NODE_HAS_NO_INCOMING_CONNECTIONS         = "End node has no incoming connection.";
    String NO_END_NODE                                  = "No end node found.";
    String RULE_SET_NODE_WITHOUT_INCOMING_CONNECTIONS   = "RuleSet node has no incoming connection.";
    String RULE_SET_NODE_WITHOUT_OUTGOING_CONNECTIONS   = "RuleSet node has no outgoing connection.";
    String RULE_SET_NODE_WITHOUT_RULE_SET_GROUP         = "RuleSet node has no ruleflow-group specified.";
    String SPLIT_WITHOUT_TYPE                           = "Split node has no type.";
    String SPLIT_WITHOUT_INCOMING_CONNECTION            = "Split node has no incoming connection.";
    String SPLIT_NOT_ENOUGH_OUTGOING_CONNECTIONS        = "Split node does not have enough outgoing connections.";
    String SPLIT_OUTGOING_CONNECTION_WITHOUT_CONSTRAINT = "An outgoing connection of a split node has no constraint.";
    String JOIN_WITHOUT_TYPE                            = "Join node has no type.";
    String JOIN_NOT_ENOUGH_INCOMING_CONNECTIONS         = "Join node does not have enough incoming connections.";
    String JOIN_WITHOUT_OUTGOING_CONNECTION             = "Join node has no outgoing connection.";
    String VARIABLE_WITHOUT_TYPE                        = "A variable has no type.";
    String ALL_NODES_CONNECTED_TO_START                 = "A node is not connected to the start node.";
    String MILESTONE_NODE_WITHOUT_INCOMING_CONNECTIONS  = "Milestone node has no incoming connection.";
    String MILESTONE_NODE_WITHOUT_OUTGOING_CONNECTIONS  = "Milestone node has no outgoing connection.";
    String MILESTONE_WITHOUT_CONSTRAINT                 = "A milestone node has no constraint.";
    String SUBFLOW_NODE_WITHOUT_INCOMING_CONNECTIONS    = "SubFlow node has no incoming connection.";
    String SUBFLOW_NODE_WITHOUT_OUTGOING_CONNECTIONS    = "SubFlow node has no outgoing connection.";
    String SUBFLOW_WITHOUT_PROCESS_ID                   = "A SubFlow node has no process id.";
    String ACTION_NODE_WITHOUT_INCOMING_CONNECTIONS     = "Action node has no incoming connection.";
    String ACTION_NODE_WITHOUT_OUTGOING_CONNECTIONS     = "Action node has no outgoing connection.";
    String ACTION_NODE_WITHOUT_ACTION                   = "An Action node has no action.";
    String ACTION_NODE_WITH_INVALID_ACTION              = "An Action node has an invalid action.";

    String getType();
}
