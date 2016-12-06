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

package org.kie.api.runtime.builder;

import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Executable;

public interface ExecutableBuilder extends TimeFluent<ExecutableBuilder>, ContextFluent<ExecutableBuilder, ExecutableBuilder> {

    KieContainerFluent getKieContainer(ReleaseId releaseId);

    Executable getExecutable();

    static ExecutableBuilder create() {
        try {
            return (ExecutableBuilder) Class.forName( "org.drools.core.fluent.impl.ExecutableBuilderImpl" ).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner", e);
        }
    }
}
