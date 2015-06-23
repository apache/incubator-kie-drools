/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.example.api.kiemodulefrommultiplefiles;

import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.io.PrintStream;


public class KieModuleFromMultipleFilesExample {

    public void go(PrintStream out) {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();

        Resource ex1Res = ks.getResources().newFileSystemResource(getFile("kiebase-inclusion"));
        Resource ex2Res = ks.getResources().newFileSystemResource(getFile("named-kiesession"));

        KieModule kModule = kr.addKieModule(ex1Res, ex2Res);
        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());

        KieSession kSession = kContainer.newKieSession("ksession2");
        kSession.setGlobal("out", out);

        Object msg1 = createMessage(kContainer, "Dave", "Hello, HAL. Do you read me, HAL?");
        kSession.insert(msg1);
        kSession.fireAllRules();

        Object msg2 = createMessage(kContainer, "Dave", "Open the pod bay doors, HAL.");
        kSession.insert(msg2);
        kSession.fireAllRules();
    }

    public static void main(String[] args) {
        new KieModuleFromMultipleFilesExample().go(System.out);
    }

    private static Object createMessage(KieContainer kContainer, String name, String text) {
        Object o = null;
        try {
            Class cl = kContainer.getClassLoader().loadClass("org.drools.example.api.namedkiesession.Message");
            o = cl.getConstructor(new Class[]{String.class, String.class}).newInstance(name, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static File getFile(String exampleName) {
        File folder = new File( KieModuleFromMultipleFilesExample.class.getProtectionDomain().getCodeSource().getLocation().getPath() );
        File exampleFolder = null;
        while (folder != null) {
            exampleFolder = new File(folder,
                                     exampleName);
            if (exampleFolder.exists()) {
                break;
            }
            exampleFolder = null;
            folder = folder.getParentFile();
        }

        if (exampleFolder != null) {

            File targetFolder = new File(exampleFolder,
                                         "target");
            if (!targetFolder.exists()) {
                throw new RuntimeException("The target folder does not exist, please build project " + exampleName + " first");
            }

            for (String str : targetFolder.list()) {
                if (str.startsWith(exampleName) && !str.endsWith("-sources.jar") && !str.endsWith("-tests.jar") && !str.endsWith("-javadoc.jar")) {
                    return new File(targetFolder, str);
                }
            }
        }

        throw new RuntimeException("The target jar does not exist, please build project " + exampleName + " first");
    }

}
