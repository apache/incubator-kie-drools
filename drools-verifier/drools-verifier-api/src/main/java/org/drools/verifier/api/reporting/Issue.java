/**
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
package org.drools.verifier.api.reporting;

import java.util.HashSet;
import java.util.Set;

public class Issue {

    public static final Issue EMPTY = new Issue();

    private Severity severity;
    private Set<Integer> rowNumbers;
    private CheckType checkType;
    private String debugMessage;

    public Issue() {
        severity = null;
        rowNumbers = new HashSet<>();
    }

    public Issue(final Severity severity,
                 final CheckType checkType,
                 final Set<Integer> rowNumbers) {
        this.severity = severity;
        this.checkType = checkType;
        this.rowNumbers = rowNumbers;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Set<Integer> getRowNumbers() {
        return rowNumbers;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(final CheckType checkType) {
        this.checkType = checkType;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public Issue setDebugMessage(final String debugMessage) {
        this.debugMessage = debugMessage;
        return this;
    }

    public void setSeverity(final Severity severity) {
        this.severity = severity;
    }

    public void setRowNumbers(final Set<Integer> rowNumbers) {
        this.rowNumbers = rowNumbers;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Issue issue = (Issue) o;

        if (severity != issue.severity) {
            return false;
        }
        if (rowNumbers != null ? !rowNumbers.equals(issue.rowNumbers) : issue.rowNumbers != null) {
            return false;
        }
        if (checkType != issue.checkType) {
            return false;
        }
        return debugMessage != null ? debugMessage.equals(issue.debugMessage) : issue.debugMessage == null;
    }

    @Override
    public int hashCode() {
        int result = severity != null ? ~~severity.hashCode() : 0;
        result = 31 * result + (rowNumbers != null ? ~~rowNumbers.hashCode() : 0);
        result = 31 * result + (checkType != null ? ~~checkType.hashCode() : 0);
        result = 31 * result + (debugMessage != null ? ~~debugMessage.hashCode() : 0);
        return ~~result;
    }
}
