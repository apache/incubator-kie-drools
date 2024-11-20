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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing use of default Package.
 */
public class KieDefaultPackageTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAllInDefaultPackage(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //This Model will be in the "default package"
        String model_drl = ""
                + "declare Smurf\n"
                + "Field1 : String\n"
                + "end\n";

        //This DRL is in the "default package"
        String drl = ""
                + "rule \"test\"\n"
                + "when\n"
                + "Smurf()\n"
                + "then\n"
                + "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/model.drl", model_drl );
        kfs.write( "src/main/resources/drl.drl", drl );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testInTestPackage(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String javaClass = ""
                + "package org.jbpm;\n"
                + "public class Test{}\n";

       

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/test/java/org/jbpm/Test.java", javaClass );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }
    

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    @Disabled("How do you access Type 'Smurf'? Test 1 - No import prefix")
    public void testModelInDefaultPackage1(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //This Model will be in the "default package"
        String model_drl = ""
                + "declare Smurf\n"
                + "Field1 : String\n"
                + "end\n";

        //This DRL is in a named package, but imports the model
        String drl = ""
                + "package org.smurf\n"
                + "import Smurf\n"
                + "rule \"test\"\n"
                + "when\n"
                + "Smurf()\n"
                + "then\n"
                + "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/model.drl", model_drl );
        kfs.write( "src/main/resources/drl.drl", drl );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testModelInDefaultPackage2(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //This Model will be in the "default package"
        String model_drl = ""
                + "declare Smurf\n"
                + "Field1 : String\n"
                + "end\n";

        //This DRL is in a named package, but imports the model (trying with defaultPkg prefix)
        String drl = ""
                + "package org.smurf\n"
                + "import defaultpkg.Smurf\n"
                + "rule \"test\"\n"
                + "when\n"
                + "Smurf()\n"
                + "then\n"
                + "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/model.drl", model_drl );
        kfs.write( "src/main/resources/drl.drl", drl );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAllInExplicitPackage(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //This Model will be in package "org.smurf"
        String model_drl = ""
                + "package org.smurf\n"
                + "declare Smurf\n"
                + "Field1 : String\n"
                + "end\n";

        //This DRL is in package "org.smurf" too
        String drl = ""
                + "package org.smurf\n"
                + "rule \"test\"\n"
                + "when\n"
                + "Smurf()\n"
                + "then\n"
                + "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/model.drl", model_drl );
        kfs.write( "src/main/resources/drl.drl", drl );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testAllInDifferentExplicitPackages(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        //This Model will be in package "org.smurf"
        String model_drl = ""
                + "package org.smurf\n"
                + "declare Smurf\n"
                + "Field1 : String\n"
                + "end\n";

        //This DRL is in package "org.smurf.subpackage"
        String drl = ""
                + "package org.smurf.subpackage\n"
                + "import org.smurf.Smurf\n"
                + "rule \"test\"\n"
                + "when\n"
                + "Smurf()\n"
                + "then\n"
                + "end";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/main/resources/model.drl", model_drl );
        kfs.write( "src/main/resources/drl.drl", drl );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }

}
