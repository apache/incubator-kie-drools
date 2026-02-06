/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.gradle.plugin;

import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;

import static org.kie.kogito.gradle.plugin.GenerateModelTask.GENERATE_MODEL_TASK_NAME;

public class KogitoGradlePlugin implements Plugin<Project> {

    public static final String PLUGIN_ID = "org.kie.kogito.gradle";
    public static final String CLASSES = "classes";

    @Override
    public void apply(Project project) {
        KogitoGradleExtension extension = project.getExtensions().create("generateModelConfig", KogitoGradleExtension.class);
        GenerateModelTask generateModelTask = project.getTasks().create(GENERATE_MODEL_TASK_NAME,  GenerateModelTask.class, extension);
        generateModelTask.setGroup(GENERATE_MODEL_TASK_NAME);
        generateModelTask.dependsOn(project.getTasks().named(CLASSES));
        File secondaryGenDir = project.getLayout().getBuildDirectory().getAsFile().get().toPath().resolve("generated").resolve("sources").resolve("kogito").toFile();
        JavaPluginExtension javaExt =
                project.getExtensions().getByType(JavaPluginExtension.class);

        generateModelTask.setGradleCompilerJavaVersion(javaExt.getTargetCompatibility().toString());
        SourceSetContainer sourceSets = javaExt.getSourceSets();
        SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        registerCompileSecondaryJavaTask(project, secondaryGenDir, main);
    }

    private void registerCompileSecondaryJavaTask(Project project, File secondaryGenDir, SourceSet main) {
        JavaCompile compileSecondaryJava = project.getTasks().register("compileSecondaryJava", JavaCompile.class).get();
        compileSecondaryJava.setDescription("Compile generated secondary Java sources");
        compileSecondaryJava.dependsOn(project.getTasks().named(GENERATE_MODEL_TASK_NAME));
        compileSecondaryJava.setSource(secondaryGenDir);
        File compiledClassesDirectory = project.getProjectDir().toPath()
                .resolve("build")
                .resolve(CLASSES)
                .resolve("java")
                .resolve("main")
                .toFile();
        FileCollection fileCollection = project.getConfigurations().getByName("compileClasspath")
                .plus(main.getCompileClasspath())
                        .plus(project.files(compiledClassesDirectory));
        compileSecondaryJava.setClasspath(fileCollection);
        compileSecondaryJava.getDestinationDirectory().set(compiledClassesDirectory);
    }

}