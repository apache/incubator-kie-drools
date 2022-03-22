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
package org.kogito.workitem.rest.bodybuilders;

import java.util.Map;

import org.kogito.workitem.rest.RestWorkItemHandler;

public class DefaultWorkItemHandlerBodyBuilder implements RestWorkItemHandlerBodyBuilder {
    @Override
    public Object apply(Map<String, Object> parameters) {
        Object contentData = parameters.get(RestWorkItemHandler.CONTENT_DATA);
        return contentData != null ? contentData : buildFromParams(parameters);
    }

    protected Object buildFromParams(Map<String, Object> parameters) {
        return parameters;
    }
}
