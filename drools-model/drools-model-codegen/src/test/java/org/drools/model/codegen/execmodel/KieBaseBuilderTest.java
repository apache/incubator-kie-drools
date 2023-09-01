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
package org.drools.model.codegen.execmodel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.model.Model;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.modelcompiler.KieBaseBuilder;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.PropertySpecificOption;

import static org.assertj.core.api.Assertions.assertThat;

public class KieBaseBuilderTest extends BaseModelTest {

    // Only exec-model test
    @Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{RUN_TYPE.PATTERN_DSL};
    }

    public KieBaseBuilderTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void createKieBaseWithKnowledgeBuilderConfiguration() {
        // DROOLS-7239
        final String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                           "\n" +
                           "rule R when\n" +
                           "    $p : Person()\n" +
                           "then\n" +
                           "    modify($p) { setAge( $p.getAge()+1 ) };\n" +
                           "end\n";

        // 1st round: Create a kieBuilder with PropertySpecificOption.DISABLED
        PropertySpecificOption option = PropertySpecificOption.DISABLED;
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-" + UUID.randomUUID(), "1.0");
        KieModuleModel model = ks.newKieModuleModel();
        model.setConfigurationProperty(option.getPropertyName(), option.name());
        KieBuilder kieBuilder = createKieBuilder(ks, model, releaseId, toKieFiles(new String[]{str}));

        // 2nd round: Create a kbase with generated classes and PropertySpecificOption.DISABLED
        KnowledgeBuilderConfiguration knowledgeBuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        knowledgeBuilderConf.setOption(option);
        KieBaseConfiguration kieBaseConf = KieServices.get().newKieBaseConfiguration(); // empty kieBaseConf in this test
        KieBase kbase = buildKieBaseFromExtractedModel(kieBuilder, kieBaseConf, knowledgeBuilderConf);

        KieSession ksession = kbase.newKieSession();

        Person p = new Person("Mario", 40);
        ksession.insert(p);
        int fired = ksession.fireAllRules(10); // intentional loop to test propertySpecific DISABLED

        assertThat(fired).isEqualTo(10);
    }

    private KieBase buildKieBaseFromExtractedModel(KieBuilder kieBuilder, KieBaseConfiguration kieBaseConf, KnowledgeBuilderConfiguration knowledgeBuilderConf) {
        MemoryFileSystem trgMfs = ((KieBuilderImpl) kieBuilder).getTrgMfs();
        TestByteArrayClassLoader cl = new TestByteArrayClassLoader(this.getClass().getClassLoader());
        List<? extends Class<?>> classes = trgMfs.getMap().entrySet()
                                                 .stream()
                                                 .filter(entry -> entry.getKey().endsWith(".class"))
                                                 .map(entry -> {
                                                     String fileName = entry.getKey().asString();
                                                     String className = fileName.replace("/", ".")
                                                                                .replace(".class", "");
                                                     return cl.defineClass(className, entry.getValue());
                                                 }).collect(Collectors.toList());

        return createKieBaseFromModelClass(classes, kieBaseConf, knowledgeBuilderConf);
    }

    private KieBase createKieBaseFromModelClass(final List<? extends Class<?>> classes, KieBaseConfiguration kieBaseConf, KnowledgeBuilderConfiguration knowledgeBuilderConf) {
        return classes.stream()
                      .filter(Model.class::isAssignableFrom)
                      .findFirst()
                      .map(c -> {
                          try {
                              return c.getDeclaredConstructor();
                          } catch (NoSuchMethodException e) {
                              throw new RuntimeException(e);
                          }
                      })
                      .map(c -> {
                          try {
                              return c.newInstance();
                          } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                              throw new RuntimeException(e);
                          }
                      })
                      .map(Model.class::cast)
                      .map(model -> KieBaseBuilder.createKieBaseFromModel(model, kieBaseConf, knowledgeBuilderConf))
                      .orElseThrow(() -> new RuntimeException("No Model class found"));
    }

    public static class TestByteArrayClassLoader extends ClassLoader {

        public TestByteArrayClassLoader(final ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(final String name, final byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length, null);
        }
    }
}
