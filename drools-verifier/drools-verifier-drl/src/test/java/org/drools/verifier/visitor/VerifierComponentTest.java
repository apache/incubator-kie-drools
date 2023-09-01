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
package org.drools.verifier.visitor;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.verifier.Verifier;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.NumberRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternOperatorDescr;
import org.drools.verifier.components.RuleOperatorDescr;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.WorkingMemory;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.junit.jupiter.api.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

public class VerifierComponentTest {

    @Test
    void testVisit() throws Exception {
        // Drools Package description from Drl file
        Reader drlReader = new InputStreamReader(Verifier.class.getResourceAsStream("Misc3.drl"));
        PackageDescr descr = new DrlParser(LanguageLevelOption.DRL5).parse(drlReader);

        // Drools Verifier objects
        VerifierData verifierData = VerifierReportFactory.newVerifierData();
        PackageDescrVisitor visitor = new PackageDescrVisitor(verifierData, Collections.EMPTY_LIST);
        visitor.visitPackageDescr(descr);

        // Collect the results.
        Collection<VerifierComponent> datas = verifierData.getAll();
        VerifierComponent[] components = datas.toArray(new VerifierComponent[datas.size()]);

        // Misc3.drl
        assertVerifierComponent(components[0], 51);
        assertVerifierComponent(components[1], 42);
        assertVerifierComponent(components[2], 48);
        assertVerifierComponent(components[3], 39);
        assertVerifierComponent(components[4], 40);
        assertVerifierComponent(components[5], 41);
        assertVerifierComponent(components[6], 19);
        assertVerifierComponent(components[7], 48);
        assertVerifierComponent(components[8], 19);
        assertVerifierComponent(components[9], 39);
        assertVerifierComponent(components[10], 40);
        assertVerifierComponent(components[11], 41);
        assertVerifierComponent(components[12], 48);
        assertVerifierComponent(components[13], 48);
        assertVerifierComponent(components[14], 49);
        assertVerifierComponent(components[15], 50);
        assertVerifierComponent(components[16], 39);
        assertVerifierComponent(components[17], 39);
        assertVerifierComponent(components[18], 40);
        assertVerifierComponent(components[19], 41);
        assertVerifierComponent(components[20], 48);
        assertVerifierComponent(components[21], 49);
        assertVerifierComponent(components[22], 50);
        assertVerifierComponent(components[23], 39);
        assertVerifierComponent(components[24], 40);
        assertVerifierComponent(components[25], 41);
        assertVerifierComponent(components[26], 48);
        assertVerifierComponent(components[27], 49);
        assertVerifierComponent(components[28], 50);
        assertVerifierComponent(components[29], 39);
        assertVerifierComponent(components[30], 40);
        assertVerifierComponent(components[31], 41);
        assertVerifierComponent(components[32], 41);
        assertVerifierComponent(components[33], 46);
        assertVerifierComponent(components[34], 37);
        assertVerifierComponent(components[35], 17);
        assertVerifierComponent(components[36], 48);
        assertVerifierComponent(components[37], 49);
        assertVerifierComponent(components[38], 50);
        assertVerifierComponent(components[39], 39);
        assertVerifierComponent(components[40], 40);
        assertVerifierComponent(components[41], 41);
        assertVerifierComponent(components[42], 46);
        assertVerifierComponent(components[43], 37);
        assertVerifierComponent(components[44], -1);
    }

  void assertVerifierComponent(VerifierComponent component, int line) {
    assertThat(component).isNotNull();
    assertThat(component.getDescr()).isNotNull();
    if(component instanceof TextConsequence) {
        assertThat(((TextConsequence) component).getDescr().getConsequenceLine()).isEqualTo(line);
    } else {
        assertThat(component.getDescr().getLine()).isEqualTo(line);
    }
  }
}
