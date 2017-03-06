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

package org.drools.testcoverage.regression;

import java.io.StringReader;

import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;

public class AmbiguousExceptionTest {
    private static final String DRL =
            "   package " + TestConstants.PACKAGE_REGRESSION + "\n\n "
            + " import " + TestConstants.PACKAGE_REGRESSION + ".AmbiguousExceptionTest.Exception\n\n"
            + " rule ruleOne\n"
            + "    when\n "
            + "    then\n"
            + " end\n";

    @Test
    public void testCompilation() {
        final KieFileSystem fileSystem = KieServices.Factory.get().newKieFileSystem();
        fileSystem.write(
                TestConstants.DRL_TEST_TARGET_PATH,
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(DRL)));

        KieUtil.getKieBuilderFromKieFileSystem(fileSystem, true);
    }

    public static class Exception {
    }
}
