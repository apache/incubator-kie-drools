/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;

public class DslParserTest extends ParserTest {
    private final File dsl;

    public DslParserTest(File dslr, File dsl) {
        super(dslr);
        this.dsl = dsl;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Set<Object[]> set = new HashSet<>();

        for (File f : getFiles("dsl", "dslr")) {
            final String dslPath = f.getAbsolutePath();
            final File dsl = new File(dslPath.substring(0, dslPath.length() - 1));
            set.add(new Object[] { dsl, f });
        }

        return set;
    }

    @Test
    public void testParserDsl() {
        final Resource dslResource = KieServices.Factory.get().getResources().newFileSystemResource(dsl);
        final Resource dslrResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(true, dslResource, dslrResource);
    }

    @Test
    public void testParserDsl2() {
        final Resource dslResource = KieServices.Factory.get().getResources().newFileSystemResource(dsl);
        final Resource dslrResource = KieServices.Factory.get().getResources().newFileSystemResource(file);
        KieUtil.getKieBuilderFromResources(true, dslrResource, dslResource);
    }
}
