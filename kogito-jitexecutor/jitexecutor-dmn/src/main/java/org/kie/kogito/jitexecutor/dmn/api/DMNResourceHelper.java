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
package org.kie.kogito.jitexecutor.dmn.api;

import java.util.function.Supplier;

import jakarta.ws.rs.core.Response;

public class DMNResourceHelper {

    private DMNResourceHelper() {
    }

    public static Response manageResponse(Supplier<Response> responseSupplier) {
        try {
            return responseSupplier.get();
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Failed to get result due to " + e.getClass().getName();
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }
    }
}
