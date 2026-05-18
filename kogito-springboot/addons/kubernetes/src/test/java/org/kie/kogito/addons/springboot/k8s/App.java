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
package org.kie.kogito.addons.springboot.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages = "org.kie.kogito.addons.springboot.k8s.**")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    // Jackson 2 @Bean (test fixture; production apps get this from the codegen GlobalObjectMapper).
    // Remove together with https://github.com/apache/incubator-kie-drools/issues/6702 (Jackson 3 migration).
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
