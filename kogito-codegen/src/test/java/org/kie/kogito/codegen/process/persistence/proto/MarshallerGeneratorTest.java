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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.data.PersonWithAddress;
import org.kie.kogito.codegen.data.PersonWithAddresses;
import org.kie.kogito.codegen.process.persistence.MarshallerGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class MarshallerGeneratorTest {

    private ProtoGenerator<Class<?>> generator = new ReflectionProtoGenerator();
    
    @Test
    public void testPersonMarshallers() throws Exception {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(Person.class));
        assertThat(proto).isNotNull();        
        assertThat(proto.getMessages()).hasSize(1);
        
        MarshallerGenerator marshallerGenerator = new MarshallerGenerator(this.getClass().getClassLoader());
        
        List<CompilationUnit> classes = marshallerGenerator.generate(proto.toString());
        assertThat(classes).isNotNull();       
        assertThat(classes).hasSize(1);
        
        Optional<ClassOrInterfaceDeclaration> marshallerClass = classes.get(0).getClassByName("PersonMessageMarshaller");
        assertThat(marshallerClass).isPresent();
    }
    
    @Test
    public void testPersonWithAdressMarshallers() throws Exception {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(PersonWithAddress.class));
        assertThat(proto).isNotNull();        
        assertThat(proto.getMessages()).hasSize(2);
        
        MarshallerGenerator marshallerGenerator = new MarshallerGenerator(this.getClass().getClassLoader());
        
        List<CompilationUnit> classes = marshallerGenerator.generate(proto.toString());
        assertThat(classes).isNotNull();       
        assertThat(classes).hasSize(2);
        
        Optional<ClassOrInterfaceDeclaration> marshallerClass = classes.get(0).getClassByName("AddressMessageMarshaller");
        assertThat(marshallerClass).isPresent();
        marshallerClass = classes.get(1).getClassByName("PersonWithAddressMessageMarshaller");
        assertThat(marshallerClass).isPresent();
    }
    
    @Test
    public void testPersonWithAdressesMarshallers() throws Exception {
        
        Proto proto = generator.generate("org.kie.kogito.test", Collections.singleton(PersonWithAddresses.class));
        assertThat(proto).isNotNull();        
        assertThat(proto.getMessages()).hasSize(2);
        
        MarshallerGenerator marshallerGenerator = new MarshallerGenerator(this.getClass().getClassLoader());
        
        List<CompilationUnit> classes = marshallerGenerator.generate(proto.toString());
        assertThat(classes).isNotNull();       
        assertThat(classes).hasSize(2);
        
        Optional<ClassOrInterfaceDeclaration> marshallerClass = classes.get(0).getClassByName("AddressMessageMarshaller");
        assertThat(marshallerClass).isPresent();
        marshallerClass = classes.get(1).getClassByName("PersonWithAddressesMessageMarshaller");
        assertThat(marshallerClass).isPresent();
    }
}
