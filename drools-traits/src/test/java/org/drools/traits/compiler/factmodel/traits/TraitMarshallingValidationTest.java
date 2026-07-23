/*
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
package org.drools.traits.compiler.factmodel.traits;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.protobuf.ByteString;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.SerializablePlaceholderResolverStrategy;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.serialization.protobuf.ProtobufMarshaller;
import org.drools.serialization.protobuf.ProtobufMessages;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TraitMarshallingValidationTest {

    @Test
    void testInjectedRuntimeClassDefLandsInStoreWhenTraitsPresent() throws Exception {
        String drl =
                "package org.drools.test;\n" +
                "declare MyFact\n" +
                "    value : String\n" +
                "end\n" +
                "rule R1 when\n" +
                "    String()\n" +
                "then\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();

        ObjectMarshallingStrategy[] strategies = new ObjectMarshallingStrategy[]{
                new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
        };
        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strategies);

        KieSession ksession = kbase.newKieSession(null, env);
        ksession.insert("test");
        ksession.fireAllRules();

        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase, strategies);

        byte[] serializedSession;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            marshaller.marshall(bos, ksession, ksession.getSessionClock().getCurrentTime());
            serializedSession = bos.toByteArray();
        }
        ksession.dispose();

        ProjectClassLoader pcl = (ProjectClassLoader) ((InternalKnowledgeBase) kbase).getRootClassLoader();

        byte[] protobufBytes = extractProtobufBytes(serializedSession);
        ProtobufMessages.Header originalHeader = ProtobufMessages.Header.parseFrom(protobufBytes);

        String injectedClassName = "example/Payload.class";
        byte[] fakeBytecode = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

        ProtobufMessages.Header tamperedHeader = originalHeader.toBuilder()
                .addRuntimeClassDefinitions(ProtobufMessages.RuntimeClassDef.newBuilder()
                        .setClassFqName(injectedClassName)
                        .setClassDef(ByteString.copyFrom(fakeBytecode))
                        .build())
                .build();

        byte[] tamperedSession = wrapInObjectStream(tamperedHeader.toByteArray());

        assertThatThrownBy(() ->
                marshaller.unmarshall(new ByteArrayInputStream(tamperedSession)))
                .as("With drools-traits on the classpath, TraitFactory is non-null, "
                        + "but readRuntimeDefinedClasses() should still reject "
                        + "class names that don't match the trait proxy naming pattern")
                .isInstanceOf(RuntimeException.class);

        assertThat(pcl.getStore() != null && pcl.getStore().containsKey(injectedClassName))
                .as("Injected bytecode for '%s' should NOT be in the classloader store",
                        injectedClassName)
                .isFalse();
    }

    private static byte[] extractProtobufBytes(byte[] objectStreamBytes) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objectStreamBytes))) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int read;
            while ((read = ois.read(buf)) != -1) {
                result.write(buf, 0, read);
            }
            return result.toByteArray();
        }
    }

    private static byte[] wrapInObjectStream(byte[] protobufBytes) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.write(protobufBytes);
            oos.flush();
            return bos.toByteArray();
        }
    }
}
