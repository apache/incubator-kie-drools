/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.ruleflow.core;

public class Metadata {

    public static final String ASSOCIATION = "association";
    public static final String ACTION = "Action";
    public static final String TRIGGER_REF = "TriggerRef";
    public static final String REF = "Ref";
    public static final String MESSAGE_TYPE = "MessageType";
    public static final String SIGNAL_TYPE = "SignalType";
    public static final String PRODUCE_MESSAGE = "ProduceMessage";
    public static final String CONSUME_MESSAGE = "ConsumeMessage";
    public static final String NODE_TYPE = "NodeType";
    public static final String TRIGGER_TYPE = "TriggerType";
    public static final String TRIGGER_MAPPING = "TriggerMapping";
    public static final String TRIGGER_MAPPING_INPUT = "TriggerMappingInput";
    public static final String MAPPING_VARIABLE = "MappingVariable";
    public static final String MAPPING_VARIABLE_INPUT = "MappingVariableInput";
    public static final String EVENT_TYPE = "EventType";
    public static final String EVENT_TYPE_TIMER = "Timer";
    public static final String EVENT_TYPE_SIGNAL = "signal";
    public static final String EVENT_TYPE_MESSAGE = "message";
    public static final String EVENT_TYPE_COMPENSATION = "Compensation";
    public static final String EVENT_TYPE_ERROR = "error";
    public static final String EVENT_TYPE_CONDITIONAL = "conditional";
    public static final String EVENT_TYPE_LINK = "link";
    public static final String CUSTOM_SCOPE = "customScope";
    public static final String ATTACHED_TO = "AttachedTo";
    public static final String TIME_CYCLE = "TimeCycle";
    public static final String TIME_DURATION = "TimeDuration";
    public static final String TIME_DATE = "TimeDate";
    public static final String CANCEL_ACTIVITY = "CancelActivity";
    public static final String HIDDEN = "hidden";
    public static final String UNIQUE_ID = "UniqueId";
    public static final String LINK_NODE_HIDDEN = "linkNodeHidden";
    public static final String SIGNAL_NAME = "SignalName";
    public static final String LINK_NAME = "LinkName";
    public static final String CONDITION = "Condition";
    public static final String IS_FOR_COMPENSATION = "isForCompensation";
    public static final String CORRELATION_KEY = "CorrelationKey";
    public static final String CUSTOM_ASYNC = "customAsync";
    public static final String CUSTOM_AUTO_START = "customAutoStart";
    public static final String COMPENSATION = "Compensation";
    public static final String CUSTOM_SLA_DUE_DATE = "customSLADueDate";
    public static final String INCOMING_CONNECTION = "IncomingConnection";
    public static final String OUTGOING_CONNECTION = "OutgoingConnection";
    public static final String CUSTOM_ACTIVATION_CONDITION = "customActivationCondition";
    public static final String COMPLETION_CONDITION = "completionCondition";
    public static final String DATA_OUTPUTS = "DataOutputs";
    public static final String VARIABLE = "Variable";
    public static final String HAS_ERROR_EVENT = "HasErrorEvent";
    public static final String ERROR_EVENT = "ErrorEvent";
    public static final String ERROR_STRUCTURE_REF = "ErrorStructureRef";
    public static final String ASYNC_WAITING = "ASYNC_WAITING";
    public static final String CONSTANTS = "customConstants";
    public static final String DATA_ONLY = "DataOnly";
    public static final String TAGS = "Tags";

    private Metadata() {
    }
}
