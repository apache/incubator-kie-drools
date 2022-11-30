/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.rules;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.OptionalInt;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.impl.AbstractRuleUnitDescription;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.SessionsPoolOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.rules.RuleUnitConfig;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

/**
 * Utility class to discover/interact with KieModuleModel.
 *
 */
public class KieModuleModelWrapper {
    private KieModuleModel kieModuleModel;

    public KieModuleModelWrapper(KieModuleModel kieModuleModel) {
        this.kieModuleModel = kieModuleModel;
        setDefaultsforEmptyKieModule(kieModuleModel);
    }

    static KieModuleModelWrapper fromResourcePaths(Path[] resourcePaths) {
        return new KieModuleModelWrapper(lookupKieModuleModel(resourcePaths));
    }

    private static KieModuleModel lookupKieModuleModel(Path[] resourcePaths) {
        for (Path resourcePath : resourcePaths) {
            Path moduleXmlPath = resourcePath.resolve(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
            if (Files.exists(moduleXmlPath)) {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                    return KieModuleModelImpl.fromXML(bais);
                } catch (IOException e) {
                    throw new UncheckedIOException("Impossible to open " + moduleXmlPath, e);
                }
            }
        }

        return new KieModuleModelImpl();
    }

    Map<String, KieBaseModel> kieBaseModels() {
        return kieModuleModel.getKieBaseModels();
    }

    void addRuleUnitConfig(RuleUnitDescription ruleUnitDescription, RuleUnitConfig overridingConfig) {
        // merge config from the descriptor with configs from application.conf
        // application.conf overrides any other config
        org.drools.ruleunits.api.conf.RuleUnitConfig config =
                ((AbstractRuleUnitDescription) ruleUnitDescription).getConfig()
                        .merged(overridingConfig);

        // only Class<?> has config for now
        KieBaseModel unitKieBaseModel = kieModuleModel.newKieBaseModel(ruleUnit2KieBaseName(ruleUnitDescription.getCanonicalName()));
        unitKieBaseModel.setEventProcessingMode(org.kie.api.conf.EventProcessingOption.CLOUD);
        unitKieBaseModel.addPackage(ruleUnitDescription.getPackageName());

        OptionalInt sessionsPool = config.getSessionPool();
        if (sessionsPool.isPresent()) {
            unitKieBaseModel.setSessionsPool(SessionsPoolOption.get(sessionsPool.getAsInt()));
        }
        EventProcessingType eventProcessingType = config.getDefaultedEventProcessingType();
        if (eventProcessingType == EventProcessingType.STREAM) {
            unitKieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
        }

        KieSessionModel unitKieSessionModel = unitKieBaseModel.newKieSessionModel(ruleUnit2KieSessionName(ruleUnitDescription.getCanonicalName()));
        unitKieSessionModel.setType(KieSessionModel.KieSessionType.STATEFUL);
        ClockType clockType = config.getDefaultedClockType();
        if (clockType == ClockType.PSEUDO) {
            unitKieSessionModel.setClockType(ClockTypeOption.PSEUDO);
        }
    }

    private String ruleUnit2KieBaseName(String ruleUnit) {
        return ruleUnit.replace('.', '$') + "KieBase";
    }

    private String ruleUnit2KieSessionName(String ruleUnit) {
        return ruleUnit.replace('.', '$') + "KieSession";
    }

}
