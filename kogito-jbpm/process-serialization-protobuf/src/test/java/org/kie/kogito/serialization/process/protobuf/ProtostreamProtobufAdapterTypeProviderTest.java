/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serialization.process.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.infinispan.protostream.descriptors.FileDescriptor;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Descriptors;

class ProtostreamProtobufAdapterTypeProviderTest {

    // list of the message descriptors present in .proto files "kogito-types.proto" and "application-types.proto";
    // "application-type.proto" is using all of the "kogito-types.proto" messages
    List<String> msgDescriptors = Arrays.asList("kogito.String", "kogito.Integer", "kogito.Long",
            "kogito.Double", "kogito.Float", "kogito.Boolean", "kogito.Date",
            "org.kie.kogito.app.Address", "org.kie.kogito.app.Traveller");

    @Test
    void testSuccessConversionProtostreamToProtobuf() {
        ProtostreamProtobufAdapterTypeProvider prov = new ProtostreamProtobufAdapterTypeProvider();
        Collection<Descriptors.Descriptor> descriptors = prov.descriptors();
        Assertions.assertThat(descriptors.stream().map(Descriptors.Descriptor::getFullName)).hasSameElementsAs(msgDescriptors);
    }

    @Test
    void testFileDescriptorSortWithKogitoFirstInOrigList() {
        ProtostreamProtobufAdapterTypeProvider prov = new ProtostreamProtobufAdapterTypeProvider();
        FileDescriptor fd1 = new FileDescriptor.Builder().withPackageName("kogito").build();
        FileDescriptor fd2 = new FileDescriptor.Builder().withPackageName("org.kie.kogito.app").build();

        List<FileDescriptor> fdCollectionKogitoOrder1 = Arrays.asList(fd1, fd2);
        List<FileDescriptor> fdCollectionSorted = prov.sortFds(fdCollectionKogitoOrder1);
        Assertions.assertThat(fdCollectionSorted).hasSameElementsAs(fdCollectionKogitoOrder1);
        Assertions.assertThat(fdCollectionSorted.get(0)).isEqualTo(fd1);
    }

    @Test
    void testFileDescriptorSortWithKogitoLastInOrigList() {
        ProtostreamProtobufAdapterTypeProvider prov = new ProtostreamProtobufAdapterTypeProvider();
        FileDescriptor fd1 = new FileDescriptor.Builder().withPackageName("kogito").build();
        FileDescriptor fd2 = new FileDescriptor.Builder().withPackageName("org.kie.kogito.app").build();

        List<FileDescriptor> fdCollectionKogitoOrder2 = Arrays.asList(fd2, fd1);
        List<FileDescriptor> fdCollectionSorted2 = prov.sortFds(fdCollectionKogitoOrder2);
        Assertions.assertThat(fdCollectionSorted2).hasSameElementsAs(fdCollectionKogitoOrder2);
        Assertions.assertThat(fdCollectionSorted2.get(0)).isEqualTo(fd1);
    }
}
