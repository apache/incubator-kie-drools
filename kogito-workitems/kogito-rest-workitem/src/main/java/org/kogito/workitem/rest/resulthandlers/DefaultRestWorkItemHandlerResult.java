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
package org.kogito.workitem.rest.resulthandlers;

import java.util.Map;

import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;

import static org.kogito.workitem.rest.RestWorkItemHandlerUtils.checkStatusCode;

public class DefaultRestWorkItemHandlerResult implements RestWorkItemHandlerResult {

    @Override
    public Object apply(HttpResponse<Buffer> response, Class<?> target) {
        checkStatusCode(response);
        return target == null ? response.bodyAsJson(Map.class) : response.bodyAsJson(target);
    }
}
