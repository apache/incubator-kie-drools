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
package org.kie.kogito.persistence.filesystem;

import java.nio.file.Paths;

import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;

public abstract class AbstractProcessInstancesFactory implements ProcessInstancesFactory {

    private final String path;

    public AbstractProcessInstancesFactory(String path) {
        this.path = path;
    }

    public FileSystemProcessInstances createProcessInstances(Process<?> process) {
        return new FileSystemProcessInstances(process, Paths.get(path));
    }

}
