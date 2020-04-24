/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence;

import java.nio.file.Paths;

import org.kie.kogito.persistence.filesystem.FileSystemProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.persistence.KogitoProcessInstancesFactory</code>
 *
 */
public abstract class KogitoProcessInstancesFactory implements ProcessInstancesFactory {
   
	
    public FileSystemProcessInstances createProcessInstances(Process<?> process) {
        return new FileSystemProcessInstances(process, Paths.get(path()));
    }
    
    public abstract String path();
    
}
