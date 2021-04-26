/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impact.analysis.integrationtests;

import java.util.List;
import java.util.UUID;

import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.fail;

public class RuleExecutionHelper {

    public static KieSession getKieSession(String... rules) {
        return getKieSession(null, rules);
    }

    public static KieSession getKieSession(KieModuleModel model, String... stringRules) {
        return getKieContainer(model, stringRules).newKieSession();
    }

    public static KieSession getKieSession(KieFileSystem kfs) {
        KieServices ks = KieServices.get();
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        List<Message> messages = kieBuilder.getResults().getMessages();
        if (!messages.isEmpty()) {
            fail(messages.toString());
        }
        KieContainer kieContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
        return kieContainer.newKieSession();
    }

    public static KieContainer getKieContainer(KieModuleModel model, String... stringRules) {
        return getKieContainer(model, toKieFiles(stringRules));
    }

    public static KieContainer getKieContainer(KieModuleModel model, KieFile... stringRules) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");

        createKieBuilder(ks, model, releaseId, stringRules);
        return ks.newKieContainer(releaseId);
    }

    public static KieBuilder createKieBuilder(KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... stringRules) {
        return createKieBuilder(ks, model, releaseId, true, stringRules);
    }

    public static KieBuilder createKieBuilder(KieServices ks, KieModuleModel model, ReleaseId releaseId, boolean failIfBuildError, KieFile... stringRules) {
        ks.getRepository().removeKieModule(releaseId);

        KieFileSystem kfs = ks.newKieFileSystem();
        if (model != null) {
            kfs.writeKModuleXML(model.toXML());
        }
        kfs.writePomXML(getPom(releaseId));
        for (int i = 0; i < stringRules.length; i++) {
            kfs.write(stringRules[i].path, stringRules[i].content);
        }

        KieBuilder kieBuilder;

        kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);

        if (failIfBuildError) {
            List<Message> messages = kieBuilder.getResults().getMessages();
            if (!messages.isEmpty()) {
                fail(messages.toString());
            }
        }

        return kieBuilder;
    }

    public static String getPom(ReleaseId releaseId) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                     "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                     "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                     "  <modelVersion>4.0.0</modelVersion>\n" +
                     "\n" +
                     "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                     "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                     "  <version>" + releaseId.getVersion() + "</version>\n" +
                     "</project>";
        return pom;
    }

    public static class KieFile {

        public final String path;
        public final String content;

        public KieFile(int index, String content) {
            this(String.format("src/main/resources/r%d.drl", index), content);
        }

        public KieFile(String path, String content) {
            this.path = path;
            this.content = content;
        }
    }

    public static KieFile[] toKieFiles(String[] stringRules) {
        KieFile[] kieFiles = new KieFile[stringRules.length];
        for (int i = 0; i < stringRules.length; i++) {
            kieFiles[i] = new KieFile(i, stringRules[i]);
        }
        return kieFiles;
    }
}
