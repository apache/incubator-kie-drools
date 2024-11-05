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
package org.drools.mvel.compiler.kie.builder.impl;

import java.util.stream.Stream;

import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieBuilderSetImpl;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class KieBuilderSetImplTest extends CommonTestMethodBase {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testBuild(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/rule%201.drl", ruleContent() );

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl( kieBuilder( kieBaseTestConfiguration, ks, kfs ) );

        kieBuilderSet.setFiles( new String[]{ "src/main/resources/rule%201.drl" } );

        final IncrementalResults build = kieBuilderSet.build();

        assertThat(build.getAddedMessages().size()).isEqualTo(0);
        assertThat(build.getRemovedMessages().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    @Disabled("RHPAM-1184, RHDM-601")
    public void testBuildPercentageAndWhiteSpaceInName(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/my rule 100% okay.rdrl",
                  ResourceFactory.newInputStreamResource(this.getClass().getResourceAsStream("my rule 100% okay.rdrl")));

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl(kieBuilder(kieBaseTestConfiguration, ks, kfs));

        kieBuilderSet.setFiles(new String[]{"src/main/resources/my rule 100% okay.rdrl"});

        final IncrementalResults build = kieBuilderSet.build();

        assertThat(build.getAddedMessages().size()).isEqualTo(0);
        assertThat(build.getRemovedMessages().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testDummyResourceWithAnEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource( "Dummy%20Resource" );
        final Resource testResource = new KieBuilderSetImpl.DummyResource( "Dummy Resource" );

        assertThat(dummyResource).isEqualTo(testResource);
    }

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testDummyResourceWithWrongEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource("Dummy 100%");
        assertThat("Dummy 100%").isEqualTo(dummyResource.getSourcePath());
    }

    private KieBuilderImpl kieBuilder(KieBaseTestConfiguration kieBaseTestConfiguration,
    									final KieServices ks,
    									final KieFileSystem kfs) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, true);
        return (KieBuilderImpl) kieBuilder;
    }

    private String ruleContent() {
        return "package org.kie.test\n"
                + "import java.util.concurrent.atomic.AtomicInteger\n"
                + "global java.util.List list\n"
                + "rule 'rule 1'\n"
                + "when\n"
                + " $i: AtomicInteger(intValue > 0)\n"
                + "then\n"
                + " list.add( $i );\n"
                + "end\n"
                + "\n";
    }
}
