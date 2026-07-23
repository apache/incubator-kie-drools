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
package org.kie.kogito.app.jobs.impl;

import java.time.ZonedDateTime;

import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobDescription;

public class TestJobDescription implements JobDescription {

    private String id;
    private ExpirationTime expirationTime;

    public TestJobDescription(String id, ExpirationTime expirationTime) {
        this.id = id;
        this.expirationTime = expirationTime;
    }

    public TestJobDescription(String id, ZonedDateTime zonedDateTime) {
        this(id, ExactExpirationTime.of(zonedDateTime));
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ExpirationTime expirationTime() {
        return expirationTime;
    }

    @Override
    public Integer priority() {
        return 0;
    }

    @Override
    public String path() {
        return "/";
    }

}
