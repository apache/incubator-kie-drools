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
package org.drools.core.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import org.drools.io.ByteArrayResource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteArrayResourceSerializationTest {

    //FIX https://issues.redhat.com/browse/DROOLS-5681
    @Test
    public void bytesAttributesIsStillSerializedDeserializedCorrectly() throws IOException, ClassNotFoundException {

        final byte[] content = "some content".getBytes(StandardCharsets.UTF_8);

        ByteArrayResource bar = new ByteArrayResource(content, StandardCharsets.UTF_8.toString());
        byte[] serializedBar;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(bar);
            serializedBar = baos.toByteArray();
        }
        ByteArrayResource desBar;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serializedBar))){
            desBar = (ByteArrayResource) ois.readObject();
        }
        assertThat(desBar.getBytes()).isEqualTo(content);
    }

}
