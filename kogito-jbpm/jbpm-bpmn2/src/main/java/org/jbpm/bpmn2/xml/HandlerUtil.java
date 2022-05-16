/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.xml;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.core.Collaboration;
import org.jbpm.bpmn2.core.CorrelationProperty;
import org.jbpm.bpmn2.core.CorrelationSubscription;
import org.jbpm.bpmn2.core.ItemDefinition;
import org.jbpm.bpmn2.core.Message;
import org.jbpm.compiler.xml.Parser;
import org.jbpm.compiler.xml.ProcessBuildData;
import org.jbpm.ruleflow.core.RuleFlowProcess;

@SuppressWarnings("unchecked")
public final class HandlerUtil {

    public static Map<String, Message> messages(Parser parser) {
        Map<String, Message> messages = (Map<String, Message>) ((ProcessBuildData) parser.getData()).getMetaData("Messages");
        if (messages == null) {
            messages = new HashMap<>();
            ((ProcessBuildData) parser.getData()).setMetaData("Messages", messages);
        }
        return messages;
    }

    public static Map<String, ItemDefinition> definitions(Parser parser) {
        Map<String, ItemDefinition> definitions = (Map<String, ItemDefinition>) ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
        if (definitions == null) {
            definitions = new HashMap<>();
            ((ProcessBuildData) parser.getData()).setMetaData("ItemDefinitions", definitions);
        }
        return definitions;
    }

    public static Map<String, CorrelationProperty> correlationProperties(Parser parser) {
        Map<String, CorrelationProperty> properties = (Map<String, CorrelationProperty>) ((ProcessBuildData) parser.getData()).getMetaData("CorrelationProperties");
        if (properties == null) {
            properties = new HashMap<>();
            ((ProcessBuildData) parser.getData()).setMetaData("CorrelationProperties", properties);
        }
        return properties;
    }

    public static Map<String, Collaboration> collaborations(Parser parser) {
        Map<String, Collaboration> collaborations = (Map<String, Collaboration>) ((ProcessBuildData) parser.getData()).getMetaData("Collaborations");
        if (collaborations == null) {
            collaborations = new HashMap<>();
            ((ProcessBuildData) parser.getData()).setMetaData("Collaborations", collaborations);
        }
        return collaborations;
    }

    public static Map<String, CorrelationSubscription> correlationSubscription(RuleFlowProcess process) {
        Map<String, CorrelationSubscription> correlationSubscription = (Map<String, CorrelationSubscription>) process.getMetaData("CorrelationSubscriptions");
        if (correlationSubscription == null) {
            correlationSubscription = new HashMap<>();
            process.setMetaData("CorrelationSubscriptions", correlationSubscription);
        }
        return correlationSubscription;
    }
}
