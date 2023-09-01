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
package org.drools.model.codegen.execmodel;

import java.util.UUID;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.util.StringUtils.ucFirst;

public class BigPojoExecModelGenerationTest {

    @Test
    public void test() {
        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId( "org.kie", "kjar-test-" + UUID.randomUUID(), "1.0" );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);
        kfs.write( "src/main/java/huge/Person.java", getBigPojoSource() );
        kfs.write( "src/main/resources/huge/rule.drl", getRuleBigPojo() );

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kieContainer = ks.newKieContainer( releaseId );

        KieSession ksession = kieContainer.newKieSession();
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    private String getRuleBigPojo() {
        return "package huge;\n" +
                "rule R1 when then Person p = new Person(); p.setName(\"Mario\"); insert(p); end\n" +
                "rule R2 when Person(name == \"Mario\") then end\n";

    }

    private String getBigPojoSource() {
        StringBuilder pojo = new StringBuilder(
                "package huge;\n" +
                "public class Person {\n");
        pojo.append(getFieldSource("name"));
        for (int i = 0; i < 10_000; i++) {
            pojo.append(getFieldSource("field"+i));
        }
        pojo.append("}\n");
        return pojo.toString();
    }

    private String getFieldSource(String fieldName) {
        String ucFirst = ucFirst(fieldName);
        return
                "    private String " + fieldName + ";\n" +
                "    public String get" + ucFirst + "() { return " + fieldName + "; }\n" +
                "    public void set" + ucFirst + "(String " + fieldName + ") { this." + fieldName + " = " + fieldName + "; }\n";
    }
}
