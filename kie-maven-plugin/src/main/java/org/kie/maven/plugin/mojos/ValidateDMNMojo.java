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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.DMNValidatorFactory;

import static org.kie.maven.plugin.helpers.DMNValidationHelper.computeDMNProfiles;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.computeFlagsFromCSVString;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.logValidationMessages;
import static org.kie.maven.plugin.helpers.DMNValidationHelper.resourcesPaths;

@Mojo(name = "validateDMN",
        requiresDependencyResolution = ResolutionScope.NONE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class ValidateDMNMojo extends AbstractKieMojo {

    @Parameter
    private Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    private MavenProject project;

    private List<Validation> actualFlags = new ArrayList<>();

    private DMNValidator validator;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        actualFlags.addAll(computeFlagsFromCSVString(getValidateDMN(), getLog()));
        // for this phase, keep only the following flags (the rest requires the BuildMojo).
        actualFlags.retainAll(Arrays.asList(Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL));
        if (actualFlags.isEmpty()) {
            getLog().info("No VALIDATE_SCHEMA or VALIDATE_MODEL flags set, skipping.");
            return;
        }

        List<Path> dmnModelPaths = computeDmnModelPaths();
        if (dmnModelPaths.isEmpty()) {
            getLog().info("No DMN Models found.");
            return;
        }

        getLog().info("Initializing DMNValidator...");
        initializeDMNValidator();
        getLog().info("DMNValidator initialized.");

        dmnModelPaths.forEach(x -> getLog().info("Will validate DMN model: " + x.toString()));
        List<DMNMessage> validation = validator.validateUsing(actualFlags.toArray(new Validation[]{}))
                                               .theseModels(dmnModelPaths.stream().map(Path::toFile).collect(Collectors.toList()).toArray(new File[]{}));
        logValidationMessages(validation, this::validateMsgPrefixer, DMNMessage::getText, getLog());
        if (validation.stream().anyMatch(m -> m.getLevel() == Level.ERROR)) {
            throw new MojoFailureException("There are DMN Validation Error(s).");
        }
    }

    private String validateMsgPrefixer(DMNMessage msg) {
        if (msg.getSourceReference() instanceof DMNModelInstrumentedBase) {
            DMNModelInstrumentedBase ib = (DMNModelInstrumentedBase) msg.getSourceReference();
            while (ib.getParent() != null) {
                ib = ib.getParent();
            }
            if (ib instanceof Definitions) {
                return (((Definitions) ib).getName() + ": ");
            }
        }
        return "";
    }

    private List<Path> computeDmnModelPaths() throws MojoExecutionException {
        List<Path> dmnModelPaths = new ArrayList<>();
        for (Path p : resourcesPaths(resources, getLog())) {
            getLog().info("Looking for DMN models in path: " + p);
            try (Stream<Path> walk = Files.walk(p)) {
                walk.filter(f -> f.toString().endsWith(".dmn"))
                        .forEach(dmnModelPaths::add);
            } catch (Exception e) {
                throw new MojoExecutionException("Failed executing ValidateDMNMojo", e);
            }
        }
        return dmnModelPaths;
    }

    private void initializeDMNValidator() throws MojoExecutionException {
        List<DMNProfile> dmnProfiles = computeDMNProfiles(resources, getLog());
        validator = DMNValidatorFactory.newValidator(dmnProfiles);
    }
}

