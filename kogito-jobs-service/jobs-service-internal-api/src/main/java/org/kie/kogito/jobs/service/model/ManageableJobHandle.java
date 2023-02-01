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
package org.kie.kogito.jobs.service.model;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.kie.kogito.timer.JobHandle;

public class ManageableJobHandle implements JobHandle {

    private boolean cancel;
    private Long id;
    private ZonedDateTime scheduledTime;

    public ManageableJobHandle(Long id) {
        this.id = id;
    }

    public ManageableJobHandle(String id) {
        this.id = Optional.ofNullable(id).map(Long::parseLong).orElse(null);
    }

    public ManageableJobHandle(boolean cancel) {
        this.cancel = cancel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return Optional.ofNullable(id).orElse(0l);
    }

    @Override
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }

    public ZonedDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(ZonedDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
