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
package org.drools.model.codegen.execmodel;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

// DROOLS-6423
public class KjarBuildTest {

    private static final String KIEBASE1 = "kiebase";

    private static final String KIEBASE2 = "kiebase2";

    private static final String PACKAGE_DECLARES = "org.drools.reproducer.declare";

    private static final String PACKAGE_IMPORTED_DECLARES = "org.drools.reproducer.imported";

    private static final String DECLARES =
            "package " + PACKAGE_DECLARES + "\n" +
            "\n" +
            "import " + PACKAGE_IMPORTED_DECLARES + ".Fact2\n" +
            "declare Fact1\n" +
            "    x : String\n" +
            "end\n";

    private static final String IMPORTED_DECLARES =
            "package " + PACKAGE_IMPORTED_DECLARES + "\n" +
            "\n" +
            "declare Fact2\n" +
            "    y : int\n" +
            "end\n";


    private KieModule buildKieModule(String version, boolean withExecModel) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        ReleaseId rid = ks.newReleaseId("org.build.example", "reproducer", version);
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kModuleModel = ks.newKieModuleModel();
        kModuleModel.newKieBaseModel(KIEBASE2)
                .addPackage(PACKAGE_IMPORTED_DECLARES);
        kModuleModel.newKieBaseModel(KIEBASE1)
                .addPackage(PACKAGE_DECLARES)
                .addPackage(PACKAGE_IMPORTED_DECLARES);

        kfs.writeKModuleXML(kModuleModel.toXML());

        kfs.write("src/main/resources/org/drools/reproducer/declare/declare.drl", DECLARES);
        kfs.write("src/main/resources/org/drools/reproducer/imported/imported.drl", IMPORTED_DECLARES);

        KieBuilder kb = ks.newKieBuilder(kfs);
        if (withExecModel) {
            kb.buildAll(ExecutableModelProject.class);
        } else {
            kb.buildAll();
        }

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        return kb.getKieModule();
    }

    @Test
    public void buildImportedDeclaresWithKieBaseNameWithDotsWithExecModelTest() {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieModule kModule = buildKieModule("1.0.0", true);
        kr.removeKieModule(kModule.getReleaseId());
    }

    @Test
    public void buildImportedDeclaresWithKieBaseNameWithDotsWithoutExecModelTest() {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieModule kModule = buildKieModule("1.0.0", false);
        kr.removeKieModule(kModule.getReleaseId());
    }
}
