/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloService {
	
    public String hello(String name) {
        System.out.println("Service invoked with " + name + " on service " + this.toString());
        return "Hello " + name.toString() + "!";
    }
    public JsonNode jsonHello(JsonNode person) {
        System.out.println("Service invoked with " + person + " on service " + this.toString());

        String retJsonStr = "{\"result\":\"Hello " + person.get("name").textValue() + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(retJsonStr);
        } catch(Exception e) {
            return null;
        }
    }
    
    public String goodbye(String name) {
        System.out.println("Service invoked with " + name + " on service " + this.toString());
        return "Goodbye " + name.toString() + "!";
    }
    
    public String helloMulti(String name, String lastName) {
        System.out.println("Service invoked with " + name + " and " + lastName + " on service " + this.toString());
        return "Hello (first and lastname) " + name.toString() + " " + lastName + "!";
    }
    
    public void helloNoOutput(String name, Integer age) {
        System.out.println("Service invoked with " + name + " " + age + " on service " + this.toString());
        
    }
    
    public String helloOutput(String name, Integer age) {
        System.out.println("Service invoked with " + name + " " + age + " on service " + this.toString());
        return "Hello " + name.toString() + " " + age + "!";
    }
}
