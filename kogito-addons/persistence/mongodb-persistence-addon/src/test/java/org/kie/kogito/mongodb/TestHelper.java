/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bson.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class TestHelper {

    @Container
    final static KogitoMongoDBContainer mongoDBContainer = new KogitoMongoDBContainer();
    public final static String DB_NAME = "testdb";
    public final static String PROCESS_NAME = "test";
    private static MongoClient mongoClient;

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
    }

    @AfterAll
    public static void close() {
        mongoDBContainer.stop();
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static Address getTestObject() {
        return new Address("main street", "Boston", "10005", "US");
    }

    public static byte[] getTestByteArrays() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance);
        String json = mapper.writeValueAsString(getTestObject());
        return json.getBytes();
    }

    public static Document getProcessInstanceDocument() throws URISyntaxException, IOException {
        Document doc = Document.parse(readFileContent("process_instance_document.json"));
        return doc;
    }

    public static KogitoProcessInstanceProtobuf.ProcessInstance getprocessInstance() throws InvalidProtocolBufferException, URISyntaxException, IOException {
        KogitoProcessInstanceProtobuf.ProcessInstance.Builder builder = KogitoProcessInstanceProtobuf.ProcessInstance.newBuilder();
        JsonFormat.Parser parser = JsonFormat.parser();
        parser.merge(readFileContent("process_instance.json"), builder);
        return builder.build();
    }

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return new String(Files.readAllBytes(path));
    }

}
