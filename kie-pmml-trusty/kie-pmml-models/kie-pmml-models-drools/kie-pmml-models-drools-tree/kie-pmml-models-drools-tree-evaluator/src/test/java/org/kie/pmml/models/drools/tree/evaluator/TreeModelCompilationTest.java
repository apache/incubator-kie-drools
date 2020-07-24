/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.tree.evaluator;

import java.io.File;
import java.nio.file.Files;

import org.drools.compiler.compiler.DrlParser;
import org.junit.Test;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.fail;

public class TreeModelCompilationTest {

    @Test
    public void testTreeModelFile() throws Exception {
        DrlParser drlParser = new DrlParser();
        File file = FileUtils.getFile("TreeSample.drl");
        String content = new String(Files.readAllBytes(file.toPath()));
        try {
            drlParser.parse(false, content);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}