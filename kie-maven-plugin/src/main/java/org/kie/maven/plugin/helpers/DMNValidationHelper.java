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
package org.kie.maven.plugin.helpers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.feel.util.ClassLoaderUtil;
import org.kie.dmn.validation.DMNValidator.Validation;
import org.kie.dmn.validation.dtanalysis.InternalDMNDTAnalyser;
import org.kie.dmn.validation.dtanalysis.InternalDMNDTAnalyserFactory;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.internal.utils.ChainedProperties;

public class DMNValidationHelper {

    private DMNValidationHelper() {
    }

    public static void logValidationMessages(List<DMNMessage> validation,
                                         Function<DMNMessage, String> prefixer,
                                         Function<DMNMessage, String> computeMessage,
                                             Log log) {
        for (DMNMessage msg : validation) {
            Consumer<CharSequence> logFn;
            switch (msg.getLevel()) {
                case ERROR:
                    logFn = log::error;
                    break;
                case WARNING:
                    logFn = log::warn;
                    break;
                case INFO:
                default:
                    logFn = log::info;
                    break;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(prefixer.apply(msg));
            sb.append(computeMessage.apply(msg));
            logFn.accept(sb.toString());
        }
    }

    public static List<Validation> computeFlagsFromCSVString(String csvString,
                                                             Log log) {
        List<Validation> flags = new ArrayList<>();
        boolean resetFlag = false;
        for (String p : csvString.split(",")) {
            try {
                flags.add(Validation.valueOf(p));
            } catch (IllegalArgumentException e) {
                log.info("validateDMN configured with flag: '" + p + "' determines this Mojo will not be executed (reset all flags).");
                resetFlag = true;
            }
        }
        if (resetFlag) {
            flags.clear();
        }
        return flags;
    }

    public static boolean shallPerformDMNDTAnalysis(String validateDMN, Log log) {
        return computeFlagsFromCSVString(validateDMN, log).contains(Validation.ANALYZE_DECISION_TABLE);
    }

    public static void performDMNDTAnalysis(InternalKieModule kieModule,
                                            List<Resource> resources,
                                            Log log) throws MojoExecutionException, MojoFailureException {
        Collection<DMNModel> dmnModels = extractDMNModelsFromKieModule(kieModule);
        log.info("Initializing DMN DT Validator...");
        InternalDMNDTAnalyser analyser = InternalDMNDTAnalyserFactory.newDMNDTAnalyser(computeDMNProfiles(resources, log));
        log.info("DMN DT Validator initialized.");
        for (DMNModel model : dmnModels) {
            log.info("Analysing decision tables in DMN Model '" + model.getName() + "' ...");
            List<DTAnalysis> results = analyser.analyse(model, new HashSet<>(List.of(Validation.ANALYZE_DECISION_TABLE)));
            if (results.isEmpty()) {
                log.info(" no decision tables found.");
            } else {
                for (DTAnalysis r : results) {
                    log.info(" analysis for decision table '" + r.nameOrIDOfTable() + "':");
                    List<DMNMessage> messages = r.asDMNMessages();
                    logValidationMessages(messages, (u) -> "  ", DMNMessage::getText, log);
                    if (messages.stream().anyMatch(m -> m.getLevel() == Level.ERROR)) {
                        throw new MojoFailureException("There are DMN Validation Error(s).");
                    }
                }
            }
        }
    }

    public static List<Path> resourcesPaths(List<Resource> resources, Log log) {
        List<Path> resourcesPaths = resources.stream().map(r -> new File(r.getDirectory()).toPath()).collect(Collectors.toList());
        if (log.isDebugEnabled()) {
            log.debug("resourcesPaths: " + resourcesPaths.stream().map(Path::toString).collect(Collectors.joining(",\n")));
        }
        return resourcesPaths;
    }

    private static Collection<DMNModel> extractDMNModelsFromKieModule(InternalKieModule kieModule) {
        Collection<KiePackage> kpkgs = kieModule.getKieModuleModel().getKieBaseModels().keySet().stream()
                                                .flatMap(name -> kieModule.getKnowledgePackagesForKieBase(name).stream())
                                                .collect(Collectors.toList());
        Set<DMNModel> models = new HashSet<>();
        for (KiePackage kp : kpkgs) {
            InternalKnowledgePackage ikpkg = (InternalKnowledgePackage) kp;
            ResourceTypePackage<DMNModel> rtp = (ResourceTypePackage<DMNModel>) ikpkg.getResourceTypePackages().get(ResourceType.DMN);
            if (rtp == null) {
                continue;
            }
            for (DMNModel dmnModel : rtp) {
                models.add(dmnModel);
            }
        }
        Map<org.kie.api.io.Resource, DMNModel> removeDups = new HashMap<>();
        for (DMNModel m : models) {
            removeDups.put(m.getResource(), m);
        }
        return removeDups.values();
    }

    public static List<DMNProfile> computeDMNProfiles(List<Resource> resources, Log log) throws MojoExecutionException {
        ClassLoader classLoader = ClassLoaderUtil.findDefaultClassLoader();
        ChainedProperties chainedProperties = ChainedProperties.getChainedProperties(classLoader);
        List<KieModuleModel> kieModules = new ArrayList<>();
        for (Path p : resourcesPaths(resources, log)) {
            try (Stream<Path> walk = Files.walk(p)) {
                List<Path> collect = walk.filter(f -> f.toString().endsWith("kmodule.xml")).collect(Collectors.toList());
                for (Path k : collect) {
                    kieModules.add(KieModuleModelImpl.fromXML(k.toFile()));
                }
            } catch (Exception e) {
                throw new MojoExecutionException("Failed executing AbstractDMNValidationAwareMojo while computing DMNProfile(s)", e);
            }
        }
        for (KieModuleModel kmm : kieModules) {
            Properties ps = new Properties();
            ps.putAll(kmm.getConfigurationProperties());
            chainedProperties.addProperties(ps);
        }
        List<DMNProfile> dmnProfiles = new ArrayList<>();
        dmnProfiles.addAll(DMNAssemblerService.getDefaultDMNProfiles(chainedProperties));
        try {
            Map<String, String> dmnProfileProperties = new HashMap<>();
            chainedProperties.mapStartsWith(dmnProfileProperties, DMNAssemblerService.DMN_PROFILE_PREFIX, false);
            for (Map.Entry<String, String> dmnProfileProperty : dmnProfileProperties.entrySet()) {
                DMNProfile dmnProfile = (DMNProfile) classLoader.loadClass(dmnProfileProperty.getValue()).newInstance();
                dmnProfiles.add(dmnProfile);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new MojoExecutionException("Failed executing AbstractDMNValidationAwareMojo while computing DMNProfile(s)", e);
        }
        return dmnProfiles;
    }
}
