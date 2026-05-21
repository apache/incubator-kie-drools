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
package org.kie.kogito.jobs.service.repository;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;

public interface ReactiveJobRepository {

    enum SortTermField {
        FIRE_TIME,
        CREATED,
        ID
    }

    class SortTerm {
        private final SortTermField field;
        private final boolean asc;

        private SortTerm(SortTermField field, boolean asc) {
            this.field = field;
            this.asc = asc;
        }

        public SortTermField getField() {
            return field;
        }

        public boolean isAsc() {
            return asc;
        }

        public static SortTerm byFireTime(boolean asc) {
            return new SortTerm(SortTermField.FIRE_TIME, asc);
        }

        public static SortTerm byCreated(boolean asc) {
            return new SortTerm(SortTermField.CREATED, asc);
        }

        public static SortTerm byId(boolean asc) {
            return new SortTerm(SortTermField.ID, asc);
        }
    }

    CompletionStage<JobDetails> save(JobDetails job);

    CompletionStage<JobDetails> merge(String id, JobDetails job);

    CompletionStage<JobDetails> get(String id);

    CompletionStage<Boolean> exists(String id);

    CompletionStage<JobDetails> delete(String id);

    CompletionStage<JobDetails> delete(JobDetails job);

    PublisherBuilder<JobDetails> findByStatusBetweenDates(ZonedDateTime fromFireTime,
            ZonedDateTime toFireTime,
            JobStatus[] status,
            SortTerm[] orderBy);
}
