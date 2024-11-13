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
package org.kie.kogito.jackson.utils;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonObjectUtilsTest {

    @Test
    void testPojo() {
        assertThat(JsonObjectUtils.fromValue(new Person("javierito"))).isEqualTo(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"));
    }

    @Test
    void testArrayOfPojo() {
        assertThat(JsonObjectUtils.fromValue(Arrays.asList(new Person("javierito"), new Person("fulanito"))))
                .isEqualTo(ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"))
                        .add(ObjectMapperFactory.get().createObjectNode().put("name", "fulanito")));
    }

    @Test
    void testJavaArray() {
        assertThat(JsonObjectUtils.toJavaValue(ObjectMapperFactory.get().createArrayNode()
                .add(ObjectMapperFactory.get().createObjectNode().put("name", "javierito")).add(ObjectMapperFactory.get().createObjectNode().put("name", "fulanito"))))
                        .isEqualTo(Arrays.asList(Collections.singletonMap("name", "javierito"), Collections.singletonMap("name", "fulanito")));
    }

    @Test
    void testJavaObject() {
        assertThat(JsonObjectUtils.toJavaValue(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"))).isEqualTo(Collections.singletonMap("name", "javierito"));
    }

    @Test
    void testNullObject() {
        assertThat(JsonObjectUtils.toJavaValue(NullNode.getInstance())).isNull();
    }

    @Test
    void testJavaInt() {
        assertThat(JsonObjectUtils.toJavaValue(new IntNode(5))).isEqualTo(5);
    }

    @Test
    void testJavaDouble() {
        assertThat(JsonObjectUtils.toJavaValue(new DoubleNode(5.0f))).isEqualTo(5.0);
    }

    @Test
    void testJavaFloat() {
        assertThat(JsonObjectUtils.toJavaValue(new FloatNode(5.0f))).isEqualTo(5.0f);
    }

    @Test
    void testJavaByteArray() {
        byte[] bytes = { 1, 2, 3, 4 };
        assertThat((byte[]) JsonObjectUtils.toJavaValue(BinaryNode.valueOf(bytes))).isEqualTo(bytes);
    }

    @Test
    void testURI() {
        final String uri = "www.google.com";
        assertThat(JsonObjectUtils.convertValue(ObjectMapperFactory.get().createObjectNode().put("uri", uri), URI.class)).isEqualTo(URI.create(uri));
        assertThat(JsonObjectUtils.convertValue(new TextNode(uri), URI.class)).isEqualTo(URI.create(uri));
    }

    @Test
    void testFile() {
        final String file = "/home/myhome/sample.txt";
        final String additionalData = "Javierito";
        assertThat(JsonObjectUtils.convertValue(ObjectMapperFactory.get().createObjectNode().put("file", file), File.class)).isEqualTo(new File(file));
        assertThat(JsonObjectUtils.convertValue(ObjectMapperFactory.get().createArrayNode().add(file), File.class)).isEqualTo(new File(file));
        assertThat(JsonObjectUtils.convertValue(ObjectMapperFactory.get().createObjectNode().put("file", file).put("additionalData", additionalData), PseudoPOJO.class))
                .isEqualTo(new PseudoPOJO(additionalData, new File(file)));
        assertThat(JsonObjectUtils.convertValue(new TextNode(file), File.class)).isEqualTo(new File(file));
    }

    @Test
    void testFileNullInput() {
        assertThat(JsonObjectUtils.convertValue(NullNode.getInstance(), File.class)).isNull();
    }

    @Test
    void testFileEmptyPath() {
        final String emptyPath = "";
        assertThat(JsonObjectUtils.convertValue(new TextNode(emptyPath), File.class)).isEqualTo(new File(emptyPath));
    }

    @Test
    void testFileWithSpecialCharacters() {
        final String pathWithSpecialChars = "/home/user/my file en español.txt";
        assertThat(JsonObjectUtils.convertValue(new TextNode(pathWithSpecialChars), File.class))
                .isEqualTo(new File(pathWithSpecialChars));
    }

    @Test
    void testUnsupportedNodeType() {
        final String errorMessage = "should be a string or have exactly one property of type string";
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(BooleanNode.TRUE, URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(new IntNode(1), URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(ObjectMapperFactory.get().createArrayNode(), URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(ObjectMapperFactory.get().createObjectNode(), URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(ObjectMapperFactory.get().createObjectNode().put("name", 1), URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
        assertThatThrownBy(() -> JsonObjectUtils.convertValue(ObjectMapperFactory.get().createArrayNode().add("first.txt").add("second.txt"), URI.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(errorMessage);
    }

    private static record PseudoPOJO(String additionalData, File file) {
    }

    private static record Person(String name) {
    }
}
