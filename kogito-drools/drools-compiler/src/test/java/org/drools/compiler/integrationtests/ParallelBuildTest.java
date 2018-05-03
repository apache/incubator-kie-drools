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

package org.drools.compiler.integrationtests;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class ParallelBuildTest {

    private final List<Class<?>> classes = Arrays.asList(
            java.util.List.class,
            java.awt.Color.class,
            java.util.concurrent.Callable.class,
            java.util.concurrent.atomic.AtomicBoolean.class,
            java.util.concurrent.locks.Lock.class,
            java.util.zip.ZipFile.class,
            java.awt.color.ColorSpace.class,
            java.awt.font.TextMeasurer.class,
            java.awt.geom.Area.class,
            java.awt.im.InputContext.class,
            java.net.Inet4Address.class,
            java.io.File.class
    );

    @Test
    public void testParallelBuild() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        StringBuilder sb = new StringBuilder();
        int rc = 0;
        for (Class<?> c : classes) {
            sb.append("rule \"rule_" + rc++ + "\"\n");
            sb.append("  when\n");
            sb.append("    a : " + c.getName() + "()\n");
            sb.append("  then\n");
            sb.append("    System.out.print(\".\");\n");
            sb.append("end\n");
            sb.append("\n");
        }

        kbuilder.add(ResourceFactory.newByteArrayResource(sb.toString().getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
    }

}
