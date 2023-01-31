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
package org.kie.kogito.serverless.workflow.asyncapi;

public class AsyncChannelInfo {
    private final String name;
    private final boolean isPublish;

    public AsyncChannelInfo(String name, boolean isPublish) {
        this.name = name;
        this.isPublish = isPublish;
    }

    public String getName() {
        return name;
    }

    public boolean isPublish() {
        return isPublish;
    }

    @Override
    public String toString() {
        return "AsyncChannelInfo [name=" + name + ", isPublish=" + isPublish + "]";
    }
}
