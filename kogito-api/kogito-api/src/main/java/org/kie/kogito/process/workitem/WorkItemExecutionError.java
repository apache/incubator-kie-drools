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
package org.kie.kogito.process.workitem;

public class WorkItemExecutionError extends RuntimeException {

    private static final long serialVersionUID = 4739415822214766299L;

    private final String errorCode;

    public WorkItemExecutionError(String errorCode) {
        super("WorkItem execution failed with error code " + errorCode);
        this.errorCode = errorCode;
    }

    public WorkItemExecutionError(String errorCode, Throwable e) {
        super("WorkItem execution failed with error code " + errorCode, e);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "WorkItemExecutionError [errorCode=" + errorCode + "]";
    }

}
