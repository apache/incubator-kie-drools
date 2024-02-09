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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class EnableAuditLogCommandTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public EnableAuditLogCommandTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private String auditFileDir = "target";
    private String auditFileName = "EnableAuditLogCommandTest";

    @After
    public void cleanUp() {
        File file = new File( auditFileDir + File.separator + auditFileName + ".log" );
        if ( file.exists() ) {
            file.delete();
        }
    }

    @Test
    public void testEnableAuditLogCommand() throws Exception {

        String str = "";
        str += "package org.drools.mvel.integrationtests \n";
        str += "import " + Cheese.class.getCanonicalName() + " \n";
        str += "rule StringRule \n";
        str += " when \n";
        str += " $c : Cheese() \n";
        str += " then \n";
        str += " System.out.println($c); \n";
        str += "end \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession kSession = kbase.newKieSession();

        List<Command> commands = new ArrayList<Command>();
        commands.add( CommandFactory.newEnableAuditLog( auditFileDir, auditFileName ) );
        commands.add( CommandFactory.newInsert( new Cheese() ) );
        commands.add( CommandFactory.newFireAllRules() );
        kSession.execute( CommandFactory.newBatchExecution( commands ) );
        kSession.dispose();

        File file = new File( auditFileDir + File.separator + auditFileName + ".log" );

        assertThat(file.exists()).isTrue();

    }
}
