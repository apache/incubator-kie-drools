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
package org.kie.kogito.index.jpa.mapper;

import java.time.ZonedDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.model.MilestoneEntity;
import org.kie.kogito.index.jpa.model.MilestoneEntityId;
import org.kie.kogito.index.jpa.model.NodeInstanceEntity;
import org.kie.kogito.index.jpa.model.ProcessInstanceEntity;
import org.kie.kogito.index.jpa.model.ProcessInstanceErrorEntity;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.inject.Inject;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractProcessInstanceEntityMapperIT {

    ObjectMapper jsonMapper = new ObjectMapper();
    ProcessInstance processInstance = new ProcessInstance();
    ProcessInstanceEntity processInstanceEntity = new ProcessInstanceEntity();

    @Inject
    ProcessInstanceEntityMapper mapper;

    @BeforeEach
    void setup() {
        String nodeInstanceId = "testNodeInstanceId";
        String nodeInstanceName = "testNodeInstanceName";
        String nodeInstanceNodeId = "testNodeInstanceNodeId";
        String nodeInstanceType = "testNodeInstanceType";
        String nodeInstanceDefinitionId = "testNodeInstanceDefinitionId";

        String processInstanceErrorMessage = "testProcessInstanceErrorMessage";
        String processInstanceErrorNodeDefinitionId = "testProcessInstanceErrorNodeDefinitionId";

        String milestoneId = "testMilestone";
        String milestoneName = "testMilestoneName";
        String milestoneStatus = "testMilestoneStatus";

        String testId = "testId";
        String processId = "testProcessId";
        Set<String> roles = singleton("testRoles");
        ObjectNode variables = jsonMapper.createObjectNode();
        variables.put("test", "testValue");
        String endpoint = "testEndpoint";
        Integer state = 2;
        ZonedDateTime time = ZonedDateTime.now();
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        String parentProcessInstanceId = "testParentProcessInstanceId";
        String processName = "testProcessName";
        Set<String> addons = singleton("testAddons");
        String businessKey = "testBusinessKey";
        String createdBy = "initiatorUser";
        String updatedBy = "currentUser";

        NodeInstance nodeInstance = new NodeInstance();
        nodeInstance.setId(nodeInstanceId);
        nodeInstance.setDefinitionId(nodeInstanceDefinitionId);
        nodeInstance.setExit(time);
        nodeInstance.setEnter(time);
        nodeInstance.setType(nodeInstanceType);
        nodeInstance.setNodeId(nodeInstanceNodeId);
        nodeInstance.setName(nodeInstanceName);

        ProcessInstanceError processInstanceError = new ProcessInstanceError();
        processInstanceError.setMessage(processInstanceErrorMessage);
        processInstanceError.setNodeDefinitionId(processInstanceErrorNodeDefinitionId);

        Milestone milestone = new Milestone();
        milestone.setId(milestoneId);
        milestone.setName(milestoneName);
        milestone.setStatus(milestoneStatus);

        processInstance.setId(testId);
        processInstance.setProcessId(processId);
        processInstance.setRoles(roles);
        processInstance.setVariables(variables);
        processInstance.setEndpoint(endpoint);
        processInstance.setNodes(singletonList(nodeInstance));
        processInstance.setState(state);
        processInstance.setStart(time);
        processInstance.setEnd(time);
        processInstance.setRootProcessId(rootProcessId);
        processInstance.setRootProcessInstanceId(rootProcessInstanceId);
        processInstance.setParentProcessInstanceId(parentProcessInstanceId);
        processInstance.setProcessName(processName);
        processInstance.setError(processInstanceError);
        processInstance.setAddons(addons);
        processInstance.setLastUpdate(time);
        processInstance.setBusinessKey(businessKey);
        processInstance.setMilestones(singletonList(milestone));
        processInstance.setCreatedBy(createdBy);
        processInstance.setUpdatedBy(updatedBy);

        NodeInstanceEntity nodeInstanceEntity = new NodeInstanceEntity();
        nodeInstanceEntity.setId(nodeInstanceId);
        nodeInstanceEntity.setDefinitionId(nodeInstanceDefinitionId);
        nodeInstanceEntity.setEnter(time);
        nodeInstanceEntity.setExit(time);
        nodeInstanceEntity.setName(nodeInstanceName);
        nodeInstanceEntity.setNodeId(nodeInstanceNodeId);
        nodeInstanceEntity.setType(nodeInstanceType);
        nodeInstanceEntity.setProcessInstance(processInstanceEntity);

        ProcessInstanceErrorEntity processInstanceErrorEntity = new ProcessInstanceErrorEntity();
        processInstanceErrorEntity.setMessage(processInstanceErrorMessage);
        processInstanceErrorEntity.setNodeDefinitionId(processInstanceErrorNodeDefinitionId);

        MilestoneEntity milestoneEntity = new MilestoneEntity();
        MilestoneEntityId milestoneEntityId = new MilestoneEntityId();
        milestoneEntityId.setId(milestoneId);
        milestoneEntityId.setProcessInstance(testId);
        milestoneEntity.setId(milestoneId);
        milestoneEntity.setName(milestoneName);
        milestoneEntity.setStatus(milestoneStatus);
        milestoneEntity.setProcessInstance(processInstanceEntity);

        processInstanceEntity.setId(testId);
        processInstanceEntity.setProcessId(processId);
        processInstanceEntity.setRoles(roles);
        processInstanceEntity.setVariables(variables);
        processInstanceEntity.setEndpoint(endpoint);
        processInstanceEntity.setNodes(singletonList(nodeInstanceEntity));
        processInstanceEntity.setState(state);
        processInstanceEntity.setStart(time);
        processInstanceEntity.setEnd(time);
        processInstanceEntity.setRootProcessId(rootProcessId);
        processInstanceEntity.setRootProcessInstanceId(rootProcessInstanceId);
        processInstanceEntity.setParentProcessInstanceId(parentProcessInstanceId);
        processInstanceEntity.setProcessName(processName);
        processInstanceEntity.setError(processInstanceErrorEntity);
        processInstanceEntity.setAddons(addons);
        processInstanceEntity.setLastUpdate(time);
        processInstanceEntity.setBusinessKey(businessKey);
        processInstanceEntity.setMilestones(singletonList(milestoneEntity));
        processInstanceEntity.setCreatedBy(createdBy);
        processInstanceEntity.setUpdatedBy(updatedBy);
    }

    @Test
    void testMapToEntity() {
        ProcessInstanceEntity result = mapper.mapToEntity(processInstance);
        assertThat(result).usingRecursiveComparison().ignoringFieldsMatchingRegexes(".*\\$\\$_hibernate_tracker").isEqualTo(processInstanceEntity);
    }

    @Test
    void testMapToModel() {
        ProcessInstance result = mapper.mapToModel(processInstanceEntity);
        assertThat(result).usingRecursiveComparison().isEqualTo(processInstance);
    }

}
