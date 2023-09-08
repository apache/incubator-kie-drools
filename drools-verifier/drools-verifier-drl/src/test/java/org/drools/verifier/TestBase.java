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
package org.drools.verifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarInputStream;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.visitor.PackageDescrVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestBase {

    protected VerifierData verifierData;
    protected PackageDescrVisitor packageDescrVisitor;

    @BeforeEach
    public void setUp() throws Exception {
        verifierData = VerifierReportFactory.newVerifierData();
        packageDescrVisitor = new PackageDescrVisitor(verifierData,
                Collections.emptyList());
    }

    protected PackageDescr getPackageDescr(InputStream resourceAsStream) throws DroolsParserException {
        Reader drlReader = new InputStreamReader(resourceAsStream);
        return new DrlParser(LanguageLevelOption.DRL5).parse(drlReader);
    }

    protected void assertContainsVariable(String ruleName, String variableName) {
        Variable variable = verifierData.getVariableByRuleAndVariableName(ruleName,
                variableName);

        assertThat(variable).as(String.format("Could not find Variable : %s ", variableName)).isNotNull();
    }

    protected void assertContainsField(String name) {
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        for (Field field : allFields) {
            if (name.equals(field.getName())) {
                return;
            }
        }

        fail("Could not find Field");
    }

    protected void assertContainsFields(int amount) {
        Collection<Field> allFields = verifierData.getAll(VerifierComponentType.FIELD);

        assertThat(allFields.size()).isEqualTo(amount);
    }
}
