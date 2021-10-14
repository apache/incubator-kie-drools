/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.concurrency;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.test.testcategory.TurtleTestCategory;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class FromAccumulateConcurrencyTest extends BaseConcurrencyTest {

    public FromAccumulateConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false); // fails with exec-model. file JIRA
    }

    protected String getDrl() {
        return "package com.example.reproducer\n" +
               "import " + Bus.class.getCanonicalName() + ";\n" +
               "import static " + StaticUtils.class.getCanonicalName() + ".TOSTRING;\n" +
               "dialect \"mvel\"\n" +
               "global java.util.List result;\n" +
               "rule \"rule_mt_1a\"\n" +
               "    when\n" +
               "        $busX : Bus()\n" +
               "        $sum : Integer() from accumulate ($bus : Bus( $title: \"POWER PLANT\" ), " +
               "                           init( int sum = TOSTRING($busX.karaoke.dvd[$title].artist).length(); ),\n" +
               "                           action( sum += TOSTRING($bus.karaoke.dvd[$title].artist).length(); ),\n" +
               "                           reverse( sum -= TOSTRING($bus.karaoke.dvd[$title].artist).length(); ),\n" +
               "                           result( sum += TOSTRING($busX.karaoke.dvd[$title].artist).length();))\n" +
               "    then\n" +
               "end";
    }

}
