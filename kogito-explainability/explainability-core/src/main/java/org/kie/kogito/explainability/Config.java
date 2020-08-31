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
package org.kie.kogito.explainability;

import java.util.concurrent.TimeUnit;

public class Config {

    public static final long DEFAULT_ASYNC_TIMEOUT = 5;
    public static final TimeUnit DEFAULT_ASYNC_TIMEUNIT = TimeUnit.SECONDS;

    public static final Config INSTANCE = new Config();

    private long asyncTimeout = DEFAULT_ASYNC_TIMEOUT;
    private TimeUnit asyncTimeUnit = DEFAULT_ASYNC_TIMEUNIT;

    private Config() {}

    public long getAsyncTimeout() {
        return asyncTimeout;
    }

    public void setAsyncTimeout(long asyncTimeout) {
        this.asyncTimeout = asyncTimeout;
    }

    public TimeUnit getAsyncTimeUnit() {
        return asyncTimeUnit;
    }

    public void setAsyncTimeUnit(TimeUnit asyncTimeUnit) {
        this.asyncTimeUnit = asyncTimeUnit;
    }
}
