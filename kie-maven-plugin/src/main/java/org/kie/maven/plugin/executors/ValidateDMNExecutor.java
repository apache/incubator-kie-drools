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

package org.kie.maven.plugin.executors;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
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

public class ValidateDMNExecutor {

    private ValidateDMNExecutor() {
    }

    public static void validateDMN(final List<Resource> resources,
                                   final String validateDMN,
                                   final Log log) throws MojoExecutionException, MojoFailureException {

        final List<Validation> actualFlags = new ArrayList<>(computeFlagsFromCSVString(validateDMN, log));
        // for this phase, keep only the following flags (the rest requires the BuildMojo).
        actualFlags.retainAll(Arrays.asList(Validation.VALIDATE_SCHEMA, Validation.VALIDATE_MODEL));
        if (actualFlags.isEmpty()) {
            log.info("No VALIDATE_SCHEMA or VALIDATE_MODEL flags set, skipping.");
            return;
        }

        final List<Path> dmnModelPaths = computeDmnModelPaths(resources, log);
        if (dmnModelPaths.isEmpty()) {
            log.info("No DMN Models found.");
            return;
        }

        dmnModelPaths.forEach(x -> log.info("Will validate DMN model: " + x.toString()));
        log.info("Initializing DMNValidator...");
        final DMNValidator validator = getDMNValidator(resources, log);
        log.info("DMNValidator initialized.");
        List<DMNMessage> validation = validator.validateUsing(actualFlags.toArray(new Validation[]{}))
                .theseModels(dmnModelPaths.stream().map(Path::toFile).collect(Collectors.toList()).toArray(new File[]{}));
        logValidationMessages(validation, ValidateDMNExecutor::validateMsgPrefixer, DMNMessage::getText, log);
        if (validation.stream().anyMatch(m -> m.getLevel() == Level.ERROR)) {
            throw new MojoFailureException("There are DMN Validation Error(s).");
        }
    }

    private static String validateMsgPrefixer(DMNMessage msg) {
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

    private static List<Path> computeDmnModelPaths(List<Resource> resources, Log log) throws MojoExecutionException {
        List<Path> dmnModelPaths = new ArrayList<>();
        for (Path p : resourcesPaths(resources, log)) {
            log.info("Looking for DMN models in path: " + p);
            try (Stream<Path> walk = Files.walk(p)) {
                walk.filter(f -> f.toString().endsWith(".dmn"))
                        .forEach(dmnModelPaths::add);
            } catch (Exception e) {
                throw new MojoExecutionException("Failed executing ValidateDMNMojo", e);
            }
        }
        return dmnModelPaths;
    }

    private static DMNValidator getDMNValidator(List<Resource> resources, Log log) throws MojoExecutionException {
        List<DMNProfile> dmnProfiles = computeDMNProfiles(resources, log);
        return DMNValidatorFactory.newValidator(dmnProfiles);
    }
}

