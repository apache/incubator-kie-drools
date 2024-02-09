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

import java.util.Collection;

import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieBuilderSetImpl;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class KieBuilderSetImplTest extends CommonTestMethodBase {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieBuilderSetImplTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testBuild() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( "src/main/resources/rule%201.drl", ruleContent() );

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl( kieBuilder( ks, kfs ) );

        kieBuilderSet.setFiles( new String[]{ "src/main/resources/rule%201.drl" } );

        final IncrementalResults build = kieBuilderSet.build();

        assertThat(build.getAddedMessages().size()).isEqualTo(0);
        assertThat(build.getRemovedMessages().size()).isEqualTo(0);
    }

    @Test
    @Ignore("RHPAM-1184, RHDM-601")
    public void testBuildPercentageAndWhiteSpaceInName() throws Exception {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/my rule 100% okay.rdrl",
                  ResourceFactory.newInputStreamResource(this.getClass().getResourceAsStream("my rule 100% okay.rdrl")));

        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl(kieBuilder(ks, kfs));

        kieBuilderSet.setFiles(new String[]{"src/main/resources/my rule 100% okay.rdrl"});

        final IncrementalResults build = kieBuilderSet.build();

        assertThat(build.getAddedMessages().size()).isEqualTo(0);
        assertThat(build.getRemovedMessages().size()).isEqualTo(0);
    }

    @Test
    public void testDummyResourceWithAnEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource( "Dummy%20Resource" );
        final Resource testResource = new KieBuilderSetImpl.DummyResource( "Dummy Resource" );

        assertThat(dummyResource).isEqualTo(testResource);
    }

    @Test
    public void testDummyResourceWithWrongEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource("Dummy 100%");
        assertThat("Dummy 100%").isEqualTo(dummyResource.getSourcePath());
    }

    private KieBuilderImpl kieBuilder( final KieServices ks,
                                       final KieFileSystem kfs ) {
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
