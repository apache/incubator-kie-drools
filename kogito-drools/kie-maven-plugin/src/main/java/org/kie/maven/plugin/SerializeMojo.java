/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Compiles and serializes knowledge packages.
 */
@Mojo(name = "serialize",
      requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
      requiresProject = true,
      defaultPhase = LifecyclePhase.COMPILE,
      configurator = "include-project-dependencies")
public class SerializeMojo extends AbstractMojo {

    /**
     * KnowledgeBases to serialize
     */
    @Parameter(property = "kie.kiebases",required = true)
    private List<String> kiebases;

    /**
     * Output folder
     */
    @Parameter(property = "kie.resDirectory", defaultValue = "${project.basedir}/src/main/res/raw" )
    private String resDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File outputFolder = new File(resDirectory);
            outputFolder.mkdirs();

            KieServices ks = KieServices.Factory.get();
            KieContainer kc = ks.newKieClasspathContainer();
            Results messages = kc.verify();

            List<Message> warnings = messages.getMessages(Message.Level.WARNING);
            for (Message warning : warnings) {
                getLog().warn(warnings.toString());
            }
            List<Message> errors = messages.getMessages(Message.Level.ERROR);
            if (!errors.isEmpty()) {
                for (Message error : errors) {
                    getLog().error(error.toString());
                }
                throw new MojoFailureException("Build failed!");
            }

            for(String kbase : kiebases) {
                KieBase kb = kc.getKieBase(kbase);
                getLog().info("Writing KBase: " + kbase);
                File file = new File(outputFolder, kbase.replace('.', '_').toLowerCase());
                FileOutputStream out = new FileOutputStream(file);
                DroolsStreamUtils.streamOut(out, kb.getKiePackages());
                out.close();
            }
        } catch (Exception e) {
            throw new MojoExecutionException("error", e);
        }
    }
}
