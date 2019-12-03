/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public abstract class OnlyPatternModelTest {

    protected KieSession getKieSession(String... rules) {
        KieModuleModel kproj = null;
        return getKieSession(kproj, rules);
    }

    protected KieSession getKieSession(KieModuleModel model, String... stringRules) {
        return getKieContainer(model, stringRules).newKieSession();
    }

    protected KieContainer getKieContainer(KieModuleModel model, String... stringRules) {
        return getKieContainer(model, toKieFiles(stringRules));
    }

    protected KieContainer getKieContainer(KieModuleModel model, KieFile... stringRules) {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");

        KieBuilder kieBuilder = createKieBuilder(ks, model, releaseId, stringRules);
        return ks.newKieContainer(releaseId);
    }


    protected KieBuilder createKieBuilder(KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... stringRules) {
        return createKieBuilder(ks, model, releaseId, true, stringRules);
    }

    protected KieBuilder createKieBuilder(KieServices ks, KieModuleModel model, ReleaseId releaseId, boolean failIfBuildError, KieFile... stringRules) {
        ks.getRepository().removeKieModule(releaseId);

        KieFileSystem kfs = ks.newKieFileSystem();
        if (model != null) {
            kfs.writeKModuleXML(model.toXML());
        }
        kfs.writePomXML(KJARUtils.getPom(releaseId));
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

    public static <T> List<T> getObjectsIntoList(KieSession ksession, Class<T> clazz) {
        return (List<T>) ksession.getObjects(new ClassObjectFilter(clazz)).stream().collect(Collectors.toList());
    }


    protected void createAndDeployJar(KieServices ks, KieModuleModel model, ReleaseId releaseId, String... drls) {
        createAndDeployJar(ks, model, releaseId, toKieFiles(drls));
    }

    protected void createAndDeployJar(KieServices ks, KieModuleModel model, ReleaseId releaseId, KieFile... ruleFiles) {
        KieBuilder kieBuilder = createKieBuilder(ks, model, releaseId, ruleFiles);
        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        ks.getRepository().addKieModule(kieModule);
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

    public KieFile[] toKieFiles(String[] stringRules) {
        KieFile[] kieFiles = new KieFile[stringRules.length];
        for (int i = 0; i < stringRules.length; i++) {
            kieFiles[i] = new KieFile(i, stringRules[i]);
        }
        return kieFiles;
    }
}
