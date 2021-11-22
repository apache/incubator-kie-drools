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
package org.kie.kogito.jobs;

import java.text.MessageFormat;
import java.text.ParseException;

public class AsyncJobId implements JobId<String, Object> {

    public final static String TYPE = "ASYNC";
    private static final String SIGNAL = "asyncTriggered";
    private static MessageFormat format = new MessageFormat("{0}:{1}");
    private String uuid;

    public AsyncJobId() {
    }

    public AsyncJobId(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String encode() {
        return format.format(new Object[] { TYPE, uuid });
    }

    @Override
    public String signal() {
        return SIGNAL + ":" + uuid;
    }

    @Override
    public AsyncJobId decode(String value) {
        try {
            Object[] values = format.parse(value);
            this.uuid = (String) values[1];
            return this;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Object payload(Object... parameters) {
        return null;
    }

    @Override
    public String correlationId() {
        return uuid;
    }
}
