/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation;

public class ValidationMsg {
    public static enum Severity {
        TRACE, INFO, WARN, ERROR;
    }
    private Object reference;
    private Msg message;
    private Severity severity;
    
    public ValidationMsg(Severity severity, Msg message, Object reference) {
        this.severity = severity;
        this.message = message;
        this.reference = reference;
    }

    public Object getReference() {
        return reference;
    }
    
    public String getShortname() {
        return message.getShortname();
    }

    public Msg getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return this.severity;
    }
}
