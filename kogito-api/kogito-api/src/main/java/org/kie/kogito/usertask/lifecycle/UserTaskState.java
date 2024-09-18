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
package org.kie.kogito.usertask.lifecycle;

import java.util.Objects;
import java.util.Optional;

public class UserTaskState {

    public enum TerminationType {
        COMPLETED,
        ABORT,
        FAILED,
        EXITED,
        OBSOLETE,
        ERROR
    }

    private TerminationType terminate;

    private String name;

    public static UserTaskState of(String name) {
        return of(name, null);
    }

    public static UserTaskState of(String name, TerminationType terminate) {
        return new UserTaskState(name, terminate);
    }

    public UserTaskState() {

    }

    public TerminationType getTerminate() {
        return terminate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTerminate(TerminationType terminate) {
        this.terminate = terminate;
    }

    private UserTaskState(String name, TerminationType terminate) {
        this.name = name;
        this.terminate = terminate;
    }

    public String getName() {
        return name;
    }

    public Optional<TerminationType> isTerminate() {
        return Optional.ofNullable(terminate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, terminate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskState other = (UserTaskState) obj;
        return Objects.equals(name, other.name) && terminate == other.terminate;
    }

    public static UserTaskState initalized() {
        return of(null);
    }

    @Override
    public String toString() {
        return "UserTaskState [terminate=" + terminate + ", name=" + name + "]";
    }

}
