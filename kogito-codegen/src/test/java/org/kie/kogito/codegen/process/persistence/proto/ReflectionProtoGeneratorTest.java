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

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.data.PersonWithAddress;
import org.kie.kogito.codegen.data.PersonWithAddresses;

public class ReflectionProtoGeneratorTest {

    
    private ProtoGenerator<Class<?>> generator = new ReflectionProtoGenerator();
    
    @Test
    public void testPersonProtoFile() {
        
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
    public void testPersonWithAddressProtoFile() {
        
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
    public void testPersonWithAddressesProtoFile() {
        
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
    public void testPersonAsModelProtoFile() {
        
        Proto proto = generator.generate("org.kie.kogito.test.persons", Person.class);
        assertThat(proto).isNotNull();
        
        assertThat(proto.getPackageName()).isEqualTo("org.kie.kogito.test.persons");
        assertThat(proto.getSyntax()).isEqualTo("proto2");
        assertThat(proto.getMessages()).hasSize(1);
        
        ProtoMessage person = proto.getMessages().get(0);
        assertThat(person).isNotNull();
        assertThat(person.getName()).isEqualTo("Person");
        assertThat(person.getJavaPackageOption()).isEqualTo("org.kie.kogito.test.persons");        
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
}
