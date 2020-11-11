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
package org.kie.kogito.codegen.rules;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.builder.ModelSourceClass;
import org.kie.api.KieBase;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.KieRuntimeBuilder;

public class ProjectSourceClass {

    public static final String PROJECT_RUNTIME_CLASS = "org.drools.project.model.ProjectRuntime";
    private static final String PROJECT_RUNTIME_RESOURCE_CLASS = PROJECT_RUNTIME_CLASS.replace('.', '/') + ".class";
    private static final String PROJECT_RUNTIME_SOURCE = PROJECT_RUNTIME_CLASS.replace('.', '/') + ".java";

    final ModelSourceClass.KieModuleModelMethod modelMethod;
    private String dependencyInjection = "";

    public ProjectSourceClass(ModelSourceClass.KieModuleModelMethod modelMethod) {
        this.modelMethod = modelMethod;
    }

    public ProjectSourceClass withDependencyInjection(String dependencyInjection) {
        this.dependencyInjection = dependencyInjection;
        return this;
    }
    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "package org.drools.project.model;\n" +
                        "\n" +
                        "import " + KieBase.class.getCanonicalName()  + ";\n" +
                        "import " + KieBaseModel.class.getCanonicalName()  + ";\n" +
                        "import " + KieSession.class.getCanonicalName()  + ";\n" +
                        "import " + KieBaseBuilder.class.getCanonicalName()  + ";\n" +
                        "\n" +
                        dependencyInjection + "\n"+
                        "public class ProjectRuntime implements " + KieRuntimeBuilder.class.getCanonicalName() + " {\n" +
                        "\n" +
                        "    private final ProjectModel model = new ProjectModel();\n" +
                        "    private final java.util.Map<String, KieBase> kbases = new java.util.HashMap<>();\n" +
                        "\n" +
                        "    public static final ProjectRuntime INSTANCE = new ProjectRuntime();\n" +
                        "\n");
        sb.append(modelMethod.toGetKieBaseMethods());
        sb.append("\n");
        sb.append(modelMethod.toNewKieSessionMethods());
        sb.append("\n");
        sb.append(modelMethod.toGetKieBaseForSessionMethod());
        sb.append("\n");
        sb.append(modelMethod.toKieSessionConfMethod());
        sb.append("\n}" );
        return sb.toString();
    }

    public void write(MemoryFileSystem srcMfs) {
        srcMfs.write(getName(), generate().getBytes());
    }

    public String getName() {
        return PROJECT_RUNTIME_SOURCE;
    }
}
