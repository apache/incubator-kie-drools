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

package org.kie.kogito.index.oracle.storage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.oracle.mapper.JobEntityMapper;
import org.kie.kogito.index.oracle.model.AbstractEntity;
import org.kie.kogito.index.oracle.model.JobEntity;
import org.kie.kogito.index.oracle.model.JobEntityRepository;

@ApplicationScoped
public class JobEntityStorage extends AbstractStorage<JobEntity, Job> {

    public JobEntityStorage() {
    }

    @Inject
    public JobEntityStorage(JobEntityRepository repository, JobEntityMapper mapper) {
        super(repository, Job.class, JobEntity.class, mapper::mapToModel, mapper::mapToEntity, AbstractEntity::getId);
    }
}
