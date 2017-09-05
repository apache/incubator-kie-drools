/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.workitem.core.util;

import java.util.HashMap;
import java.util.Map;
import javax.tools.JavaFileObject;

import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.Assert.*;

public class WidProcessorTest {

    private static final JavaFileObject source1 = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestClass",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "\n" +
                    "@Wid(widfile=\"mywidfile.wid\", name=\"MyTest\",\n" +
                    "        displayName=\"My Test Class\", icon=\"/my/icons/myicon.png\",\n" +
                    "        defaultHandler=\"mvel: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters={\n" +
                    "                @WidParameter(name=\"sampleParam\"),\n" +
                    "                @WidParameter(name=\"sampleParamTwo\")\n" +
                    "        },\n" +
                    "        mavenDepends={\n" +
                    "                @WidMavenDepends(group=\"org.jboss\", artifact=\"myworitem\", version=\"1.0\")\n" +
                    "        })\n" +
                    "public class MyTestClass {\n" +
                    "    // do nothing\n" +
                    "}"
    );

    private WidProcessor widProcessor = new WidProcessor();

    @Test
    public void testWidAnnotationResults() throws Exception {
        Map<String, Wid> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source1);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        Wid widInfo = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClass");
        assertEquals("MyTest",
                     widInfo.name());
        assertEquals("My Test Class",
                     widInfo.displayName());
        assertEquals("/my/icons/myicon.png",
                     widInfo.icon());
        assertEquals("mywidfile.wid", widInfo.widfile());
        assertEquals("mvel: new com.sample.MyWorkItemHandler()", widInfo.defaultHandler());

        WidParameter[] widParameters = widInfo.parameters();
        assertEquals(2, widParameters.length);
        assertEquals("sampleParam", widParameters[0].name());
        assertEquals("StringDataType", widParameters[0].type());
        assertEquals("sampleParamTwo", widParameters[1].name());
        assertEquals("StringDataType", widParameters[1].type());

        WidMavenDepends[] widMavenDepends = widInfo.mavenDepends();
        assertEquals(1, widMavenDepends.length);
        assertEquals("org.jboss", widMavenDepends[0].group());
        assertEquals("myworitem", widMavenDepends[0].artifact());
        assertEquals("1.0", widMavenDepends[0].version());

    }

    private Compiler compileWithGenerator() {
        return javac().withProcessors(widProcessor);
    }
}
