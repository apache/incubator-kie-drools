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
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class ITTestsUtils {

    private ITTestsUtils() {

    }

    public static File getKjarFile(final URL targetLocation, final String gavArtifactId, final  String gavVersion) throws Exception {
        final File basedir = new File(targetLocation.getFile().replace("/test-classes/", ""));
        final File toReturn = new File(basedir, gavArtifactId + "-" + gavVersion + ".jar");
        Assertions.assertThat(toReturn).exists();
        return toReturn;
    }

    public static KieContainer getKieContainer(final URL targetLocation, final String gavArtifactId, final  String gavVersion) throws Exception {
        final File kjarFile = getKjarFile(targetLocation, gavArtifactId, gavVersion);
        Set<URL> urls = new HashSet<>();
        urls.add(kjarFile.toURI().toURL());
        URLClassLoader projectClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]));

        final KieServices kieServices = KieServices.get();
        final KieContainer toReturn = kieServices.getKieClasspathContainer(projectClassLoader);
        Assertions.assertThat(toReturn).isNotNull();
        return toReturn;
    }

    public static KieBase getKieBase(final URL targetLocation, final String gavArtifactId, final  String gavVersion, final String kieBaseName) throws Exception {
        final KieContainer kieContainer = getKieContainer(targetLocation, gavArtifactId, gavVersion);
        KieBase toReturn = kieContainer.getKieBase(kieBaseName);
        Assertions.assertThat(toReturn).isNotNull();
        return toReturn;
    }

    public static KieSession getKieSession(final URL targetLocation, final String gavArtifactId, final  String gavVersion, final String kieBaseName) throws Exception {
        final KieBase kieBase = getKieBase(targetLocation, gavArtifactId, gavVersion, kieBaseName);
        KieSession toReturn = kieBase.newKieSession();
        Assertions.assertThat(toReturn).isNotNull();
        return toReturn;
    }

    public static KieModule fireRule(final URL targetLocation, final String gavArtifactId, final  String gavVersion, final String kieBaseName, final String ruleName) throws Exception {
        final KieContainer kieContainer = getKieContainer(targetLocation, gavArtifactId, gavVersion);
        final KieBase kieBase = kieContainer.getKieBase(kieBaseName);
        Assertions.assertThat(kieBase).isNotNull();

        KieSession kSession = null;
        try {

            kSession = kieBase.newKieSession();

            kSession.insert(ruleName);
            int rulesFired = kSession.fireAllRules();
            kSession.dispose();

            assertEquals(1, rulesFired);
        } finally {
            if (kSession != null) {
                kSession.dispose();
            }
        }
        KieModule toReturn = ((KieContainerImpl) kieContainer).getKieModuleForKBase(kieBaseName);
        Assertions.assertThat(toReturn).isNotNull();
        return toReturn;
    }

}
