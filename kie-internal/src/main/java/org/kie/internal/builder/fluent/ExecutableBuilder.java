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
package org.kie.internal.builder.fluent;

import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.utils.ClassLoaderUtil;

public interface ExecutableBuilder extends TimeFluent<ExecutableBuilder>,
                                           ContextFluent<ExecutableBuilder, ExecutableBuilder> {

    KieContainerFluent getKieContainer(ReleaseId releaseId);

    KieContainerFluent setKieContainer(KieContainer kieContainer);

    Executable getExecutable();

    static ExecutableBuilder create() {
        try {
            return (ExecutableBuilder) ClassLoaderUtil.getClassLoader(null, null, true)
                    .loadClass("org.drools.commands.fluent.ExecutableBuilderImpl")
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
