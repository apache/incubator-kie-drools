/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process.events;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.core.events.CloudEventMetaBuilder;
import org.kie.kogito.codegen.process.ProcessContainerGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerator;

public class ProcessCloudEventMetaBuilder implements CloudEventMetaBuilder<ProcessCloudEventMeta, Collection<ProcessExecutableModelGenerator>> {

    public ProcessCloudEventMetaBuilder() {
    }

    public Set<ProcessCloudEventMeta> build(Set<ProcessContainerGenerator> sourceModel) {
        return this.build(sourceModel.stream().flatMap(
                p -> p.getProcesses().stream().map(ProcessGenerator::getProcessExecutable))
                .collect(Collectors.toSet()));
    }

    @Override
    public Set<ProcessCloudEventMeta> build(Collection<ProcessExecutableModelGenerator> sourceModel) {
        if (sourceModel != null) {
            return sourceModel
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null && !m.generate().getTriggers().isEmpty())
                    .flatMap(m -> m.generate().getTriggers().stream()
                            .filter(t -> !TriggerMetaData.TriggerType.Signal.equals(t.getType()))
                            .map(ce -> new ProcessCloudEventMeta(m.getProcessId(), ce)))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
