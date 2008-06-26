package org.drools.rule.builder.dialect.clips;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.clips.Appendable;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;
import org.drools.clips.StringBuilderAppendable;
import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.compiler.ReturnValueDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.builder.dialect.mvel.MVELDialectConfiguration;

public class ClipsDialect extends MVELDialect {

    private static final ClipsConsequenceBuilder CONSEQUENCE_BUILDER  = new ClipsConsequenceBuilder();
    private static final ClipsEvalBuilder        EVAL_BUILDER         = new ClipsEvalBuilder();
    private static final ClipsReturnValueBuilder RETURN_VALUE_BUILDER = new ClipsReturnValueBuilder();
    private static final ClipsPredicateBuilder   PREDICATE_BUILDER    = new ClipsPredicateBuilder();

    // a map of registered builders
    private static Map                           builders;

    public final static String                   ID                   = "clips";

    public ClipsDialect(PackageBuilder builder,
                        PackageRegistry pkgRegistry,
                        Package pkg) {
        super( builder,
               pkgRegistry,
               pkg,
               ID );
        setLanguageLevel( 5 );
    }

    public String getId() {
        return ID;
    }

    public Map getBuilders() {
        return this.builders;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.CONSEQUENCE_BUILDER;
    }

    public RuleConditionBuilder getEvalBuilder() {
        return this.EVAL_BUILDER;
    }

    public ReturnValueBuilder getReturnValueBuilder() {
        return this.RETURN_VALUE_BUILDER;
    }

    public PredicateBuilder getPredicateBuilder() {
        return this.PREDICATE_BUILDER;
    }

    public Dialect.AnalysisResult analyzeExpression(PackageBuildContext context,
                                                    BaseDescr descr,
                                                    Object content,
                                                    final Set[] availableIdentifiers,
                                                    Map localTypes) {
        if ( descr instanceof PredicateDescr ) {
            Appendable builder = new StringBuilderAppendable();
            PredicateDescr pdescr = (PredicateDescr) descr;
            if ( pdescr.getContent() instanceof LispForm ) {
                FunctionHandlers.dump( (LispForm) pdescr.getContent(),
                                       builder );
    
                content = builder.toString();
                pdescr.setContent( content );
            }
        } else if ( descr instanceof ReturnValueRestrictionDescr ) {
//            Appendable builder = new StringBuilderAppendable();
//            ReturnValueRestrictionDescr rdescr = (ReturnValueRestrictionDescr) descr;
//            FunctionHandlers.dump( (LispForm) rdescr.getContent(),
//                                   builder );
//            content = builder.toString();
//            rdescr.setContent( content );
        }
        return super.analyzeExpression( context,
                                        descr,
                                        content,
                                        availableIdentifiers,
                                        localTypes );
    }

}
