/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.drools.core.io.impl.ClassPathResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;
import org.kie.kogito.legacy.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.RuleConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@ApplicationScoped
public class KieRuntimeBuilderMock implements KieRuntimeBuilder {

    @Override
    public KieBase getKieBase() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        KieHelper kieHelper = new KieHelper(PropertySpecificOption.ALLOWED)
                .setClassLoader(classLoader);
        List<String> scoreDrlList;
        if (Thread.currentThread().getContextClassLoader().getResource("constraints.drl") != null) {
            scoreDrlList = Collections.singletonList("constraints.drl");
        } else {
            scoreDrlList = Collections.singletonList("customConstraints.drl");
        }
        if (!ConfigUtils.isEmptyCollection(scoreDrlList)) {
            for (String scoreDrl : scoreDrlList) {
                if (scoreDrl == null) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                }
                kieHelper.addResource(new ClassPathResource(scoreDrl, classLoader));
            }
        }

        try {
            return kieHelper.build(ExecutableModelProject.class, KieBaseMutabilityOption.DISABLED);
        } catch (Exception ex) {
            throw new IllegalStateException("There is an error in a scoreDrl or scoreDrlFile.", ex);
        }
    }

    @Override
    public KieBase getKieBase(String s) {
        return null;
    }

    @Override
    public KieSession newKieSession() {
        return null;
    }

    @Override
    public KieSession newKieSession(String s) {
        return null;
    }

    @Override
    public KieSession newKieSession(String s, RuleConfig ruleConfig) {
        return null;
    }
}
