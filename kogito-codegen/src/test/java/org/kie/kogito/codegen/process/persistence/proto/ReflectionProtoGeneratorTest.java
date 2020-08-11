/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.process.persistence.proto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.data.Answer;
import org.kie.kogito.codegen.data.AnswerWitAnnotations;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.data.PersonVarInfo;
import org.kie.kogito.codegen.data.PersonWithAddress;
import org.kie.kogito.codegen.data.PersonWithAddresses;
import org.kie.kogito.codegen.data.PersonWithList;
import org.kie.kogito.codegen.data.Question;
import org.kie.kogito.codegen.data.QuestionWithAnnotatedEnum;

class ReflectionProtoGeneratorTest {

    private ProtoGenerator<Class<?>> generator = new ReflectionProtoGenerator();
    
    @Test
    void testPersonProtoFile() {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(Person.class));
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);
        
        ProtoMessage person = proto.getMessages().get(0);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("Person");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");        
        assertThat(person.getFields()).hasSize(3);
        
        ProtoField field = person.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
    }
    
    @Test
    void testPersonWithAddressProtoFile() {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(PersonWithAddress.class));
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(2);
        
        ProtoMessage address = proto.getMessages().get(0);
        assertThat(address).isNotNull();
        assertThat(address.getName()).isEqualTo("Address");
        assertThat(address.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");        
        assertThat(address.getFields()).hasSize(4);
        
        ProtoField field = address.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("city");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("country");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("street");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(3);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("zipCode");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        ProtoMessage person = proto.getMessages().get(1);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("PersonWithAddress");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");        
        assertThat(person.getFields()).hasSize(4);
        
        field = person.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("address");
        assertThat(field.getType()).isEqualTo("Address");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(3);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
    }

    @Test
    void testPersonWithListProtoFile() {

        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(PersonWithList.class));
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);

        ProtoMessage address = proto.getMessages().get(0);
        assertThat(address).isNotNull();
        assertThat(address.getName()).isEqualTo("PersonWithList");
        assertThat(address.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(address.getFields()).hasSize(7);

        ProtoField field = address.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");

        field = address.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");

        field = address.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("booleanList");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("repeated");

        field = address.getFields().get(3);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("integerList");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("repeated");

        field = address.getFields().get(4);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("longList");
        assertThat(field.getType()).isEqualTo("int64");
        assertThat(field.getApplicability()).isEqualTo("repeated");

        field = address.getFields().get(5);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");

        field = address.getFields().get(6);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("stringList");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("repeated");
    }
    
    @Test
    void testPersonWithAddressesProtoFile() {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(PersonWithAddresses.class));
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(2);
        
        ProtoMessage address = proto.getMessages().get(0);
        assertThat(address).isNotNull();
        assertThat(address.getName()).isEqualTo("Address");
        assertThat(address.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");        
        assertThat(address.getFields()).hasSize(4);
        
        ProtoField field = address.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("city");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("country");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("street");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = address.getFields().get(3);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("zipCode");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        ProtoMessage person = proto.getMessages().get(1);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("PersonWithAddresses");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");        
        assertThat(person.getFields()).hasSize(4);
        
        field = person.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("addresses");
        assertThat(field.getType()).isEqualTo("Address");
        assertThat(field.getApplicability()).isEqualTo("repeated");
        
        field = person.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");
        
        field = person.getFields().get(3);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
    }
    
    @Test
    void testPersonAsModelProtoFile() {
        
        Proto proto = generator.generate("@Indexed", "@Field(store = Store.YES)", "org.kie.kogito.test.persons", Person.class);
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);
        
        ProtoMessage person = proto.getMessages().get(0);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("Person");
        assertThat(person.getComment()).isEqualTo("@Indexed");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.test.persons");        
        assertThat(person.getFields()).hasSize(3);
        
        ProtoField field = person.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)");
        
        field = person.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)");
        
        field = person.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)");
    }
    
    @Test
    void testPersonWithVariableInfoAsModelProtoFile() {
        
        Proto proto = generator.generate("@Indexed", "@Field(store = Store.YES)", "org.kie.kogito.test.persons", PersonVarInfo.class);
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);
        
        ProtoMessage person = proto.getMessages().get(0);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("PersonVarInfo");
        assertThat(person.getComment()).isEqualTo("@Indexed");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.test.persons");        
        assertThat(person.getFields()).hasSize(3);
        
        ProtoField field = person.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("adult");
        assertThat(field.getType()).isEqualTo("bool");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)");
        
        field = person.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("age");
        assertThat(field.getType()).isEqualTo("int32");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)");
        
        field = person.getFields().get(2);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("name");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
        assertThat(field.getComment()).isEqualTo("@Field(store = Store.YES)\n @VariableInfo(tags=\"test\")");
    }

    @Test
    void testAnswerProtoFile() {

        Proto proto = generator.generate("org.kie.kogito.test.persons", Collections.singleton(Answer.class));
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getEnums()).hasSize(1);

        ProtoEnum answer = proto.getEnums().get(0);
        assertThat(answer).isNotNull();
        assertThat(answer.getName()).isEqualTo("Answer");
        assertThat(answer.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(answer.getFields()).hasSize(3);

        Map<String, Integer> fields = answer.getFields();
        assertThat(fields).isNotNull()
                .containsEntry("YES", 0)
                .containsEntry("MAYBE", 1)
                .containsEntry("NO", 2);
    }

    @Test
    void testAnswerWithAnnotationsProtoFile() {

        Proto proto = generator.generate("org.kie.kogito.test.persons", Collections.singleton(AnswerWitAnnotations.class));
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getEnums()).hasSize(1);

        ProtoEnum answer = proto.getEnums().get(0);
        assertThat(answer).isNotNull();
        assertThat(answer.getName()).isEqualTo("AnswerWitAnnotations");
        assertThat(answer.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(answer.getFields()).hasSize(3);

        Map<String, Integer> fields = answer.getFields();
        assertThat(fields).isNotNull()
                .containsEntry("YES", 1)
                .containsEntry("MAYBE", 2)
                .containsEntry("NO", 3);
    }

    @Test
    void testAnswerWithVariableInfoProtoFile() {

        Proto proto = generator.generate("@Indexed", "@Field(store = Store.YES)", "org.kie.kogito.test.persons", Answer.class);
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getEnums()).hasSize(1);

        ProtoEnum answer = proto.getEnums().get(0);
        assertThat(answer).isNotNull();
        assertThat(answer.getName()).isEqualTo("Answer");
        assertThat(answer.getComment()).isBlank();
        assertThat(answer.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(answer.getFields()).hasSize(3);

        Map<String, Integer> fields = answer.getFields();
        assertThat(fields).isNotNull()
                .containsEntry("YES", 0)
                .containsEntry("MAYBE", 1)
                .containsEntry("NO", 2);
    }

    @Test
    void testQuestionWithEnumProtoFile() {

        Proto proto = generator.generate("org.kie.kogito.test.persons", Collections.singleton(Question.class));
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);

        ProtoMessage question = proto.getMessages().get(0);
        assertThat(question).isNotNull();
        assertThat(question.getName()).isEqualTo("Question");
        assertThat(question.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(question.getFields()).hasSize(2);

        ProtoField field = question.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("answer");
        assertThat(field.getType()).isEqualTo("Answer");
        assertThat(field.getApplicability()).isEqualTo("optional");

        field = question.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("question");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
    }

    @Test
    void testQuestionWithAnnotatedEnumProtoFile() {

        Proto proto = generator.generate("org.kie.kogito.test.persons", Collections.singleton(QuestionWithAnnotatedEnum.class));
        assertThat(proto).isNotNull();

        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);

        ProtoMessage question = proto.getMessages().get(0);
        assertThat(question).isNotNull();
        assertThat(question.getName()).isEqualTo("QuestionWithAnnotatedEnum");
        assertThat(question.getJavaPackageOption()).isEqualTo("org.kie.kogito.codegen.data");
        assertThat(question.getFields()).hasSize(2);

        ProtoField field = question.getFields().get(0);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("answer");
        assertThat(field.getType()).isEqualTo("AnswerWitAnnotations");
        assertThat(field.getApplicability()).isEqualTo("optional");

        field = question.getFields().get(1);
        assertThat(field).isNotNull();
        assertThat(field.getName()).isEqualTo("question");
        assertThat(field.getType()).isEqualTo("string");
        assertThat(field.getApplicability()).isEqualTo("optional");
    }

}
