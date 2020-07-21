/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageExceptionField {

    public static final String CAUSE_FIELD = "cause";
    public static final String CLASS_NAME_FIELD = "className";
    public static final String MESSAGE_FIELD = "message";

    @JsonProperty(CLASS_NAME_FIELD)
    private String className;

    @JsonProperty(MESSAGE_FIELD)
    private String message;

    @JsonProperty(CAUSE_FIELD)
    private MessageExceptionField cause;

    public MessageExceptionField() {
    }

    public MessageExceptionField(String className, String message, MessageExceptionField cause) {
        this.className = className;
        this.message = message;
        this.cause = cause;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageExceptionField getCause() {
        return cause;
    }

    public void setCause(MessageExceptionField cause) {
        this.cause = cause;
    }

    public static MessageExceptionField from(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return new MessageExceptionField(throwable.getClass().getName(), throwable.getMessage(), from(throwable.getCause()));
    }
}
