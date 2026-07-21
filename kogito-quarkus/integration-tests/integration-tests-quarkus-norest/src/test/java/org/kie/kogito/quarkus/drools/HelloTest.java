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
package org.kie.kogito.quarkus.drools;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.examples.Hello;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class HelloTest {

    @Inject
    RuleUnit<Hello> ruleUnit;

    @Test
    public void testHelloEndpoint() {
        Hello data = new Hello();
        data.getStrings().add("hello");

        RuleUnitInstance<Hello> ruleUnitInstance = ruleUnit.createInstance(data);
        List<Map<String, Object>> results = ruleUnitInstance.executeQuery("hello").toList();

        List<String> stringResults = results.stream()
                .flatMap(entry -> entry.values().stream())
                .map(String.class::cast)
                .collect(Collectors.toList());

        assertThat(stringResults).contains("hello", "world");
    }
}
