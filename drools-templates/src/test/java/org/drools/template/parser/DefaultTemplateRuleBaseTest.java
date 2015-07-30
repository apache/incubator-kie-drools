/*
 * Copyright 2015 JBoss Inc
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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.Constraint;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        assertEquals(1, packages.length);
        Map<String, String> globals = packages[0].getGlobals();
        assertEquals(DefaultGenerator.class.getName(), globals.get("generator"));
        Collection<org.kie.api.definition.rule.Rule> rules = packages[0].getRules();
        assertEquals(1, rules.size());
        assertEquals("template1", rules.iterator().next().getName());
        GroupElement lhs = ((RuleImpl)rules.iterator().next()).getLhs();
        //when
        //  r : Row()
        //  column1 : Column(name == "column1")
        //  exists LongCell(row == r, column == column1, value == 10)
        //  column2 : Column(name == "column2")
        //  exists LongCell(row == r, column == column2, value < 5 | > 20)
        //  column3 : Column(name == "column3")
        //  exists StringCell(row == r, column == column3, value == "xyz")
        assertEquals(7, lhs.getChildren().size());
        org.drools.core.rule.Pattern pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(1);
        assertEquals(1, pattern.getConstraints().size());
        Constraint constraint = pattern.getConstraints().get(0);
        GroupElement exists = (GroupElement) lhs.getChildren().get(2);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertEquals(3, pattern.getConstraints().size());
        IndexableConstraint vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        assertEquals("column1", vconstraint.getRequiredDeclarations()[0].getIdentifier());
        pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(3);
        assertEquals(1, pattern.getConstraints().size());
        constraint = pattern.getConstraints().get(0);
        exists = (GroupElement) lhs.getChildren().get(4);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertEquals(3, pattern.getConstraints().size());
        vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        assertEquals("column2", vconstraint.getRequiredDeclarations()[0].getIdentifier());
        pattern = (org.drools.core.rule.Pattern) lhs.getChildren().get(5);
        assertEquals(1, pattern.getConstraints().size());
        constraint = pattern.getConstraints().get(0);
        exists = (GroupElement) lhs.getChildren().get(6);
        pattern = (org.drools.core.rule.Pattern) exists.getChildren().get(0);
        assertEquals(3, pattern.getConstraints().size());
        vconstraint = (IndexableConstraint) pattern.getConstraints().get(1);
        assertEquals(Column.class, vconstraint.getFieldIndex().getExtractor().getExtractToClass());
        assertEquals("column3", vconstraint.getRequiredDeclarations()[0].getIdentifier());
    }
}
