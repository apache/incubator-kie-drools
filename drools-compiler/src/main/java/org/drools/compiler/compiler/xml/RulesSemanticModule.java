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

package org.drools.compiler.compiler.xml;

import org.drools.compiler.compiler.xml.rules.AccumulateHandler;
import org.drools.compiler.compiler.xml.rules.AccumulateHelperHandler;
import org.drools.compiler.compiler.xml.rules.AndHandler;
import org.drools.compiler.compiler.xml.rules.CollectHandler;
import org.drools.compiler.compiler.xml.rules.EvalHandler;
import org.drools.compiler.compiler.xml.rules.ExistsHandler;
import org.drools.compiler.compiler.xml.rules.ExprConstraintHandler;
import org.drools.compiler.compiler.xml.rules.ExpressionHandler;
import org.drools.compiler.compiler.xml.rules.FieldBindingHandler;
import org.drools.compiler.compiler.xml.rules.FieldConstraintHandler;
import org.drools.compiler.compiler.xml.rules.ForallHandler;
import org.drools.compiler.compiler.xml.rules.FromHandler;
import org.drools.compiler.compiler.xml.rules.FunctionHandler;
import org.drools.compiler.compiler.xml.rules.LiteralRestrictionHandler;
import org.drools.compiler.compiler.xml.rules.NotHandler;
import org.drools.compiler.compiler.xml.rules.OrHandler;
import org.drools.compiler.compiler.xml.rules.PackageHandler;
import org.drools.compiler.compiler.xml.rules.PatternHandler;
import org.drools.compiler.compiler.xml.rules.PredicateHandler;
import org.drools.compiler.compiler.xml.rules.QualifiedIdentifierRestrictionHandler;
import org.drools.compiler.compiler.xml.rules.QueryHandler;
import org.drools.compiler.compiler.xml.rules.RestrictionConnectiveHandler;
import org.drools.compiler.compiler.xml.rules.ReturnValueRestrictionHandler;
import org.drools.compiler.compiler.xml.rules.RuleHandler;
import org.drools.compiler.compiler.xml.rules.VariableRestrictionsHandler;
import org.drools.core.xml.DefaultSemanticModule;
import org.drools.core.xml.SemanticModule;

public class RulesSemanticModule extends DefaultSemanticModule
    implements
    SemanticModule {
    public RulesSemanticModule(String url) {
        super( url );

        addHandler( "package",
                    new PackageHandler() );
        addHandler( "rule",
                    new RuleHandler() );
        addHandler( "query",
                    new QueryHandler() );
        addHandler( "attribute",
                    null );
        addHandler( "function",
                    new FunctionHandler() );

        // Conditional Elements
        addHandler( "lhs",
                    new AndHandler() );

        addHandler( "and-restriction-connective",
                    new RestrictionConnectiveHandler() );

        addHandler( "or-restriction-connective",
                    new RestrictionConnectiveHandler() );
        

        addHandler( "and-constraint-connective",
                    new RestrictionConnectiveHandler() );
        addHandler( "or-constraint-connective",
                    new RestrictionConnectiveHandler() );        

        addHandler( "and-conditional-element",
                    new AndHandler() );

        addHandler( "or-conditional-element",
                    new OrHandler() );


        addHandler( "not",
                    new NotHandler() );
        addHandler( "exists",
                    new ExistsHandler() );
        addHandler( "eval",
                    new EvalHandler() );
        addHandler( "pattern",
                    new PatternHandler() );

        addHandler( "from",
                    new FromHandler() );
        addHandler( "forall",
                    new ForallHandler() );
        addHandler( "collect",
                    new CollectHandler() );
        addHandler( "accumulate",
                    new AccumulateHandler() );

        // Field Constraints
        addHandler( "expr",
                    new ExprConstraintHandler() );        
        addHandler( "field-constraint",
                    new FieldConstraintHandler() );
        addHandler( "literal-restriction",
                    new LiteralRestrictionHandler() );
        addHandler( "variable-restriction",
                    new VariableRestrictionsHandler() );
        addHandler( "predicate",
                    new PredicateHandler() );

        addHandler( "return-value-restriction",
                    new ReturnValueRestrictionHandler() );
        addHandler( "qualified-identifier-restriction",
                    new QualifiedIdentifierRestrictionHandler() );

        addHandler( "field-binding",
                    new FieldBindingHandler() );

        addHandler( "field-binding",
                    new FieldBindingHandler() );

        addHandler( "init",
                    new AccumulateHelperHandler() );
        addHandler( "action",
                    new AccumulateHelperHandler() );
        addHandler( "result",
                    new AccumulateHelperHandler() );
        addHandler( "reverse",
                    new AccumulateHelperHandler() );

        addHandler( "external-function",
                    new AccumulateHelperHandler() );

        addHandler( "expression",
                    new ExpressionHandler() );
    }
}
