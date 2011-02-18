/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.template.model.Condition;
import org.drools.template.model.Consequence;
import org.drools.template.model.DRLOutput;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.model.Rule;

/**
 * 
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Create a rule base for the set of rule templates in the 
 * TemplateContainer. These rules are used internally by the
 * engine to generate the actual decision table rules based on
 * which columns have been filled in.
 * 
 * Basically, if a rule template requires columns A and B then 
 * the template rule base will generate a rule with columns A and B
 * as the LHS and a RHS which triggers the rule to be generated.
 * ie.
 * rule "template1"
 *   when
 *     r : Row()
 *     column1 : Column(name == "column1")
 *     Cell(row == r, column == column1)
 *     column2 : Column(name == "column2")
 *     Cell(row == r, column == column2, value == "xyz")
 *   then
 *     generator.generate( "template1", r);
 *   end
 * 
 */
public class DefaultTemplateRuleBase implements TemplateRuleBase {
    private RuleBase ruleBase;

    public DefaultTemplateRuleBase(final TemplateContainer tc) {
        ruleBase = readRule(getDTRules(tc.getTemplates()));
    }

    /* (non-Javadoc)
     * @see org.drools.decisiontable.parser.TemplateRuleBase#newWorkingMemory()
     */
    public StatefulSession newStatefulSession() {
        return ruleBase.newStatefulSession();
    }
    /**
     *
     * @param templates
     * @return
     */
    private String getDTRules(Map<String, RuleTemplate> templates) {
        org.drools.template.model.Package p = new org.drools.template.model.Package(
                DefaultTemplateRuleBase.class.getPackage().getName());
        addImports(p);
        addGlobals(p);
        int i = 1;
        for ( RuleTemplate template : templates.values() ) {
            createTemplateRule(p, i++, template);
        }
        DRLOutput out = new DRLOutput();
        p.renderDRL(out);
        return out.getDRL();

    }

    private void createTemplateRule(org.drools.template.model.Package p, int index, RuleTemplate template) {
        Rule rule = new Rule(template.getName(), null, index);
        Condition condition = new Condition();
        condition.setSnippet("r : Row()");
        rule.addCondition(condition);
        createColumnConditions(template, rule);
        rule.addConsequence(createConsequence(template));
        p.addRule(rule);
    }

    private void createColumnConditions(RuleTemplate template, Rule rule) {
        for ( TemplateColumn column : template.getColumns() ) {
            column.addCondition(rule);
        }
    }


    private void addGlobals(org.drools.template.model.Package p) {
        Global global = new Global();
        global.setClassName(DefaultGenerator.class.getName());
        global.setIdentifier("generator");
        p.addVariable(global);
    }

    private void addImports(org.drools.template.model.Package p) {
        Import drlImport1 = new Import();
        drlImport1.setClassName(Map.class.getName());
        Import drlImport2 = new Import();
        drlImport2.setClassName(HashMap.class.getName());
        p.addImport(drlImport1);
        p.addImport(drlImport2);
    }

    private Consequence createConsequence(RuleTemplate template) {
        StringBuffer action = new StringBuffer();
        action.append("generator.generate( \"");
        action.append(template.getName()).append("\", r);");
        final Consequence consequence = new Consequence();
        consequence.setSnippet(action.toString());
        return consequence;
    }

    private RuleBase readRule(String drl) {
        try {
//			System.out.println(drl);
            // read in the source
            Reader source = new StringReader(drl);
            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl(source);
            Package pkg = builder.getPackage();

            // add the package to a rulebase (deploy the rule package).
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage(pkg);
            return ruleBase;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
