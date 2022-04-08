/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.test.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobServiceResource extends AbstractJobServiceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceResource.class);

    @Override
    public void start() {
        LOGGER.info("Start JobService test resource");
        properties.clear();
        jobService.start();
        LOGGER.info("JobService test resource started");
    }

    @Override
    public void stop() {
        LOGGER.info("Stop JobService test resource");
        jobService.stop();
        LOGGER.info("JobService test resource stopped");
    }
}