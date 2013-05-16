/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.task.model;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Status {
    Created, Ready, Reserved, InProgress, Suspended, Completed, Failed, Error, Exited, Obsolete;

    private final String value;

    private Status() {
        value = this.name();
    }

    public String value() {
        return value;
    }

    public static Status fromValue(String newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException(newValue);
        }
        for (Status value : Status.values()) {
            if (value.value.toLowerCase().equals(newValue.toLowerCase())) {
                return value;
            }
        }
        throw new IllegalArgumentException(newValue);
    }
}
