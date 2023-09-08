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
package org.drools.decisiontable;

import java.io.IOException;
import java.math.BigDecimal;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.After;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IgnoreNumericFormatTest {

    private KieSession ksession;

    @After
    public void tearDown() {
        if (ksession != null) {
            ksession.dispose();
        }
    }
    

    @Test
    public void testPercentAndCurrencyFormat() throws IOException {
        ignoreNumericFormat("ignore-numeric-format.drl.xls");
    }

    @Test
    public void testPercentAndCurrencyFormatWithReferenceCells() throws IOException {
        ignoreNumericFormat("ignore-numeric-format-ref-cell.drl.xls");
    }

    private void ignoreNumericFormat(String fileName) throws IOException {
        SpreadsheetCompiler compiler = new SpreadsheetCompiler();
        String drl = compiler.compile(ResourceFactory.newClassPathResource(fileName, getClass()).getInputStream(), InputType.XLS);
        // print drl if you want to debug

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLSX);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(fileName, getClass()), ResourceType.DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        ksession = kbase.newKieSession();

        ValueHolder a = new ValueHolder("A");
        ValueHolder b = new ValueHolder("B");
        ValueHolder c = new ValueHolder("C");
        ValueHolder d = new ValueHolder("D");

        ksession.insert(a);
        ksession.insert(b);
        ksession.insert(c);
        ksession.insert(d);
        
        ksession.fireAllRules();

        assertThat(a.getPercentValue()).isEqualTo(new BigDecimal("0.0"));
        assertThat(a.getCurrencyValue1()).isEqualTo(new BigDecimal("0.0"));
        assertThat(a.getCurrencyValue2()).isEqualTo(new BigDecimal("0.0"));
        assertThat(a.getCurrencyValue3()).isEqualTo(new BigDecimal("0.0"));
        assertThat(a.getIntValue()).isZero();
        assertThat(a.getDoubleValue()).isZero();

        assertThat(b.getPercentValue()).isEqualTo(new BigDecimal("0.01"));
        assertThat(b.getCurrencyValue1()).isEqualTo(new BigDecimal("0.5"));
        assertThat(b.getCurrencyValue2()).isEqualTo(new BigDecimal("0.5"));
        assertThat(b.getCurrencyValue3()).isEqualTo(new BigDecimal("0.5"));
        assertThat(b.getIntValue()).isEqualTo(-1);
        assertThat(b.getDoubleValue()).isEqualTo(0.5);

        assertThat(c.getPercentValue()).isEqualTo(new BigDecimal("0.5"));
        assertThat(c.getCurrencyValue1()).isEqualTo(new BigDecimal("1.0"));
        assertThat(c.getCurrencyValue2()).isEqualTo(new BigDecimal("1.0"));
        assertThat(c.getCurrencyValue3()).isEqualTo(new BigDecimal("1.0"));
        assertThat(c.getIntValue()).isEqualTo(1);
        assertThat(c.getDoubleValue()).isEqualTo(-1);

        assertThat(d.getPercentValue()).isEqualTo(new BigDecimal("1.0"));
        assertThat(d.getCurrencyValue1()).isEqualTo(new BigDecimal("100.0"));
        assertThat(d.getCurrencyValue2()).isEqualTo(new BigDecimal("100.0"));
        assertThat(d.getCurrencyValue3()).isEqualTo(new BigDecimal("100.0"));
        assertThat(d.getIntValue()).isEqualTo(100);
        assertThat(d.getDoubleValue()).isEqualTo(100);
    }
}
