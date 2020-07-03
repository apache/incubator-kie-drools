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

package org.kie.pmml.evaluator.assembler.service;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PMMLCompilerServiceTest {


    @Test
    public void getFileName() {
        String fileName = "TestFile.pmml";
        String fullPath = String.format("%1$sthis%1$sis%1$sfull%1$spath%1$s%2$s",
                                        File.separator,
                                        fileName);
        String retrieved = PMMLCompilerService.getFileName(fullPath);
        assertEquals(fileName, retrieved);
    }
}