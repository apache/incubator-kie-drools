/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.verifier.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;

import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
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
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class VerifierComponentTest {

  @Test
  public void testVisit() throws Exception {
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
    assertVerifierComponent((TextConsequence) components[0],51);
    assertVerifierComponent((TextConsequence) components[1],42);
    assertVerifierComponent((Field) components[2],48);
    assertVerifierComponent((Field) components[3],39);
    assertVerifierComponent((Field) components[4],40);
    assertVerifierComponent((Field) components[5],41);
    assertVerifierComponent((Import) components[6],19);
    assertVerifierComponent((ObjectType) components[7],48);
    assertVerifierComponent((ObjectType) components[8],19);
    assertVerifierComponent((ObjectType) components[9],39);
    assertVerifierComponent((ObjectType) components[10],40);
    assertVerifierComponent((ObjectType) components[11],41);
    assertVerifierComponent((RuleOperatorDescr) components[12],48);
    assertVerifierComponent((PatternOperatorDescr) components[13],48);
    assertVerifierComponent((PatternOperatorDescr) components[14],49);
    assertVerifierComponent((PatternOperatorDescr) components[15],50);
    assertVerifierComponent((RuleOperatorDescr) components[16],39);
    assertVerifierComponent((PatternOperatorDescr) components[17],39);
    assertVerifierComponent((PatternOperatorDescr) components[18],40);
    assertVerifierComponent((PatternOperatorDescr) components[19],41);
    assertVerifierComponent((Pattern) components[20],48);
    assertVerifierComponent((Pattern) components[21],49);
    assertVerifierComponent((Pattern) components[22],50);
    assertVerifierComponent((Pattern) components[23],39);
    assertVerifierComponent((Pattern) components[24],40);
    assertVerifierComponent((Pattern) components[25],41);
    assertVerifierComponent((NumberRestriction) components[26],48);
    assertVerifierComponent((NumberRestriction) components[27],49);
    assertVerifierComponent((NumberRestriction) components[28],50);
    assertVerifierComponent((NumberRestriction) components[29],39);
    assertVerifierComponent((NumberRestriction) components[30],40);
    assertVerifierComponent((NumberRestriction) components[31],41);
    assertVerifierComponent((NumberRestriction) components[32],41);
    assertVerifierComponent((VerifierRule) components[33],46);
    assertVerifierComponent((VerifierRule) components[34],37);
    assertVerifierComponent((RulePackage) components[35],17);    
    assertVerifierComponent((SubPattern) components[36],48);
    assertVerifierComponent((SubPattern) components[37],49);
    assertVerifierComponent((SubPattern) components[38],50);
    assertVerifierComponent((SubPattern) components[39],39);
    assertVerifierComponent((SubPattern) components[40],40);
    assertVerifierComponent((SubPattern) components[41],41);
    assertVerifierComponent((SubRule) components[42],46);
    assertVerifierComponent((SubRule) components[43],37);
    assertVerifierComponent((WorkingMemory) components[44],-1);
  }

  void assertVerifierComponent(VerifierComponent component, int line) {
    assertNotNull(component);
    assertNotNull(component.getDescr());
    if(component instanceof TextConsequence) {
      assertEquals(line, ((TextConsequence)component).getDescr().getConsequenceLine());
    } else {
      assertEquals(line, component.getDescr().getLine());
    }
  }
}
