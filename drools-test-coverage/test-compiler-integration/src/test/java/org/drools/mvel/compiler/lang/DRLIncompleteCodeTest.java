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
package org.drools.mvel.compiler.lang;

import java.util.LinkedList;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.builder.impl.EvaluatorRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.Location;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.assertj.core.api.Assertions.assertThat;

public class DRLIncompleteCodeTest {

    @Before
    public void setup() {
        // just initialising the static operator definitions
        new EvaluatorRegistry();
    }
    
    @Test
    @Ignore
    public void testIncompleteCode1() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.* rule MyRule when Class ( property memberOf collexction ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        System.out.println(parser.getErrors());

        assertThat(descr).isNotNull();
        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");

        assertThat(getLastIntegerValue(parser.getEditorSentences().get(2)
                .getContent())).isEqualTo(Location.LOCATION_LHS_INSIDE_CONDITION_END);
    }

    @Test
    public void testIncompleteCode2() throws DroolsParserException,
            RecognitionException {
        String input = "rule MyRule when Class ( property memberOf collection ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr).isNotNull();
        assertThat(getLastIntegerValue(parser.getEditorSentences().get(0)
                .getContent())).isEqualTo(Location.LOCATION_LHS_INSIDE_CONDITION_END);
    }

    @Test
    public void testIncompleteCode3() throws DroolsParserException,
            RecognitionException {
        String input = "rule MyRule when Class ( property > somevalue ) then end query MyQuery Class ( property == collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr).isNotNull();
        assertThat(descr.getRules().get(0).getName()).isEqualTo("MyRule");

        assertThat(descr).isNotNull();
        assertThat(descr.getRules().get(1).getName()).isEqualTo("MyQuery");

        assertThat(getLastIntegerValue(parser
                .getEditorSentences().get(0).getContent())).isEqualTo(Location.LOCATION_RHS);
    }

    @Test
    public void testIncompleteCode4() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property == collection ) then end "
                + " query MyQuery Class ( property == collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");

        assertThat(descr).isNotNull();
        assertThat(descr.getRules().get(0).getName()).isEqualTo("MyRule");

        assertThat(descr).isNotNull();
        assertThat(descr.getRules().get(1).getName()).isEqualTo("MyQuery");
    }

    @Test
    public void testIncompleteCode5() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr).isNotNull();
    }

    @Test
    public void testIncompleteCode6() throws DroolsParserException,
            RecognitionException {
        String input = "packe 1111.111 import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr).isNotNull();
    }

    @Test
    public void testIncompleteCode7() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c imrt a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr).isNotNull();
    }

    @Test
    public void testIncompleteCode8() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.1111.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        System.out.println(parser.getErrors());

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        // FIXME: assertEquals(2, descr.getRules().size());
        assertThat(parser.hasErrors()).isEqualTo(true);
    }

    @Test @Ignore
    public void testIncompleteCode9() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule xxxxx Class ( property memberOf collection ) then end "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");

        assertThat(descr.getRules().size()).isEqualTo(1);
        assertThat(descr.getRules().get(0).getName()).isEqualTo("MyQuery");
    }

    @Test @Ignore
    public void testIncompleteCode10() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule xxxxx Class ( property memberOf "
                + " query MyQuery Class ( property memberOf collection ) end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");

        assertThat(descr.getRules().size()).isEqualTo(0);
    }

    @Test
    public void testIncompleteCode11() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c import a.b.c.*"
                + " rule MyRule when Class ( property memberOf collection ) then end "
                + " qzzzzuery MyQuery Class ( property ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");

        assertThat(descr).isNotNull();
        assertThat(descr.getRules().get(0).getName()).isEqualTo("MyRule");
    }

    @Test
    public void testIncompleteCode12() throws DroolsParserException,
            RecognitionException {
        String input = "package a.b.c " + "import a.b.c.* " + "rule MyRule"
                + "  when " + "    m: Message(  ) " + "    " + "  then"
                + "end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        assertThat(descr).isNotNull();

        assertThat(descr.getNamespace()).isEqualTo("a.b.c");
        assertThat(descr.getImports().get(0)
                .getTarget()).isEqualTo("a.b.c.*");
    }

    @Test
    public void testIncompleteCode13() throws DroolsParserException,
            RecognitionException {
        String input = "package com.sample "
                + "import com.sample.DroolsTest.Message; "
                + "rule \"Hello World\"" + "  when " + "  then" + "     \\\" "
                + "end ";
        DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        PackageDescr descr = parser.parse(true, input);
        assertThat(descr).isNotNull();
    }

    @SuppressWarnings("unchecked")
    private int getLastIntegerValue(LinkedList list) {
        // System.out.println(list.toString());
        int lastIntergerValue = -1;
        for (Object object : list) {
            if (object instanceof Integer) {
                lastIntergerValue = (Integer) object;
            }
        }
        return lastIntergerValue;
    }
}
