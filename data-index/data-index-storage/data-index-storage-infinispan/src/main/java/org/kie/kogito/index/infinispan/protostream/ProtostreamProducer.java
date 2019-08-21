/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.MessageMarshaller;

@ApplicationScoped
public class ProtostreamProducer {

    @Produces
    FileDescriptorSource kogitoTypesDescriptor() throws IOException {
        FileDescriptorSource source = new FileDescriptorSource();
        source.addProtoFile("kogito-index.proto", Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/kogito-index.proto"));
        source.addProtoFile("kogito-types.proto", Thread.currentThread().getContextClassLoader().getResourceAsStream("kogito-types.proto"));
        return source;
    }

    @Produces
    MessageMarshaller processInstanceMetaMarshaller() {
        return new ProcessInstanceMetaMarshaller();
    }

    @Produces
    MessageMarshaller processInstanceMarshaller() {
        return new ProcessInstanceMarshaller();
    }

    @Produces
    MessageMarshaller nodeInstanceMarshaller() {
        return new NodeInstanceMarshaller();
    }
}
