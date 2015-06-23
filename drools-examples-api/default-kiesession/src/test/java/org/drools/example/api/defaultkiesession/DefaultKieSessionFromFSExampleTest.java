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

package org.drools.example.api.defaultkiesession;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.runtime.KieSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class DefaultKieSessionFromFSExampleTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testGo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        go(ps);
        ps.close();

        String actual = new String(baos.toByteArray());

        String expected = "" +
                          "Dave: Hello, HAL. Do you read me, HAL?" + NL +
                          "HAL: Dave. I read you." + NL;

        assertEquals(expected, actual);
    }

    public void go(PrintStream out) {
        String currentFolder = null;
        try {
            currentFolder = new File(".").getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File rootFolder = null;
        if (currentFolder.endsWith("drools")) {
            rootFolder = new File("drools-examples-api/default-kiesession");
        } else if (currentFolder.endsWith("drools-examples-api")) {
            rootFolder = new File("default-kiesession");
        } else {
            rootFolder = new File(".");
        }

        KieServices ks = KieServices.Factory.get();
        KieBuilder kieBuilder = ks.newKieBuilder(rootFolder).buildAll();

        KieSession kSession = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId()).newKieSession();
        kSession.setGlobal("out", out);
        kSession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        kSession.fireAllRules();
    }
}
