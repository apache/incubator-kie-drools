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

import java.util.Collection;

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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing use of default Package.
 */
@RunWith(Parameterized.class)
public class KieDefaultPackageTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieDefaultPackageTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testAllInDefaultPackage() throws Exception {
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
    
    @Test
    public void testInTestPackage() throws Exception {
        String javaClass = ""
                + "package org.jbpm;\n"
                + "public class Test{}\n";

       

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( "src/test/java/org/jbpm/Test.java", javaClass );
        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
    }
    

    @Test
    @Ignore("How do you access Type 'Smurf'? Test 1 - No import prefix")
    public void testModelInDefaultPackage1() throws Exception {
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

    @Test
    public void testModelInDefaultPackage2() throws Exception {
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

    @Test
    public void testAllInExplicitPackage() throws Exception {
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

    @Test
    public void testAllInDifferentExplicitPackages() throws Exception {
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
