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
package org.drools.drlonyaml.integration.tests;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drlonyaml.model.DrlPackage;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.drlonyaml.model.Utils.getYamlMapper;

public class ProgrammaticProjectTest {

    @Test
    public void testDrl() {
        KieSession ksession = new KieHelper()
                .addContent(getDrlRule(), "org/drools/drlonyaml/integration/tests/rule.drl")
                .build(ExecutableModelProject.class)
                .newKieSession();

        checkKieSession(ksession);
    }

    @Test
    public void testYaml() {
        KieSession ksession = new KieHelper()
                .addContent(getYamlRule(), "org/drools/drlonyaml/integration/tests/rule.drl.yaml")
                .build(ExecutableModelProject.class)
                .newKieSession();

        checkKieSession(ksession);
    }

    private static void checkKieSession(KieSession ksession) {
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Message("test"));
        ksession.insert(new Message("Hello World"));
        ksession.insert(10);
        ksession.insert(11);

        int count = ksession.fireAllRules();
        assertThat(count).isEqualTo(1);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("Hello World");
    }

    private String drl2yaml(String drl) {
        try (StringWriter writer = new StringWriter()) {
            PackageDescr pkgDescr = new DrlParser().parse(new StringReader(drl));
            DrlPackage model = DrlPackage.from(pkgDescr);
            getYamlMapper().writeValue(writer, model);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getDrlRule() {
        return """
                package org.drools.drlonyaml.integration.tests

                global java.util.List result;

                rule R when
                    $i : Integer()
                    $m : Message( size == $i )
                then
                    result.add( $m.getText() );
                end""";
    }

    private String getYamlRule() {
        return """
                name: org.drools.drlonyaml.integration.tests
                globals:
                - type: java.util.List
                  id: result
                rules:
                - name: R
                  when:
                  - given: Integer
                    as: $i
                  - given: Message
                    as: $m
                    having:
                    - size == $i
                  then: |
                    result.add( $m.getText() );""";
    }
}
