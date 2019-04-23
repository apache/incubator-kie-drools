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

package org.kie.submarine.codegen;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.submarine.codegen.process.config.ProcessConfigGenerator;

public class ConfigGenerator {

    private ProcessConfigGenerator processConfig;

    public ConfigGenerator withProcessConfig(ProcessConfigGenerator cfg) {
        this.processConfig = cfg;
        return this;
    }

    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(org.kie.submarine.StaticConfig.class.getCanonicalName())
                .addArgument(/* first arg is process config */ processConfig());

    }

    private Expression processConfig() {
        return processConfig == null ? new NullLiteralExpr() : processConfig.newInstance();
    }
}
