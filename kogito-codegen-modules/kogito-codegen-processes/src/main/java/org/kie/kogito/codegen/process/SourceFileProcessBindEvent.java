/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process;

import org.kie.kogito.codegen.api.SourceFileCodegenBindEvent;

/**
 * Event that is fired when a process is bound to a source file.
 */
public class SourceFileProcessBindEvent implements SourceFileCodegenBindEvent {

    private final String processId;

    private final String sourceFile;

    public SourceFileProcessBindEvent(String processId, String sourceFile) {
        this.processId = processId;
        this.sourceFile = sourceFile;
    }

    public String getSourceFileId() {
        return processId;
    }

    @Override
    public String getUri() {
        return sourceFile;
    }

    @Override
    public String toString() {
        return "SourceFileProcessBindEvent{" +
                "processId='" + processId + '\'' +
                ", sourceFile='" + sourceFile + '\'' +
                '}';
    }
}
