/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaFileObject;

import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.jbpm.process.workitem.core.util.service.WidService;
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
                    "import org.jbpm.process.workitem.core.util.WidResult;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "\n" +
                    "@Wid(widfile=\"mywidfile.wid\", name=\"MyTest\",\n" +
                    "        displayName=\"My Test Class\", icon=\"/my/icons/myicon.png\",\n" +
                    "        defaultHandler=\"mvel: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters={\n" +
                    "                @WidParameter(name=\"sampleParam\", required = true),\n" +
                    "                @WidParameter(name=\"sampleParamTwo\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues={\n" +
                    "                @WidParameterValues(parameterName=\"sampleParam\", values=\"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName=\"sampleParamTwo\", values=\"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        results = {\n" +
                    "                @WidResult(name = \"sampleResult\")\n" +
                    "        },\n" +
                    "        mavenDepends={\n" +
                    "                @WidMavenDepends(group=\"org.jboss\", artifact=\"myworkitem\", version=\"1.0\")\n" +
                    "        })\n" +
                    "public class MyTestClass {\n" +
                    "    // do nothing\n" +
                    "}"
    );

    private static final JavaFileObject source1reflectionhandler = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestClassRefl",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "\n" +
                    "@Wid(widfile=\"mywidfilerefl.wid\", name=\"MyTestRefl\",\n" +
                    "        displayName=\"My Test Class Refl\", category=\"testcategory\", icon=\"/my/icons/myicon.png\",\n" +
                    "        defaultHandler=\"reflection: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters={\n" +
                    "                @WidParameter(name=\"sampleParam\", required = true),\n" +
                    "                @WidParameter(name=\"sampleParamTwo\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues={\n" +
                    "                @WidParameterValues(parameterName=\"sampleParam\", values=\"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName=\"sampleParamTwo\", values=\"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        mavenDepends={\n" +
                    "                @WidMavenDepends(group=\"org.jboss\", artifact=\"myworkitem\", version=\"1.0\")\n" +
                    "        })\n" +
                    "public class MyTestClassRefl {\n" +
                    "    // do nothing\n" +
                    "}"
    );

    private static final JavaFileObject source2 = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestClassTwo",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "\n" +
                    "@Wid(widfile = \"mywidfiletwo.wid\",\n" +
                    "        parameters = {\n" +
                    "                @WidParameter(name = \"sampleParamThree\", required = true),\n" +
                    "                @WidParameter(name = \"sampleParamFour\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues={\n" +
                    "                @WidParameterValues(parameterName=\"sampleParamThree\", values=\"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName=\"sampleParamFour\", values=\"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        mavenDepends = {\n" +
                    "                @WidMavenDepends(group = \"org.jboss\", artifact = \"myworkitem\", version = \"2.0\"),\n" +
                    "                @WidMavenDepends(group = \"org.jboss\", artifact = \"myotherworkitemdepends\", version = \"2.0\")\n" +
                    "        })\n" +
                    "public class MyTestClassTwo implements MyTestInterface {}"
    );

    private static final JavaFileObject source2Interface = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestInterface",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "\n" +
                    "@Wid(widfile = \"mywidfile.wid\", name = \"MyTest\",\n" +
                    "        displayName = \"My Test Class\", category=\"testcategory\", icon = \"/my/icons/myicon.png\",\n" +
                    "        defaultHandler = \"mvel: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters = {\n" +
                    "                @WidParameter(name = \"sampleParam\", required = true),\n" +
                    "                @WidParameter(name = \"sampleParamTwo\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues={\n" +
                    "                @WidParameterValues(parameterName=\"sampleParam\", values=\"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName=\"sampleParamTwo\", values=\"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        mavenDepends = {\n" +
                    "                @WidMavenDepends(group = \"org.jboss\", artifact = \"myworkitem\", version = \"1.0\")\n" +
                    "        })\n" +
                    "public interface MyTestInterface {}"
    );

    private static final JavaFileObject source3ServiceWithTrigger = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestClass",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidResult;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "import org.jbpm.process.workitem.core.util.service.WidService;\n" +
                    "import org.jbpm.process.workitem.core.util.service.WidTrigger;\n" +
                    "\n" +
                    "@Wid(widfile = \"mywidfile.wid\", name = \"MyTest\",\n" +
                    "        displayName = \"My Test Class\", icon = \"/my/icons/myicon.png\",\n" +
                    "        defaultHandler = \"mvel: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters = {\n" +
                    "                @WidParameter(name = \"sampleParam\", required = true),\n" +
                    "                @WidParameter(name = \"sampleParamTwo\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues = {\n" +
                    "                @WidParameterValues(parameterName = \"sampleParam\", values = \"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName = \"sampleParamTwo\", values = \"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        results = {\n" +
                    "                @WidResult(name = \"sampleResult\")\n" +
                    "        },\n" +
                    "        mavenDepends = {\n" +
                    "                @WidMavenDepends(group = \"org.jboss\", artifact = \"myworkitem\", version = \"1.0\")\n" +
                    "        },\n" +
                    "        serviceInfo = @WidService(category = \"testService\",\n" +
                    "                description = \"testServiceDescription\",\n" +
                    "                keywords = \"a,b,c,d,e\",\n" +
                    "                trigger = @WidTrigger(title = \"testServiceTrigger\", description = \"testServiceTriggerDesc\")\n" +
                    "        )\n" +
                    "\n" +
                    ")\n" +
                    "public class MyTestClass {\n" +
                    "    // do nothing\n" +
                    "}"
    );

    private static final JavaFileObject source3ServiceWithAction = JavaFileObjects.forSourceLines(
            "org.jbpm.process.workitem.core.util.MyTestClass",
            "package org.jbpm.process.workitem.core.util;\n" +
                    "\n" +
                    "import org.jbpm.process.workitem.core.util.Wid;\n" +
                    "import org.jbpm.process.workitem.core.util.WidParameter;\n" +
                    "import org.jbpm.process.workitem.core.util.WidResult;\n" +
                    "import org.jbpm.process.workitem.core.util.WidMavenDepends;\n" +
                    "import org.jbpm.process.workitem.core.util.service.WidService;\n" +
                    "import org.jbpm.process.workitem.core.util.service.WidAction;\n" +
                    "\n" +
                    "@Wid(widfile = \"mywidfile.wid\", name = \"MyTest\",\n" +
                    "        displayName = \"My Test Class\", icon = \"/my/icons/myicon.png\",\n" +
                    "        defaultHandler = \"mvel: new com.sample.MyWorkItemHandler()\",\n" +
                    "        parameters = {\n" +
                    "                @WidParameter(name = \"sampleParam\", required = true),\n" +
                    "                @WidParameter(name = \"sampleParamTwo\", required = true)\n" +
                    "        },\n" +
                    "        parameterValues = {\n" +
                    "                @WidParameterValues(parameterName = \"sampleParam\", values = \"a,b,c,d,e\"),\n" +
                    "                @WidParameterValues(parameterName = \"sampleParamTwo\", values = \"1,2,3,4,5\")\n" +
                    "        },\n" +
                    "        results = {\n" +
                    "                @WidResult(name = \"sampleResult\")\n" +
                    "        },\n" +
                    "        mavenDepends = {\n" +
                    "                @WidMavenDepends(group = \"org.jboss\", artifact = \"myworkitem\", version = \"1.0\")\n" +
                    "        },\n" +
                    "        serviceInfo = @WidService(category = \"testService\",\n" +
                    "                description = \"testServiceDescription\",\n" +
                    "                keywords = \"a,b,c,d,e\",\n" +
                    "                action = @WidAction(title = \"testServiceAction\", description = \"testServiceActionDesc\", requiredFieldsOnly = false)\n" +
                    "        )\n" +
                    "\n" +
                    ")\n" +
                    "public class MyTestClass {\n" +
                    "    // do nothing\n" +
                    "}"
    );

    private WidProcessor widProcessor = new WidProcessor();

    @Test
    public void testWidAnnotationResults() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source1);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        List<Wid> widInfoList = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClass");
        assertNotNull(widInfoList);
        assertEquals(1,
                     widInfoList.size());

        Wid widInfo = widInfoList.get(0);
        assertEquals("MyTest",
                     widInfo.name());
        assertEquals("My Test Class",
                     widInfo.displayName());
        assertEquals("/my/icons/myicon.png",
                     widInfo.icon());
        assertEquals("mywidfile.wid",
                     widInfo.widfile());
        assertEquals("mvel: new com.sample.MyWorkItemHandler()",
                     widInfo.defaultHandler());

        WidParameter[] widParameters = widInfo.parameters();
        assertEquals(2,
                     widParameters.length);
        assertEquals("sampleParam",
                     widParameters[0].name());
        assertTrue(widParameters[0].required());
        assertEquals("new StringDataType()",
                     widParameters[0].type());
        assertEquals("sampleParamTwo",
                     widParameters[1].name());
        assertTrue(widParameters[1].required());
        assertEquals("new StringDataType()",
                     widParameters[1].type());

        WidResult[] widResults = widInfo.results();
        assertEquals(1,
                     widResults.length);
        assertEquals("sampleResult",
                     widResults[0].name());
        assertEquals("new StringDataType()",
                     widResults[0].type());

        WidParameterValues[] widParameterValues = widInfo.parameterValues();
        assertEquals(2,
                     widParameterValues.length);
        assertEquals("sampleParam",
                     widParameterValues[0].parameterName());
        assertEquals("a,b,c,d,e",
                     widParameterValues[0].values());
        assertEquals("sampleParamTwo",
                     widParameterValues[1].parameterName());
        assertEquals("1,2,3,4,5",
                     widParameterValues[1].values());

        WidMavenDepends[] widMavenDepends = widInfo.mavenDepends();
        assertEquals(1,
                     widMavenDepends.length);
        assertEquals("org.jboss",
                     widMavenDepends[0].group());
        assertEquals("myworkitem",
                     widMavenDepends[0].artifact());
        assertEquals("1.0",
                     widMavenDepends[0].version());

        // no service info specified - test for defaults
        WidService service = widInfo.serviceInfo();
        assertEquals("",
                     service.category());
        assertEquals("",
                     service.description());
        assertNotNull(service.trigger());
        assertNotNull(service.action());
        assertEquals("",
                     service.trigger().title());
        assertEquals("",
                     service.action().title());
    }

    @Test
    public void testWidInheritanceFromInterface() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source2Interface,
                         source2);

        assertNotNull(processingResults);
        assertEquals(2,
                     processingResults.keySet().size());

        List<Wid> widListOne = processingResults.get("org.jbpm.process.workitem.core.util.MyTestInterface");
        List<Wid> widListTwo = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClassTwo");

        WidInfo widInfo = new WidInfo(Stream.concat(widListOne.stream(),
                                                    widListTwo.stream())
                                              .collect(Collectors.toList()));

        assertNotNull(widInfo);

        assertEquals("mywidfiletwo.wid",
                     widInfo.getWidfile()); // was overwritten
        assertEquals("MyTest",
                     widInfo.getName()); // from interface
        assertEquals("My Test Class",
                     widInfo.getDisplayName()); // from interface
        assertEquals("testcategory",
                     widInfo.getCategory()); // from interface
        assertEquals("mvel: new com.sample.MyWorkItemHandler()",
                     widInfo.getDefaultHandler());
        assertEquals("new com.sample.MyWorkItemHandler()",
                     widInfo.getDefaultHandlerNoType());
        assertNotNull(widInfo.getParameters());
        // make sure parameters from interface and class got put together
        assertEquals(4,
                     widInfo.getParameters().size());
        // make sure parameter values from interface and class got put together
        assertEquals(4,
                     widInfo.getParameterValues().size());
        // make sure one of the maven depends was overwritten by the class @Wid
        assertNotNull(widInfo.getMavenDepends());
        assertEquals(2,
                     widInfo.getMavenDepends().size());
        // make sure version of org.jboss.myworkitem is 2.0 (overwritten)
        assertEquals("2.0",
                     widInfo.getMavenDepends().get("org.jboss.myworkitem").getVersion());
    }

    @Test
    public void testWidWithReflectionHandler() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source1reflectionhandler);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        List<Wid> widInfoList = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClassRefl");
        assertNotNull(widInfoList);
        assertEquals(1,
                     widInfoList.size());

        WidInfo widInfo = new WidInfo(widInfoList);

        assertNotNull(widInfo);

        assertEquals("mywidfilerefl.wid",
                     widInfo.getWidfile()); // was overwritten
        assertEquals("MyTestRefl",
                     widInfo.getName()); // from interface
        assertEquals("My Test Class Refl",
                     widInfo.getDisplayName()); // from interface
        assertEquals("testcategory",
                     widInfo.getCategory()); // from interface
        assertEquals("reflection: new com.sample.MyWorkItemHandler()",
                     widInfo.getDefaultHandler());
        assertEquals("new com.sample.MyWorkItemHandler()",
                     widInfo.getDefaultHandlerNoType());
        assertNotNull(widInfo.getParameters());
        // make sure parameters from interface and class got put together
        assertEquals(2,
                     widInfo.getParameters().size());
        // make sure parameter values from interface and class got put together
        assertEquals(2,
                     widInfo.getParameterValues().size());
        // make sure one of the maven depends was overwritten by the class @Wid
        assertNotNull(widInfo.getMavenDepends());
        assertEquals(1,
                     widInfo.getMavenDepends().size());
        // make sure version of org.jboss.myworkitem is 2.0 (overwritten)
        assertEquals("1.0",
                     widInfo.getMavenDepends().get("org.jboss.myworkitem").getVersion());
    }

    @Test
    public void testGenerationWithProcessingOptions() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        List<String> processorOptions = Arrays.asList("-AwidName=testwid",
                                                      "-AgenerateTemplates=true",
                                                      "-AtemplateResources=testwid.wid:dummytemplate.st,testwid.json:dummytemplate.st,index.html:dummytemplate.st");

        Compiler compiler = compileWithGenerator(processorOptions);
        compiler.compile(source1);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        List<Wid> widInfoList = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClass");
        assertNotNull(widInfoList);
        assertEquals(1,
                     widInfoList.size());
    }

    @Test
    public void testWidAnnotationResultsWithServiceTrigger() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source3ServiceWithTrigger);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        List<Wid> widInfoList = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClass");
        assertNotNull(widInfoList);
        assertEquals(1,
                     widInfoList.size());

        Wid widInfo = widInfoList.get(0);
        assertEquals("MyTest",
                     widInfo.name());
        assertEquals("My Test Class",
                     widInfo.displayName());
        assertEquals("/my/icons/myicon.png",
                     widInfo.icon());
        assertEquals("mywidfile.wid",
                     widInfo.widfile());
        assertEquals("mvel: new com.sample.MyWorkItemHandler()",
                     widInfo.defaultHandler());

        WidParameter[] widParameters = widInfo.parameters();
        assertEquals(2,
                     widParameters.length);
        assertEquals("sampleParam",
                     widParameters[0].name());
        assertTrue(widParameters[0].required());
        assertEquals("new StringDataType()",
                     widParameters[0].type());
        assertEquals("sampleParamTwo",
                     widParameters[1].name());
        assertTrue(widParameters[1].required());
        assertEquals("new StringDataType()",
                     widParameters[1].type());

        WidResult[] widResults = widInfo.results();
        assertEquals(1,
                     widResults.length);
        assertEquals("sampleResult",
                     widResults[0].name());
        assertEquals("new StringDataType()",
                     widResults[0].type());

        WidParameterValues[] widParameterValues = widInfo.parameterValues();
        assertEquals(2,
                     widParameterValues.length);
        assertEquals("sampleParam",
                     widParameterValues[0].parameterName());
        assertEquals("a,b,c,d,e",
                     widParameterValues[0].values());
        assertEquals("sampleParamTwo",
                     widParameterValues[1].parameterName());
        assertEquals("1,2,3,4,5",
                     widParameterValues[1].values());

        WidMavenDepends[] widMavenDepends = widInfo.mavenDepends();
        assertEquals(1,
                     widMavenDepends.length);
        assertEquals("org.jboss",
                     widMavenDepends[0].group());
        assertEquals("myworkitem",
                     widMavenDepends[0].artifact());
        assertEquals("1.0",
                     widMavenDepends[0].version());

        WidService service = widInfo.serviceInfo();
        assertEquals("testService",
                     service.category());
        assertEquals("testServiceDescription",
                     service.description());
        assertEquals("a,b,c,d,e",
                     service.keywords());

        assertNotNull(service.trigger());
        assertNotNull(service.action());

        assertEquals("testServiceTrigger",
                     service.trigger().title());
        assertEquals("testServiceTriggerDesc",
                     service.trigger().description());

        assertEquals("",
                     service.action().title());
        assertEquals("",
                     service.action().description());
        assertTrue(service.action().requiredFieldsOnly());
    }

    @Test
    public void testWidAnnotationResultsWithServiceAction() throws Exception {
        Map<String, List<Wid>> processingResults = new HashMap<>();

        widProcessor.setProcessingResults(processingResults);
        widProcessor.setResetResults(false);

        Compiler compiler = compileWithGenerator();
        compiler.compile(source3ServiceWithAction);

        assertNotNull(processingResults);
        assertEquals(1,
                     processingResults.keySet().size());

        List<Wid> widInfoList = processingResults.get("org.jbpm.process.workitem.core.util.MyTestClass");
        assertNotNull(widInfoList);
        assertEquals(1,
                     widInfoList.size());

        Wid widInfo = widInfoList.get(0);
        assertEquals("MyTest",
                     widInfo.name());
        assertEquals("My Test Class",
                     widInfo.displayName());
        assertEquals("/my/icons/myicon.png",
                     widInfo.icon());
        assertEquals("mywidfile.wid",
                     widInfo.widfile());
        assertEquals("mvel: new com.sample.MyWorkItemHandler()",
                     widInfo.defaultHandler());

        WidParameter[] widParameters = widInfo.parameters();
        assertEquals(2,
                     widParameters.length);
        assertEquals("sampleParam",
                     widParameters[0].name());
        assertTrue(widParameters[0].required());
        assertEquals("new StringDataType()",
                     widParameters[0].type());
        assertEquals("sampleParamTwo",
                     widParameters[1].name());
        assertTrue(widParameters[1].required());
        assertEquals("new StringDataType()",
                     widParameters[1].type());

        WidResult[] widResults = widInfo.results();
        assertEquals(1,
                     widResults.length);
        assertEquals("sampleResult",
                     widResults[0].name());
        assertEquals("new StringDataType()",
                     widResults[0].type());

        WidParameterValues[] widParameterValues = widInfo.parameterValues();
        assertEquals(2,
                     widParameterValues.length);
        assertEquals("sampleParam",
                     widParameterValues[0].parameterName());
        assertEquals("a,b,c,d,e",
                     widParameterValues[0].values());
        assertEquals("sampleParamTwo",
                     widParameterValues[1].parameterName());
        assertEquals("1,2,3,4,5",
                     widParameterValues[1].values());

        WidMavenDepends[] widMavenDepends = widInfo.mavenDepends();
        assertEquals(1,
                     widMavenDepends.length);
        assertEquals("org.jboss",
                     widMavenDepends[0].group());
        assertEquals("myworkitem",
                     widMavenDepends[0].artifact());
        assertEquals("1.0",
                     widMavenDepends[0].version());

        // no service info specified - test for defaults
        WidService service = widInfo.serviceInfo();
        assertEquals("testService",
                     service.category());
        assertEquals("testServiceDescription",
                     service.description());
        assertEquals("a,b,c,d,e",
                     service.keywords());

        assertNotNull(service.trigger());
        assertNotNull(service.action());

        assertEquals("",
                     service.trigger().title());
        assertEquals("",
                     service.trigger().description());

        assertEquals("testServiceAction",
                     service.action().title());
        assertEquals("testServiceActionDesc",
                     service.action().description());
        assertFalse(service.action().requiredFieldsOnly());
    }

    private Compiler compileWithGenerator() {
        return javac().withProcessors(widProcessor);
    }

    private Compiler compileWithGenerator(List<String> options) {
        return javac().withProcessors(widProcessor).withOptions(options);
    }
}