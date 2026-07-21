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
package org.kie.kogito.services.context;

import java.util.Map;

/**
 * Simplified interface for context extensions that need to participate in MDC context preservation
 * during async operations in ProcessInstanceContext.
 *
 * Extensions can register themselves to preserve their MDC keys during context restoration.
 *
 * Thread Safety: Implementations must be thread-safe as they may be invoked
 * concurrently from multiple threads.
 */
public interface ContextExtension {

    /**
     * Returns the MDC key prefix that this extension uses.
     * All MDC keys with this prefix will be preserved during context restoration.
     *
     * @return the MDC key prefix (e.g., "otel.", "custom."), must not be null or empty
     */
    String getMdcKeyPrefix();

    /**
     * Called to restore extension-specific keys after core context restoration.
     * Extensions should restore their preserved keys to MDC during this phase.
     *
     * @param preservedKeys the keys that were preserved for this extension
     */
    void restoreKeys(Map<String, String> preservedKeys);
}