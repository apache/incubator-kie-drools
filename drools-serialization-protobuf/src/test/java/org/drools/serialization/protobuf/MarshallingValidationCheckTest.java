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
package org.drools.serialization.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import com.google.protobuf.ByteString;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.SerializablePlaceholderResolverStrategy;
import org.drools.core.util.KeyStoreConstants;
import org.drools.core.util.KeyStoreHelper;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.serialization.protobuf.marshalling.JavaSerializableResolverStrategy;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.utils.KieHelper;
import testdata.enhancement.DeserializationProbe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MarshallingValidationCheckTest {

    private final DeserializationFilterTestSupport filterSupport = new DeserializationFilterTestSupport();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        filterSupport.setUp();
    }

    @AfterEach
    void tearDown() {
        filterSupport.tearDown();
        System.clearProperty(KeyStoreConstants.PROP_ENABLE_DESER_FILTER);
        System.clearProperty(KeyStoreConstants.PROP_SIGN);
        System.clearProperty(KeyStoreConstants.PROP_PVT_KS_URL);
        System.clearProperty(KeyStoreConstants.PROP_PVT_KS_PWD);
        System.clearProperty(KeyStoreConstants.PROP_PVT_ALIAS);
        System.clearProperty(KeyStoreConstants.PROP_PVT_PWD);
        System.clearProperty(KeyStoreConstants.PROP_PUB_KS_URL);
        System.clearProperty(KeyStoreConstants.PROP_PUB_KS_PWD);
        KeyStoreHelper.reInit();
        DeserializationProbe.deserialized = false;
    }

    @Test
    void testDeserializationFilterRejectsUnexpectedClasses() throws Exception {
        byte[] probeBytes = serializeObject(new DeserializationProbe());

        JavaSerializableResolverStrategy strategy =
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);

        assertThatThrownBy(() ->
                strategy.unmarshal(null, null, probeBytes, getClass().getClassLoader()))
                .as("Deserialization of non-whitelisted class should be rejected by the ObjectInputFilter")
                .isInstanceOf(RuntimeException.class);

        assertThat(DeserializationProbe.deserialized)
                .as("DeserializationProbe.readObject() should NOT have been invoked")
                .isFalse();
    }

    @Test
    void testDeserializationFilterRejectsEnum() throws Exception {
        byte[] enumBytes = serializeObject(testdata.enhancement.EnumProbe.INSTANCE);

        JavaSerializableResolverStrategy strategy =
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);

        testdata.enhancement.EnumProbe.initialized = false;

        assertThatThrownBy(() ->
                strategy.unmarshal(null, null, enumBytes, getClass().getClassLoader()))
                .as("Deserialization of non-whitelisted enum should be rejected — "
                        + "enums can execute code in static initializers and constructors")
                .isInstanceOf(RuntimeException.class);

        assertThat(testdata.enhancement.EnumProbe.initialized)
                .as("EnumProbe static initializer should NOT have been invoked")
                .isFalse();
    }

    @Test
    void testTamperedFactHandleDeserialization() throws Exception {
        String drl =
                "rule R1 when\n" +
                "    String()\n" +
                "then\n" +
                "end\n";

        ObjectMarshallingStrategy[] strategies = new ObjectMarshallingStrategy[]{
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
        };

        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strategies);

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession(null, env);
        ksession.insert("test-fact");
        ksession.fireAllRules();

        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase, strategies);

        byte[] serializedSession;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            marshaller.marshall(bos, ksession, ksession.getSessionClock().getCurrentTime());
            serializedSession = bos.toByteArray();
        }
        ksession.dispose();

        byte[] probeBytes = serializeObject(new DeserializationProbe());

        byte[] protobufBytes = extractProtobufBytes(serializedSession);
        ProtobufMessages.Header header = ProtobufMessages.Header.parseFrom(protobufBytes);
        ProtobufMessages.KnowledgeSession session =
                ProtobufMessages.KnowledgeSession.parseFrom(header.getPayload());

        ProtobufMessages.KnowledgeSession.Builder sessionBuilder = session.toBuilder();
        boolean replaced = false;
        for (int epIdx = 0; epIdx < session.getRuleData().getEntryPointCount(); epIdx++) {
            ProtobufMessages.EntryPoint ep = session.getRuleData().getEntryPoint(epIdx);
            for (int fhIdx = 0; fhIdx < ep.getHandleCount(); fhIdx++) {
                ProtobufMessages.FactHandle fh = ep.getHandle(fhIdx);
                if (fh.hasObject()) {
                    ProtobufMessages.FactHandle tamperedFh = fh.toBuilder()
                            .setObject(ByteString.copyFrom(probeBytes))
                            .build();
                    ProtobufMessages.EntryPoint tamperedEp = ep.toBuilder()
                            .setHandle(fhIdx, tamperedFh)
                            .build();
                    sessionBuilder.setRuleData(session.getRuleData().toBuilder()
                            .setEntryPoint(epIdx, tamperedEp)
                            .build());
                    replaced = true;
                    break;
                }
            }
            if (replaced) {
                break;
            }
        }

        assertThat(replaced)
                .as("Should have found a fact handle to tamper with")
                .isTrue();

        ProtobufMessages.Header tamperedHeader = header.toBuilder()
                .setPayload(ByteString.copyFrom(sessionBuilder.build().toByteArray()))
                .build();
        byte[] tamperedSession = wrapInObjectStream(tamperedHeader.toByteArray());

        assertThatThrownBy(() ->
                marshaller.unmarshall(new ByteArrayInputStream(tamperedSession)))
                .as("Unmarshalling a tampered payload with a non-whitelisted class should fail")
                .isInstanceOf(RuntimeException.class);

        assertThat(DeserializationProbe.deserialized)
                .as("DeserializationProbe.readObject() should NOT have been invoked during unmarshalling "
                        + "of a tampered payload — the deserialization filter should block it")
                .isFalse();
    }

    @Test
    void testDeserializationAllowsJdkTypes() throws Exception {
        String drl =
                "rule R1 when\n" +
                "    String()\n" +
                "then\n" +
                "end\n";

        ObjectMarshallingStrategy[] strategies = new ObjectMarshallingStrategy[]{
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)
        };

        Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strategies);

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSession ksession = kbase.newKieSession(null, env);
        ksession.insert("hello");
        ksession.fireAllRules();

        ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase, strategies);

        byte[] serializedSession;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            marshaller.marshall(bos, ksession, ksession.getSessionClock().getCurrentTime());
            serializedSession = bos.toByteArray();
        }
        ksession.dispose();

        KieSession restored = marshaller.unmarshall(new ByteArrayInputStream(serializedSession));
        assertThat(restored.getObjects().iterator().next()).isEqualTo("hello");
        restored.dispose();
    }

    @Test
    void testDeserializationAllowsConfiguredPatterns() throws Exception {
        System.setProperty(KeyStoreConstants.PROP_ALLOWED_DESER_CLASS_PATTERNS,
                "testdata.enhancement.*");

        byte[] probeBytes = serializeObject(new DeserializationProbe());

        JavaSerializableResolverStrategy strategy =
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);

        Object result = strategy.unmarshal(null, null, probeBytes, getClass().getClassLoader());
        assertThat(result).isInstanceOf(DeserializationProbe.class);
        assertThat(DeserializationProbe.deserialized).isTrue();
    }

    @Test
    void testDeserializationFilterOptOut() throws Exception {
        System.setProperty(KeyStoreConstants.PROP_ENABLE_DESER_FILTER, "false");

        byte[] probeBytes = serializeObject(new DeserializationProbe());

        JavaSerializableResolverStrategy strategy =
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);

        Object result = strategy.unmarshal(null, null, probeBytes, getClass().getClassLoader());
        assertThat(result).isInstanceOf(DeserializationProbe.class);
        assertThat(DeserializationProbe.deserialized).isTrue();
    }

    @Test
    void testSignatureMustCoverRuntimeClassDefinitions() throws Exception {
        setPrivateKeyProperties();
        setPublicKeyProperties();

        String drl =
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

        byte[] protobufBytes = extractProtobufBytes(serializedSession);
        ProtobufMessages.Header originalHeader = ProtobufMessages.Header.parseFrom(protobufBytes);

        byte[] fakeBytecode = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

        ProtobufMessages.Header tamperedHeader = originalHeader.toBuilder()
                .addRuntimeClassDefinitions(ProtobufMessages.RuntimeClassDef.newBuilder()
                        .setClassFqName("com/example/Payload")
                        .setClassDef(ByteString.copyFrom(fakeBytecode))
                        .build())
                .build();

        byte[] tamperedSession = wrapInObjectStream(tamperedHeader.toByteArray());

        assertThatThrownBy(() ->
                marshaller.unmarshall(new ByteArrayInputStream(tamperedSession)))
                .as("Signature check should detect that RuntimeClassDef entries were "
                        + "tampered — currently the signature only covers the payload, "
                        + "not the full Header")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Signature");
    }

    @Test
    void testSignatureCheckedBeforeBytecodeLoading() throws Exception {
        setPrivateKeyProperties();
        setPublicKeyProperties();

        String drl =
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
        pcl.storeClass("dummy.Placeholder", "dummy/Placeholder.class", new byte[0]);

        byte[] protobufBytes = extractProtobufBytes(serializedSession);
        ProtobufMessages.Header originalHeader = ProtobufMessages.Header.parseFrom(protobufBytes);

        String injectedClassName = "com/example/Payload";
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
                .as("Signature check should reject the tampered header")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Signature");

        assertThat(pcl.getStore().containsKey(injectedClassName))
                .as("Bytecode for injected class '%s' must NOT be in the classloader "
                        + "— checkSignature() must run before loadStrategiesIndex() so "
                        + "that a rejected payload never loads bytecode",
                        injectedClassName)
                .isFalse();
    }

    @Test
    void testUnsignedSessionRejectsInjectedRuntimeClassDef() throws Exception {
        String drl =
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
        pcl.storeClass("dummy.Placeholder", "dummy/Placeholder.class", new byte[0]);

        byte[] protobufBytes = extractProtobufBytes(serializedSession);
        ProtobufMessages.Header originalHeader = ProtobufMessages.Header.parseFrom(protobufBytes);

        String injectedClassName = "example/Payload";
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
                .as("Unmarshalling with injected RuntimeClassDef should fail "
                        + "— without drools-traits on the classpath, TraitFactory is null "
                        + "and readRuntimeDefinedClasses() rejects all RuntimeClassDef entries")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no TraitFactory is available");

        assertThat(pcl.getStore().containsKey(injectedClassName))
                .as("Bytecode for injected class '%s' must NOT be in the classloader store",
                        injectedClassName)
                .isFalse();
    }

    @Test
    void testReadRuntimeDefinedClassesRejectsNonTraitClasses() throws Exception {
        String injectedClassName = "org/drools/example/Payload";
        byte[] fakeBytecode = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

        ProtobufMessages.Header header = ProtobufMessages.Header.newBuilder()
                .addRuntimeClassDefinitions(ProtobufMessages.RuntimeClassDef.newBuilder()
                        .setClassFqName(injectedClassName)
                        .setClassDef(ByteString.copyFrom(fakeBytecode))
                        .build())
                .build();

        java.util.Map<String, byte[]> store = new java.util.HashMap<>();
        ProjectClassLoader pcl = ProjectClassLoader.createProjectClassLoader(
                getClass().getClassLoader(), store);

        assertThatThrownBy(() ->
                PersisterHelper.readRuntimeDefinedClasses(header, pcl, null))
                .as("readRuntimeDefinedClasses() should reject when no TraitFactory is available")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no TraitFactory is available");

        assertThat(store.containsKey(injectedClassName))
                .as("Rejected class must not be in the classloader store")
                .isFalse();
    }

    private void setPrivateKeyProperties() {
        URL serverKeyStoreURL = getClass().getResource("droolsServer.keystore");
        System.setProperty(KeyStoreConstants.PROP_SIGN, "true");
        System.setProperty(KeyStoreConstants.PROP_PVT_KS_URL, serverKeyStoreURL.toExternalForm());
        System.setProperty(KeyStoreConstants.PROP_PVT_KS_PWD, "serverpwd");
        System.setProperty(KeyStoreConstants.PROP_PVT_ALIAS, "droolsKey");
        System.setProperty(KeyStoreConstants.PROP_PVT_PWD, "keypwd");
        KeyStoreHelper.reInit();
    }

    private void setPublicKeyProperties() {
        URL clientKeyStoreURL = getClass().getResource("droolsClient.keystore");
        System.setProperty(KeyStoreConstants.PROP_SIGN, "true");
        System.setProperty(KeyStoreConstants.PROP_PUB_KS_URL, clientKeyStoreURL.toExternalForm());
        System.setProperty(KeyStoreConstants.PROP_PUB_KS_PWD, "clientpwd");
        KeyStoreHelper.reInit();
    }

    @Test
    void testDeserializationFilterBlocksBeforeStaticInitializer() throws Exception {
        byte[] probeBytes = serializeObject(new testdata.enhancement.StaticInitProbe());

        // Clear the marker set by the serialization above (loading the class runs its static init)
        System.clearProperty(testdata.enhancement.StaticInitProbe.MARKER_PROPERTY);

        // Use a child-first classloader so Class.forName triggers a fresh class load
        // (the JVM only runs static initializers once per classloader)
        ClassLoader childFirstCL = new ChildFirstClassLoader(getClass().getClassLoader());

        JavaSerializableResolverStrategy strategy =
                new JavaSerializableResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT);

        assertThatThrownBy(() ->
                strategy.unmarshal(null, null, probeBytes, childFirstCL))
                .as("Deserialization of non-whitelisted class should be rejected")
                .isInstanceOf(RuntimeException.class);

        assertThat(System.getProperty(testdata.enhancement.StaticInitProbe.MARKER_PROPERTY))
                .as("Static initializer should NOT have run — the filter should reject "
                        + "the class before Class.forName initializes it")
                .isNull();
    }

    private static class ChildFirstClassLoader extends ClassLoader {
        private final ClassLoader parent;

        ChildFirstClassLoader(ClassLoader parent) {
            super(parent);
            this.parent = parent;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("testdata.enhancement.")) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded != null) {
                    return loaded;
                }
                String resourceName = name.replace('.', '/') + ".class";
                try (java.io.InputStream is = parent.getResourceAsStream(resourceName)) {
                    if (is == null) {
                        throw new ClassNotFoundException(name);
                    }
                    byte[] bytes = is.readAllBytes();
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            return super.loadClass(name, resolve);
        }
    }

    private static byte[] serializeObject(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
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
