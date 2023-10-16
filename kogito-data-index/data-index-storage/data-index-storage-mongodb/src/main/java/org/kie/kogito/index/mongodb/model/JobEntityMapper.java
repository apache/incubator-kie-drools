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
package org.kie.kogito.index.mongodb.model;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static org.kie.kogito.persistence.mongodb.model.ModelUtils.instantToZonedDateTime;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.zonedDateTimeToInstant;

public class JobEntityMapper implements MongoEntityMapper<Job, JobEntity> {

    @Override
    public Class<JobEntity> getEntityClass() {
        return JobEntity.class;
    }

    @Override
    public JobEntity mapToEntity(String key, Job job) {
        if (job == null) {
            return null;
        }

        JobEntity entity = new JobEntity();
        entity.setId(job.getId());
        entity.setProcessId(job.getProcessId());
        entity.setProcessInstanceId(job.getProcessInstanceId());
        entity.setRootProcessId(job.getRootProcessId());
        entity.setRootProcessInstanceId(job.getRootProcessInstanceId());
        entity.setExpirationTime(zonedDateTimeToInstant(job.getExpirationTime()));
        entity.setPriority(job.getPriority());
        entity.setCallbackEndpoint(job.getCallbackEndpoint());
        entity.setRepeatInterval(job.getRepeatInterval());
        entity.setRepeatLimit(job.getRepeatLimit());
        entity.setScheduledId(job.getScheduledId());
        entity.setRetries(job.getRetries());
        entity.setStatus(job.getStatus());
        entity.setLastUpdate(zonedDateTimeToInstant(job.getLastUpdate()));
        entity.setExecutionCounter(job.getExecutionCounter());
        entity.setEndpoint(job.getEndpoint());
        entity.setNodeInstanceId(job.getNodeInstanceId());
        return entity;
    }

    @Override
    public Job mapToModel(JobEntity entity) {
        if (entity == null) {
            return null;
        }

        Job job = new Job();
        job.setId(entity.getId());
        job.setProcessId(entity.getProcessId());
        job.setProcessInstanceId(entity.getProcessInstanceId());
        job.setRootProcessId(entity.getRootProcessId());
        job.setRootProcessInstanceId(entity.getRootProcessInstanceId());
        job.setExpirationTime(instantToZonedDateTime(entity.getExpirationTime()));
        job.setPriority(entity.getPriority());
        job.setCallbackEndpoint(entity.getCallbackEndpoint());
        job.setRepeatInterval(entity.getRepeatInterval());
        job.setRepeatLimit(entity.getRepeatLimit());
        job.setScheduledId(entity.getScheduledId());
        job.setRetries(entity.getRetries());
        job.setStatus(entity.getStatus());
        job.setLastUpdate(instantToZonedDateTime(entity.getLastUpdate()));
        job.setExecutionCounter(entity.getExecutionCounter());
        job.setEndpoint(entity.getEndpoint());
        job.setNodeInstanceId(entity.getNodeInstanceId());
        return job;
    }
}
