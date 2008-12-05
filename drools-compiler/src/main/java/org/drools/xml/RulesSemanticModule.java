package org.drools.xml;

import org.drools.xml.rules.AccumulateHandler;
import org.drools.xml.rules.AccumulateHelperHandler;
import org.drools.xml.rules.AndHandler;
import org.drools.xml.rules.CollectHandler;
import org.drools.xml.rules.EvalHandler;
import org.drools.xml.rules.ExistsHandler;
import org.drools.xml.rules.ExpressionHandler;
import org.drools.xml.rules.FieldBindingHandler;
import org.drools.xml.rules.FieldConstraintHandler;
import org.drools.xml.rules.ForallHandler;
import org.drools.xml.rules.FromHandler;
import org.drools.xml.rules.FunctionHandler;
import org.drools.xml.rules.LiteralRestrictionHandler;
import org.drools.xml.rules.NotHandler;
import org.drools.xml.rules.OrHandler;
import org.drools.xml.rules.PackageHandler;
import org.drools.xml.rules.PatternHandler;
import org.drools.xml.rules.PredicateHandler;
import org.drools.xml.rules.QualifiedIdentifierRestrictionHandler;
import org.drools.xml.rules.QueryHandler;
import org.drools.xml.rules.RestrictionConnectiveHandler;
import org.drools.xml.rules.ReturnValueRestrictionHandler;
import org.drools.xml.rules.RuleHandler;
import org.drools.xml.rules.VariableRestrictionsHandler;

public class RulesSemanticModule extends DefaultSemanticModule
    implements
    SemanticModule {
    public RulesSemanticModule() {
        super( "http://drools.org/drools-5.0" );

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

        addHandler( "and-conditional-element",
                    new AndHandler() );

        addHandler( "or-conditional-element",
                    new OrHandler() );

        addHandler( "and-constraint-connective",
                    new AndHandler() );
        addHandler( "or-constraint-connective",
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
