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
package org.kie.kogito.index.jpa.storage;

import org.kie.kogito.index.jpa.mapper.JobEntityMapper;
import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.index.jpa.model.JobEntity;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.storage.JobInstanceStorage;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class JobEntityStorage extends AbstractStorage<String, JobEntity, Job> implements JobInstanceStorage {

    protected JobEntityStorage() {
    }

    @Inject
    public JobEntityStorage(EntityManager em) {
        super(em, Job.class, JobEntity.class, JobEntityMapper.INSTANCE::mapToModel, JobEntityMapper.INSTANCE::mapToEntity, AbstractEntity::getId);
    }

    @Transactional
    @Override
    public void indexJob(Job job) {
        JobEntity entity = findOrInit(job.getId(), job);
        updateJobEntity(entity, job);
    }

    private JobEntity findOrInit(String jobId, Job job) {
        JobEntity entity = em.find(JobEntity.class, jobId);
        if (entity == null) {
            entity = new JobEntity();
            entity.setId(jobId);
            entity.setProcessId(job.getProcessId());
            entity.setRootProcessId(job.getRootProcessId());
            em.persist(entity);
        } else {
            if (entity.getProcessId() == null) {
                entity.setProcessId(job.getProcessId());
            }
            if (entity.getRootProcessId() == null) {
                entity.setRootProcessId(job.getRootProcessId());
            }
        }
        return entity;
    }

    private void updateJobEntity(JobEntity entity, Job job) {
        entity.setProcessInstanceId(job.getProcessInstanceId());
        entity.setNodeInstanceId(job.getNodeInstanceId());
        entity.setRootProcessInstanceId(job.getRootProcessInstanceId());
        entity.setExpirationTime(job.getExpirationTime());
        entity.setPriority(job.getPriority());
        entity.setCallbackEndpoint(job.getCallbackEndpoint());
        entity.setRepeatInterval(job.getRepeatInterval());
        entity.setRepeatLimit(job.getRepeatLimit());
        entity.setScheduledId(job.getScheduledId());
        entity.setRetries(job.getRetries());
        entity.setStatus(job.getStatus());
        entity.setLastUpdate(job.getLastUpdate());
        entity.setExecutionCounter(job.getExecutionCounter());
        entity.setEndpoint(job.getEndpoint());
    }
}
