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

import org.kie.kogito.index.jpa.model.MilestoneEntity;
import org.kie.kogito.index.jpa.model.ProcessInstanceEntity;
import org.kie.kogito.index.model.Milestone;
import org.kie.kogito.index.model.ProcessInstance;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface ProcessInstanceEntityMapper {

    MilestoneEntity mapMilestoneToEntity(Milestone mi);

    @InheritInverseConfiguration
    Milestone mapMilestoneToModel(MilestoneEntity pi);

    ProcessInstanceEntity mapToEntity(ProcessInstance pi);

    @InheritInverseConfiguration
    ProcessInstance mapToModel(ProcessInstanceEntity pi);

    @AfterMapping
    default void afterMapping(@MappingTarget ProcessInstanceEntity entity) {
        entity.getNodes().forEach(n -> n.setProcessInstance(entity));
        entity.getMilestones().forEach(m -> m.setProcessInstance(entity));
    }
}
