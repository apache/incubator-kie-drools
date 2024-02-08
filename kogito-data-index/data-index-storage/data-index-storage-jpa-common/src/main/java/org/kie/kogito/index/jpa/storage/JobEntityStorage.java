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
import org.kie.kogito.index.jpa.model.JobEntityRepository;
import org.kie.kogito.index.model.Job;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JobEntityStorage extends AbstractStorage<JobEntity, Job> {

    protected JobEntityStorage() {
    }

    @Inject
    public JobEntityStorage(JobEntityRepository repository, JobEntityMapper mapper) {
        super(repository, Job.class, JobEntity.class, mapper::mapToModel, mapper::mapToEntity, AbstractEntity::getId);
    }
}
