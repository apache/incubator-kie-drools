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
package org.drools.docs.renderer;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import org.drools.docs.model.RuleSetDocumentation;

/**
 * Renders a {@link RuleSetDocumentation} into a human-readable format.
 */
public interface DocumentRenderer {

    /**
     * Renders the documentation to the given writer.
     */
    void render(RuleSetDocumentation documentation, Writer writer) throws IOException;

    /**
     * Renders the documentation to a string.
     */
    default String render(RuleSetDocumentation documentation) throws IOException {
        java.io.StringWriter sw = new java.io.StringWriter();
        render(documentation, sw);
        return sw.toString();
    }

    /**
     * Renders the documentation to a file.
     */
    default void renderToFile(RuleSetDocumentation documentation, Path outputFile) throws IOException {
        try (var writer = java.nio.file.Files.newBufferedWriter(outputFile)) {
            render(documentation, writer);
        }
    }
}
