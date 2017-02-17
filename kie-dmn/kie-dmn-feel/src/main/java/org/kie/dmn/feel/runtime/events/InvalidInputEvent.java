/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report a syntax error as returned by the parser
 */
public class InvalidInputEvent
        extends FEELEventBase
        implements FEELEvent {

    private final String nodeName;
    private final String inputName;
    private final String validInputs;

    public InvalidInputEvent(Severity severity, String msg, String nodeName, String inputName, String validInputs) {
        super( severity, msg, null );
        this.nodeName = nodeName;
        this.inputName = inputName;
        this.validInputs = validInputs;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getInputName() {
        return inputName;
    }

    public String getValidInputs() {
        return validInputs;
    }

    @Override
    public String toString() {
        return "InvalidInputEvent{" +
               "severity=" + getSeverity() +
               ", message='" + getMessage() + '\'' +
               ", nodeName='" + nodeName + '\'' +
               ", inputName='" + inputName + '\'' +
               ", validInputs=" + validInputs +
               '}';
    }
}
