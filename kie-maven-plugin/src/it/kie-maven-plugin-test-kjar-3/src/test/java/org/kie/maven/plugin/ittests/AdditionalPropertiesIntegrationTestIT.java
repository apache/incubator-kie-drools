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
package org.kie.maven.plugin.ittests;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AdditionalPropertiesIntegrationTestIT {

    @Test
    public void testAdditionalPropertiesCorrectlySet() throws Exception {
        // additional properties are logged during debug (-X) build
        // following string is created directly inside the KIE Maven plugin execution (the property names and values
        // are logged multiple by maven itself as well, so we should check directly against that string)
        final URL targetLocation = AdditionalPropertiesIntegrationTestIT.class.getProtectionDomain().getCodeSource().getLocation();
        final File basedir = new File(targetLocation.getFile().replace("/target/test-classes/", ""));
        final File buildLog = new File(basedir, "build.log");
        final String expected = "Additional system properties: {drools.dialect.java.compiler.lnglevel=1.8, my.property=some-value}";
        assertTrue(Files.lines(buildLog.toPath(), StandardCharsets.UTF_8)
                           .anyMatch(line -> line.contains(expected)));
    }
}
