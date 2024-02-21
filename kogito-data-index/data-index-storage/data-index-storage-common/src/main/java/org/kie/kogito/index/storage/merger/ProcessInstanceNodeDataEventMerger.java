/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.storage.merger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.MilestoneStatus;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@ApplicationScoped
public class ProcessInstanceNodeDataEventMerger extends ProcessInstanceEventMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceNodeDataEventMerger.class);

    @Override
    public ProcessInstance merge(ProcessInstance pi, ProcessInstanceDataEvent<?> data) {
        ProcessInstanceNodeDataEvent event = (ProcessInstanceNodeDataEvent) data;
        pi = getOrNew(pi, data, event.getData().getEventDate());

        List<NodeInstance> nodeInstances = Optional.ofNullable(pi.getNodes()).orElse(new ArrayList<>());

        ProcessInstanceNodeEventBody body = event.getData();

        NodeInstance nodeInstance = nodeInstances.stream().filter(e -> body.getNodeInstanceId().equals(e.getId())).findAny().orElse(new NodeInstance());
        nodeInstances.removeIf(e -> e.getId().equals(body.getNodeInstanceId()));

        LOGGER.debug("before merging: {}", nodeInstance);
        nodeInstance.setDefinitionId(body.getNodeDefinitionId());
        nodeInstance.setId(body.getNodeInstanceId());
        nodeInstance.setNodeId(body.getNodeDefinitionId());
        nodeInstance.setName(body.getNodeName());
        nodeInstance.setType(body.getNodeType());
        ZonedDateTime eventDate = toZonedDateTime(body.getEventDate());
        switch (body.getEventType()) {
            case EVENT_TYPE_ENTER:
                nodeInstance.setEnter(eventDate);
                break;
            case EVENT_TYPE_EXIT:
                nodeInstance.setExit(eventDate);
            default:
                if (nodeInstance.getEnter() == null) {
                    // Adding a default enter time for exit events triggered by EventNodeInstances
                    nodeInstance.setEnter(eventDate);
                }
        }
        nodeInstances.add(nodeInstance);

        // milestone
        if ("MilestoneNode".equals(event.getData().getNodeType())) {
            List<Milestone> milestones = Optional.ofNullable(pi.getMilestones()).orElse(new ArrayList<>());
            Optional<Milestone> found = milestones.stream().filter(e -> body.getNodeInstanceId().equals(e.getId())).findAny();
            Milestone milestone = null;
            if (found.isEmpty()) {
                milestone = new Milestone();
                milestones.add(milestone);
            } else {
                milestone = found.get();
            }

            milestone.setId(nodeInstance.getId());
            milestone.setName(nodeInstance.getName());
            milestone.setStatus(nodeInstance.getExit() != null ? MilestoneStatus.COMPLETED.name() : MilestoneStatus.ACTIVE.name());
            milestones.add(milestone);

            pi.setMilestones(milestones);
        }

        if (pi.getDefinition() != null) {
            List<Node> nodes = Optional.ofNullable(pi.getDefinition().getNodes()).orElse(new ArrayList<>());

            nodes.removeIf(e -> e.getId().equals(body.getNodeDefinitionId()));
            Node node = new Node();
            node.setId(body.getNodeDefinitionId());
            node.setType(body.getNodeType());
            node.setUniqueId(body.getNodeDefinitionId());
            node.setMetadata(new HashMap<>());

            nodes.add(node);
            pi.getDefinition().setNodes(nodes);
        }
        LOGGER.debug("after merging: {}", nodeInstance);
        pi.setNodes(nodeInstances);
        return pi;
    }

}
