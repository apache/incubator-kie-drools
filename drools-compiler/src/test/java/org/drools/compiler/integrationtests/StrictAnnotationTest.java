/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.common.EventFactHandle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.kie.internal.utils.KieHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StrictAnnotationTest extends CommonTestMethodBase {

    @Test
    public void testUnknownAnnotation() throws Exception {
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
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
    }

    @Test
    public void testImportedAnnotation() throws Exception {
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
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(0, results.getMessages().size());
    }

    @Test
    public void testEagerEvaluation() throws Exception {
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

        KieServices ks = KieServices.Factory.get();

        KieSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ForceEagerActivationOption.YES);

        KieSession ksession = new KieHelper()
                .setKieModuleModel(ks.newKieModuleModel()
                                     .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                               LanguageLevelOption.DRL6_STRICT.toString()))
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession(conf, null);

        final List list = new ArrayList();

        AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
            public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                list.add("activated");
            }
        };
        ksession.addEventListener(agendaEventListener);

        ksession.insert("test");
        assertEquals(2, list.size());
    }

    @Test
    public void testWatch() throws Exception {
        String str =
                "package com.sample;\n" +
                "import " + MyClass.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    @Watch( \"!value\" ) $m : MyClass( value < 10 )\n" +
                "then \n" +
                "    modify( $m ) { setValue( $m.getValue()+1 ) };\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .setKieModuleModel(KieServices.Factory.get().newKieModuleModel()
                                     .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                               LanguageLevelOption.DRL6_STRICT.toString()))
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        MyClass myClass = new MyClass("test", 1);
        ksession.insert(myClass);
        ksession.fireAllRules();
        assertEquals(2, myClass.getValue());
    }

    @Test
    public void testStirctWatchWithoutQuotes() throws Exception {
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
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertEquals(1, results.getMessages().size());
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

        KieBase kieBase = new KieHelper()
                .setKieModuleModel(KieServices.Factory.get().newKieModuleModel()
                                                      .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                                                LanguageLevelOption.DRL6_STRICT.toString()))
                .addContent(str, ResourceType.DRL)
                .build();

        FactType factType = kieBase.getFactType("org.test", "Person");
        Object instance = factType.newInstance();
        factType.set(instance, "name", "Mark");
        factType.set(instance, "age", 37);

        List<String> names = new ArrayList<String>();
        KieSession ksession = kieBase.newKieSession();
        ksession.setGlobal("names", names);

        ksession.insert(instance);
        ksession.fireAllRules();

        assertEquals(1, names.size());
        assertEquals("Mark", names.get(0));
    }

    @Test
    public void testJavaSqlTimestamp() {
        String str =
                "package " + Message.class.getPackage().getName() + "\n" +
                "@Role( Role.Type.EVENT ) @Timestamp( \"startTime\" ) @Duration( \"duration\" )\n" +
                "declare " + Message.class.getCanonicalName() + "\n" +
                "end\n";

        KieSession ksession = new KieHelper()
                .setKieModuleModel(KieServices.Factory.get().newKieModuleModel()
                                                      .setConfigurationProperty(LanguageLevelOption.PROPERTY_NAME,
                                                                                LanguageLevelOption.DRL6_STRICT.toString()))
                .addContent(str, ResourceType.DRL)
                .build()
                .newKieSession();

        Message msg = new Message();
        msg.setStartTime( new Timestamp( 10000 ) );
        msg.setDuration( 1000l );

        EventFactHandle efh = (EventFactHandle) ksession.insert( msg );
        assertEquals( 10000,
                      efh.getStartTimestamp() );
        assertEquals( 1000,
                      efh.getDuration() );
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
