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
package org.drools.decisiontable.integrationtests;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class IncrementalCompilationTest {

    @Test
    public void testDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream("incrementalBuild.dtable.drl.xls");
            kfs.write("src/main/resources/incrementalBuild1.dtable.drl.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource(in1));

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream("incrementalBuild.dtable.drl.xls");
            kfs.write("src/main/resources/incrementalBuild2.dtable.drl.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource(in2));

            //Check errors on a full build
            List<Message> messages = ks.newKieBuilder(kfs).buildAll().getResults().getMessages();
            assertThat(messages.isEmpty()).isFalse();

        } finally {
            if (in1 != null) {
                in1.close();
            }
            if (in2 != null) {
                in2.close();
            }
        }

    }

    @Test
    public void testIncrementalCompilationDuplicateXLSResources() throws Exception {

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        InputStream in1 = null;
        InputStream in2 = null;

        try {

            //Add XLS decision table
            in1 = this.getClass().getResourceAsStream("incrementalBuild.dtable.drl.xls");
            kfs.write("src/main/resources/incrementalBuild1.dtable.drl.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource(in1));

            //Expect no errors
            KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
            assertThat(kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR)).hasSize(0);

            //Add the same XLS decision table again as a different resource
            in2 = this.getClass().getResourceAsStream("incrementalBuild.dtable.drl.xls");
            kfs.write("src/main/resources/incrementalBuild2.dtable.drl.xls",
                       KieServices.Factory.get().getResources().newInputStreamResource(in2));
            IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/incrementalBuild2.dtable.drl.xls").build();

            //Expect duplicate rule errors
            assertThat(addResults.getAddedMessages()).hasSize(1);
            assertThat(addResults.getRemovedMessages()).hasSize(0);

            //Check errors on a full build
            List<Message> messages = ks.newKieBuilder(kfs).buildAll().getResults().getMessages();
            assertThat(messages).isNotEmpty();

        } finally {
            if (in1 != null) {
                in1.close();
            }
            if (in2 != null) {
                in2.close();
            }
        }

    }

}
