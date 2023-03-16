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

package org.drools.template.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.Constraint;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultTemplateRuleBaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleTemplate() throws Exception {
        TemplateContainer tc = new TemplateContainer() {
            private Column[] columns = new Column[]{
                    new LongColumn("column1"),
                    new LongColumn("column2"),
                    new StringColumn("column3")
            };

            public Column[] getColumns() {
                return columns;
            }

            public String getHeader() {
                return null;
            }

            public Map<String, RuleTemplate> getTemplates() {
                Map<String, RuleTemplate> templates = new HashMap<String, RuleTemplate>();
                RuleTemplate ruleTemplate = new RuleTemplate("template1", this);
                ruleTemplate.addColumn("column1 == 10");
                ruleTemplate.addColumn("column2 < 5 || > 20");
                ruleTemplate.addColumn("column3 == \"xyz\"");
                templates.put("template1", ruleTemplate);
                return templates;
            }

            public Column getColumn(String name) {
                return columns[Integer.parseInt(name.substring(6)) - 1];
            }

        };
        DefaultTemplateRuleBase ruleBase = new DefaultTemplateRuleBase(tc);
        InternalKnowledgePackage[] packages = ((KnowledgeBaseImpl)ruleBase.newStatefulSession().getKieBase()).getPackages();
        assertThat(packages.length).isEqualTo(1);
        Map<String, Class<?>> globals = packages[0].getGlobals();
        assertThat(globals.get("generator")).isEqualTo(DefaultGenerator.class);
        Collection<org.kie.api.definition.rule.Rule> rules = packages[0].getRules();
        assertThat(rules.size()).isEqualTo(1);
        assertThat(rules.iterator().next().getName()).isEqualTo("template1");
        GroupElement lhs = ((RuleImpl)rules.iterator().next()).getLhs();
        //when
        //  r : Row()
        //  column1 : Column(name == "column1")
        //  exists LongCell(row == r, column == column1, value == 10)
        //  column2 : Column(name == "column2")
        //  exists LongCell(row == r, column == column2, value < 5 | > 20)
        //  column3 : Column(name == "column3")
        //  exists StringCell(row == r, column == column3, value == "xyz")
        assertThat(lhs.getChildren().size()).isEqualTo(7);
        org.drools.core.rule.Pattern pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(1);
        assertThat(pattern.getConstraints().size()).isEqualTo(1);
        Constraint constraint = pattern.getConstraints().get(0);
        GroupElement exists = (GroupElement) lhs.getChildren().get(2);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertThat(pattern.getConstraints().size()).isEqualTo(3);
        IndexableConstraint vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertThat(vconstraint.getFieldIndex().getRightExtractor().getExtractToClass()).isEqualTo(Column.class);
        assertThat(vconstraint.getRequiredDeclarations()[0].getIdentifier()).isEqualTo("column1");
        pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(3);
        assertThat(pattern.getConstraints().size()).isEqualTo(1);
        constraint = pattern.getConstraints().get(0);
        exists = (GroupElement) lhs.getChildren().get(4);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertThat(pattern.getConstraints().size()).isEqualTo(3);
        vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertThat(vconstraint.getFieldIndex().getRightExtractor().getExtractToClass()).isEqualTo(Column.class);
        assertThat(vconstraint.getRequiredDeclarations()[0].getIdentifier()).isEqualTo("column2");
        pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(5);
        assertThat(pattern.getConstraints().size()).isEqualTo(1);
        constraint = pattern.getConstraints().get(0);
        exists = (GroupElement) lhs.getChildren().get(6);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertThat(pattern.getConstraints().size()).isEqualTo(3);
        vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertThat(vconstraint.getFieldIndex().getRightExtractor().getExtractToClass()).isEqualTo(Column.class);
        assertThat(vconstraint.getRequiredDeclarations()[0].getIdentifier()).isEqualTo("column3");
    }
}
