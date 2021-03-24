/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.event;

import java.net.URI;

import org.kie.kogito.index.model.Job;

public class KogitoJobCloudEvent extends KogitoCloudEvent<Job> {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void setSource(URI source) {
        super.setSource(source);
        if (getData() != null && source != null) {
            getData().setEndpoint(source.toString());
        }
    }

    @Override
    public void setData(Job data) {
        super.setData(data);
        setSource(getSource());
    }

    @Override
    public String toString() {
        return "KogitoJobCloudEvent{} " + super.toString();
    }

    public static final class Builder extends AbstractBuilder<Builder, Job, KogitoJobCloudEvent> {

        private Builder() {
            super(new KogitoJobCloudEvent());
        }
    }
}
