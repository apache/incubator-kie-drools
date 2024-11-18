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
package org.drools.mvel.integrationtests;

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for MessageImpl
 */
public class MessageImplTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    //See DROOLS-193 (KnowledgeBuilderResult does not always contain a Resource)
    public void testMessageFromInvalidDSL(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //Some suitably duff DSL
        String dsl = "bananna\n";

        //Some suitably valid DRL
        String drl = "import org.drools.mvel.compiler.Person;\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/dsl.dsl", dsl )
                .write( "src/main/resources/drl.dslr", drl );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results results = kieBuilder.getResults();

        assertThat(results.getMessages().size()).isEqualTo(3);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMessageWithIncrementalBuild(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //Some suitably duff DSL to generate errors
        String dsl1 = "bananna\n";

        //Some suitably valid DRL
        String drl1 = "import org.drools.mvel.compiler.Person;\n"
                + "rule R1\n"
                + "when\n"
                + "There is a Person\n"
                + "then\n"
                + "end\n";

        //Some suitably valid DRL
        String drl2 = "rule R2\n"
                + "when\n"
                + "then\n"
                + "end\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write( "src/main/resources/dsl.dsl", dsl1 )
                .write( "src/main/resources/drl.dslr", drl1 );

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        Results fullBuildResults = kieBuilder.getResults();
        assertThat(fullBuildResults.getMessages().size()).isEqualTo(3);

        kfs.write( "src/main/resources/r2.drl", drl2 );
        IncrementalResults incrementalBuildResults = ( (InternalKieBuilder) kieBuilder ).createFileSet( "src/main/resources/r2.drl" ).build();

        assertThat(incrementalBuildResults.getAddedMessages().size()).isEqualTo(0);
        assertThat(incrementalBuildResults.getRemovedMessages().size()).isEqualTo(0);
    }

}
