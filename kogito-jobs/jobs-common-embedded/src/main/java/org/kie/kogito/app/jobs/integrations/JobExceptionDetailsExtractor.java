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
package org.kie.kogito.app.jobs.integrations;

import org.kie.kogito.jobs.service.model.JobExecutionExceptionDetails;

/**
 * Interface for extracting exception details from an Exception.
 * Allows custom implementations for sensitive content sanitization or custom formatting.
 */
public interface JobExceptionDetailsExtractor {

    /**
     * Extracts exception details from an Exception.
     *
     * @param exception the exception to extract details from
     * @return JobExecutionExceptionDetails or null if exception is null
     */
    JobExecutionExceptionDetails extractExceptionDetails(Exception exception);
}
