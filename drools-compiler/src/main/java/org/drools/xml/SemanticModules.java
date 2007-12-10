package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

import org.drools.xml.processes.ActionNodeHandler;
import org.drools.xml.processes.ConnectionHandler;
import org.drools.xml.processes.EndNodeHandler;
import org.drools.xml.processes.GlobalHandler;
import org.drools.xml.processes.ImportHandler;
import org.drools.xml.processes.ProcessHandler;
import org.drools.xml.processes.StartNodeHandler;
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

public class SemanticModules {
    public Map<String, SemanticModule> modules;

    public SemanticModules() {
        this.modules = new HashMap<String, SemanticModule>();
        initDrl();
        initProcess();
    }

    public void addSemanticModule(SemanticModule module) {
        this.modules.put( module.getUri(),
                          module );
    }

    public SemanticModule getSemanticModule(String uri) {
        return this.modules.get( uri );
    }

    private void initProcess() {
        SemanticModule module = new DefaultSemanticModule( "http://drools.org/drools-4.0/process" );

        module.addHandler( "process",
                           new ProcessHandler() );
        module.addHandler( "start",
                           new StartNodeHandler() );
        module.addHandler( "end",
                           new EndNodeHandler() );
        module.addHandler( "action",
                           new ActionNodeHandler() );
        module.addHandler( "connection",
                           new ConnectionHandler() );
        module.addHandler( "import",
                           new ImportHandler() );
        module.addHandler( "global",
                           new GlobalHandler() );        
        addSemanticModule( module );
    }

    private void initDrl() {
        SemanticModule module = new DefaultSemanticModule( "http://drools.org/drools-4.0" );

        module.addHandler( "package",
                           new PackageHandler() );
        module.addHandler( "rule",
                           new RuleHandler() );
        module.addHandler( "query",
                           new QueryHandler() );
        module.addHandler( "attribute",
                           null );
        module.addHandler( "function",
                           new FunctionHandler() );

        // Conditional Elements
        module.addHandler( "lhs",
                           new AndHandler() );

        module.addHandler( "and-restriction-connective",
                           new RestrictionConnectiveHandler() );

        module.addHandler( "or-restriction-connective",
                           new RestrictionConnectiveHandler() );

        module.addHandler( "and-conditional-element",
                           new AndHandler() );

        module.addHandler( "or-conditional-element",
                           new OrHandler() );

        module.addHandler( "and-constraint-connective",
                           new AndHandler() );
        module.addHandler( "or-constraint-connective",
                           new OrHandler() );

        module.addHandler( "not",
                           new NotHandler() );
        module.addHandler( "exists",
                           new ExistsHandler() );
        module.addHandler( "eval",
                           new EvalHandler() );
        module.addHandler( "pattern",
                           new PatternHandler() );

        module.addHandler( "from",
                           new FromHandler() );
        module.addHandler( "forall",
                           new ForallHandler() );
        module.addHandler( "collect",
                           new CollectHandler() );
        module.addHandler( "accumulate",
                           new AccumulateHandler() );

        // Field Constraints
        module.addHandler( "field-constraint",
                           new FieldConstraintHandler() );
        module.addHandler( "literal-restriction",
                           new LiteralRestrictionHandler() );
        module.addHandler( "variable-restriction",
                           new VariableRestrictionsHandler() );
        module.addHandler( "predicate",
                           new PredicateHandler() );

        module.addHandler( "return-value-restriction",
                           new ReturnValueRestrictionHandler() );
        module.addHandler( "qualified-identifier-restriction",
                           new QualifiedIdentifierRestrictionHandler() );

        module.addHandler( "field-binding",
                           new FieldBindingHandler() );

        module.addHandler( "field-binding",
                           new FieldBindingHandler() );

        module.addHandler( "init",
                           new AccumulateHelperHandler() );
        module.addHandler( "action",
                           new AccumulateHelperHandler() );
        module.addHandler( "result",
                           new AccumulateHelperHandler() );
        module.addHandler( "reverse",
                           new AccumulateHelperHandler() );

        module.addHandler( "external-function",
                           new AccumulateHelperHandler() );

        module.addHandler( "expression",
                           new ExpressionHandler() );

        addSemanticModule( module );
    }
}
