/**
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

package org.drools.marshalling.impl;

public class PersisterEnums {
    public static final short REPEAT                 = 0;
    public static final short END                    = 1;
    public static final short FACT_HANDLE            = 2;
    public static final short LEFT_TUPLE             = 3;
    public static final short RIGHT_TUPLE            = 4;

    public static final short INITIAL_FACT_NODE      = 5;
    
    public static final short LEFT_TUPLE_BLOCKED     = 6;
    public static final short LEFT_TUPLE_NOT_BLOCKED = 7;

    public static final short ACTIVATION             = 8;
    public static final short PROPAGATION_CONTEXT    = 9;
    
    public static final short WORKING_MEMORY_ACTION  = 10;
    
    public static final short EQUALITY_KEY           = 11;
    public static final short LOGICAL_DEPENDENCY     = 12; 
    
    public static final short AGENDA_GROUP           = 13;
    public static final short ACTIVATION_GROUP       = 14;
    
    public static final short RULE_FLOW_GROUP        = 15;
    public static final short RULE_FLOW_NODE         = 16;
    
    public static final short PROCESS_INSTANCE          = 17;
    public static final short NODE_INSTANCE             = 18;
    public static final short WORK_ITEM                 = 19;
    public static final short RULE_SET_NODE_INSTANCE    = 20;
    public static final short WORK_ITEM_NODE_INSTANCE   = 21;
    public static final short SUB_PROCESS_NODE_INSTANCE = 22;
    public static final short MILESTONE_NODE_INSTANCE   = 23;
    public static final short TIMER_NODE_INSTANCE       = 24;
    public static final short JOIN_NODE_INSTANCE        = 25;
    public static final short COMPOSITE_NODE_INSTANCE   = 26;
    public static final short HUMAN_TASK_NODE_INSTANCE  = 27;
    public static final short FOR_EACH_NODE_INSTANCE    = 28;
    public static final short TIMER                     = 29;
    public static final short STATE_NODE_INSTANCE       = 30;
    public static final short DYNAMIC_NODE_INSTANCE     = 31;
    public static final short EVENT_NODE_INSTANCE       = 32;

}
