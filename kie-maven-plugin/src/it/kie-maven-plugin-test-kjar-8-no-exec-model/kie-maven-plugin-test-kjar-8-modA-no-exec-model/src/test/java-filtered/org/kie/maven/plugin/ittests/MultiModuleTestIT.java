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
package org.kie.maven.plugin.ittests;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.*;

public class MultiModuleTestIT {

    private final static String GROUP_ID = "org.kie";
    private final static String GAV_ARTIFACT_ID = "kie-maven-plugin-test-kjar-8-modA-no-exec-model";
    private static final String GAV_VERSION = "${org.kie.version}";

    @Test
    public void testMultiModule() throws Exception {
        final URL targetLocation = MultiModuleTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        KieContainer kContainer =  null;
        try {
            kContainer = (KieContainerImpl) ITTestsUtils.getKieContainer(targetLocation, GAV_ARTIFACT_ID, GAV_VERSION);

            Collection<String> kieBaseNames = kContainer.getKieBaseNames();
            assertThat(kieBaseNames).hasSameElementsAs(asList("modC", "modB", "modA"));

            List<KiePackage> kiePackages = kieBaseNames.stream()
                    .map(kContainer::getKieBase)
                    .flatMap(kb -> kb.getKiePackages().stream())
                    .collect(toList());

            assertThat(kiePackages.stream()
                               .map(KiePackage::getName)
                               .collect(toList()))
                    .hasSameElementsAs(asList("org.kie.modC", "org.kie.modB", "org.kie.modA"));

            List<FactType> factTypes = kiePackages.stream()
                    .flatMap(kb -> kb.getFactTypes().stream())
                    .collect(toList());

            assertThat(factTypes.stream()
                               .map(FactType::getName)
                               .collect(toList()))
                    .hasSameElementsAs(asList("org.kie.modC.FactC", "org.kie.modB.FactB", "org.kie.modA.FactA"));
        } finally {
            if (kContainer != null) {
                kContainer.dispose();
            }
        }
    }
}