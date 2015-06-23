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

package org.kie.scanner;

import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class KieScannerIncrementalCompilationTest extends AbstractKieCiTest {

    private final int FIRST_VALUE = 5;
    private final int SECOND_VALUE = 10;

    private FileManager fileManager;
    private File kPom;
    private ReleaseId releaseId;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        releaseId = KieServices.Factory.get().newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");
        kPom = createKPom(releaseId);
    }

    @Test
    public void testChangeJavaClassButNotDrl() throws IOException {
        checkIncrementalCompilation(true);
    }

    @Test
    public void testChangeDrlNotUsingJava() throws IOException {
        checkIncrementalCompilation(false);
    }

    private void checkIncrementalCompilation(boolean useJavaInDrl) throws IOException {
        KieServices ks = KieServices.Factory.get();
        MavenRepository repository = getMavenRepository();

        InternalKieModule kJar1 = createKieJarWithClass(ks, releaseId, FIRST_VALUE, useJavaInDrl);
        repository.deployArtifact(releaseId, kJar1, kPom);

        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieScanner scanner = ks.newKieScanner(kieContainer);

        checkValue(kieContainer, FIRST_VALUE);

        InternalKieModule kJar2 = createKieJarWithClass(ks, releaseId, SECOND_VALUE, useJavaInDrl);
        repository.deployArtifact(releaseId, kJar2, kPom);

        scanner.scanNow();

        checkValue(kieContainer, SECOND_VALUE);

        ks.getRepository().removeKieModule(releaseId);
    }

    private void checkValue(KieContainer kieContainer, int value) {
        List<Integer> list = new ArrayList<Integer>();
        KieSession ksession = kieContainer.newKieSession("KSession1");

        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();
        assertTrue("Expected:<" + value + "> but was:<" + list.get(0)  + ">", list.get(0) == value);
    }

    private InternalKieModule createKieJarWithClass(KieServices ks, ReleaseId releaseId, int value, boolean useJavaInDrl) throws IOException {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, false);
        kfs.writePomXML(getPom(releaseId));

        if (useJavaInDrl) {
            kfs.write("src/main/resources/KBase1/rule1.drl", createDRLForJavaSource());
        } else {
            kfs.write("src/main/resources/KBase1/rule1.drl", createDRLNotUsingJava(value));
        }
        kfs.write("src/main/java/org/kie/test/Value.java", createJavaSource(value));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue("", kieBuilder.buildAll().getResults().getMessages().isEmpty());
        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private String createDRLForJavaSource() {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule simple\n" +
               "when\n" +
               "then\n" +
               "   list.add(Value.getValue());\n" +
               "end\n";
    }

    private String createDRLNotUsingJava(int value) {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule simple\n" +
               "when\n" +
               "then\n" +
               "   list.add(" + value + ");\n" +
               "end\n";
    }

    private String createJavaSource(int value) {
        return "package org.kie.test;\n" +
               "public class Value {\n" +
               "   public static int getValue() {\n" +
               "      return " + value + ";\n" +
               "   }\n" +
               "}";
    }

    private File createKPom(ReleaseId releaseId) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId));
        return pomFile;
    }
}
