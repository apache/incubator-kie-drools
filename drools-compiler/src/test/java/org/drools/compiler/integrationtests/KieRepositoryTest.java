/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;

import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class KieRepositoryTest {

    @Test
    public void testLoadKjarFromClasspath() {
        // DROOLS-1335
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/kie-project-simple-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "kie-project-simple", "1.0.0" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertNotNull( kieModule );
            assertEquals( releaseId, kieModule.getReleaseId() );
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }

    @Test
    public void testTryLoadNotExistingKjarFromClasspath() {
        // DROOLS-1335
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/kie-project-simple-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "kie-project-simple", "1.0.1" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertNull( kieModule );
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }
    
    @Test
    public void testLoadingNotAKJar() {
        // DROOLS-1351
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URLClassLoader urlClassLoader = new URLClassLoader( new URL[]{this.getClass().getResource( "/only-jar-pojo-not-kjar-no-kmodule-1.0.0.jar" )} );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId( "org.test", "only-jar-pojo-not-kjar-no-kmodule", "1.0.0" );
            KieModule kieModule = kieRepository.getKieModule( releaseId );
            assertNull( kieModule );
        } finally {
            Thread.currentThread().setContextClassLoader( cl );
        }
    }
}
