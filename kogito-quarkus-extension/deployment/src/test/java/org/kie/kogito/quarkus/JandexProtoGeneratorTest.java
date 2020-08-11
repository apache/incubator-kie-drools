/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.process.persistence.proto.Proto;
import org.kie.kogito.quarkus.deployment.JandexProtoGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JandexProtoGeneratorTest {

    @Test
    void testGenerate() {
        JandexProtoGenerator generator = new JandexProtoGenerator(null, null, null);
        List<ClassInfo> dataModel = new ArrayList<>();

        DotName enumName = DotName.createComponentized(DotName.createComponentized(DotName.createComponentized(null, "com"), "acme"), "ExampleEnum");
        ClassInfo enumClassInfo = ClassInfo.create(enumName, DotName.createSimple(Enum.class.getName()), (short) 0, new DotName[0], new HashMap<>(), false);
        dataModel.add(enumClassInfo);

        DotName objectName = DotName.createComponentized(DotName.createComponentized(DotName.createComponentized(null, "com"), "acme"), "ExampleObject");
        ClassInfo objectClassName = ClassInfo.create(objectName, DotName.createSimple(Object.class.getName()), (short) 0, new DotName[0], new HashMap<>(), false);
        dataModel.add(objectClassName);

        Proto proto = generator.generate("com.acme", dataModel);
        assertEquals(1, proto.getEnums().size());
        assertEquals(enumName.local(), proto.getEnums().get(0).getName());
        assertEquals(1, proto.getMessages().size());
        assertEquals(objectName.local(), proto.getMessages().get(0).getName());
    }

    @Test
    void testGenerateComments() {
        JandexProtoGenerator generator = new JandexProtoGenerator(null, null, null);
        List<ClassInfo> dataModel = new ArrayList<>();

        DotName enumName = DotName.createComponentized(DotName.createComponentized(DotName.createComponentized(null, "com"), "acme"), "ExampleEnum");
        ClassInfo enumClassInfo = ClassInfo.create(enumName, DotName.createSimple(Enum.class.getName()), (short) 0, new DotName[0], new HashMap<>(), false);
        dataModel.add(enumClassInfo);

        DotName objectName = DotName.createComponentized(DotName.createComponentized(DotName.createComponentized(null, "com"), "acme"), "ExampleObject");
        ClassInfo objectClassName = ClassInfo.create(objectName, DotName.createSimple(Object.class.getName()), (short) 0, new DotName[0], new HashMap<>(), false);
        dataModel.add(objectClassName);

        Proto enumProto = generator.generate("message comment", "field comment", "com.acme", enumClassInfo);
        assertEquals(1, enumProto.getEnums().size());
        assertEquals(enumName.local(), enumProto.getEnums().get(0).getName());

        Proto objectProto = generator.generate("message comment", "field comment", "com.acme", objectClassName);
        assertEquals(1, objectProto.getMessages().size());
        assertEquals(objectName.local(), objectProto.getMessages().get(0).getName());
    }

}
