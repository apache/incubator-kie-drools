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
package org.kie.kogito.index.addon;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.service.IndexingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

@ApplicationScoped
public class DataIndexEventPublisher implements EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexEventPublisher.class);

    @Inject
    IndexingService indexingService;

    @Override
    @Transactional
    public void publish(DataEvent<?> event) {
        LOGGER.debug("Sending event to embedded data index: {}", event);
        switch (event.getType()) {
            case "ProcessInstanceErrorDataEvent":
            case "ProcessInstanceNodeDataEvent":
            case "ProcessInstanceSLADataEvent":
            case "ProcessInstanceStateDataEvent":
            case "ProcessInstanceVariableDataEvent":
                indexingService.indexProcessInstanceEvent((ProcessInstanceDataEvent<?>) event);
                break;
            case "UserTaskInstanceAssignmentDataEvent":
            case "UserTaskInstanceAttachmentDataEvent":
            case "UserTaskInstanceCommentDataEvent":
            case "UserTaskInstanceDeadlineDataEvent":
            case "UserTaskInstanceStateDataEvent":
            case "UserTaskInstanceVariableDataEvent":
                indexingService.indexUserTaskInstanceEvent((UserTaskInstanceDataEvent<?>) event);
                break;
            case "JobEvent":
                try {
                    Job job = getObjectMapper().readValue(new String((byte[]) event.getData()), Job.class);
                    job.setEndpoint(event.getSource() == null ? null : event.getSource().toString());
                    indexingService.indexJob(job);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                break;
            default:
                LOGGER.debug("Unknown type of event '{}', ignoring for this publisher", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

    protected void setIndexingService(IndexingService indexingService) {
        this.indexingService = indexingService;
    }
}
