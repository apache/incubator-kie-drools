/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.base.rule.MapBackedClassLoader;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class MarshallingTest {

    /**
     * In this case we are dealing with facts which are not on the systems classpath.
     */
    @Test
    void testSerializabilityWithJarFacts() throws Exception {
        MapBackedClassLoader loader = new MapBackedClassLoader(this.getClass().getClassLoader());

        JarInputStream jis = new JarInputStream(this.getClass().getResourceAsStream("/surf.jar"));

        JarEntry entry = null;
        byte[] buf = new byte[1024];
        int len = 0;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (!entry.isDirectory()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = jis.read(buf)) >= 0) {
                    out.write(buf,
                              0,
                              len);
                }
                loader.addResource(entry.getName(),
                                   out.toByteArray());
            }
        }

        String drl = "package foo.bar \n" +
                "import org.example.surf.Board\n" +
                "rule 'MyGoodRule' \n dialect 'mvel' \n when " +
                "   Board() " +
                "then \n" +
                " System.err.println(42); \n" +
                "end\n";

        KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, loader);

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
        kbuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        assertThat(kbuilder.hasErrors()).as(kbuilder.getErrors().toString()).isFalse();

        Collection<KiePackage> kpkgs = kbuilder.getKnowledgePackages();

        assertThatCode(() -> {
            SerializationHelper.serializeObject(kpkgs, loader);
        }).doesNotThrowAnyException();
    }
}
