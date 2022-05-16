/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.xml.compiler;

import org.jbpm.compiler.xml.SemanticModule;
import org.jbpm.compiler.xml.compiler.rules.AccumulateHandler;
import org.jbpm.compiler.xml.compiler.rules.AccumulateHelperHandler;
import org.jbpm.compiler.xml.compiler.rules.AndHandler;
import org.jbpm.compiler.xml.compiler.rules.CollectHandler;
import org.jbpm.compiler.xml.compiler.rules.EvalHandler;
import org.jbpm.compiler.xml.compiler.rules.ExistsHandler;
import org.jbpm.compiler.xml.compiler.rules.ExprConstraintHandler;
import org.jbpm.compiler.xml.compiler.rules.ExpressionHandler;
import org.jbpm.compiler.xml.compiler.rules.FieldBindingHandler;
import org.jbpm.compiler.xml.compiler.rules.FieldConstraintHandler;
import org.jbpm.compiler.xml.compiler.rules.ForallHandler;
import org.jbpm.compiler.xml.compiler.rules.FromHandler;
import org.jbpm.compiler.xml.compiler.rules.FunctionHandler;
import org.jbpm.compiler.xml.compiler.rules.LiteralRestrictionHandler;
import org.jbpm.compiler.xml.compiler.rules.NotHandler;
import org.jbpm.compiler.xml.compiler.rules.OrHandler;
import org.jbpm.compiler.xml.compiler.rules.PackageHandler;
import org.jbpm.compiler.xml.compiler.rules.PatternHandler;
import org.jbpm.compiler.xml.compiler.rules.PredicateHandler;
import org.jbpm.compiler.xml.compiler.rules.QualifiedIdentifierRestrictionHandler;
import org.jbpm.compiler.xml.compiler.rules.QueryHandler;
import org.jbpm.compiler.xml.compiler.rules.RestrictionConnectiveHandler;
import org.jbpm.compiler.xml.compiler.rules.ReturnValueRestrictionHandler;
import org.jbpm.compiler.xml.compiler.rules.RuleHandler;
import org.jbpm.compiler.xml.compiler.rules.VariableRestrictionsHandler;
import org.jbpm.compiler.xml.core.DefaultSemanticModule;

public class RulesSemanticModule extends DefaultSemanticModule
        implements
        SemanticModule {
    public RulesSemanticModule(String url) {
        super(url);

        addHandler("package",
                new PackageHandler());
        addHandler("rule",
                new RuleHandler());
        addHandler("query",
                new QueryHandler());
        addHandler("attribute",
                null);
        addHandler("function",
                new FunctionHandler());

        // Conditional Elements
        addHandler("lhs",
                new AndHandler());

        addHandler("and-restriction-connective",
                new RestrictionConnectiveHandler());

        addHandler("or-restriction-connective",
                new RestrictionConnectiveHandler());

        addHandler("and-constraint-connective",
                new RestrictionConnectiveHandler());
        addHandler("or-constraint-connective",
                new RestrictionConnectiveHandler());

        addHandler("and-conditional-element",
                new AndHandler());

        addHandler("or-conditional-element",
                new OrHandler());

        addHandler("not",
                new NotHandler());
        addHandler("exists",
                new ExistsHandler());
        addHandler("eval",
                new EvalHandler());
        addHandler("pattern",
                new PatternHandler());

        addHandler("from",
                new FromHandler());
        addHandler("forall",
                new ForallHandler());
        addHandler("collect",
                new CollectHandler());
        addHandler("accumulate",
                new AccumulateHandler());

        // Field Constraints
        addHandler("expr",
                new ExprConstraintHandler());
        addHandler("field-constraint",
                new FieldConstraintHandler());
        addHandler("literal-restriction",
                new LiteralRestrictionHandler());
        addHandler("variable-restriction",
                new VariableRestrictionsHandler());
        addHandler("predicate",
                new PredicateHandler());

        addHandler("return-value-restriction",
                new ReturnValueRestrictionHandler());
        addHandler("qualified-identifier-restriction",
                new QualifiedIdentifierRestrictionHandler());

        addHandler("field-binding",
                new FieldBindingHandler());

        addHandler("field-binding",
                new FieldBindingHandler());

        addHandler("init",
                new AccumulateHelperHandler());
        addHandler("action",
                new AccumulateHelperHandler());
        addHandler("result",
                new AccumulateHelperHandler());
        addHandler("reverse",
                new AccumulateHelperHandler());

        addHandler("external-function",
                new AccumulateHelperHandler());

        addHandler("expression",
                new ExpressionHandler());
    }
}
