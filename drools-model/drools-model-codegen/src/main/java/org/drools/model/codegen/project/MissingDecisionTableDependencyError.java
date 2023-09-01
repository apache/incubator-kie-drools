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
package org.drools.model.codegen.project;

public class MissingDecisionTableDependencyError extends Error {

    public MissingDecisionTableDependencyError() {
        super("A Decision Table resource was found, but a necessary dependency is missing. \n" +
                "Verify that you have the drools bom in your dependencyManagement:\n" +
                "\n" +
                "<dependencyManagement>" +
                "    <dependency>\n" +
                "        <groupId>org.drools</groupId>\n" +
                "        <artifactId>drools-bom</artifactId>\n" +
                "        <type>pom</type>\n" +
                "        <scope>import</scope>\n" +
                "    </dependency>\n" +
                "</dependencyManagement>" +
                "\n" +
                "and added decision table support to your project dependencies: \n" +
                "\n" +
                "<dependency>\n" +
                "    <groupId>org.drools</groupId>\n" +
                "    <artifactId>drools-decisiontables</artifactId>\n" +
                "</dependency>");
    }
}
