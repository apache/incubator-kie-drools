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
package org.kie.dmn.core.classloader;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNClassloaderTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNClassloaderTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void classloaderFunctionInvocation(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final String javaSource = "package com.acme.functions;\n" +
                                  "\n" +
                                  "import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;\n" +
                                  "import java.math.BigDecimal;\n" +
                                  "import java.util.List;\n" +
                                  "\n" +
                                  "public class StandardDeviation {\n" +
                                  "\n" +
                                  "    public static BigDecimal std( List<Number> values ) {\n" +
                                  "        DescriptiveStatistics stats = new DescriptiveStatistics();\n" +
                                  "        for( Number value : values ) {\n" +
                                  "            stats.addValue( value.doubleValue() );\n" +
                                  "        }\n" +
                                  "        return new BigDecimal( stats.getStandardDeviation() );\n" +
                                  "    }\n" +
                                  "\n" +
                                  "    public static BigDecimal ignoring( DescriptiveStatistics ds ) {\n" +
                                  "         return std(java.util.Arrays.asList(new BigDecimal(1), new BigDecimal(3), new BigDecimal(5)));" +
                                  "    }\n" +
                                  "}";

        final KieServices ks = KieServices.Factory.get();
        
        final ReleaseId kjarReleaseId = ks.newReleaseId("org.kie.dmn.core.classloader", "testClassloaderFunctionInvocation", UUID.randomUUID().toString());
        final ReleaseId commonsMathGAV = ks.newReleaseId("org.apache.commons", "commons-math3", "3.6.1");

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/java/com/acme/functions/StandardDeviation.java", javaSource);
        kfs.write(ks.getResources().newClassPathResource("Standard Deviation.dmn", this.getClass()));
        kfs.writePomXML(getPom(kjarReleaseId, commonsMathGAV));

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();

        final KieContainer container = ks.newKieContainer(kjarReleaseId);
        final DMNRuntime runtime = container.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_48c4b6e2-25da-44bc-97b2-1e842ff28c71", "Standard Deviation");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("Values", Arrays.asList(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3)));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.info("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Standard Deviation")).isEqualTo(new BigDecimal(1));
        assertThat(result.get("using ignoring")).isEqualTo(new BigDecimal(2));
    }

    public static String getPom(final ReleaseId releaseId, final ReleaseId... dependencies) {
        final StringBuilder pom =
                new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                          "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                                          "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                                          "  <modelVersion>4.0.0</modelVersion>\n" +
                                          "\n" +
                                          "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                                          "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                                          "  <version>" + releaseId.getVersion() + "</version>\n" +
                                          "\n");
        if (dependencies != null && dependencies.length > 0) {
            pom.append("<dependencies>\n");
            for (final ReleaseId dep : dependencies) {
                pom.append("<dependency>\n");
                pom.append("  <groupId>").append(dep.getGroupId()).append("</groupId>\n");
                pom.append("  <artifactId>").append(dep.getArtifactId()).append("</artifactId>\n");
                pom.append("  <version>").append(dep.getVersion()).append("</version>\n");
                pom.append("</dependency>\n");
            }
            pom.append("</dependencies>\n");
        }
        pom.append("</project>");
        return pom.toString();
    }

    @ParameterizedTest
    @MethodSource("params")
    void invokeJavaReturnArrayPrimitives(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final String javaSource = "package org.acme.functions;\n" +
                                  "\n" +
                                  "public class MyFunctions {\n" +
                                  "\n" +
                                  "    public static String[] bkmS( String p1 ) {\n" +
                                  "        String[] results = {\"a\", \"e\", \"i\", \"o\", \"u\"};" +
                                  "        return results;\n" +
                                  "    }\n" +
                                  "\n" +
                                  "    public static int[] bkmI( String p1 ) {\n" +
                                  "        int[] results = {9,8,7,6,5};" +
                                  "        return results;\n" +
                                  "    }\n" +
                                  "}";

        final KieServices ks = KieServices.Factory.get();
        final ReleaseId kjarReleaseId = ks.newReleaseId("org.kie.dmn.core.classloader", "invokeJavaReturnArrayPrimitives", UUID.randomUUID().toString());

        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/java/org/acme/functions/MyFunctions.java", javaSource);
        kfs.write(ks.getResources().newClassPathResource("invokeJavaReturnArrayPrimitives.dmn", this.getClass()));
        kfs.writePomXML(getPom(kjarReleaseId));

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();

        final KieContainer container = ks.newKieContainer(kjarReleaseId);
        final DMNRuntime runtime = container.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_8B620EB6-9E5E-4095-B990-19827F316887", "invokeJavaReturnArrayPrimitives");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("my index", 2);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.info("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("IndexedS")).isEqualTo("e");
        assertThat(result.get("IndexedI")).isEqualTo(new BigDecimal(8));
    }
}
