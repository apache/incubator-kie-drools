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
package org.drools.mvel.integrationtests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class StrictAnnotationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public StrictAnnotationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testUnknownAnnotation() {
        String str =
                "package org.simple \n" +
                "@Xyz rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", str )
                              .writeKModuleXML(ks.newKieModuleModel()
                                                 .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                                           LanguageLevelOption.DRL6_STRICT.toString())
                                                 .toXML());
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results results = kieBuilder.getResults();
        assertThat(results.getMessages().size()).isEqualTo(1);
    }

    @Test
    public void testImportedAnnotation() {
        String str =
                "package org.simple \n" +
                "import " + Xyz.class.getCanonicalName() + " \n" +
                "@Xyz rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", str )
                              .writeKModuleXML(ks.newKieModuleModel()
                                                 .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                                           LanguageLevelOption.DRL6_STRICT.toString())
                                                 .toXML());
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results results = kieBuilder.getResults();
        assertThat(results.getMessages().size()).isEqualTo(0);
    }

    @Test
    public void testEagerEvaluation() {
        String str =
                "package org.simple \n" +
                "@Propagation(EAGER) rule xxx \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n" +
                "@Propagation(EAGER) rule yyy \n" +
                "when \n" +
                "  $s : String()\n" +
                "then \n" +
                "end  \n";

        KieSessionConfiguration conf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ForceEagerActivationOption.YES);

        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(LanguageLevelOption.PROPERTY_NAME, LanguageLevelOption.DRL6_STRICT.toString());
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, kieModuleConfigurationProperties, str);
        KieSession ksession = kbase.newKieSession(conf, null);
        try {
            final List list = new ArrayList();

            AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
                public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                    list.add("activated");
                }
            };
            ksession.addEventListener(agendaEventListener);

            ksession.insert("test");
            assertThat(list.size()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testWatch() {
        String str =
                "package com.sample;\n" +
                "import " + MyClass.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    @Watch( \"!value\" ) $m : MyClass( value < 10 )\n" +
                "then \n" +
                "    modify( $m ) { setValue( $m.getValue()+1 ) };\n" +
                "end\n";

        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(LanguageLevelOption.PROPERTY_NAME, LanguageLevelOption.DRL6_STRICT.toString());
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, kieModuleConfigurationProperties, str);
        KieSession ksession = kbase.newKieSession();
        try {
            MyClass myClass = new MyClass("test", 1);
            ksession.insert(myClass);
            ksession.fireAllRules();
            assertThat(myClass.getValue()).isEqualTo(2);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testStirctWatchWithoutQuotes() {
        String str =
                "package com.sample;\n" +
                "import " + MyClass.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    @Watch( !value ) $m : MyClass( value < 10 )\n" +
                "then \n" +
                "    modify( $m ) { setValue( $m.getValue()+1 ) };\n" +
                "end\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                              .write( "src/main/resources/r1.drl", str )
                              .writeKModuleXML(ks.newKieModuleModel()
                                                 .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                                           LanguageLevelOption.DRL6_STRICT.toString())
                                                 .toXML());
        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results results = kieBuilder.getResults();
        assertThat(results.getMessages().size()).isEqualTo(1);
    }

    @Test
    public void testExplictPositionalArguments() throws InstantiationException, IllegalAccessException {
        String str = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    @Position(1) name : String \n" +
                     "    @Position(0) age : int \n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( 37, \"Mark\"; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(LanguageLevelOption.PROPERTY_NAME, LanguageLevelOption.DRL6_STRICT.toString());
        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, kieModuleConfigurationProperties, str);

        FactType factType = kieBase.getFactType("org.test", "Person");
        Object instance = factType.newInstance();
        factType.set(instance, "name", "Mark");
        factType.set(instance, "age", 37);

        List<String> names = new ArrayList<String>();
        KieSession ksession = kieBase.newKieSession();
        try {
            ksession.setGlobal("names", names);

            ksession.insert(instance);
            ksession.fireAllRules();

            assertThat(names.size()).isEqualTo(1);
            assertThat(names.get(0)).isEqualTo("Mark");
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testJavaSqlTimestamp() {
        String str =
                "package " + Message.class.getPackage().getName() + "\n" +
                "@Role( Role.Type.EVENT ) @Timestamp( \"startTime\" ) @Duration( \"duration\" )\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                "end\n";

        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(LanguageLevelOption.PROPERTY_NAME, LanguageLevelOption.DRL6_STRICT.toString());
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, kieModuleConfigurationProperties, str);
        KieSession ksession = kbase.newKieSession();
        try {
            Message msg = new Message();
            msg.setStartTime( new Timestamp( 10000 ) );
            msg.setDuration( 1000l );

            DefaultEventHandle efh = (DefaultEventHandle) ksession.insert(msg);
            assertThat(efh.getStartTimestamp()).isEqualTo(10000);
            assertThat(efh.getDuration()).isEqualTo(1000);
        } finally {
            ksession.dispose();
        }
    }

    @PropertyReactive
    public static class MyClass {
        private String name;
        private int value;

        public MyClass(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class Message {
        private Properties properties;
        private Timestamp timestamp;
        private Long duration;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties( Properties properties ) {
            this.properties = properties;
        }

        public Timestamp getStartTime() {
            return timestamp;
        }

        public void setStartTime(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Xyz { }
}
