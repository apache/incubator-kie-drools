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
package org.kie.kogito.integrationtests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class HelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloService.class);

    public String hello(String name) {
        logMethodCall("hello", name);
        return "Hello " + name + "!";
    }

    public JsonNode jsonHello(JsonNode person) {
        logMethodCall("jsonHello", person);

        String retJsonStr = "{\"result\":\"Hello " + person.get("name").textValue() + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(retJsonStr);
        } catch (Exception e) {
            return null;
        }
    }

    public String goodbye(String name) {
        logMethodCall("goodbye", name);
        return "Goodbye " + name + "!";
    }

    public String bye(String name) {
        logMethodCall("bye", name);
        return "Bye " + name + "!";
    }

    public String helloMulti(String name, String lastName) {
        logMethodCall("helloMulti", name, lastName);
        return "Hello (first and lastname) " + name.concat(" ").concat(lastName).concat("!");
    }

    public void helloNoOutput(String name, Integer age) {
        logMethodCall("helloNoOutput", name, age);
    }

    public String helloOutput(String name, Integer age) {
        logMethodCall("helloOutput", name, age);
        return "Hello " + name.concat(" ").concat(String.valueOf(age)).concat("!");
    }

    private static void logMethodCall(String method, Object... arguments) {
        LOGGER.info("HelloService.{} invoked with params: {}", method, arguments);
    }

}
