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

package org.kie.dmn.core.util;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.InternalKieBuilder;

public class KieHelper {

    public static KieContainer getKieContainer(ReleaseId releaseId,
                                               Resource... resources ) {
        KieServices ks = KieServices.Factory.get();
        createAndDeployJar( ks, releaseId, resources );
        return ks.newKieContainer( releaseId );
    }

    public static KieModule createAndDeployJar(KieServices ks,
                                               ReleaseId releaseId,
                                               Resource... resources) {
        byte[] jar = createJar(ks, releaseId, resources);

        KieModule km = deployJarIntoRepository(ks, jar);
        return km;
    }

    public static byte[] createJar(KieServices ks, ReleaseId releaseId, Resource... resources) {
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML( releaseId );
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] != null) {
                kfs.write(resources[i]);
            }
        }
        KieBuilder kieBuilder = ks.newKieBuilder( kfs);
        ((InternalKieBuilder) kieBuilder).buildAll( o -> true );
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException(results.getMessages(Message.Level.ERROR).toString());
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository()
                .getKieModule(releaseId);
        byte[] jar = kieModule.getBytes();
        return jar;
    }

    private static KieModule deployJarIntoRepository(KieServices ks, byte[] jar) {
        Resource jarRes = ks.getResources().newByteArrayResource(jar);
        KieModule km = ks.getRepository().addKieModule(jarRes);
        return km;
    }

}
