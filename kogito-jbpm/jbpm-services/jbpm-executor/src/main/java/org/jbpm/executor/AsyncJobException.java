/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor;


public class AsyncJobException extends RuntimeException {

    private static final long serialVersionUID = 3657806705802679562L;
    
    private Long jobId;
    private String commandName;

    public AsyncJobException(Long jobId, String commandName, String message, Throwable cause) {
        super(message, cause);
        this.jobId = jobId;
        this.commandName = commandName;
    }

    public AsyncJobException(Long jobId, String commandName, String message) {
        super(message);
        this.jobId = jobId;
        this.commandName = commandName;
    }

    public AsyncJobException(Long jobId, String commandName, Throwable cause) {
        super(cause);
        this.jobId = jobId;
        this.commandName = commandName;
    }
    
    public Long getJobId() {
        return jobId;
    }
    
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

}
