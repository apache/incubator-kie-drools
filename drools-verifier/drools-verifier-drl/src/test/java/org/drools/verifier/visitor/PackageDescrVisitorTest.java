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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.verifier.Verifier;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReportFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class PackageDescrVisitorTest {

    private VerifierData        verifierData;
    private PackageDescrVisitor packageDescrVisitor;

    @BeforeEach
    public void setUp() throws Exception {
        verifierData = VerifierReportFactory.newVerifierData();
        packageDescrVisitor = new PackageDescrVisitor(verifierData,
                                                      Collections.EMPTY_LIST);
    }


    @Test
    void testVisit() throws Exception {

        PackageDescr packageDescr = getPackageDescr(Verifier.class.getResourceAsStream("Misc3.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> all = verifierData.getAll();

        Set<String> names = new HashSet<String>();
        for (VerifierComponent verifierComponent : all) {
            String path = verifierComponent.getPath();

            System.out.println("-" + verifierComponent);
            if (verifierComponent.getDescr() != null) {
                System.out.println(" \n\t\t => " + verifierComponent.getDescr().getLine() + ":" + +verifierComponent.getDescr().getEndLine() + " " + verifierComponent.getDescr().getText());
            } else {
                System.out.println(" \n\t\t => null for " + verifierComponent.getClass().getSimpleName());
            }

            if (names.contains(path)) {
                fail("Dublicate path " + path);
            } else {
                names.add(path);
            }
        }

        assertThat(all).isNotNull();
        assertThat(all.size()).isEqualTo(45);

    }

    @Test
    void testSubPatterns() throws Exception {

        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("SubPattern.drl"));

        assertThat(packageDescr).isNotNull();

        packageDescrVisitor.visitPackageDescr(packageDescr);

        Collection<VerifierComponent> all = verifierData.getAll();

        assertThat(all).isNotNull();

        SubPattern test1SubPattern = null;
        SubPattern test2SubPattern = null;
        SubRule test1SubRule = null;
        SubRule test2SubRule = null;

        for (VerifierComponent verifierComponent : all) {
            //            System.out.println( verifierComponent );

            System.out.println("-" + verifierComponent);
            if (verifierComponent.getDescr() != null) {
                System.out.println(" \n\t\t => " + verifierComponent.getDescr().getLine() + ":" + +verifierComponent.getDescr().getEndLine() + " " + verifierComponent.getDescr().getText());
            } else {
                System.out.println(" \n\t\t => null for " + verifierComponent.getClass().getSimpleName());
            }


            if (verifierComponent.getVerifierComponentType().equals(VerifierComponentType.SUB_PATTERN)) {
                SubPattern subPattern = (SubPattern) verifierComponent;
                if ("Test 1".equals(subPattern.getRuleName())) {
                    assertThat(test1SubPattern).isNull();
                    test1SubPattern = subPattern;
                } else if ("Test 2".equals(subPattern.getRuleName())) {
                    assertThat(test2SubPattern).isNull();
                    test2SubPattern = subPattern;
                }
            }
            if (verifierComponent.getVerifierComponentType().equals(VerifierComponentType.SUB_RULE)) {
                SubRule subRule = (SubRule) verifierComponent;
                if ("Test 1".equals(subRule.getRuleName())) {
                    assertThat(test1SubRule).isNull();
                    test1SubRule = subRule;
                } else if ("Test 2".equals(subRule.getRuleName())) {
                    assertThat(test2SubRule).isNull();
                    test2SubRule = subRule;
                }
            }
        }

        assertThat(test1SubPattern).isNotNull();
        assertThat(test1SubPattern.getItems().size()).isEqualTo(3);
        assertThat(test2SubPattern).isNotNull();
        assertThat(test2SubPattern.getItems().size()).isEqualTo(3);
        assertThat(test1SubRule).isNotNull();
        assertThat(test1SubRule.getItems().size()).isEqualTo(1);
        assertThat(test2SubRule).isNotNull();
        assertThat(test2SubRule.getItems().size()).isEqualTo(1);

    }

    private PackageDescr getPackageDescr(InputStream resourceAsStream) throws DroolsParserException {
        Reader drlReader = new InputStreamReader(resourceAsStream);
        return new DrlParser(LanguageLevelOption.DRL5).parse(drlReader);
    }

}
