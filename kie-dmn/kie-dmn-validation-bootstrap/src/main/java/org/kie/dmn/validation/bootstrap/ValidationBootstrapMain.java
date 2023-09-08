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
package org.kie.dmn.validation.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationBootstrapMain {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationBootstrapMain.class);

    public static void main(String[] args) throws IOException {
        LOG.info("Invoked with: {}", Arrays.asList(args));
        File kieDmnValidationBaseDir = new File(args[0]);
        if (!kieDmnValidationBaseDir.isDirectory()) {
            LOG.error("The supplied base directory is not valid: {}", kieDmnValidationBaseDir);
            LOG.error("ValidationBootstrapMain terminates without generating files");
            return;
        }
        try (Stream<String> lines = Files.lines(Paths.get(kieDmnValidationBaseDir.getAbsolutePath(), "pom.xml"))) {
            if (lines.noneMatch(l -> l.contains("<artifactId>kie-dmn-validation</artifactId>"))) {
                LOG.error("Unable to find the expected pom.xml.");
                LOG.error("ValidationBootstrapMain terminates without generating files");
                return;
            }
        }
        GenerateModel generator = new GenerateModel(kieDmnValidationBaseDir);
        generator.generate();
        LOG.info("ValidationBootstrapMain finished.");
    }
}
