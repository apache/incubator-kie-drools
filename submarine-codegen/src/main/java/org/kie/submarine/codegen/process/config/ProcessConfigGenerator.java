/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.submarine.codegen.process.config;

import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.submarine.process.impl.DefaultProcessEventListenerConfig;
import org.kie.submarine.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.submarine.process.impl.StaticProcessConfig;

public class ProcessConfigGenerator {
    private String workItemConfigClass = DefaultWorkItemHandlerConfig.class.getCanonicalName();
    private String processEventListenerConfigClass = DefaultProcessEventListenerConfig.class.getCanonicalName();

    public ProcessConfigGenerator withWorkItemConfig(String cfg) {
        this.workItemConfigClass = cfg;
        return this;
    }
    public ProcessConfigGenerator withProcessEventListenerConfig(String cfg) {
        this.processEventListenerConfigClass = cfg;
        return this;
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(StaticProcessConfig.class.getCanonicalName())
                .addArgument(new ObjectCreationExpr().setType(workItemConfigClass))
                .addArgument(new ObjectCreationExpr().setType(processEventListenerConfigClass));
    }
}
