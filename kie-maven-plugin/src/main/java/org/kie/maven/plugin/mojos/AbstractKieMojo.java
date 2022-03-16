/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.maven.plugin.mojos;

import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.kie.memorycompiler.JavaConfiguration;

import static org.kie.maven.plugin.helpers.ExecModelModeHelper.execModelParameterEnabled;

public abstract class AbstractKieMojo extends AbstractMojo {

    @Parameter(property = "dumpKieSourcesFolder", defaultValue = "")
    protected String dumpKieSourcesFolder;

    @Parameter(property = "generateModel", defaultValue = "YES_WITHDRL") // DROOLS-5663 align kie-maven-plugin default value for generateModel configuration flag
    private String generateModel;


    @Parameter(property = "javaCompiler", defaultValue = "ecj")
    private String javaCompiler;

    @Parameter(required = true, defaultValue = "${project.build.resources}")
    protected List<Resource> resources;

    @Parameter(property = "validateDMN", defaultValue = "VALIDATE_SCHEMA,VALIDATE_MODEL,ANALYZE_DECISION_TABLE")
    protected String validateDMN;

    public String getGenerateModelOption() {
        return generateModel;
    }

    protected boolean isModelParameterEnabled() {
        return execModelParameterEnabled(generateModel);
    }

    protected JavaConfiguration.CompilerType getCompilerType() {
        return javaCompiler.equalsIgnoreCase("native") ? JavaConfiguration.CompilerType.NATIVE : JavaConfiguration.CompilerType.ECLIPSE;
    }

}
