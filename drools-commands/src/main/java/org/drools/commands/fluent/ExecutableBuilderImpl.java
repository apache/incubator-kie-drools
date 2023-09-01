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
package org.drools.commands.fluent;

import org.drools.commands.GetKieContainerCommand;
import org.drools.commands.SetKieContainerCommand;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;

public class ExecutableBuilderImpl extends BaseBatchFluent<ExecutableBuilder, ExecutableBuilder> implements ExecutableBuilder {

    public ExecutableBuilderImpl() {
        super(new ExecutableImpl());
        getFluentContext().setExecutableBuilder(this);
    }

    @Override
    public KieContainerFluent getKieContainer(ReleaseId releaseId) {
        addCommand(new GetKieContainerCommand(releaseId));
        KieContainerFluentImpl fluent = new KieContainerFluentImpl(fluentCtx);
        return fluent;
    }

    @Override
    public KieContainerFluent setKieContainer(KieContainer kieContainer) {
        addCommand(new SetKieContainerCommand(kieContainer));
        KieContainerFluentImpl fluent = new KieContainerFluentImpl(fluentCtx);
        return fluent;
    }

    @Override
    public Executable getExecutable() {
        return getFluentContext();
    }
}
